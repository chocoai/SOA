package bps.common;

import java.util.HashMap;

public class Constant {
	/**有效*/
	public final static long STATE_VAILD	= 1;
	/**无效*/
	public final static long STATE_INVAILD	= 0;
	
	/**账户有效期类型 永久*/
	public final static long VALIDITYTYPE_FOREVER		= 1;
	/**账户有效期类型 延长*/
	public final static long VALIDITYTYPE_EXTEND		= 2;
	/**账户有效期类型 固定 */
	public final static long VALIDITYTYPE_FIXED			= 3;
	
	/**正常状态*/
	public final static long ACCOUNT_STATE_VAILD		= 1;
	/**冻结状态*/
	public final static long ACCOUNT_STATE_FREEZE		= 2;
	

	
	public final static long VALID_MERGE_DAY	= 1;	//固定有效期合并类型按日合并
	public final static long VALID_MERGE_MONTH	= 2;	//固定有效期合并类型按月合并
	public final static long VALID_MERGE_NEVER	= 0;	//固定有效期合并类型不合并

	/**会员类型：个人*/
	public static final Long MEMBER_TYPE_PERSON			= 3L;
	/**会员类型：企业*/
	public static final Long MEMBER_TYPE_ENTERPRISE  	= 2L;
	/**会员类型：系统*/
	public static final long MEMBER_TYPE_SYSTEM = 1;   //系统会员

	/** 密码验证 成功 */
	public static final Long CHECK_PWD_OK			= 0L;
	/** 密码验证 不存在 */
	public static final Long CHECK_PWD_UNDEFINED	= 1L;
	/** 密码验证 错误 */	
	public static final Long CHECK_PWD_ERROR		= 2L;
	/** 密码验证 锁定 */
	public static final Long CHECK_PWD_LOCKED		= 3L;
	
	/**登录失败次数*/
	public static final String LOGIN_PASSWORD_FAIL_AMOUNT 	= "login.password.fail.amount";
	/**支付失败次数*/
	public static final String PAY_PASSWORD_FAIL_AMOUNT		= "pay.password.fail.amount";
	/**登录密码锁定时间*/
	public static final String LOGIN_PASSWORD_LOCKED_TIME	= "login.password.locked.time";
	/**支付密码锁定时间*/
	public static final String PAY_PASSWORD_LOCKED_TIME		= "pay.password.locked.time";
	
	//手机支付密码失败次数
    public static final String PHONE_PAY_PASSWORD_FAIL_AMOUNT = "phone.pay.password.fail.amount";
    //手机支付密码锁定时间
    public static final String PHONE_PAY_PASSWORD_LOCKED_TIME = "phone.pay.password.locked.time";
	
	/**邮箱替换参数*/
	public static final String MAIL_REPLACEMENT	=  "param";
	

	
	/**订单状态:未付款*/
	public static final Long ORDER_STATE_WAIT_PAY		= 1L;
	/**订单状态:支付完成*/
	public static final Long ORDER_STATE_PAY	= 2L;
	/**订单状态:关闭*/
	public static final Long ORDER_STATE_CLOSE	= 3L;
	/**订单状态:交易成功*/
	public static final Long ORDER_STATE_SUCCESS		= 4L;
	/**订单状态:进行中*/
	public static final Long ORDER_STATE_PENDING		= 99L;
	
	/**交易类型：充值*/
	public static final Long TRADE_TYPE_DEPOSIT			= 1L;
	/**交易类型：转账*/
	public static final Long TRADE_TYPE_TRANSFER		= 2L;
	/**交易类型：取现*/
	public static final Long TRADE_TYPE_WITHDRAW		= 3L;
	/**交易类型：退款*/
	public static final Long TRADE_TYPE_REFUNDMENT		= 4L;

