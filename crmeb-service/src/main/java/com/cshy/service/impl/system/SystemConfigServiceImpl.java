package com.cshy.service.impl.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.config.CrmebConfig;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.system.SystemFormCheckRequest;
import com.cshy.common.model.request.system.SystemFormItemCheckRequest;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.model.vo.ExpressSheetVo;
import com.cshy.common.model.entity.system.SystemConfig;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.dao.system.SystemConfigDao;
import com.cshy.service.service.system.SystemAttachmentService;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.system.SystemFormTempService;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SystemConfigServiceImpl 接口实现
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigDao dao;

    @Autowired
    private SystemFormTempService systemFormTempService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    CrmebConfig crmebConfig;

    private static final String redisKey = RedisKey.SYS_CONFIG_KEY;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 根据menu name 获取 value
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKey(String name) {
        return get(name).getValue();
    }


    /**
     * 同时获取多个配置
     *
     * @param keys 多个配置key
     * @return List<String>
     */
    @Override
    public List<String> getValuesByKeys(List<String> keys) {
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            result.add(getValueByKey(key));
        }
        return result;
    }

    /**
     * 根据 name 获取 value 找不到抛异常
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKeyException(String name) {
        String value = get(name).getValue();
        if (null == value) {
            throw new CrmebException("没有找到" + name + "数据");
        }

        return value;
    }

    /**
     * 整体保存表单数据
     *
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return boolean
     */
    @Override
    public Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(systemFormCheckRequest);

        List<SystemConfig> systemConfigList = new ArrayList<>();

        //批量添加
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setName(systemFormItemCheckRequest.getName());
            String value = systemAttachmentService.clearPrefix(systemFormItemCheckRequest.getValue());
            if (StrUtil.isBlank(value)) {
                //去掉图片域名之后没有数据则说明当前数据就是图片域名
                value = systemFormItemCheckRequest.getValue();
            }
            systemConfig.setValue(value);
            systemConfig.setFormId(systemFormCheckRequest.getId());
            systemConfig.setTitle(systemFormItemCheckRequest.getTitle());
            systemConfigList.add(systemConfig);
        }

        //修改之前的数据
        updateStatusByFormId(systemFormCheckRequest.getId());

        saveBatch(systemConfigList);

        //删除之前隐藏的数据
        deleteStatusByFormId(systemFormCheckRequest.getId());

        List<SystemConfig> forAsyncPram = systemConfigList.stream().map(e -> {
            e.setStatus(true);
            return e;
        }).collect(Collectors.toList());
        async(forAsyncPram);

        return true;
    }


    /**
     * updateStatusByGroupId
     *
     * @param formId Integer formId
     */
    private void updateStatusByFormId(Integer formId) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getFormId, formId).eq(SystemConfig::getStatus, false);

        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setStatus(true);
        update(new LambdaUpdateWrapper<SystemConfig>().set(SystemConfig::getStatus, true).eq(SystemConfig::getFormId, formId).eq(SystemConfig::getStatus, false));

    }

    private void deleteStatusByFormId(Integer formId) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //删除已经隐藏的数据
        lambdaQueryWrapper.eq(SystemConfig::getFormId, formId).eq(SystemConfig::getStatus, true);
        List<SystemConfig> systemConfigList = dao.selectList(lambdaQueryWrapper);
        dao.delete(lambdaQueryWrapper);
        async(systemConfigList);
    }


    /**
     * 保存或更新配置数据
     *
     * @param name  菜单名称
     * @param value 菜单值
     * @return boolean
     */
    @Override
    public Boolean updateOrSaveValueByName(String name, String value) {
        value = systemAttachmentService.clearPrefix(value);

        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        List<SystemConfig> systemConfigs = dao.selectList(lambdaQueryWrapper);
        if (systemConfigs.size() >= 2) {
            throw new CrmebException("配置名称存在多个请检查配置 sys_config 重复数据：" + name + "条数：" + systemConfigs.size());
        } else if (systemConfigs.size() == 1) {
            SystemConfig systemConfig = systemConfigs.get(0);
            systemConfig.setValue(value);
            updateById(systemConfig);
            setRedis(systemConfig);
            return true;
        } else {
            SystemConfig systemConfig = new SystemConfig().setName(name).setValue(value).setFormId(0);
            save(systemConfig);
            setRedis(systemConfig);
            return true;
        }
    }


    /**
     * 根据formId查询数据
     *
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    @Override
    public HashMap<String, String> info(Integer formId) {
        //缓存获取
        Collection<String> keys = redisUtil.keys(getCacheKey(formId + ":*"));
        HashMap<String, String> map = new HashMap<>();
        if (keys.size() == 0) {
            //重新加载缓存
            List<SystemConfig> systemConfigs = loadingConfigCacheByFormId(formId);
            systemConfigs.stream().forEach(systemConfig -> {
                map.put(systemConfig.getName(), systemConfig.getValue());
                map.put("id", formId.toString());
            });
            return map;
        }
        keys.stream().forEach(key -> {
            Object o = redisUtil.get(key);
            SystemConfig systemConfig = (SystemConfig) o;
            map.put(systemConfig.getName(), systemConfig.getValue());
            map.put("id", formId.toString());
        });
        return map;
    }

    /**
     * 获取面单默认配置信息
     *
     * @return ExpressSheetVo
     */
    @Override
    public ExpressSheetVo getDeliveryInfo() {
        String exportId = get("config_export_id").getValue();
        String exportTempId = get("config_export_temp_id").getValue();
        String exportCom = get("config_export_com").getValue();
        String exportToName = get("config_export_to_name").getValue();
        String exportToTel = get("config_export_to_tel").getValue();
        String exportToAddress = get("config_export_to_address").getValue();
        String exportSiid = get("config_export_siid").getValue();
        String exportOpen = get("config_export_open").getValue();
        return new ExpressSheetVo(Integer.valueOf(exportId), exportCom, exportTempId, exportToName, exportToTel, exportToAddress, exportSiid, Integer.valueOf(exportOpen));
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SystemConfig> configsList = this.list();
        Map<Integer, List<SystemConfig>> listMap = configsList.stream().collect(Collectors.groupingBy(SystemConfig::getFormId));
        listMap.forEach((k, list) -> {
            list.forEach(config -> redisUtil.set(getCacheKey(k + ":" + config.getName()), config));
        });
    }

    @Override
    public List<SystemConfig> loadingConfigCacheByFormId(Integer formId) {
        List<SystemConfig> configsList = this.list(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getFormId, formId));
        Map<Integer, List<SystemConfig>> listMap = configsList.stream().collect(Collectors.groupingBy(SystemConfig::getFormId));
        listMap.forEach((k, list) -> {
            list.forEach(config -> redisUtil.set(getCacheKey(k + ":" + config.getName()), config));
        });
        return configsList;
    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisUtil.keys(redisKey + "*");
        redisUtil.deleteObject(keys);
    }

    @Override
    public List<String> modelNameList(String modelName) {
        Set<Class<?>> classes = scanEntities("com.cshy.common.model.entity");
        List<String> descList = classes.stream().filter(clazz -> {
            if (StringUtils.isNotBlank(modelName)) {
                ApiModel annotation = clazz.getAnnotation(ApiModel.class);
                return annotation.description().contains(modelName) || annotation.value().contains(modelName);
            } else {
                return true;
            }
        }).map(clazz -> {
            ApiModel annotation = clazz.getAnnotation(ApiModel.class);
            return StringUtils.isNotBlank(annotation.description()) ? annotation.description() : annotation.value();
        }).collect(Collectors.toList());
        return descList;
    }

    @Override
    public Class<?> queryModel(String modelName) {
        Set<Class<?>> classes = scanEntities("com.cshy.common.model.entity");
        Optional<Class<?>> first = classes.stream().filter(clazz -> clazz.getAnnotation(ApiModel.class).description().equals(modelName)).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    @Override
    public String getTableCNNameByClass(Class clazz) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ApiModel.class));

        try {
            Optional<BeanDefinition> first = scanner.findCandidateComponents("com.cshy.common.model.entity")
                    .stream()
                    .filter(bd -> {
                        try {
                            return Class.forName(bd.getBeanClassName()).equals(clazz);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst();
            if (first.isPresent()) {
                Class<?> aClass = Class.forName(first.get().getBeanClassName());
                ApiModel annotation = aClass.getAnnotation(ApiModel.class);
                return annotation.description();
            }
        } catch (ClassNotFoundException e) {
            throw new CrmebException("获取实体类名称异常");
        }
        return null;
    }

    @Override
    public List<String> getFieldsByClass(Class clazz) {
        List<String> fields = Arrays.stream(clazz.getDeclaredFields()).map(field -> {
            if (field.isAnnotationPresent(ApiModelProperty.class)) {
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                return StringUtils.isNotBlank(annotation.value()) ? annotation.value() : field.getName();
            } else {
                return field.getName();
            }
        }).collect(Collectors.toList());
        return fields;
    }

    @Override
    public void scanNoAnnotationModel() {
        Set<Class<?>> classes = scanEntities("com.cshy.common.model.entity");
        //检查是否有实体类未标注apiModel注解
        List<Class<?>> classList = classes.stream()
//                .filter(clazz -> !(clazz.isAnnotationPresent(ApiModel.class) && clazz.isAnnotationPresent(TableName.class)) && !clazz.getSimpleName().toLowerCase().contains("base"))
                .filter(clazz -> !clazz.getSimpleName().toLowerCase().contains("base"))
                .collect(Collectors.toList());

        List<Class<?>> noAnClassList = Lists.newArrayList();
        List<Class<?>> noFieldAnClassList = Lists.newArrayList();
        classList.forEach(clazz -> {
            if (!(clazz.isAnnotationPresent(ApiModel.class) && clazz.isAnnotationPresent(TableName.class)))
                noAnClassList.add(clazz);
            Field[] fields = clazz.getDeclaredFields();
            List<Field> fieldList = Arrays.asList(fields).stream().filter(field -> !(field.isAnnotationPresent(ApiModelProperty.class)
                    || field.isAnnotationPresent(TableId.class)
                    || (field.isAnnotationPresent(TableField.class)
                    && !field.getAnnotation(TableField.class).exist()))
                    && !field.getName().equals("serialVersionUID")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fieldList))
                noFieldAnClassList.add(clazz);

        });
        if (CollUtil.isNotEmpty(noAnClassList)) {
            StringBuilder sb = new StringBuilder();
            noAnClassList.forEach(clazz -> sb.append(clazz.getSimpleName()).append(","));
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("未添加apiModel或tableName注解");
            throw new CrmebException(sb.toString());
        }
        if (CollUtil.isNotEmpty(noFieldAnClassList)) {
            StringBuilder sb = new StringBuilder();
            noFieldAnClassList.forEach(clazz -> sb.append(clazz.getSimpleName()).append(","));
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("存在未添加ApiModelProperty注解字段");
            throw new CrmebException(sb.toString());
        }
    }

    public Set<Class<?>> scanEntities(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ApiModel.class));

        Set<Class<?>> entityClasses = scanner.findCandidateComponents(basePackage)
                .stream()
                .map(beanDefinition -> {
                    try {
                        return Class.forName(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        return entityClasses;
    }

    /**
     * 获取颜色配置
     *
     * @return SystemConfig
     */
    @Override
    public SystemConfig getColorConfig() {
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemConfig::getName, "change_color_config");
        lqw.eq(SystemConfig::getStatus, 0);
        return dao.selectOne(lqw);
    }


    private void asyncRedis(String name) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        List<SystemConfig> systemConfigList = dao.selectList(lambdaQueryWrapper);
        if (systemConfigList.size() == 0) {
            //说明数据已经被删除了
            deleteRedis(name);
            return;
        }

        async(systemConfigList);
    }

    /**
     * 把数据同步到redis
     *
     * @param systemConfigList List<SystemConfig> 需要同步的数据
     */
    private void async(List<SystemConfig> systemConfigList) {
        for (SystemConfig systemConfig : systemConfigList) {
            redisUtil.set(redisKey + systemConfig.getFormId() + ":" + systemConfig.getName(), systemConfig);
        }
    }

    private void deleteRedis(String name) {
        redisUtil.hmDelete(redisKey, "*:" + name);
    }

    /**
     * 把数据同步到redis
     *
     * @param name String
     * @return String
     */
    private SystemConfig get(String name) {
        Collection<String> keys = redisUtil.keys(redisKey + "*:*:" + name);
        if (CollUtil.isEmpty(keys)) {
            //没有找到数据
            //去数据库查找，然后写入redis
            SystemConfig systemConfig = dao.selectOne(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getName, name));
            if (Objects.nonNull(systemConfig)) {
                setRedis(systemConfig);
                return systemConfig;
            }
            return new SystemConfig();
        }
        String[] keyArr = new String[keys.size()];
        return (SystemConfig) redisUtil.get(keys.toArray(keyArr)[0]);
    }

    /**
     * 把数据同步到redis, 此方法适用于redis为空的时候进行一次批量输入
     */
    private void setRedisByVoList() {
        //检测redis是否为空
        Long size = redisUtil.getHashSize(redisKey);
        if (size > 0) {
            return;
        }

        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getStatus, false);
        List<SystemConfig> systemConfigList = dao.selectList(lambdaQueryWrapper);
        async(systemConfigList);
    }

    private void setRedis(SystemConfig systemConfig) {
        async(Lists.newArrayList(systemConfig));
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return redisKey + configKey;
    }
}

