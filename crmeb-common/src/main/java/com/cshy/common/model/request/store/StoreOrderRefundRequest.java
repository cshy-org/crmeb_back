package com.cshy.common.model.request.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 订单退款表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderRefundRequest对象", description="订单退款")
public class StoreOrderRefundRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "退款金额")
    @DecimalMin(value = "0.00", message = "退款金额不能少于0.00")
    private BigDecimal amount;

    private Integer orderId;

    @ApiModelProperty(value = "退货收件人")
    private String returnName;

    @ApiModelProperty(value = "退货收件地址")
    private String returnAddress;

    @ApiModelProperty(value = "退货收件电话")
    private String returnMobile;
}