	/**订单类型：购物*/
	public static final Long SUB_TRADE_TYPE_SHOPPING		= 1L;
	/**交易子类型:代收 */
	public static final Long SUB_TRADE_TYPE_DAISHOU		= 2L;
	/**交易子类型:代付 */
	public static final Long SUB_TRADE_TYPE_DAIFU			= 3L;
	/**交易子类型:批量代付 */
	public static final Long SUB_TRADE_TYPE_BATCH_DAIFU			= 4L;
	/**交易子类型:代付到银行卡*/
	public static final Long SUB_TRADE_TYPE_2BANK			= 5L;
	/**交易子类型：平台转账 */
	public static final Long SUB_TRADE_TYPE_APPLICATION     = 6L;
	/**交易子类型:跨应用转账*/
	public static final Long SUB_TRADE_TYPE_CROSS_APP			= 7L;
	/**交易子类型:营销活动*/
	public static final Long SUB_TRADE_TYPE_MARKETING_ACTIVITY	= 8L;
	/**交易子类型:分账*/
	public static final Long SUB_TRADE_TYPE_SPLIT			= 9L;
	/**交易子类型:流标退款*/
	public static final Long SUB_TRADE_TYPE_BATCH_REFUND		= 11L;
	/**交易子类型:手续费*/
	public static final Long SUB_TRADE_TYPE_FEE			= 30L;
	/**交易子类型:应用手续费*/
	public static final Long SUB_TRADE_TYPE_ORG_FEE			= 31L;
	/**交易子类型：手续费退款*/
	public static final Long SUB_TRADE_TYPE_FEE_REFUND  = 32L;
	/**交易子类型:强实名认证*/
	public static final Long SUB_TRADE_TYPE_HIGHER_CARD     =33L;
	/**交易子类型:无验证充值*/
	public static final Long SUB_TRADE_TYPE_DEPOSIT_WITHOUT_CONFIRM     =34L;
	/**交易子类型:无验证提现*/
	public static final Long SUB_TRADE_TYPE_WITHDRAW_WITHOUT_CONFIRM     =35L;
	/**交易子类型:无验证消费*/
	public static final Long SUB_TRADE_TYPE_SHOPPING_WITHOUT_CONFIRM     =36L;
	/**交易子类型:平台转帐*/
	public static final Long SUB_TRADE_TYPE_APPLICATION_TRANSFER     	=37L;

	
	/** 订单类型:充值*/
	public static final Long ORDER_TYPE_DEPOSIT 			= 1L;
	/** 订单类型:消费*/
	public static final Long ORDER_TYPE_SHOPPING 			= 2L;
	/** 订单类型:体现*/
	public static final Long ORDER_TYPE_WITHDRAW 	= 3L;
	/** 订单类型:代收*/
	public static final Long ORDER_TYPE_DAISHOU		= 4L;
	/** 订单类型:代付*/
	public static final Long ORDER_TYPE_DAIFU 			= 5L;
	/** 订单类型:批量代付*/
	public static final Long ORDER_TYPE_BATCH_DAIFU 			= 6L;
	/** 订单类型：退款*******/
	public static final Long ORDER_TYPE_REFUNDMENT = 10L;
	/** 订单类型：跨应用转账*******/
	public static final Long ORDER_TYPE_CROSS_APP = 7L;
	/** 订单类型：流标退款*******/
	public static final Long ORDER_TYPE_BATCH_REFUNDMENT = 11L;

	/** 订单类型：平台转帐*******/
	public static final Long ORDER_TYPE_APPLICATION_TRANSFER = 12L;

	/** 订单过期时间，单位：小时 **/
	public static final Long ORDER_OVERDUE_HOUR = 24L;
    
    
    /** 通联手续费账户 */
//    public static final String SYSTEM_USERID_FEE                    = "ALLINPAY_HANDLING_IN_I9D5Z1M7IYF5A3G2";
    /** 人工提现账户 */
//    public static final String SYSTEM_USERID_ARTIFICIAL             = "ARTIFICIAL_WITHDRAWAL_sdkfashfiuwehuifhbxzbvyh";
	
	/** 手续费账户 */
//	public static final String FEE_USERID							= "FEE_LSKM92J7G5F7N0C3W1Y9I6D5";
	
	/** 手续费:银联B2C */
//	public static final String FEE_UNIONPAY_B2C			= "FEE_UNIONPAY_B2C";
//	/** 手续费:银联B2C快捷 */
//	public static final String FEE_UNIONPAY_B2C_QUICK	= "FEE_UNIONPAY_B2C_QUICK";
//	/** 手续费:取现 */
//	public static final String FEE_WITHFRAW				= "FEE_WITHFRAW";
//	/** 手续费:通联B2C手续费 */
//	public static final String FEE_ALLINPAY_B2C			= "FEE_ALLINPAY_B2C";
//	/** 手续费: 交易服务费*/
//	public static final String FEE_TRADE				= "FEE_TRADE";
	

	//有效
	public static final Long USER_STATE_ACTIVATION 		= 1L;
	//已创建
//	public static final Long USER_STATE_CREATED 		= 2L;
	//审核失败 
	public static final Long USER_STATE_AUDITED_FAIL	= 3L;
//	//关闭
//	public static final Long USER_STATE_CLOSED			= 4L;
	//锁定
	public static final Long USER_STATE_LOCKED			= 5L;
//    //未激活
//    public static final Long USER_STATE_NOACTIVATED     = 6L;
    //待审核
    public static final Long USER_STATE_AUDITED_WAIT     = 7L;
	
