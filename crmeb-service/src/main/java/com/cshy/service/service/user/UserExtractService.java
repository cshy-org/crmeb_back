package com.cshy.service.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.response.UserExtractRecordResponse;
import com.github.pagehelper.PageInfo;
import com.cshy.common.model.entity.finance.UserExtract;
import com.cshy.common.model.request.user.UserExtractRequest;
import com.cshy.common.model.request.user.UserExtractSearchRequest;
import com.cshy.common.model.response.BalanceResponse;
import com.cshy.common.model.response.UserExtractResponse;

import java.math.BigDecimal;
import java.util.List;

/**
* UserExtractService 接口

*/
public interface UserExtractService extends IService<UserExtract> {

    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 提现总金额
     */
    BalanceResponse getBalance(String dateLimit);

    BigDecimal getWithdrawn(String startTime,String endTime);

    UserExtractResponse getUserExtractByUserId(Integer userId);

    /**
     * 提现审核
     * @param id    提现申请id
     * @param status 审核状态 -1 未通过 0 审核中 1 已提现
     * @param backMessage   驳回原因
     * @return  审核结果
     */
    Boolean updateStatus(Integer id,Integer status,String backMessage);

    /**
     * 获取提现记录列表
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest);

    BigDecimal getExtractTotalMoney(Integer userId);

    /**
     * 提现申请
     * @return Boolean
     */
    Boolean extractApply(UserExtractRequest request);

    /**
     * 修改提现申请
     * @param id 申请id
     * @param userExtractRequest 具体参数
     */
    Boolean updateExtract(Integer id, UserExtractRequest userExtractRequest);

    /**
     * 提现申请待审核数量
     * @return Integer
     */
    Integer getNotAuditNum();
}
