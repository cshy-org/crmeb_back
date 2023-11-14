package com.cshy.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.system.SystemGroupData;
import com.cshy.common.model.request.system.SystemGroupDataRequest;
import com.cshy.common.model.request.system.SystemGroupDataSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemGroupDataService 接口

 */
public interface SystemGroupDataService extends IService<SystemGroupData> {

    /**
     * 分页组合数据详情
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增组合数据详情
     * @param systemGroupDataRequest SystemFormCheckRequest 新增参数
     */
    Boolean create(SystemGroupDataRequest systemGroupDataRequest);

    /**
     * 修改组合数据详情表
     * @param id integer id
     * @param request 修改参数
     */
    Boolean update(Integer id, SystemGroupDataRequest request);

    /**
     * 通过gid获取列表 推荐二开使用
     * @param gid Integer group id
     * @return List<T>
     */
    <T> List<T> getListByGid(Integer gid, Class<T> cls);

    List<HashMap<String, Object>> getListMapByGid(Integer gid);

    /**
     * 通过gid获取列表
     * @param groupDataId Integer group id
     * @return <T>
     */
    <T> T getNormalInfo(Integer groupDataId, Class<T> cls);

    /**
     * 获取个人中心菜单
     * @return HashMap<String, Object>
     */
    HashMap<String, Object> getMenuUser();

    /**
     * 获取列表通过gid
     * @param gid gid
     * @return 列表
     */
    List<SystemGroupData> findListByGid(Integer gid);

    /**
     * 删除通过gid
     * @param gid gid
     * @return Boolean
     */
    Boolean deleteByGid(Integer gid);
}