	/** 会员银行卡状态:绑定*/
	public static final Long MEMBER_BANK_STATUS_SUCCESS = 1L;
	/** 会员银行卡状态:解绑*/
	public static final Long MEMBER_BANK_STATUS_ERROR = 2L;
	/** 会员银行卡加密key*/
	public static final String MEMBER_BANK_ENCODE_KEY = "allinpay";
	
	/** 通联机构号 */
	public static final String ORG_NO_ALLINPAY		= "99999";
	
	/** 通联总公司 */
	public static final String COMPANY_NAME			= "通联总公司";
	

	/** 邮箱注册验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_REGISTER_EMAIL	= 1L;
	/** 手机注册验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_REGISTER_PHONE	= 2L;
	/** 修改登录密码验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_CHANGE_LOGINPWD	= 3L;
	/** 修改支付密码验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_CHANGE_PAYPWD	= 4L;
	/** 找回支付密码验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_FIND_PAYPWD		= 5L;
	/** 修改手机验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_CHANGE_PHONE	= 6L;
	/** 找回登录密码验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_FIND_LOGINPWD	= 8L;
	/** 设置新手机验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_SET_NEW_PHONE	= 9L;
	/** 充值验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_RECHARGE		= 11L;
	/** 提现验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_WITHDRAW		= 13L;
	/** 支付验证码类型 */
	public static final Long VERIFICATION_CODE_TYPE_PAY				= 14L;
	/** 设置安全问题类型 */
	public static final Long VERIFICATION_CODE_TYPE_SI				= 15L;
	/** 发送绑定银行卡 */
	public static final Long VERIFICATION_CODE_TYPE_BIND_BANK_CARD	= 16L;
    /** 会员激活 */
    public static final Long VERIFICATION_CODE_TYPE_ACTIVATED       = 18L;
    /** 账户增加 */
    public static final Long VERIFICATION_CODE_TYPE_ACCOUNT_UP      = 21L;
    /** 账户减少*/
    public static final Long VERIFICATION_CODE_TYPE_ACCOUNT_DOWN    = 22L;
    /** 邮件模版编号:审核失败*/
    public static final Long EMAIL_CODE_TYPE_USER_AUDITED_WAIT      = 19L;
    /** 邮件模版编号:审核成功 */
    public static final Long EMAIL_CODE_TYPE_USER_AUDITED_SUCCESS   = 20L;
    /** 雇员验证验证码类型 */
    public static final Long VERIFICATION_CODE_TYPE_EMPLOYEE   = 23L;
    /** 修改雇员验证验证码类型 */
    public static final Long VERIFICATION_CODE_TYPE_UPDATE_EMPLOYEE   = 24L;
    
	
	
	/**命令分隔符*/
	public static final String COMMAND_SPLIT_SIGN	= "D";
	
	/**银行卡类型  储蓄卡*/
	public static final Long BANK_CARD_CX	= 1L;
	/**银行卡类型  信用卡*/
	public static final Long BANK_CARD_XY	= 2L;
	/**卡类型  通联卡 记名卡*/
	public static final Long CARD_TYPE_TLCARD_REGISTERED	= 11L;
	/**卡类型  通联卡 不记名卡*/
	public static final Long CARD_TYPE_TLCARD_UNREGISTERED	= 12L;

	/**卡业务类型  银行卡*/
	public static final Long BUSINESS_TYPE_BANK_CARD		= 1L;
	/**卡业务类型  通联卡*/
	public static final Long BUSINESS_TYPE_ALLINPAY_CARD	= 2L;
	
	public static final String OUT_ACCOUNT_SYSNO = "90001";
	/**外部会员账户  开通状态-开通*/
	public static final Long OUT_ACCOOUNT_OPEN	= 1L;
	/**外部会员账户  开通状态-开通－说明*/
	public static final String OUT_ACCOOUNT_OPEN_NOTE	= "已开通";
	
	/**外部会员账户  开通状态-已锁定*/
	public static final Long OUT_ACCOOUNT_LOCK	= 2L;
	/**外部会员账户  开通状态-已锁定－说明*/
	public static final String OUT_ACCOOUNT_OPEN_LOCK	= "已锁定";
	
	/**外部会员账户  开通状态-未开通*/
	public static final Long OUT_ACCOOUNT_UNOPEN	= 0L;
	/**外部会员账户  开通状态-未开通－说明*/
	public static final String OUT_ACCOOUNT_UNOPEN_NOTE	= "未开通";
	
	/**留言表   类型  用户反馈*/
	public static final Long MESSAGEBOX_TYPE	= 1L;
	
