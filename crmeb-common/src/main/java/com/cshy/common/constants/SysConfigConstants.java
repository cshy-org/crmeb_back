package com.cshy.common.constants;

/**
 *  系统设置常量类

 */
public class SysConfigConstants {
    /** 是否启用分销 */
    public static final String CONFIG_KEY_BROKERAGE_FUNC_STATUS = "brokerage_func_status";
    /** 分销模式 :1-指定分销，2-人人分销 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_STATUS = "store_brokerage_status";
    /** 分销模式 :1-指定分销 */
    public static final String STORE_BROKERAGE_STATUS_APPOINT = "1";
    /** 分销模式 :2-人人分销 */
    public static final String STORE_BROKERAGE_STATUS_PEOPLE = "2";
    /** 一级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATIO = "store_brokerage_ratio";
    /** 二级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_TWO = "store_brokerage_two";
    /** 判断是否开启气泡 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE = "store_brokerage_is_bubble";
    /** 判断是否分销消费门槛 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_QUOTA = "store_brokerage_quota";

    /** 是否开启会员功能 */
    public static final String CONFIG_KEY_VIP_OPEN = "vip_open";
    /** 是否开启充值功能 */
    public static final String CONFIG_KEY_RECHARGE_SWITCH = "recharge_switch";
    /** 是否开启门店自提 */
    public static final String CONFIG_KEY_STORE_SELF_MENTION = "store_self_mention";
    /** 腾讯地图key */
    public static final String CONFIG_SITE_TENCENT_MAP_KEY = "tencent_map_key";
    /** 退款理由 */
    public static final String CONFIG_KEY_STORE_REASON = "store_reason";
    /** 提现最低金额 */
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price";
    /** 提现冻结时间 */
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time";

    /** 全场满额包邮开关 */
    public static final String STORE_FEE_POSTAGE_SWITCH = "store_free_postage_switch";
    /** 全场满额包邮金额 */
    public static final String STORE_FEE_POSTAGE = "store_free_postage";
    /** 积分抵用比例(1积分抵多少金额) */
    public static final String CONFIG_KEY_INTEGRAL_RATE = "integral_ratio";
    /** 下单支付金额按比例赠送积分（实际支付1元赠送多少积分) */
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral";

    /** 微信支付开关 */
    public static final String CONFIG_PAY_WEIXIN_OPEN  = "pay_weixin_open";
//    /** 余额支付状态 */
//    public static final String CONFIG_YUE_PAY_STATUS  = "yue_pay_status";
//    /** 支付宝支付状态 */
//    public static final String CONFIG_ALI_PAY_STATUS = "ali_pay_status";
}
