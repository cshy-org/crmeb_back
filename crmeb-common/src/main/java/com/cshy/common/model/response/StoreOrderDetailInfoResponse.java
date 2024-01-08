package com.cshy.common.model.response;

import com.cshy.common.model.entity.system.SystemStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单详情响应体
 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderDetailInfoResponse对象", description="订单详情响应体")
public class StoreOrderDetailInfoResponse implements Serializable {

    private static final long serialVersionUID = -4324222121352855551L;

    @ApiModelProperty(value = "订单ID")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户电话")
    private String userMobile;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "详细地址")
    private String addressDetail;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightPrice;

//    @ApiModelProperty(value = "订单商品总数")
//    private Integer totalNum;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;

//    @ApiModelProperty(value = "邮费")
//    private BigDecimal totalPostage;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionPrice;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
    private Integer status;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款图片")
    private String refundReasonWapImg;

    @ApiModelProperty(value = "退款用户说明")
    private String refundReasonWapExplain;

    @ApiModelProperty(value = "退款时间")
    private Date refundReasonTime;

    @ApiModelProperty(value = "前台退款原因")
    private String refundReasonWap;

    @ApiModelProperty(value = "不退款的理由")
    private String refundReason;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    @ApiModelProperty(value = "发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递单号/手机号")
    private String trackingNo;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "备注")
    private String mark;


    private Integer isMerCheck;

    @ApiModelProperty(value = "拼团商品id0一般商品")
    private Integer combinationId;

    @ApiModelProperty(value = "拼团id 0没有拼团")
    private Integer pinkId;

    @ApiModelProperty(value = "秒杀商品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "门店id")
    private Integer storeId;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private Integer shippingType;


    @ApiModelProperty(value = "支付渠道(0微信公众号1微信小程序)")
    private int isChannel;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单")
    private Integer type;


    @ApiModelProperty(value = "支付方式：前端")
    private String payTypeStr;

    @ApiModelProperty(value = "订单状态描述：前端")
    private String orderStatusMsg;

    @ApiModelProperty(value = "订单发货时间")
    private String orderDeliveryTime;

    @ApiModelProperty(value = "系统门店信息")
    private SystemStore systemStore;
    @ApiModelProperty(value = "腾讯地图key")
    private String mapKey;
    @ApiModelProperty(value = "订单状态图标")
    private String statusPic;

    @ApiModelProperty(value = "退款方式 0 退款 1 退货退款")
    @NotNull(message = "退款方式不能为空")
    private Integer refundType;

    @ApiModelProperty(value = "退款收货状态 0 未收货 1 已收货")
    @NotNull(message = "退款收货状态")
    private Integer refundReceivingStatus;
    @ApiModelProperty(value = "订单详情")
    private List<OrderInfoResponse> orderInfoList;

    @ApiModelProperty(value = "退货收件人")
    private String returnName;

    @ApiModelProperty(value = "退货收件地址")
    private String returnAddress;

    @ApiModelProperty(value = "退货收件电话")
    private String returnMobile;

    @ApiModelProperty(value = "退货图片")
    private String returnImg;

    @ApiModelProperty(value = "退货备注")
    private String returnRemark;
}