	/** 支付渠道:通联通 */
	public static final String PAY_CHANNEL_TLT			= "20001";
	/** 支付渠道:快捷支付 */
	public static final String PAY_CHANNEL_QUICK		= "20002";
	/** 认证支付渠道 **/
	public static final String PAY_CHANNEL_CERT_PAY     = "20003";
	/**通联网关支付*/
	public static final String PAY_CHANNEL_GETWAY		= "10002";
	/**通联卡支付*/
    public static final String PAY_CHANNEL_BUSINESS     = "10003";
    /**话费充值*/
    public static final String PAY_CHANNEL_PHONE        = "10004";

	/**POS支付*/
	public static final String PAY_CHANNEL_POS        = "30001";

	/**银联代扣*/
	public static final String PAY_CHANNEL_UNION_DAIKOU = "40001";

	
	public static final String PAY_CHANNEL_AMS			= "99999";
	
	/** 通联通单笔代付通道 */
	public static final String PAY_INTERFACE_TLT_DF		= "2000101";
	/** 通联通单笔代扣通道 */
	public static final String PAY_INTERFACE_TLT_DK		= "2000102";
	/** 通联通批量代付通道 */
	public static final String PAY_INTERFACE_TLT_BACH_DF	= "2000103";
	/** 通联通帐户验证通道 */
	public static final String PAY_INTERFACE_TLT_ACCOUNT	= "2000104";

	/** 通联网关支付通道 */
	public static final String PAY_INTERFACE_GETWAY_JJ	= "1000201";
	/** 通联快捷支付通道 */
	public static final String PAY_INTERFACE_QUICK		= "2000201";
	/** 通联快捷支付绑卡通道 */
	public static final String PAY_INTERFACE_QUICK_CARD	= "2000202";

	/** 认证支付通道 */
	public static final String PAY_INTERFACE_CERT_PAY   = "2000301";

	/** POS支付通道 */
	public static final String PAY_INTERFACE_POS   = "3000101";

	/** 银联代扣 */
	public static final String PAY_INTERFACE_UNION_DAIKOU = "4000101";




	
//	/** 通联卡网关支付通道 */
//    public static final String PAY_INTERFACE_BUSINESS_GETWAY = "1000301";
    /** 话费充值通道 */
    public static final String PAY_INTERFACE_PHONE      = "1000401";
    /** 通联内部账户支付通道 */
    public static final String PAY_INTERFACE_AMS        = "9999901";
	/** 通联内部账户支付通道 */
	public static final String PAY_INTERFACE_CARD        = "9999902";
	/**网关 储蓄卡**/
	public static final Long GATWAY_PAY_TYPE_CX = 1L;
	/**网关 信用卡 **/
	public static final Long GATWAY_PAY_TYPE_XY = 11L;
	/**网关 企业 **/
	public static final Long GATWAY_PAY_TYPE_QY = 4L;
	/**网关 认证 **/
	public static final Long GATWAY_PAY_TYPE_RZ = 28L;
	
	

	

	/*对账文件根目录*/
	public static final String FILE_ROOT_PATH = "CheckAccount.yzt.rootPath";
	
	
	/**通联支付接口*/
//	public static final String SYSTEM_USERID_ALLINPAY_INTERFACE	= "ALLINPAY_INTERFACE_7ZXS82MA3CEQLPA1";
	/**农行*/
//	public static final String SYSTEM_USERID_ABC	= "ABC_IT2M5XA7ZXS8MAQDAOP";
	
	
	/**对账结果:正常*/
	public static final long CHECK_RESULT_OK = 0L;
	/**对账结果:不平*/
	public static final long CHECK_RESULT_ERROR = 1L;
	
	/**对账文件下载情况：成功*/
	public static final Long CHECK_DOWNLOAD_SUCCESS = 1L;
	/**对账文件下载情况：失败*/
	public static final Long CHECK_DOWNLOAD_ERROR = 0L;
	
	/**对账情况:已进行*/
	public static final Long CHECK_STATUS_END = 1L;
	/**对账情况:未进行*/
	public static final Long CHECK_STATUS_NOT_START = 0L;
	
	/**联系人类型:转账到账户*/
	public static final long CONTACT_TYPE_2ACCOUNT = 1L;
	/**联系人类型:转账到银行卡*/
	public static final long CONTACT_TYPE_2BANK = 2L;
    /**联系人类型:积分转赠*/
    public static final long CONTACT_TYPE_INTEGRAL = 3L;
    
    /**余额类型:余额*/
    public static final Long BALANCE_TYPE_BALANCE = 1L;
    /**余额类型:轧差*/
    public static final Long BALANCE_TYPE_NETTING = 2L;
    
