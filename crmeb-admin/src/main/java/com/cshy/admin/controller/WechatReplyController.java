package com.cshy.admin.controller;

import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.response.CommonResult;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.wechat.WechatReply;
import com.cshy.common.model.request.wechat.WechatReplyRequest;
import com.cshy.common.model.request.wechat.WechatReplySearchRequest;
import com.cshy.service.service.WechatReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 微信关键字回复表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/keywords/reply")
@Api(tags = "微信开放平台 -- 微信关键字回复")
public class WechatReplyController {

    @Autowired
    private WechatReplyService wechatReplyService;

    /**
     * 分页显示微信关键字回复表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatReply>>  getList(@Validated WechatReplySearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<WechatReply> wechatReplyCommonPage = CommonPage.restPage(wechatReplyService.getList(request, pageParamRequest));
        return CommonResult.success(wechatReplyCommonPage);
    }

    /**
     * 新增微信关键字回复表
     * @param wechatReplyRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatReplyRequest wechatReplyRequest) {
        WechatReply wechatReply = new WechatReply();
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);
        if (wechatReplyService.create(wechatReply)) {
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除微信关键字回复表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (wechatReplyService.removeById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改微信关键字回复表
     * @param wechatReplyRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated WechatReplyRequest wechatReplyRequest) {
        if (wechatReplyService.updateReply(wechatReplyRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改状态
     * @param id integer id
     * @param status boolean 状态
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:status')")
    @ApiOperation(value = "状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam(value = "id") Integer id, @RequestParam(value = "status") Boolean status) {
        if (wechatReplyService.updateStatus(id, status)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询微信关键字回复表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatReply> info(@RequestParam(value = "id") Integer id) {
        WechatReply wechatReply = wechatReplyService.getInfo(id);
        return CommonResult.success(wechatReply);
   }

    /**
     * 根据关键字查询数据
     * @param keywords String 关键字
     */
    @PreAuthorize("hasAuthority('admin:wechat:keywords:reply:info:keywords')")
    @ApiOperation(value = "根据关键字查询数据")
    @RequestMapping(value = "/info/keywords", method = RequestMethod.GET)
    public CommonResult<WechatReply> info(@RequestParam(value = "keywords") String keywords) {
        WechatReply wechatReply = wechatReplyService.getVoByKeywords(keywords);
        return CommonResult.success(wechatReply);
    }
}