    /** 手续费收费模式:收支 */
    public static final Long FEE_CHARGE_TYPE_BALANDPAY    = 1L;
    /** 手续费收费模式:轧差 */
    public static final Long FEE_CHARGE_TYPE_NETTING      = 2L;

    /** 手续费收费方式:百分比 */
    public static final Long FEE_TYPE_PERCENTAGE    = 1L;
    /** 手续费收费方式:每笔 */
    public static final Long FEE_TYPE_EACH          = 2L;
    
    
    //增值服务充值状态：等待充值
    public static final Long RECHARGE_TYPE_WAIT = 0L;
    //增值服务充值状态：充值成功
    public static final Long RECHARGE_TYPE_SUCCESS = 1L;
    //增值服务充值状态：充值失败
    public static final Long RECHARGE_TYPE_FAILURE = 2L;
    //增值服务充值状态：充值异常
    public static final Long RECHARGE_TYPE_ABNORMAL = 3L;  
    
	public static final String EVENT_TYPE_BONUS = "BONUS";
	
    /** 账户属性:标准 */
    public static final Long ACCOUNT_PROP_DEFAULT    = 1L;
    /** 账户属性:扩展 */
    public static final Long ACCOUNT_PROP_EXTEND     = 2L;
    
    /** 支付方式类型:快捷 */
    public static final Long PAY_MODE_QUICK         = 1L;
    /** 支付方式类型:网关 */
    public static final Long PAY_MODE_GETWAY        = 2L;
    
    /** 收费方式:托管*/
    public static final Long TRANSACTION_TYPE_GUARANTEE     = 1L;
    /** 收费方式:即时 */
    public static final Long TRANSACTION_TYPE_IMMEDIATE     = 2L;
    
    //机构状态：正常
    public static final Long MECHANISM_STATE_NORMAL = 1L;
    //机构状态：暂停
    public static final Long MECHANISM_STATE_PAUSE = 2L;
    //机构状态：关闭
    public static final Long MECHANISM_STATE_CLOSE = 3L;
    
    //调度状态：未发送
    public static final Long DISPATCH_STATE_NOT_SEND = 1L;
    //调度状态：发送失败
    public static final Long DISPATCH_STATE_SEND_SUCCESS = 2L;
    //调度状态：发送成功
    public static final Long DISPATCH_STATE_SEND_FAILURE = 3L;
    
    /** 接入方式:支付*/
    public static final Long ACCESS_METHOD_PAY           = 1L;
    /** 接入方式:托管 */
    //public static final Long ACCESS_METHOD_              = 2L;
    
    /** 银行卡账户属性:个人 */
    public static final Long BANK_ACCOUNT_PROP_P            = 0L;
    /** 银行卡账户属性:企业*/
    public static final Long BANK_ACCOUNT_PROP_C            = 1L;
    
    /** 安全等级:1级*/
    public static final Long SAFETY_LEVEL_1              = 1L;
    /** 安全等级:2级*/
    public static final Long SAFETY_LEVEL_2              = 2L;
    /** 安全等级:3级*/
    public static final Long SAFETY_LEVEL_3              = 3L;
    /** 安全等级:4级*/
    public static final Long SAFETY_LEVEL_4              = 4L;
    
    /** 认证强度:v0*/
    public static final Long AUTHENTICATION_LEVEL_V0     = 0L;
    /** 认证强度:v1*/
    public static final Long AUTHENTICATION_LEVEL_V1     = 1L;
    /** 认证强度:v2*/
    public static final Long AUTHENTICATION_LEVEL_V2     = 2L;
    /** 认证强度:v3*/
    public static final Long AUTHENTICATION_LEVEL_V3     = 3L;
    
    /** 补充认证:未认证*/
    public static final Long AUTHENTICATION_SP_NOT      = 0L;
    /** 补充认证:行业认证*/
    public static final Long AUTHENTICATION_SP_INDUSTRY = 1L;
    /** 补充认证:电话回拨*/
    public static final Long AUTHENTICATION_SP_PHONE    = 2L;
    /** 补充认证:雇员认证*/
    public static final Long AUTHENTICATION_SP_COMPANY  = 3L;
    
    /** 认证类型:认证强度V0*/
    public static final Long AUTHENTICATION_TYPE_V0  = 0L;
    /** 认证类型:认证强度V1*/
    public static final Long AUTHENTICATION_TYPE_V1  = 1L;
    /** 认证类型:认证强度V2*/
    public static final Long AUTHENTICATION_TYPE_V2  = 2L;
    /** 认证类型:认证强度V3*/
    public static final Long AUTHENTICATION_TYPE_V3  = 3L;
    /** 认证类型:电话回访*/
    public static final Long AUTHENTICATION_TYPE_PHONE  = 4L;
    /** 认证类型:行业认证*/
    public static final Long AUTHENTICATION_TYPE_INDUSTRY  = 5L;
    /** 认证类型:雇员认证*/
    public static final Long AUTHENTICATION_TYPE_COMPANY  = 6L;
    /** 认证类型:安全等级*/
    public static final Long AUTHENTICATION_TYPE_SAFTY  = 7L;
    
    /** 处理结果:成功*/
    public static final Long DEAL_SUSSESS  = 0L;
    /** 处理结果:失败*/
    public static final Long DEAL_ERROR  = 1L;
    /** 处理结果:等待*/
    public static final Long DEAL_WAIT  = 2L;
    
    /** 交易监控:交易阻断*/
    public static final Long MONITORTYPE_TRANSACTION  = 0L;
    /** 交易监控:会员锁定-触发风控*/
    public static final Long MONITORTYPE_LOCKMEMBER_ONE  = 1L;
    /** 交易监控:会员锁定-进行锁定*/
    public static final Long MONITORTYPE_LOCKMEMBER  = 2L;
    
    
    /** 风险关注规则:当日转账业务超过3笔，且转账金额合计超过1000元*/
    public static final Long RISKRLUE_1  = 1L;
    /** 风险关注规则:当日话费充值业务超过3笔，且转账金额合计超过500元*/
    public static final Long RISKRLUE_2  = 2L;
    /** 风险关注规则:当日消费次数超过3笔，且转账金额合计超过1000元*/
    public static final Long RISKRLUE_3  = 3L;
    /** 风险关注规则:当日充值次数超过3笔，且转账金额合计超过1000元*/
    public static final Long RISKRLUE_4  = 4L;
    /** 风险关注规则:当日会员交易IP地址达到3个，且转账金额合计超过1000元*/
    public static final Long RISKRLUE_5  = 5L;
    
    /** 风险关注状态:未处理*/
    public static final Long RISK_UNTREATED  = 0L;
    /** 风险关注状态:处理中*/
    public static final Long RISK_ING  = 1L;
    /** 风险关注状态:已处理*/
    public static final Long RISK_PROCESSED = 2L;

	/** 退款状态:未退款 */
	public static final Long REFUND_STATE_NODO		= 1L;
	/** 退款状态:退款中 */
	public static final Long REFUND_STATE_DOING		= 2L;
	/** 退款状态:成功 */
	public static final Long REFUND_STATE_SUCCESS	= 3L;
    /** 退款状态:失败*/
    public static final Long REFUND_STATE_FAIL   	= 9L;

    //借记卡
    public static final Long CARD_BIN_CARD_TYPE_JJ = 1L;
    //贷记卡
    public static final Long CARD_BIN_CARD_TYPE_DJ = 2L;
    //准贷记卡
    public static final Long CARD_BIN_CARD_TYPE_ZDJ = 3L;
    
    //二维码状态：未使用
    public static final Long QR_CODE_STATE_UNUSED = 1L;
    //二维码状态：已使用
    public static final Long QR_CODE_STATE_USED = 2L;
    //二维码状态：过期
    public static final Long QR_CODE_STATE_EXPIRE = 3L;
    
    //二维码支付方式：钱包余额账户
    public static final Long QR_CODE_PAY_TYPE_BALANCE = 1L;
    //二维码支付方式：积分账户
    public static final Long QR_CODE_PAY_TYPE_INTEGRAL = 2L;
    //二维码支付方式：快捷银行卡
    public static final Long QR_CODE_PAY_TYPE_BANK_CARD = 3L;

    //用户免密额度 100元以下
    public static final Long MEMBER_FREE_PASS_LIMIT_ONE = 100L;
    //用户免密额度 200元以下
    public static final Long MEMBER_FREE_PASS_LIMIT_TWO = 200L;
    //-------------------APP接口结束---------------------------

    //受理企业会员userId
//    public static final String USERID_ACCEPTANCE_INTERFACE = "USERID_ACCEPTANCE_DIOI9EJH9F39ew87rt834929J";
    
    //银行支付方式：借记卡快捷支付
    public static final Long DEBIT_CARD_QUICK_PAY = 1L;
    //银行支付方式：贷记卡快捷支付
    public static final Long CREDIT_CARD_QUICK_PAY = 2L;
    //银行支付方式：借记卡网关支付
    public static final Long DEBIT_CARD_GATEWAY_PAY = 3L;
    //银行支付方式：贷记卡网关支付
    public static final Long CREDIT_CARD_GATEWAY_PAY = 4L;
    
    //public static final Long 
    
    //订单来源-手机
    public static final Long SOURCE_PHONE     = 1L;
    //订单来源-PC
    public static final Long SOURCE_PC = 2L;
    //订单来源-POS
    public static final Long SOURCE_POS    = 3L;
    
    //its签约类型
    public static final Long SIGN_TYPE_REFUND = 7L;
    
    //货币类型
    public static final Long CURRENCY_TYPE_RBM = 156L;
    
    /** 调用类型:页面 */
    public static final Long CALL_TYPE_PAGE             = 1L;
    /** 调用类型:接口*/
    public static final Long CALL_TYPE_INTERFACE        = 2L;
    /** 调用类型:其他*/
    public static final Long CALL_TYPE_OTHER            = 3L;
    
    /** 平台类型:PC */
    public static final Long PLATFORM_TYPE_PC           = 1L;
    /** 平台类型:IOS*/
    public static final Long PLATFORM_TYPE_IOS        	= 2L;
    /** 平台类型:Android*/
    public static final Long PLATFORM_TYPE_ANDROID      = 3L;
    
    /****账户集类型 现金******/
    public static final Long BIZ_TYPE_CASH =   1L;
    
    /****账户集类型 积分******/
    public static final Long BIZ_TYPE_INTEGRAL =   2L;
    
    /****账户集类型 货基******/
    public static final Long BIZ_TYPE_CARGO =   3L;
    /****人民币比率**********************/
    public static final Long RMB_RATE_DEFAULT = 1L;
    /****单位 **********************/
    public static final String ACCOUNT_UNIT = "元";
    /****托管类账户集 **********************/
    public static final Long ACCOUNT_KIND_TG = 6L;
    //应用配置类型 b2c模式
    public static final Long APPLICTION_CONFIG_B2C = 1L;
    //应用配置类型 跨应用模式
    public static final Long APPLICATION_CONFIG_CROASSAPP = 2L;
    
    //企业审核状态，待审核
    public static final Long ENTERPRISE_CHECK_STATE_AUDITED_WAIT = 1L;
    //企业审核状态，审核成功
    public static final Long ENTERPRISE_CHECK_STATE_AUDITED_SUCCESS = 2L;
    //企业审核状态，审核失败
    public static final Long ENTERPRISE_CHECK_STATE_AUDITED_FAIL = 3L;
    
    
    
    
    /************************************待定区域****************************************************/
    
//	/**现金账户 */
//	public static final String ACCOUNT_NO_CASH	= "1000";
//	/**积分帐户 */
//	public static final String ACCOUNT_NO_INTEGRAL ="2000";
	public static final String ACCOUNT_NO_STANDARD_BALANCE = "100001";//标准余额账户集   待定
	public static final String ACCOUNT_NO_STANDARD_BOND = "100002";//标准保证金  待定
	public static final String ACCOUNT_NO_STANDARD_READY = "100003";//准备金
	public static final String ACCOUNT_NO_STANDARD_REPLACE_COLLECT = "100004"; //中间代收
	public static final String ACCOUNT_NO_STANDARD_REPLACE_PAY = "100005";    //中间代付
	public static final String ACCOUNT_NO_COUPON        = "2000000";  //营销专用账户    
	
	/*****云账户系统下的默认应用*****/
    public static final Long APPLICATION_ID_BPS_YUN = 1L;//待定
    public static final String APPLICATION_LABEL_BPS_YUN = "云账户应用";//待定
    
    /** 认证签约短信验证 */
	public static final Long VERIFICATION_CODE_AUTH_SIGN = 30L;
	/** 认证支付短信验证 */
	public static final Long VERIFICATION_CODE_AUTH_PAY	= 31L;
	
	/** 商品类型 **/
	public static final Long GOODS_TYPE_SUBJECT = 1L; //标的
	
	/** 还款方式 **/
	public static final Long REPAY_TYPE_DQHB  = 1L; //到期还本还息
	public static final Long REPAY_TYPE_MYFX  = 2L; //每月还息，到期还本
	public static final Long REPAY_TYPE_DEBJ  = 3L; //等额本金
	public static final Long REPAY_TYPE_DEBX  = 4L; //等额本息
	public static final Long REPAY_TYPE_OTHER = 99L; //其他
	
	/** 担保方式 **/
	public static final Long GUARANTEE_TYPE_APPLICATION = 1L;    //平台担保
	public static final Long GUARANTEE_TYPE_INSURANCE   = 2L;    //保险担保
	public static final Long GUARANTEE_TYPE_BANK        = 3L;    //银行担保
	public static final Long GUARANTEE_TYPE_COMPANY     = 4L;    //企业担保
	public static final Long GUARANTEE_TYPE_NONE        = 5L;    //无担保
	public static final Long GUARANTEE_TYPE_OTHER       = 99L;   //其他
	
	/** redis的key **/
	public static final String REDIS_KEY_PI_APP_CONF = "payInterfaceAppConfig";  //通道商户配置信息
	public static final String REDIS_KEY_APP_SOA_API_CONF = "appSoaApiConfig";   //soa api
	public static final String REDIS_KEY_ORGLIST = "orgList";					 //orgList
	
	/** 手续费类型 **/
	public static Long FEE_TYPE_TRADE          = 1L;  //交易手续费
	public static Long FEE_TYPE_SET_REAL_NAME  = 2L;  //实名认证手续费
	public static Long FEE_TYPE_BIND_BANK_CARD = 3L;  //绑定银行卡手续费
	
	/** code type **/
	public static Long CODE_TYPE_SET_REAL_NAME        = 3L; //实名认证订单号编号类型
	public static Long CODE_TYPE_BIND_BANK_CARD       = 4L; //绑定银行卡订单号编号类型
	public static Long CODE_TYPE_APPLICATION_TRANSFER = 5L; //平台转账编号类型
	
	/** code的长度 **/
	public static int CODE_LENGTH_SET_REAL_NAME        = 13; //实名认证的订单编号长度
	public static int CODE_LENGTH_BIND_BAND_CARD       = 13; //绑定银行卡的订单编号长度
	public static int CODE_LENGTH_APPLICATION_TRANSFER = 13; //平台转账编号长度
	
	/** code的前缀 **/
	public static String CODE_PREFIX_APPLICATION_TRANSFER = "PZ";
	
	/** 健康监测  **/
		/** 健康监测类型 **/
	public static int HEALTH_MONITOR_TYPE_COMMUNICATE = 1; //通信监测
	public static int HEALTH_MONITOR_TYPE_REDIS       = 2; //redis监测
	
		/** 健康监测结果 **/
	public static int HEALTH_MONITOR_RESULT_TYPE_SUCCESS = 1; //成功
	public static int HEALTH_MONITOR_RESULT_TYPE_ERROR   = 2; //失败
	
		/** 回传给监测中心的监测结果 **/
	public static String HEALTH_MONITOR_RESULT_RES_SUC    = "0000";  // 
	public static String HEALTH_MONITOR_RESULT_RES_ERR_COM = "0100";  //
	public static String HEALTH_MONITOR_RESULT_RES_ERR_REDIS = "0200";
	public static String HEALTH_MONITOR_RESULT_RES_ERR_DB = "0300";
	
	/** 网关支付方式  **/
	public static Long GATEWAY_PAY_METHOD_JJ   = 1L;   //借记卡
	public static Long GATEWAY_PAY_METHOD_QYWY = 4L;   //企业网银
	public static Long GATEWAY_PAY_METHOD_XY   = 11L;  //信用卡
	public static Long GATEWAY_PAY_METHOD_RZZF = 28L;  //认证支付
	
	/** 特殊的bizUserId **/
	public static String B2C_BIZ_USER_ID = "#yunBizUserId_B2C#";  //B2C的bizUserId
	public static String APPLICATION_BIZ_USER_ID = "#yunBizUserId_application#"; //应用的bizUserId
	
	/** 云账户应用id **/
	public static Long YUN_APPLICATION_ID = 1L;

	/**批量代付失败查询次数**/
	public static String BATCH_DAIFU_QUERY_FAIL_AMOUNT = "batchDaifuQueryFailAmount";

	/**通知商户失败次数列表**/
	public static String BACKGROUND_NOTICE_FAIL_AMOUNT = "backgroundNoticeFailAmount";

	/**通知商户通知频率时间列表**/
	public static HashMap<String, Long> BACKGROUND_NOTICE_TIME_LIST = new HashMap<String, Long>() {
		{
			//5s,2m,10m,15m,1h,2h,6h,15h
			put("1", 5L*1000L);
			put("2", 2*60L*1000L);
			put("3", 10*60L*1000L);
			put("4", 15*60L*1000L);
			put("5", 60*60L*1000L);
			put("6", 2*60*60L*1000L);
			put("7", 6*60*60L*1000L);
			put("8", 15*60*60L*1000L);
		}
	};
	/**代付准备金模式**/
	public static Long WITHDRAW_RESERVE_MODEL_ACTIVE = 1L;		//主动划款
	public static Long WITHDRAW_RESERVE_MODEL_ENTRUST = 2L;		//委托扣款

	
}
