package bps.order;

import bps.util.CodeUtil;
import ime.core.Environment;
import ime.core.services.DynamicEntityService;
import ime.security.util.RSAUtil;

import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kinorsoft.ams.risk.RiskAction;
import com.kinorsoft.ams.risk.RiskManager;
import com.kinorsoft.ams.risk.RiskResult;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;

import com.kinorsoft.ams.services.QueryService;
import com.kinorsoft.ams.services.TradeService;
import com.kinorsoft.ams.trade.User;

import bps.account.AccountServiceImpl;
import bps.code.VerificationCode;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.common.Util;
import bps.common.BizException;
import bps.member.Member;
import bps.process.Extension;
import bps.service.AccountService;
import bps.service.OrderService;
import bps.common.RabbitProducerManager;


public class Order{
	private static Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
	
	/**
	 * 获取订单
	 * @param orderNo	订单号
	 * @param session
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getOrder(String orderNo, Session session)throws Exception{
		logger.info("Order getOrder start");
		logger.info("orderNo:" + orderNo);
		
		Query query = session.createQuery("from AMS_Order where orderNo=:orderNo");
		query.setParameter("orderNo", orderNo);
		List list = query.list();
		
		if(list.size() == 0)
			return null;
		else
			return (Map<String, Object>)list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getOrder(Long applicationId,String bizOrderNo,Session session)throws Exception{
		logger.info("Order getOrder start");
		logger.info("bizOrderNo:" + bizOrderNo);
		
		Query query = session.createQuery("from AMS_Order where bizOrderNo=:bizOrderNo and application_id=:application_id");
		query.setParameter("bizOrderNo", bizOrderNo);
		query.setParameter("application_id", applicationId);
		List<Map<String,Object>> list = query.list();
		 if(list.size()>1){
	        	throw new BizException(ErrorCode.ORDER_ERROR,"订单已经存在多个，请联系管理员");
	     }else if(list.size() == 1){
	        	Map<String,Object> entity_map = list.get(0);
	        	return entity_map;
	     }
		 return null;
	}

    /**
     * 查询标得订单
     * @param order_type        订单类型
     * @param biz_trade_code    交易码
     * @param goodsType         商品类型
     * @param goodsNo           商品号
     * @param orderState        订单状态
     * @param session           session
     * @return 标得订单列表
     */
	public static List<Map<String,Object>> getOrderListByGoods(Long order_type, String biz_trade_code, Long goodsType, String goodsNo, Long orderState, Session session){
        logger.info("Order getOrderListByGoods start");
        logger.info("order_type:" + order_type +"--biz_trade_code:"+biz_trade_code+"--goodsType:"+goodsType+"--goodsNo:"+goodsNo);

        String hql = "from AMS_Order where order_type=:order_type and biz_trade_code=:biz_trade_code and goodsType=:goodsType and goodsNo=:goodsNo";
        if( orderState != null ) {
            hql += " and order_state=:order_state";
        }
        Query query = session.createQuery(hql);
        query.setParameter("order_type", order_type);
        query.setParameter("biz_trade_code", biz_trade_code);

        query.setParameter("goodsType", goodsType);
        query.setParameter("goodsNo", goodsNo);
        if( orderState != null ){
            query.setParameter("order_state", orderState);
        }
        return (List<Map<String,Object>>)query.list();
    }
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> getAgentDetailList(Long orderId,Session session)throws Exception{
		
		logger.info("Order getAgentDetailList start");
		logger.info("orderId:" + orderId);
		
		Query query = session.createQuery("from AMS_AgentDetail where agentOrder_id=:agentOrder_id");
		query.setParameter("agentOrder_id", orderId);
		List<Map<String,Object>> list = query.list();
		return list;
		
		
	}

	/**
	 * 生成订单号
	 * @param order_id
	 * @return
	 * @throws Exception
	 */
	public static String generateOrderNo(Long order_id) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String str_date = sdf.format(new Date());
		
		String number  = "";
		String str_order_id = order_id.toString();
		for(int i=0; i<10-str_order_id.length(); i++){
			number += Util.getRandom();
		}
		String orderNo = str_date + number + str_order_id;
		return orderNo;
	}
	
	/**
	 * 关闭订单
	 * @param orderNo   订单号
     * @param param     其他参数
	 * @param session   session
	 * @throws Exception
	 */
	public static void closeOrder(String orderNo, Map<String, Object> param, Session session)throws Exception{
		logger.info("Order.closeOrder start");
		logger.info("orderNo="+orderNo);
		Map<String, Object>order_entity =  getOrder(orderNo, session);
		if(order_entity == null)
			throw new Exception("请传入订单实体");
		
		Long order_state = (Long)order_entity.get("order_state");
		if(!(order_state.equals(Constant.ORDER_STATE_WAIT_PAY)||order_state.equals(Constant.ORDER_STATE_PENDING)))
			throw new Exception("订单不允许关闭");
		
		if(param == null || param.isEmpty()) {
			Query query = session.createQuery("update AMS_Order set order_state=:order_state where orderNo=:orderNo");
			query.setParameter("order_state", Constant.ORDER_STATE_CLOSE);
			query.setParameter("orderNo", orderNo);
			query.executeUpdate();
		} else {
			Query query = session.createQuery("update AMS_Order set order_state=:order_state, extend_info=:extend_info where orderNo=:orderNo");
			query.setParameter("order_state", Constant.ORDER_STATE_CLOSE);
			query.setParameter("extend_info", param.get("extend_info"));
			query.setParameter("orderNo", orderNo);
			query.executeUpdate();
		}
	}
	
	public static void updateOrderFail(String orderNo, Session session){
    	logger.info("========================updateOrderFail start========================");
    	
    	Query query = session.createQuery("update AMS_Order set order_state=:order_state where orderNo=:orderNo");
		query.setParameter("order_state", Constant.ORDER_STATE_CLOSE);
		query.setParameter("orderNo", orderNo);
		query.executeUpdate();
		
		logger.info("========================updateOrderFail end========================");
    }
	
	public static void updateOrderPending(String orderNo, Session session){
    	logger.info("========================updateOrderFail start========================");
    	
    	Query query = session.createQuery("update AMS_Order set order_state=:order_state where orderNo=:orderNo");
		query.setParameter("order_state", Constant.ORDER_STATE_PENDING);
		query.setParameter("orderNo", orderNo);
		query.executeUpdate();
		
		logger.info("========================updateOrderFail end========================");
    }
	
	/**
     * 修改订单发起ip
     * @param orderNo
     * @param member_ip
     * @return
     * @throws Exception
     */
    public static void updateOrderIP(String orderNo, String member_ip, Session session)throws Exception{
        Query query = session.createQuery("update AMS_Order set member_ip=:member_ip where orderNo=:orderNo");
        query.setParameter("member_ip", member_ip);
        query.setParameter("orderNo", orderNo);
        query.executeUpdate();
    }
    

    

    /**
     * 获取已完成指令集
     * @param orderNo
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static List getSuccessCommands(String orderNo, Session session)throws Exception{
            
            Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo and pay_state=:pay_state");
            query.setParameter("orderNo", orderNo);
            query.setParameter("pay_state", com.kinorsoft.ams.Constant.COMMAND_STATE_SUCESS);
            
            return query.list();
    }
    
    @SuppressWarnings("rawtypes")
	public static List getCommands(String orderNo, Session session)throws Exception{
            
            Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo");
            query.setParameter("orderNo", orderNo);
            return query.list();
    }

    /**
     * 通过指令号查询指令
     * @param commandNo     指令号
     * @return  指令
     * @throws Exception
     */
	public static Map<String, Object> getCommand(final String commandNo)  throws Exception{

        Object command = EntityManagerUtil.execute(new QueryWork<Object>() {
            @Override
            public Object doQuery(Session session) throws Exception {
                Query query = session.createQuery("from AMS_OrderPayDetail where command_no=:commandNo");
                query.setParameter("commandNo", commandNo);
                return query.list().get(0);
            }
        });
        return (Map<String, Object>)command;
    }
	/**
	 * 差错处理
	 * @param errorTradeLog_entity
	 * @param session
	 * @throws Exception
	 */
//	public static void errorProcessing(Map<String, Object> errorTradeLog_entity, Session session)throws Exception{
//		ErrorProcessing errorProcessing = new ErrorProcessing(errorTradeLog_entity);
//		errorProcessing.run();
//	}

    /**
     * 冲账处理
     * @param money
     * @param session
     * @throws Exception
     */
//    public static void chargeReverseEntry(Long money, Date auditor_date, Session session)throws Exception{
//        StringBuffer sb = new StringBuffer();
//        //统计个人、企业账户余额
//        sb.setLength(0);
//        sb.append("select balance ");
//        sb.append("from AMS_AccountBalance ");
//        sb.append("where balance_type=:balance_type ");
//        sb.append("order by FM_CreateTime desc ");
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("balance_type", Constant.BALANCE_TYPE_NETTING);
//        query.setFirstResult(0);
//        query.setMaxResults(1);
//        List prev_net_list = query.list();
//        
//        Map<String, String> accountBalanceMap = new HashMap<String, String>();
//        Long now_time = auditor_date.getTime();
//        accountBalanceMap.put("balance_date",       now_time.toString());
//        Long prev_balance = 0L;
//        if(!prev_net_list.isEmpty()) {
//            prev_balance = (Long)prev_net_list.get(0);
//        }
//        accountBalanceMap.put("prev_balance",   prev_balance.toString());
//        accountBalanceMap.put("withdraw_aount",  money.toString());
//        accountBalanceMap.put("balance",         (prev_balance - money) + "");
//        accountBalanceMap.put("balance_type", Constant.BALANCE_TYPE_NETTING.toString());
    /**
     * 判断应用支付权限
     * @param params
     * @param session
     * @return
     * @throws Exception
     */
    public static void judgeAppicationPayPermission(Map<String, Object> params, Session session) throws Exception{
        logger.info("judgeAppicationPayPermission params:" + params);

        //获取参数
        String orgNo = (String)params.get("orgNo");
        String payInterfaceNo = (String) params.get("payInterfaceNo");
        Long bankCardType = (Long)params.get("bankCardType");
        Boolean account = (Boolean)params.get("account");
        Long payType = (Long) params.get("payType");
        Long payInterfaceType = 1L;

        //网关
        if(Constant.GATWAY_PAY_TYPE_CX.equals(payType)){
            bankCardType = Constant.CARD_BIN_CARD_TYPE_JJ;
        }else if(Constant.GATWAY_PAY_TYPE_XY.equals(payType)){
            bankCardType = Constant.CARD_BIN_CARD_TYPE_DJ;
        }

        try{
            //判断应用支付权限
            StringBuilder hqlStr = new StringBuilder();
            hqlStr.append("from AMS_OrgPayPermission where orgNo=:orgNo and pay_interface_type=:payInterfaceType");

            if(!Constant.PAY_INTERFACE_AMS.equals(payInterfaceNo)){
                hqlStr.append(" and pay_interfaceNo=:payInterfaceNo");
                payInterfaceType = 2L;
            }
            if(Constant.CARD_BIN_CARD_TYPE_JJ.equals(bankCardType)){
                hqlStr.append(" and debit=:debit");
            }
            if(Constant.CARD_BIN_CARD_TYPE_DJ.equals(bankCardType)){
                hqlStr.append(" and credit=:credit");
            }
            if(Boolean.TRUE.equals(account)){
                hqlStr.append(" and account=:account");
            }

            Query query = session.createQuery(hqlStr.toString());
            query.setParameter("orgNo", orgNo);
            query.setParameter("payInterfaceType", payInterfaceType);
            if(!Constant.PAY_INTERFACE_AMS.equals(payInterfaceNo)){
                query.setParameter("payInterfaceNo", payInterfaceNo);
            }
            if(Constant.CARD_BIN_CARD_TYPE_JJ.equals(bankCardType)){
                query.setParameter("debit", true);
            }
            if(Constant.CARD_BIN_CARD_TYPE_DJ.equals(bankCardType)){
                query.setParameter("credit", true);
            }
            if(Boolean.TRUE.equals(account)){
                query.setParameter("account", account);
            }

            List orgPayPermissionList = query.list();
            if(orgPayPermissionList == null || orgPayPermissionList.size() == 0){
                throw new BizException(ErrorCode.PAY_PERMISSION_NOT_ALLOW, "没有支付权限");
            }
        }catch(BizException e){
            logger.error(e.getMessage(), e);
            throw e;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new BizException(ErrorCode.ORDER_ERROR, e.getMessage());
        }
    }
//        DynamicEntityService.createEntity("AMS_AccountBalance", accountBalanceMap, null);
    /**
     * 查询通道路由
     * @param param
     * @param session
     * @throws Exception
     * @return List
     */
    @SuppressWarnings("rawtypes")
    public static List getTradeRoute(Map<String, Object> param, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_TradeRoute where trade_type=:trade_type and pay_type=:pay_type");
        if(param.get("trade_money") != null && !((Long)param.get("trade_money")).equals(0L)) {
            sb.append(" and min_trade_money<=:trade_money and max_trade_money>:trade_money");
        }
        if(param.get("sub_trade_type") != null) {
            sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)param.get("trade_type"));
        query.setParameter("pay_type", (Long)param.get("pay_type"));
        if(param.get("trade_money") != null && !((Long)param.get("trade_money")).equals(0L)) {
            query.setParameter("trade_money", (Long)param.get("trade_money"));
        }
        if(param.get("sub_trade_type") != null) {
            query.setParameter("sub_trade_type", (Long)param.get("sub_trade_type"));
        }
        return query.list();
    }

//    }

	/**
     * 查询交易 路由
     * @param param
     * @param session
     * @throws Exception
     * @return List
     */
    @SuppressWarnings("rawtypes")
	public static List getTradeRule(Map<String, Object> param, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_TradeRule where trade_type=:trade_type");
        if(param.get("sub_trade_type") != null) {
            sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)param.get("trade_type"));
        if(param.get("sub_trade_type") != null) {
            query.setParameter("sub_trade_type", (Long)param.get("sub_trade_type"));
        }
        return query.list();
    }

    /**
     * 查询机构交易路由
     * @param param
     * @param session
     * @throws Exception
     * @return List
     */
//    public static List getOrgTradeRule(Map<String, Object> param, Session session)throws Exception{
//        StringBuffer sb = new StringBuffer();
//        sb.append("from AMS_TradeRule where trade_type=:trade_type");
//        if(param.get("sub_trade_type") != null) {
//            sb.append(" and sub_trade_type=:sub_trade_type");
//        }
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("trade_type", (Long)param.get("trade_type"));
//        if(param.get("sub_trade_type") != null) {
//            query.setParameter("sub_trade_type", (Long)param.get("sub_trade_type"));
//        }
//        List tr_list = query.list();
//        sb.setLength(0);
//        sb.append("from AMS_OrgPayPermission where orgNo=:orgNo");
//        query = session.createQuery(sb.toString());
//        query.setParameter("orgNo", (String)param.get("orgNo"));
//        List opp_list = query.list();
//        List list = new ArrayList();
//        for(Object tr_obj : tr_list) {
//            Map<String, Object> tr_entity = (Map<String, Object>) tr_obj;
//            for(Object opp_obj : opp_list) {
//                Map<String, Object> opp_entity = (Map<String, Object>) opp_obj;
//                //logger.info("tr_entity="+tr_entity);
//                //logger.info("opp_entity="+opp_entity);
//                if(tr_entity.get("pay_interfaceNo") != null && ((String)tr_entity.get("pay_interfaceNo")).equals((String)opp_entity.get("pay_interfaceNo"))) {
//                    list.add(tr_entity);
//                    break;
//                    /*
//                    if(tr_entity.get("debit") != null && ((Boolean)tr_entity.get("debit")).equals((Boolean)opp_entity.get("debit"))) {
//                        list.add(tr_entity);
//                        break;
//                    } else if(tr_entity.get("credit") != null && ((Boolean)tr_entity.get("credit")).equals((Boolean)opp_entity.get("credit"))) {
//                        list.add(tr_entity);
//                        break;
//                    }
//                    */
//                } else if(tr_entity.get("accountNo") != null && ((String)tr_entity.get("accountNo")).equals((String)opp_entity.get("accountNo"))) {
//                    list.add(tr_entity);
//                    break;
//                }
//            }
//        }
//        return list;
//    }
	
	/**
	 * 根据支付通道编号查询支付渠道
	 * @param pay_interfaceNo
	 * @param session
	 * @throws Exception
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public static List getPayChannelByPayInterfaceNo(String pay_interfaceNo, Session session)throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("select pc from AMS_PayChannel pc, AMS_PayInterface pi where pi.codeNo=:pay_interfaceNo and pi.pay_channel_id=pc.id");
		
		Query query = session.createQuery(sb.toString());
		query.setParameter("pay_interfaceNo", pay_interfaceNo);
		
		return query.list();
	}
	

    /**
     * 计算前台手续费
     * @param trade_type
     * @param pay_interfaceNo
     * @param account_codeNo
     * @param orgNo
     * @param sub_trade_type
     * @param member_type_id
     * @param identity_status
     * @param money
     * @throws Exception
     * @return Map<String, Object>
     */
//    public static Map<String, Object> calculateFees(Long trade_type, String pay_interfaceNo, Long account_prop, String account_codeNo, String orgNo, Long sub_trade_type, Long member_type, Long identity_status, Long money, Session session)throws Exception{
//        logger.info("trade_type="+trade_type+"-sub_trade_type="+sub_trade_type+"-member_type="+member_type+"-identity_status="+identity_status+"-money="+money+"-pay_interfaceNo="+pay_interfaceNo+"-account_codeNo="+account_codeNo+"-orgNo="+orgNo);
//        
//        if(orgNo.equals(Constant.ORG_NO_ALLINPAY) && account_prop.equals(Constant.ACCOUNT_PROP_DEFAULT)) {
//            return calculateTLFees(trade_type, sub_trade_type, member_type, identity_status, money, session);
//        } else {
//            return calculateAgencyFees(trade_type, sub_trade_type, pay_interfaceNo, account_codeNo, orgNo, money, null, session);
//        }
//    }
	
    /**
     * 计算通用账户手续费
     */
//    public static Map<String, Object> calculateTLFees(Long trade_type, Long sub_trade_type, Long member_type, Long identity_status, Long money, Session session)throws Exception{
//        logger.info("trade_type="+trade_type+"-sub_trade_type="+sub_trade_type+"-member_type="+member_type+"-identity_status="+identity_status+"-money="+money);
//        Map<String, Object> result = new HashMap<String, Object>();
//        Long fee = 0L;
//        StringBuffer sb = new StringBuffer();
//        sb.append("from AMS_HandlingParam where trade_type=:trade_type and member_type=:member_type and real_name_state=:real_name_state");
//        if(sub_trade_type != null) {
//            sb.append(" and sub_trade_type=:sub_trade_type");
//        }
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("trade_type", trade_type);
//        if(sub_trade_type != null) {
//            query.setParameter("sub_trade_type", sub_trade_type);
//        }
//        query.setParameter("member_type", member_type);
//        query.setParameter("real_name_state", identity_status);
//        
//        List list = query.list();
//        if(!list.isEmpty()) {
//            Map<String, Object> entity = (Map<String, Object>)list.get(0);
//            logger.info("entity="+entity);
//            if(Constant.FEE_TYPE_PERCENTAGE.equals((Long)entity.get("handling_type"))) {
//                fee = Math.round(money * (Double)entity.get("handling_rate"));
//            } else if(Constant.FEE_TYPE_EACH.equals((Long)entity.get("handling_type"))) {
//                fee = (Long)entity.get("handling_each");
//            }
//            if(entity.get("min_fee") != null && fee < (Long)entity.get("min_fee")) {
//                fee = (Long)entity.get("min_fee");
//            }
//            if(entity.get("max_fee") != null && fee > (Long)entity.get("max_fee")) {
//                fee = (Long)entity.get("max_fee");
//            }
//            logger.info("fee="+fee);
//            result.put("handling_mode", (Long)entity.get("handling_mode"));
//        }
//        result.put("fee", fee);
//        return result;
//    }
    
	
	
	@SuppressWarnings("rawtypes")
	public static Long getAgencyFees(Map<String, Object> order_entity, Session session)throws Exception{
		logger.info("order_entity="+order_entity);
        List commandList = getCommands(order_entity.get("orderNo").toString(), session);
        //修改
//        Map<String, Object> user = (Map<String, Object>)new Member().getUserInfoByOrgNo((String)order_entity.get("orgNo"), session);
        Long fee = 0L;
        Long handling_mode = null;
        Long sub_trade_type = (Long)order_entity.get("sub_trade_type");
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_OrgFee where trade_type=:trade_type and orgNo=:orgNo");
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
        	sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)order_entity.get("trade_type"));
        query.setParameter("orgNo", (String)order_entity.get("orgNo"));
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
        	query.setParameter("sub_trade_type", sub_trade_type);
        }
        
        List orgfee_list = query.list();
        

        AccountService accountService = new AccountServiceImpl();
        Map<String, Object> accountType = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
        Long account_type_id = (Long)accountType.get("id");
        String account_codeNo = (String)accountType.get("codeNo");;
        Map<String, Object> impl_command = null;
        for(int i = 0, j = commandList.size(); i < j; i ++) {
            Map<String, Object> command = (Map<String, Object>) commandList.get(i);
            if(account_codeNo.equals((String)command.get("account_codeNo")) && Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))) {
                impl_command = command;
            } else {
                Map<String, Object> result = calculateAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)command.get("pay_interfaceNo"), (String)command.get("account_codeNo"), (String)order_entity.get("orgNo"), (Long)command.get("trade_money"), orgfee_list, session);
                fee += (Long)result.get("fee");
                logger.info("fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        if(impl_command != null) {
            Long impl_trade_money = (Long)impl_command.get("trade_money");
            for(int i = 0, j = commandList.size(); i < j; i ++) {
                Map<String, Object> command = (Map<String, Object>) commandList.get(i);
                if(account_codeNo.equals((String)command.get("account_codeNo")) && !Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))) {
                    impl_trade_money -= (Long)command.get("trade_money");
                }
            }
            if(impl_trade_money > 0) {
                Map<String, Object> result = calculateAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)impl_command.get("pay_interfaceNo"), (String)impl_command.get("account_codeNo"), (String)order_entity.get("orgNo"), impl_trade_money, orgfee_list, session);
                fee += (Long)result.get("fee");
                logger.info("impl_fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        
        return fee;
	}

    @SuppressWarnings("rawtypes")
    public static Long getYUNAgencyFees(Map<String, Object> order_entity, Session session)throws Exception{
        logger.info("order_entity="+order_entity);

        List commandList = getSuccessCommands(order_entity.get("orderNo").toString(), session);
        //修改
        //Map<String, Object> user = (Map<String, Object>)new Member().getUserInfoByOrgNo((String)order_entity.get("orgNo"), session);
        Long memberId = (Long)order_entity.get("member_id");
        Member member = new Member(memberId);
        Long fee = 0L;
        Long handling_mode = null;

        Long sub_trade_type = (Long)order_entity.get("sub_trade_type");
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_PlatformFee where trade_type=:trade_type and member_id=:member_id");
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)order_entity.get("trade_type"));
        query.setParameter("member_id", memberId);
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            query.setParameter("sub_trade_type", sub_trade_type);
        }

        List platFormFee_list = query.list();
        logger.info("platFormFee_list="+platFormFee_list.toString());

        AccountService accountService = new AccountServiceImpl();
        Map<String, Object> accountType = accountService.getApplicationCashAccountType(Constant.YUN_APPLICATION_ID);
        Long account_type_id = (Long)accountType.get("id");
        String account_codeNo = (String)accountType.get("codeNo");

        Map<String, Object> impl_command = null;
        for(int i = 0, j = commandList.size(); i < j; i ++) {
            Map<String, Object> command = (Map<String, Object>) commandList.get(i);
            if(account_codeNo.equals((String)command.get("account_codeNo")) && Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))&&!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))) {
                impl_command = command;
            } else if(!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))){
                Map<String, Object> result = calculatePortalAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)command.get("pay_interfaceNo"), (String)command.get("account_codeNo"), (String)order_entity.get("orgNo"), (Long)command.get("trade_money"), platFormFee_list, session);
                fee += (Long)result.get("fee");
                logger.info("fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        if(impl_command != null) {
            Long impl_trade_money = (Long)impl_command.get("trade_money");
            for(int i = 0, j = commandList.size(); i < j; i ++) {
                Map<String, Object> command = (Map<String, Object>) commandList.get(i);
                if(account_codeNo.equals((String)command.get("account_codeNo")) && !Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))) {
                    impl_trade_money -= (Long)command.get("trade_money");
                }
            }
            logger.info("impl_trade_money:"+impl_trade_money);
            if(impl_trade_money > 0) {
                Map<String, Object> result = calculatePortalAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)impl_command.get("pay_interfaceNo"), (String)impl_command.get("account_codeNo"), (String)order_entity.get("orgNo"), impl_trade_money, platFormFee_list, session);
                fee += (Long)result.get("fee");
                logger.info("impl_fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        logger.info("机构手续费fee="+fee);
        return fee;

    }
    /**
     * 收取机构订单手续费
     * @param order_entity  订单实体
     * @param session       session
     * @throws Exception
     */
    public static void receiveAgencyFees(Map<String, Object> order_entity, Session session)throws Exception{
        logger.info("order_entity="+order_entity);
        List commandList = getSuccessCommands(order_entity.get("orderNo").toString(), session);
        //修改
        Map<String, Object> user = new Member().getUserInfoByOrgNo((String)order_entity.get("orgNo"), session);
        Long fee = 0L;
        Long handling_mode = null;
        
        Long sub_trade_type = (Long)order_entity.get("sub_trade_type");
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_OrgFee where trade_type=:trade_type and orgNo=:orgNo");
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
        	sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)order_entity.get("trade_type"));
        query.setParameter("orgNo", (String)order_entity.get("orgNo"));
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
        	query.setParameter("sub_trade_type", sub_trade_type);
        }
        
        List orgfee_list = query.list();
        
//        List list = getAccountTypeByOrgNo((String)order_entity.get("orgNo"), session);

        AccountService accountService = new AccountServiceImpl();
        Map<String, Object> accountType = accountService.getApplicationCashAccountType((Long)order_entity.get("application_id"));
        Long account_type_id = (Long)accountType.get("id");
        String account_codeNo = (String)accountType.get("codeNo");;
//        if(list != null && list.size() > 0) {
//            Map<String,Object> first_acc_map = (Map<String,Object>)list.get(0);
//            account_type_id = (Long)first_acc_map.get("id");
//            account_codeNo = (String)first_acc_map.get("codeNo");
//            for(Object o : list){
//                Map<String,Object> acc_map = (Map<String,Object>)o;
//                if(acc_map.get("isMaster") != null && (Boolean)acc_map.get("isMaster")){
//                    account_type_id = (Long)acc_map.get("id");
//                    account_codeNo = (String)acc_map.get("codeNo");
//                    break;
//                }
//            }
//        } else {
//            Map<String,Object> acc_map = QueryService.getAccountType(Constant.ACCOUNT_NO_CASH);
//            account_type_id = (Long)acc_map.get("id");
//            account_codeNo = (String)acc_map.get("codeNo");
//        }
        Map<String, Object> impl_command = null;
        for(int i = 0, j = commandList.size(); i < j; i ++) {
            Map<String, Object> command = (Map<String, Object>) commandList.get(i);
            if(account_codeNo.equals((String)command.get("account_codeNo")) && Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))&&!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))) {
                impl_command = command;
            } else if(!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))){
                Map<String, Object> result = calculateAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)command.get("pay_interfaceNo"), (String)command.get("account_codeNo"), (String)order_entity.get("orgNo"), (Long)command.get("trade_money"), orgfee_list, session);
                fee += (Long)result.get("fee");
                logger.info("fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        if(impl_command != null) {
            Long impl_trade_money = (Long)impl_command.get("trade_money");
            for(int i = 0, j = commandList.size(); i < j; i ++) {
                Map<String, Object> command = (Map<String, Object>) commandList.get(i);
                if(account_codeNo.equals((String)command.get("account_codeNo")) && !Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))) {
                    impl_trade_money -= (Long)command.get("trade_money");
                }
            }
            logger.info("impl_trade_money:"+impl_trade_money);
            if(impl_trade_money > 0) {
                Map<String, Object> result = calculateAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)impl_command.get("pay_interfaceNo"), (String)impl_command.get("account_codeNo"), (String)order_entity.get("orgNo"), impl_trade_money, orgfee_list, session);
                fee += (Long)result.get("fee");
                logger.info("impl_fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        logger.info("机构手续费fee="+fee);
        if(fee > 0) {
            Map<String, String> entity = new HashMap<String, String>();
            entity.put("memberNo",                  (String)user.get("memberNo"));
            entity.put("member_name",               user.get("name") == null ? "" : (String)user.get("name"));
            entity.put("member_userId",             (String)user.get("userId"));
            entity.put("trade_time",                String.valueOf(new Date().getTime()));
            entity.put("trade_money",               order_entity.get("order_money").toString());
            entity.put("handling_fee",              fee.toString());
            entity.put("orderNo",                   (String)order_entity.get("orderNo"));
            entity.put("handling_mode",             handling_mode.toString());
            entity.put("is_charge",                 "false");
            entity.put("orgNo",                     (String)order_entity.get("orgNo"));
            Map<String,Object> handlingLogMap = DynamicEntityService.createEntity("AMS_HandlingLog", entity, null);
            //应用支付手续费
            Map<String,Object> applicationMap = DynamicEntityService.getEntity(Constant.APPLICATION_ID_BPS_YUN, "AMS_Organization");
            Map<String,Object> target_member = DynamicEntityService.getEntity((Long)applicationMap.get("member_id"), "AMS_Member");
            OrderService orderService = new OrderServiceImpl();
            Map<String,Object> source_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_READY);
            Map<String,Object> target_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("source_userId",      (String)user.get("userId"));
            param.put("source_memberNo",    (String)user.get("memberNo"));
            param.put("source_member_name", (String)user.get("name"));
            param.put("target_userId",      (String)target_member.get("userId"));
            param.put("target_memberNo",    (String)target_member.get("memberNo"));
            param.put("target_member_name", (String)target_member.get("name"));
            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_ORG_FEE);
            param.put("trade_money",        fee);
            param.put("bizid",              (String)order_entity.get("orderNo"));
            param.put("remark",             (String)order_entity.get("remark"));
            param.put("account_type_id",    (Long)source_accountType.get("id"));
            param.put("target_account_type_id",    (Long)target_accountType.get("id"));
            param.put("isMaster",           true);
            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
            param.put("orgNo",    			(String)order_entity.get("orgNo"));
            try{
	            String tradeId = TradeService.customTransfer(param);
	            query = session.createQuery("update AMS_HandlingLog set is_charge=true,trade_id=:trade_id where id=:id");
	            query.setParameter("trade_id", tradeId);
	            query.setParameter("id", handlingLogMap.get("id"));
	            query.executeUpdate();
            }catch(Exception e){
            	e.printStackTrace();
            	logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 收取门户平台订单手续费
     * @param order_entity  订单实体
     * @param session       session
     * @throws Exception
     * @return Map<String, Object>
     */
    public static void receivePortalAgencyFees(Map<String, Object> order_entity, Session session)throws Exception{
        logger.info("receivePortalAgencyFees="+order_entity);
        List commandList = getSuccessCommands(order_entity.get("orderNo").toString(), session);
        //修改
        //Map<String, Object> user = (Map<String, Object>)new Member().getUserInfoByOrgNo((String)order_entity.get("orgNo"), session);
        Long memberId = (Long)order_entity.get("member_id");
        Member member = new Member(memberId);
        Long fee = 0L;
        Long handling_mode = null;

        Long sub_trade_type = (Long)order_entity.get("sub_trade_type");
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_PlatformFee where trade_type=:trade_type and member_id=:member_id");
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            sb.append(" and sub_trade_type=:sub_trade_type");
        }
        Query query = session.createQuery(sb.toString());
        query.setParameter("trade_type", (Long)order_entity.get("trade_type"));
        query.setParameter("member_id", memberId);
        if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            query.setParameter("sub_trade_type", sub_trade_type);
        }

        List platFormFee_list = query.list();
        logger.info("platFormFee_list="+platFormFee_list.toString());

        AccountService accountService = new AccountServiceImpl();
        Map<String, Object> accountType = accountService.getApplicationCashAccountType(Constant.YUN_APPLICATION_ID);
        Long account_type_id = (Long)accountType.get("id");
        String account_codeNo = (String)accountType.get("codeNo");

        Map<String, Object> impl_command = null;
        for(int i = 0, j = commandList.size(); i < j; i ++) {
            Map<String, Object> command = (Map<String, Object>) commandList.get(i);
            if(account_codeNo.equals((String)command.get("account_codeNo")) && Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))&&!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))) {
                impl_command = command;
            } else if(!Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))){
                Map<String, Object> result = calculatePortalAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)command.get("pay_interfaceNo"), (String)command.get("account_codeNo"), (String)order_entity.get("orgNo"), (Long)command.get("trade_money"), platFormFee_list, session);
                fee += (Long)result.get("fee");
                logger.info("fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        if(impl_command != null) {
            Long impl_trade_money = (Long)impl_command.get("trade_money");
            for(int i = 0, j = commandList.size(); i < j; i ++) {
                Map<String, Object> command = (Map<String, Object>) commandList.get(i);
                if(account_codeNo.equals((String)command.get("account_codeNo")) && !Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))) {
                    impl_trade_money -= (Long)command.get("trade_money");
                }
            }
            logger.info("impl_trade_money:"+impl_trade_money);
            if(impl_trade_money > 0) {
                Map<String, Object> result = calculatePortalAgencyFees((Long)order_entity.get("trade_type"), (Long)order_entity.get("sub_trade_type"), (String)impl_command.get("pay_interfaceNo"), (String)impl_command.get("account_codeNo"), (String)order_entity.get("orgNo"), impl_trade_money, platFormFee_list, session);
                fee += (Long)result.get("fee");
                logger.info("impl_fee="+result.get("fee"));
                if(result.get("handling_mode") != null) {
                    handling_mode = (Long)result.get("handling_mode");
                }
            }
        }
        logger.info("机构手续费fee="+fee);
        if(fee > 0) {
            Map<String, String> entity = new HashMap<String, String>();
            entity.put("memberNo",                  member.getMemberNo());
            entity.put("member_name",               member.getName() == null ? "" : member.getName());
            entity.put("member_userId",             member.getUserId());
            entity.put("trade_time",                String.valueOf(new Date().getTime()));
            entity.put("trade_money",               order_entity.get("order_money").toString());
            entity.put("handling_fee",              fee.toString());
            entity.put("orderNo",                   (String)order_entity.get("orderNo"));
            entity.put("handling_mode",             handling_mode.toString());
            entity.put("is_charge",                 "false");
            entity.put("orgNo",                     (String)order_entity.get("orgNo"));
            Map<String,Object> handlingLogMap = DynamicEntityService.createEntity("AMS_HandlingLog", entity, null);
            //应用支付手续费
            Map<String,Object> applicationMap = DynamicEntityService.getEntity(Constant.YUN_APPLICATION_ID, "AMS_Organization");
            Map<String,Object> target_member = DynamicEntityService.getEntity((Long)applicationMap.get("member_id"), "AMS_Member");
            OrderService orderService = new OrderServiceImpl();
            Map<String,Object> source_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_READY);
            Map<String,Object> target_accountType =  QueryService.getAccountType(Constant.ACCOUNT_NO_STANDARD_BALANCE);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("source_userId",       member.getUserId());
            param.put("source_memberNo",     member.getMemberNo());
            param.put("source_member_name",  member.getName() );
            param.put("target_userId",      (String)target_member.get("userId"));
            param.put("target_memberNo",    (String)target_member.get("memberNo"));
            param.put("target_member_name", (String)target_member.get("name"));
            param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
            param.put("sub_trade_type",         Constant.SUB_TRADE_TYPE_ORG_FEE);
            param.put("trade_money",        fee);
            param.put("bizid",              (String)order_entity.get("orderNo"));
            param.put("remark",             (String)order_entity.get("remark"));
            param.put("account_type_id",    (Long)source_accountType.get("id"));
            param.put("target_account_type_id",    (Long)target_accountType.get("id"));
            param.put("isMaster",           true);
            param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
            param.put("orgNo",    			(String)order_entity.get("orgNo"));
            try{
                String tradeId = TradeService.customTransfer(param);
                query = session.createQuery("update AMS_HandlingLog set is_charge=true,trade_id=:trade_id where id=:id");
                query.setParameter("trade_id", tradeId);
                query.setParameter("id", handlingLogMap.get("id"));
                query.executeUpdate();
            }catch(Exception e){
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }


        }
    }
    
    /**
     * 计算专用账户手续费
     * @param trade_type
     * @param sub_trade_type
     * @param pay_interfaceNo
     * @param accountNo
     * @param orgNo
     * @param money
     * @throws Exception
     * @return Map<String, Object>
     */
    public static Map<String, Object> calculateAgencyFees(Long trade_type, Long sub_trade_type, String pay_interfaceNo, String accountNo, String orgNo, Long money, List orgfee_list, Session session)throws Exception{
        logger.info("trade_type="+trade_type+"-sub_trade_type="+sub_trade_type+"-money="+money+"-pay_interfaceNo="+pay_interfaceNo+"-accountNo="+accountNo+"-orgNo="+orgNo);
        Map<String, Object> result = new HashMap<String, Object>();
        Long fee = 0L;
        if(orgfee_list == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("from AMS_OrgFee where trade_type=:trade_type and orgNo=:orgNo");
            if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            	sb.append(" and sub_trade_type=:sub_trade_type");
            }
            Query query = session.createQuery(sb.toString());
            query.setParameter("trade_type", trade_type);
            query.setParameter("orgNo", orgNo);
            if(sub_trade_type != null && !sub_trade_type.equals(0L)) {
            	query.setParameter("sub_trade_type", sub_trade_type);
            }
            
            orgfee_list = query.list();
        }
        if(!orgfee_list.isEmpty()) {
            Map<String, Object> entity = null;
            boolean flag = true;
            for(Object obj : orgfee_list) {
                entity = (Map<String, Object>)obj;
                logger.info("entity1111="+entity);
                if(pay_interfaceNo != null && !pay_interfaceNo.equals(Constant.PAY_INTERFACE_AMS)) {//走外部的手续费
                	if(entity.get("pay_interfaceNo") != null && ((String)entity.get("pay_interfaceNo")).equals(pay_interfaceNo)) {
                        logger.info("1111=");
                        flag = false;
                        break;
                    }
                } else if(accountNo != null) {//走内部的手续费
                	if(entity.get("accountNo") != null && ((String)entity.get("accountNo")).equals(accountNo)) {
                        logger.info("2222=");
                        flag = false;
                        break;
                    }
                }
            }
            if(flag) {
                for(Object obj : orgfee_list) {
                    entity = (Map<String, Object>)obj;
                    if(entity.get("accountNo") == null && entity.get("pay_interfaceNo") == null) {
                        flag = false;
                        break;
                    }
                }
            }
            if(!flag) {
                logger.info("entity="+entity);
                if(Constant.FEE_TYPE_PERCENTAGE.equals((Long)entity.get("handling_type"))) {
                    fee = Math.round(money * (Double)entity.get("handling_rate"));
                } else if(Constant.FEE_TYPE_EACH.equals((Long)entity.get("handling_type"))) {
                    fee = (Long)entity.get("handling_each");
                }
                if(entity.get("min_fee") != null && fee < (Long)entity.get("min_fee")) {
                    fee = (Long)entity.get("min_fee");
                }
                if(entity.get("max_fee") != null && fee > (Long)entity.get("max_fee")) {
                    fee = (Long)entity.get("max_fee");
                }
                logger.info("fee="+fee);
                result.put("handling_mode", (Long)entity.get("handling_mode"));
            }
        }
        result.put("fee", fee);
        return result;
    }
    /**
     * 计算门户会员手续费
     * @param trade_type
     * @param sub_trade_type
     * @param pay_interfaceNo
     * @param accountNo
     * @param orgNo
     * @param money
     * @throws Exception
     * @return Map<String, Object>
     */
    public static Map<String, Object> calculatePortalAgencyFees(Long trade_type, Long sub_trade_type, String pay_interfaceNo, String accountNo, String orgNo, Long money, List platFormFee_list, Session session)throws Exception{
        logger.info("trade_type="+trade_type+"-sub_trade_type="+sub_trade_type+"-money="+money+"-pay_interfaceNo="+pay_interfaceNo+"-accountNo="+accountNo+"-orgNo="+orgNo);
        Map<String, Object> result = new HashMap<String, Object>();
        Long fee = 0L;
        if(!platFormFee_list.isEmpty() && platFormFee_list != null) {
            for(Object obj : platFormFee_list) {
                Map<String, Object> entity = (Map<String, Object>) obj;
                if (Constant.FEE_TYPE_PERCENTAGE.equals((Long) entity.get("handling_type"))) {
                    fee = Math.round(money * (Double) entity.get("handling_rate"));
                } else if (Constant.FEE_TYPE_EACH.equals((Long) entity.get("handling_type"))) {
                    fee = (Long) entity.get("handling_each");
                }
                if (entity.get("min_fee") != null && fee < (Long) entity.get("min_fee")) {
                    fee = (Long) entity.get("min_fee");
                }
                if (entity.get("max_fee") != null && fee > (Long) entity.get("max_fee")) {
                    fee = (Long) entity.get("max_fee");
                }
                result.put("handling_mode", (Long) entity.get("handling_mode"));
            }
        }
        result.put("fee", fee);
        return result;
    }

    /**
     * 根据通道号和银行代码去获取每日限额
     * @param param
     * @throws Exception
     * @return List
     */
//    public static Map<String, Object> getDailylimit(String interfaceNo,String bank_code ,Long card_type)throws Exception{
//        Map<String, Object> result = new HashMap<String, Object>();
//        JSONArray bankList =  PayChannelManage.getPayInterfaceBankInfo(interfaceNo);
//        boolean debit = false;
//        if(card_type.equals(Constant.BANK_CARD_CX)){
//            debit = true;
//        }
//        boolean credit = false;
//        if(card_type.equals(Constant.BANK_CARD_XY)){
//            credit = true;
//        }
//        logger.info("bankList!!!!!"+bankList);
//        logger.info("bank_code!!!!!"+bank_code);
//        logger.info("card_type!!!!!"+card_type);
//        logger.info("debit!!!!!"+debit);
//        logger.info("credit!!!!!"+credit);
//        for(int i = 0, j = bankList.length(); i < j; i ++){
//        	JSONObject bank = bankList.getJSONObject(i);
//            if(((String)bank.get("bank_code")).indexOf(bank_code) >= 0){
//                if(debit && (Boolean)bank.get("debit")){//借记卡
//                    result.put("limit_single", bank.get("debit_limit_single"));
//                    result.put("limit_day", bank.get("debit_limit_day"));
//                    result.put("limit_month", bank.get("debit_limit_month"));
//                    result.put("remark", bank.get("debit_remark"));
//                    break;
//                }
//                if(credit && (Boolean)bank.get("credit")){//贷记卡
//                    result.put("limit_single", bank.get("credit_limit_single"));
//                    result.put("limit_day", bank.get("credit_limit_day"));
//                    result.put("limit_month", bank.get("credit_limit_month"));
//                    result.put("remark", bank.get("credit_remark"));
//                    break;
//                }
//            }
//        }
//        logger.info("result!!!!="+result);
//        return result;
//    
//    }
    
    /**
     * 根据实名状态。交易类型。交易子类型获取手续费
     * @param param
     * @throws Exception
     * @return List
     */
//    public static List getCounterFee(Map<String, Object> param, Session session)throws Exception{
//        StringBuffer sb = new StringBuffer();
//        Long trade_type = (Long) param.get("trade_type");
//        Long sub_trade_type = (Long) param.get("sub_trade_type");
//        Long real_name_state = (Long) param.get("real_name_state");
//        Long accountType = (Long) param.get("accountType");
//        
//        if(trade_type == null){
//            throw new Exception("请传入参数trade_type");
//        }
//        if(real_name_state == null){
//            throw new Exception("请传入参数real_name_state");
//        }
//                
//        if(trade_type != null && !trade_type.equals("")){
//            sb.append("from AMS_HandlingParam hd where hd.trade_type=:trade_type");
//        }else{
//            throw new Exception("请传入参数trade_type");
//        }
//        if(sub_trade_type != null && !sub_trade_type.equals("")){
//            sb.append(" and hd.sub_trade_type=:sub_trade_type");
//        }
//        if(real_name_state != null && !real_name_state.equals("")){
//            sb.append(" and hd.real_name_state=:real_name_state");
//        }else{
//            throw new Exception("请传入参数real_name_state");
//        }
//        sb.append(" and hd.member_type=:member_type");
//        
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("trade_type", trade_type);
//        query.setParameter("real_name_state", real_name_state);
//        if(accountType != null && accountType.equals(Constant.MEMBER_TYPE_ENTERPRISE)){
//            query.setParameter("member_type", Constant.MEMBER_TYPE_ENTERPRISE);
//        }else{
//            query.setParameter("member_type", Constant.MEMBER_TYPE_PERSON);
//        }
//        if(sub_trade_type != null && !sub_trade_type.equals("")){
//            query.setParameter("sub_trade_type", sub_trade_type);
//        }
//        return query.list();
//    }
    
    /**
     * 手工提现
     * @param param
     * @throws Exception
     * @return List
     */
//    public static void artWithdrawal(Map<String, Object> entity, Session session)throws Exception{
//        logger.info("artWithdrawal start");
//        ArtWithdrawal artWithdrawal = new ArtWithdrawal(entity);
//        artWithdrawal.run();
//    }
    /**
     * 计算总金额
     * @param param
     * @throws Exception
     * @return List
     */
//    public static void calallPrice(Map<String, Object> entity, Session session)throws Exception{
//        logger.info("calallPrice start");
//		StringBuffer sb = new StringBuffer();
//        sb.append("from AMS_ArtWithdrawalDetail where artWithdrawal_id=:artWithdrawal_id");
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("artWithdrawal_id", entity.get("id"));
//        List<Map<String,Object>> list = query.list();
//        if(list.isEmpty()) {
//            throw new Exception("提现记录不存在");
//        }
//        Long allPrice = 0L;
//        for(Map<String,Object> obj : list) {
//        	allPrice+=(Long)obj.get("trade_money");
//        }
//       sb.setLength(0);
//       sb.append("update AMS_ArtWithdrawal set total_money =:total_money where id =:id");
//       query = session.createQuery(sb.toString());
//       logger.info("total_money-----"+allPrice);
//       query.setParameter("total_money", allPrice);
//       logger.info("id-----"+entity.get("id"));
//       query.setParameter("id", entity.get("id"));
//       query.executeUpdate();
//       logger.info("calallPrice end");
//    }
    /**
     * 计算总金额
     * @param param
     * @throws Exception
     * @return List
     */
//    public static void calallPricecre(Map<String, Object> entity, Session session)throws Exception{
//        logger.info("calallPricecre start");
//		StringBuffer sb = new StringBuffer();
//        sb.append("from AMS_ArtWithdrawalDetail where artWithdrawal_id=:artWithdrawal_id");
//        Query query = session.createQuery(sb.toString());
//        query.setParameter("artWithdrawal_id", entity.get("id"));
//        List<Map<String,Object>> list = query.list();
//        if(list.isEmpty()) {
//            throw new Exception("提现记录不存在");
//        }
//        Long allPrice = 0L;
//        for(Map<String,Object> obj : list) {
//        	allPrice+=(Long)obj.get("trade_money");
//        }
//        entity.put("total_money", allPrice);
//    }
    /**
     * 根据机构号查询机构
     * @param orgNo
     * @throws Exception
     * @return List
     */
    public static Map<String, Object> getOrgByOrgNo(String orgNo, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_Organization hd where codeNo=:orgNo");
        Query query = session.createQuery(sb.toString());
        query.setParameter("orgNo", orgNo);
        List list = query.list();
        if(list.isEmpty()) {
            return null;
        } else {
            return (Map<String, Object>) list.get(0);
        }
    }
    
    /**
     * 根据机构号查询机构
     * @param bank_code ?
     * @throws Exception
     * @return List
     */
    public static String getITSBankCode(String bank_code, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("select b.its_bank_code from AMS_Bank b, AMS_BankCode bc where b.id=bc.bank_id and bc.bank_code=:bank_code");
        Query query = session.createQuery(sb.toString());
        query.setParameter("bank_code", bank_code);
        List list = query.list();
        if(list.isEmpty()) {
            return null;
        } else {
            return (String) list.get(0);
        }
    }
    
    /**
     * 根据订单id获取订单详情
     * @param oid
     * @throws Exception
     * @return String
     */
    public static Map<String, Object>  getOrderDetailByOrderNo(Long oid, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_OrderDetail where owner_order_id=:oid");
        Query query = session.createQuery(sb.toString());
        query.setParameter("oid", oid);
        List list = query.list();
        if(list.isEmpty()) {
            return null;
        } else {
            return (Map<String, Object>) list.get(0);
        }
    }
    
    /**
     * 根据机构号获取账户类型
     * @param orgNo
     * @throws Exception
     * @return List
     */
    public static List getAccountTypeByOrgNo(String orgNo, Session session)throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("from AMS_AccountType where orgNo=:orgNo order by id");
        Query query = session.createQuery(sb.toString());
        query.setParameter("orgNo", orgNo);
        List list = query.list();
        if(list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }

    
    /**
     * 创建订单
     * @param param
     * @return
     * @throws Exception
     */
    public Map<String,Object> applyOrder(Map<String, Object> param)throws Exception{
    	return null;
    }
    
    
//    /**
//     * 执行支付指令
//     * @param order_entity
//     * @throws Exception
//     */
//    public void doPayCommand(Map<String, Object> order_entity)throws Exception{
//    	
//    }
    
    /**
     * 订单支付完成
     * @param order_entity  订单实体
     * @param session       session
     * @throws Exception
     */
    public void completePay(Map<String, Object>order_entity, Session session) throws Exception{
    	Query query = session.createQuery("update AMS_Order set order_state=:order_state, pay_time=:pay_time where id=:id");
        query.setParameter("order_state", Constant.ORDER_STATE_PAY);
        query.setParameter("pay_time", new Date());
        query.setParameter("id", (Long)order_entity.get("id"));
        query.executeUpdate();
        
        List commandList = getSuccessCommands(order_entity.get("orderNo").toString(), session);
        for(int i = 0, j = commandList.size(); i < j; i ++) {
            Map<String, Object> command = (Map<String, Object>)commandList.get(i);

            Long account_type_id   = (Long)command.get("account_type_id");
            String target_userId   = null;
            Long trade_type = (Long)command.get("trade_type");
            if(trade_type.equals(Constant.TRADE_TYPE_TRANSFER)) {
                target_userId  = (String)command.get("source_userId");
            } else if(trade_type.equals(Constant.TRADE_TYPE_WITHDRAW)) {
                target_userId  = (String)command.get("source_userId");
            } else if(trade_type.equals(Constant.TRADE_TYPE_DEPOSIT)) {
                target_userId  = (String)command.get("target_userId");
            }
            String pay_channelNo    = (String)command.get("pay_interfaceNo");
            Long trade_money        = (Long)command.get("trade_money");
            trade_type         = (Long)order_entity.get("trade_type");
            Map<String, Object> account_type_entity =  DynamicEntityService.getEntity(account_type_id, "AMS_AccountType");
            Map<String, Object> member_entity = User.getUser(target_userId, session);
            Long member_id = (Long)member_entity.get("id");

            Long sub_trade_type = (Long)command.get("sub_trade_type");
//            if(sub_trade_type != null &&sub_trade_type.equals(Constant.SUB_TRADE_TYPE_FEE)) {
//                continue;
//            }
            //验证风控
            RiskAction action = new RiskAction();
            action.setAccountType((String)account_type_entity.get("codeNo"));
            action.setMemberId(member_id);
            action.setMemberType((Long)member_entity.get("member_type"));
            action.setPayChannel(pay_channelNo);
            if(member_entity.get("isIdentity_checked") != null && (Boolean)member_entity.get("isIdentity_checked")) {
                action.setRealNameState(1L);
            } else {
                action.setRealNameState(0L);
            }
            if(order_entity.get("sub_trade_type") != null) {
            	action.setSubTradeType((Long)order_entity.get("sub_trade_type"));
            }
            action.setTradeMoney(trade_money);
            action.setTradeType(trade_type);
            RiskManager.check(session, action);
            action.setRiskResult(RiskResult.OK);
            
            RiskManager.record(session, action);
        }
        Map<String, Object> up_member_entity = User.getUser((String)order_entity.get("member_uuid"), session);
        
		Map<String, Object> param = new HashMap<>();
		if(((Long)order_entity.get("trade_type")).equals(Constant.TRADE_TYPE_WITHDRAW)) {
			param.put("verification_code_type", Constant.VERIFICATION_CODE_TYPE_ACCOUNT_DOWN);
        } else if(((Long)order_entity.get("trade_type")).equals(Constant.TRADE_TYPE_DEPOSIT)) {
        	param.put("verification_code_type", Constant.VERIFICATION_CODE_TYPE_ACCOUNT_UP);
        }
        
        param.put("phone", (String)up_member_entity.get("phone"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        DecimalFormat df =new DecimalFormat("#####0.00");
        param.put("date", sdf.format(new Date()));
        param.put("money", df.format((Long)order_entity.get("order_money") / 100.0));
        param.put("orderNo", (String)order_entity.get("orderNo"));
        param.put("member_id", (Long)up_member_entity.get("id"));
        //发送短信
        try{
            String content = VerificationCode.textResolve(param).get("html");
            logger.info("短信模版："+content);
            Extension.otherService.sendSM((String)up_member_entity.get("phone"), content);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    
    /**
     * 订单完成
     * @param order_entity  订单实体
     * @param param         参数
     * @param session       session
     * @throws Exception
     */
    public String completeOrder(Map<String, Object> order_entity, Map<String, Object> param, Session session) throws Exception{
    	Query query = session.createQuery("update AMS_Order set order_state=:order_state, confirm_time=:confirm_time where id=:order_id");
        query.setParameter("order_state", Constant.ORDER_STATE_SUCCESS);
        query.setParameter("confirm_time", new Date());
        query.setParameter("order_id", (Long)order_entity.get("id"));
        query.executeUpdate();
        return null;
    }
    
    /**
     * 获取运营商
     * @param phone 手机号
     * @return 运营商
     * @throws BizException
     */
    public String getMobileCarriers(String phone) throws BizException {
		logger.info("OrderServiceImpl.getMobileCarriers start");
		String regular_move = "^1((34[0-8]|705)|(3[5-9]|47|5[0-27-9]|8[2-478]|78)\\d)\\d{7}$";
        String regular_telecom = "^1((349|700)|(33|53|8[019]|77)\\d)\\d{7}$";
        String regular_unicom = "^1(709|(3[0-2]|32|45|5[56]|8[56]|76)\\d)\\d{7}$";
        
        if(phone.matches(regular_telecom)){
            return "regular_telecom";
        }
        if(phone.matches(regular_unicom)){
            return "phone.unicom.discount";
        }
        if(phone.matches(regular_move)){
            return "phone.move.discount";
        }
        return null;
	}
    
    public String getAccountNoByTradeNo(String tradeNo)throws BizException{
		try {
			final String _tradeNo = tradeNo;
			String res=
			EntityManagerUtil.execute(new QueryWork<String>() {
				@Override
				public String doQuery(Session session) throws Exception {
					Query query = session.createQuery("from AMS_TradeCode where tradeCode=:tradeCode");
		            query.setParameter("tradeCode", _tradeNo);
		            List<Map<String,Object>> list =  query.list();
		            if(list.size() != 1){
		            	throw new BizException(ErrorCode.NO_TRADE_CODE,"交易类型不存在");
		            }
		            
		            return (String)list.get(0).get("accountTypeNo");
				}
				
			});
			return res;
		} catch (BizException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.UNKNOWN_ERROR,e.getMessage());
		}
	}
    /**
     * 获取未完成指令集
     * @param orderNo
     * @throws Exception
     */
    public static List getUnPayCommands(String orderNo, Session session)throws Exception{
        Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo and pay_state=:pay_state order by seq_no asc");
        query.setParameter("orderNo", orderNo);
        query.setParameter("pay_state", com.kinorsoft.ams.Constant.COMMAND_STATE_UNPAY);
        
        return query.list();
    }
    /**
     * 获取退款指令集
     * @param orderNo
     * @throws Exception
     */
    public static List getSuccessReturnCommands(String orderNo, Session session)throws Exception{
        Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo and refund_status=:refund_status order by seq_no asc");
        query.setParameter("orderNo", orderNo);
        query.setParameter("refund_status", Constant.REFUND_STATE_SUCCESS);
        
        return query.list();
    }
    
    public static void sendProducerMessageWithKafka(String orderNo,String state){
    	final String _orderNo =orderNo;
    	final String _state = state;
    	new Thread(new Runnable(){
    		public void run(){
    			try {
    				Thread.sleep(5000); 
    				Map<String, Object> _order_entity = EntityManagerUtil.execute(new QueryWork<Map<String, Object>>() {
    					@Override
    					public Map<String, Object> doQuery(Session session) throws Exception {
    						return getOrder(_orderNo, session);
    					}
    				});
    				
    				logger.info("d"+new Date().getTime());
//    				String  backUrl= (String)order_detail.get("backUrl");
    				String backUrl = (String) _order_entity.get("backUrl");
    				if(backUrl == null || "".equals(backUrl)){
    					return;
    				}
    				_order_entity.put("backUrl", backUrl);
    				logger.info("order_entity-------"+_order_entity);
    				String orgNo = (String)_order_entity.get("orgNo");
    				
    				if(orgNo != null){
    					if(backUrl!=null && !orgNo.equals("99999")){
//    						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    						JSONObject rps = new JSONObject();
//    						JSONObject returnValue = new JSONObject();
////    						returnValue.put("language", "1");
//    						returnValue.put("bizOrderNo", _order_entity.get("bizOrderNo"));
//    			            returnValue.put("orderNo", _order_entity.get("orderNo"));
//    			            returnValue.put("amount", _order_entity.get("order_money"));
//    			            returnValue.put("payDatetime", sdf.format(((Date)_order_entity.get("confirm_time")).getTime()));
//    			            returnValue.put("extendInfo", _order_entity.get("extend_info")==null?"":_order_entity.get("extend_info"));
//    			            Member member = new Member();
//    			            member.setUserId((String)_order_entity.get("member_uuid"));
//    			            String biz_user_id = (String)member.getUserInfo().get("biz_user_id");
//    			            if(biz_user_id == null ||"".equals(biz_user_id)){
//    			            	biz_user_id = (String)_order_entity.get("member_uuid");
//    			            }
//    			            returnValue.put("buyerBizUserId", biz_user_id);
////    			            returnValue.put("sellerUserId", _order_entity.get("target_uuid"));
////    			            returnValue.put("transactionType", _order_entity.get("transaction_type"));
//    						
//    			            rps.put("returnValue", (new JSONObject(returnValue)).toString());
//    			            rps.put("service", "OrderService");
//    			            rps.put("method", "pay");
//    			            rps.put("status", _state);
//    			            rps.put("errorCode", "");
//    			            rps.put("message", "");
//    			            
//    			            _order_entity.put("service", "OrderService");
//    			            _order_entity.put("method", "pay");
//    			    		StringBuffer sb = new StringBuffer();
////    			    		Long timestep = new Date().getTime();
//    			    		_order_entity.put("timestamp", sdf.format(new Date()));
//    			    	    sb.append(_order_entity.get("orgNo")).append(rps.toString()).append(_order_entity.get("timestamp"));
//    			           
//    			    	    logger.info("签名前："+sb.toString());
//    			    	    OrderService orderService = new OrderServiceImpl();
//    			    	    Map<String, Object> org = orderService.getOrg((String)_order_entity.get("orgNo"));
//    			    	    PrivateKey privateKey = RSAUtil.getPrivateKey((String)org.get("private_key"));
//    			    		String sign = RSAUtil.sign(privateKey, sb.toString());
//    			    		sb.setLength(0);
//    			    		sb.append("sysid=").append(_order_entity.get("orgNo"))
//    			    		.append("&rps=").append(rps.toString())
//    			    		.append("&timestamp=").append(_order_entity.get("timestamp"))
//    			    		.append("&v=1.0&sign=").append(URLEncoder.encode(sign));
//    			            logger.info("签名后："+sign);
//    			            _order_entity.put("param", sb.toString());
    						
    						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    						String version = Environment.instance().getSystemProperty("version");
    						
    						Member member = new Member();
     			            member.setUserId((String)_order_entity.get("member_uuid"));
    						String biz_user_id = (String)member.getUserInfo().get("biz_user_id");
    			            if(biz_user_id == null ||"".equals(biz_user_id)){
    			            	biz_user_id = (String)_order_entity.get("member_uuid");
    			            }
    						
    						JSONObject returnValue = new JSONObject();
    						returnValue.put("orderNo", _order_entity.get("orderNo"));
							returnValue.put("bizOrderNo", _order_entity.get("bizOrderNo"));
				            returnValue.put("amount", _order_entity.get("order_money"));
				            returnValue.put("payDatetime", _order_entity.get("confirm_time") == null ? null : sdf.format(((Date)_order_entity.get("confirm_time")).getTime()));
				            returnValue.put("buyerBizUserId", biz_user_id);
				            returnValue.put("extendInfo", _order_entity.get("extend_info")==null?"":_order_entity.get("extend_info"));
				            
				            JSONObject rps = new JSONObject();
    			            rps.put("service", "OrderService");
    			            rps.put("method", "pay");
				            rps.put("returnValue", returnValue);
				            rps.put("status", _state);
				            if(!"OK".equals(_state)){
				            	rps.put("errorCode", ErrorCode.PAY_ERROR);
	    			            rps.put("message", "支付失败。");
				            }
				            
				            String timestamp = sdf.format(new Date());
				            String signStr = _order_entity.get("orgNo") + rps.toString() + timestamp;
			    			OrderService orderService = new OrderServiceImpl();
    			    	    Map<String, Object> org = orderService.getOrg((String)_order_entity.get("orgNo"));
    			    	    PrivateKey privateKey = RSAUtil.getPrivateKey((String)org.get("private_key"));
    			    		String sign = RSAUtil.sign(privateKey, signStr);
    			    		
    			    		StringBuilder sb = new StringBuilder();
    			    		sb.append("sysid=").append(_order_entity.get("orgNo"));
    			    		sb.append("&rps=").append(rps.toString());
    			    		sb.append("&timestamp=").append(timestamp);
    			    		sb.append("&v=").append(version);
    			    		sb.append("&sign=").append(URLEncoder.encode(sign));
    			    		
    			    		JSONObject sendContent = new JSONObject();
    			    		sendContent.put("backUrl", _order_entity.get("backUrl"));
    			    		sendContent.put("param", sb.toString());
    			    		sendContent.put("orderNo", _order_entity.get("orderNo"));

                            //返回剩余可投资金额 朱成-2016-3-22
//                            Long order_type = _order_entity.get("order_type") == null ? null : (Long)_order_entity.get("order_type");
//                            String biz_trade_code =  _order_entity.get("biz_trade_code") == null ? "" : (String)_order_entity.get("biz_trade_code");
//                            Long goodsType = _order_entity.get("goodsType") == null ? null : (Long)_order_entity.get("goodsType");
//                            String goodsNo =  _order_entity.get("goodsNo") == null ? "" : (String)_order_entity.get("goodsNo");
//
//                            if (  Constant.ORDER_TYPE_DAISHOU.equals(order_type) && "1001".equals( biz_trade_code ) ){
//                                OrderServiceImpl orderServiceImpl = new OrderServiceImpl();
//                                List<Map<String, Object>> orderList = orderServiceImpl.getOrderListByGoods( order_type, biz_trade_code, goodsType, goodsNo );
//                                Long sumOldAmount = 0L;
//                                for (Map<String, Object> temp : orderList){
//                                    Long tempAmount = temp.get("order_money") == null ? 0L : (Long)temp.get("order_money");
//                                    sumOldAmount += tempAmount;
//                                }
//                                GoodsServiceImpl gsi = new GoodsServiceImpl();
//                                Map<String,Object> goods = gsi.queryGoods((Long)_order_entity.get("application_id"),goodsType,goodsNo);
//                                Long highest_amount = goods.get("highest_amount") == null ? (Long)goods.get("total_amount") : (Long)goods.get("highest_amount");
//                                Long remainAmount = highest_amount - sumOldAmount;
//                                sendContent.put("remainAmount",remainAmount);
//                            }
		            
    			            RabbitProducerManager rabbitProducerManager = RabbitProducerManager.getInstance();
    						 logger.info("-----发送开始----");
    						try {
    							
//    				    		JSONObject json = new JSONObject(_order_entity);
    				    		logger.info("-----start" + sb.toString());
//    				    		json.put("time", new Date().getTime());
    				    		rabbitProducerManager.send(URLEncoder.encode(sendContent.toString(), "UTF-8"));
    				    		logger.info("-----end");
    						} catch (Exception e1) {
    							 logger.info("--error---");
    							logger.error(e1.getMessage(), e1);
    						}
    						logger.info("-----发送完毕----");
    					}
    				}
    			}catch(Exception e){
    				logger.error(e.getMessage(), e);
    				e.printStackTrace();
    			}
    		}
		}).start();
    }
    
  //设置银行协议号
  	public static void updateBankCardContractNo(Long _bankCardId, String _contractNo, Session session) throws BizException {
  		logger.info("Order.updateBankCardContractNo start");
  		StringBuilder sb = new StringBuilder("update AMS_MemberBank set contract_no=:contract_no where id=:bankCardId");   
          Query query = session.createQuery(sb.toString());
          query.setParameter("contract_no", _contractNo);
          query.setParameter("bankCardId", _bankCardId);
          query.executeUpdate();
  	}
  	
  	public static void createPayDetail(Map<String,Object> orderEntity,Session session) throws Exception{
  		//只有充值、消费、代收才执行
  		Long orderType = (Long)orderEntity.get("order_type");
  		if(!Constant.ORDER_TYPE_DEPOSIT.equals(orderType) && !Constant.ORDER_TYPE_SHOPPING.equals(orderType) && !Constant.ORDER_TYPE_DAISHOU.equals(orderType)){
  			return;
  		}
  		
  		Query query = session.createQuery("from AMS_OrderPayDetail where bizid=:orderNo order by seq_no asc");
        query.setParameter("orderNo", orderEntity.get("orderNo"));
        List<Map<String,Object>> commandList =  query.list();
//        Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))
        List<Map<String,String>>  payDetailList = new ArrayList<Map<String,String>>();
        Map<String, Object> couponCommand = null; //代金券指令另行处理
        Map<String,Object> itsOrGatewayCommand = null;//its和网关的指令另行处理
        Map<String,Object> fee_command = null;//手续费的指令另行处理，一般累计到余额的出处去，充值的不进行处理
        for(Map<String,Object> command : commandList){
        	//代金券
        	if(Constant.TRADE_TYPE_TRANSFER.equals((Long)command.get("trade_type")) && Constant.SUB_TRADE_TYPE_MARKETING_ACTIVITY.equals((Long)command.get("sub_trade_type"))){
        		couponCommand = command;
        	}else if(!Constant.PAY_INTERFACE_AMS.equals((String)command.get("pay_interfaceNo"))){ //its或者网关
        		itsOrGatewayCommand = command;
			}else if(Constant.SUB_TRADE_TYPE_FEE.equals(command.get("sub_trade_type"))){ //
				fee_command = command;
			}else{ //内部支付
//				Map<String,String> payDetail = new HashMap<String,String>();
//				payDetail.put("pay_order_id", String.valueOf(orderEntity.get("id")));
//				payDetail.put("pay_order_label", (String)orderEntity.get("orderNo"));
//				payDetail.put("account_type_id", String.valueOf(command.get("account_type_id")));
//				payDetail.put("account_type_label", (String)command.get("account_type_label"));
//				payDetail.put("trade_money", String.valueOf(command.get("trade_money")));
//				payDetail.put("pay_channelNo", (String)command.get("pay_channelNo"));
//				payDetail.put("pay_interfaceNo", (String)command.get("pay_interfaceNo"));
				
				Map<String,String> payDetail = generatePayDetail(orderEntity, command);
				
				payDetailList.add(payDetail);
			}
        }
        
        if(couponCommand != null){
        	Long target_account_type_id = (Long)couponCommand.get("target_account_type_id");
        	Long trade_money = (Long)couponCommand.get("trade_money");
        	for(Map<String,String> payDetail : payDetailList){
        		if(target_account_type_id.toString().equals(payDetail.get("account_type_id"))){
        			Long tradeMoney = Long.parseLong(payDetail.get("trade_money"))-trade_money;
        			if(tradeMoney == 0L){
        				//如果月为0，就删除掉
        				payDetailList.remove(payDetail);
        			}else{
        				payDetail.put("trade_money", String.valueOf(tradeMoney));
        			}
        			break;
        		}
        	}
//        	Map<String,String> payDetail = new HashMap<String,String>();
//			payDetail.put("pay_order_id", String.valueOf(orderEntity.get("id")));
//			payDetail.put("pay_order_label", (String)orderEntity.get("orderNo"));
//			payDetail.put("trade_money", String.valueOf(out_command.get("trade_money")));
//			payDetail.put("pay_channelNo", (String)out_command.get("pay_channelNo"));
//			payDetail.put("pay_interfaceNo", (String)out_command.get("pay_interfaceNo"));
//			payDetail.put("bank_code", (String)out_command.get("bank_code"));
//			payDetail.put("bank_name", (String)out_command.get("bank_name"));
        	
        	Map<String,String> payDetail = generatePayDetail(orderEntity, couponCommand);
        	
			payDetailList.add(0, payDetail);//添加到第一位
        }
        
        if(itsOrGatewayCommand != null){
        	Long target_account_type_id = (Long)itsOrGatewayCommand.get("target_account_type_id");
        	Long trade_money = (Long)itsOrGatewayCommand.get("trade_money");
        	for(Map<String,String> payDetail : payDetailList){
        		if(target_account_type_id.toString().equals(payDetail.get("account_type_id"))){
        			Long tradeMoney = Long.parseLong(payDetail.get("trade_money"))-trade_money;
        			if(tradeMoney == 0L){
        				//如果月为0，就删除掉
        				payDetailList.remove(payDetail);
        			}else{
        				payDetail.put("trade_money", String.valueOf(tradeMoney));
        			}
        			break;
        		}
        	}
//        	Map<String,String> payDetail = new HashMap<String,String>();
//			payDetail.put("pay_order_id", String.valueOf(orderEntity.get("id")));
//			payDetail.put("pay_order_label", (String)orderEntity.get("orderNo"));
//			payDetail.put("trade_money", String.valueOf(out_command.get("trade_money")));
//			payDetail.put("pay_channelNo", (String)out_command.get("pay_channelNo"));
//			payDetail.put("pay_interfaceNo", (String)out_command.get("pay_interfaceNo"));
//			payDetail.put("bank_code", (String)out_command.get("bank_code"));
//			payDetail.put("bank_name", (String)out_command.get("bank_name"));
        	
        	Map<String,String> payDetail = generatePayDetail(orderEntity, itsOrGatewayCommand);
        	
			payDetailList.add(0, payDetail);//添加到第一位
        }
        //一般手续费直接直接出的情况没有，先写着
        if(fee_command != null){
        	Long account_type_id = (Long)fee_command.get("account_type_id");
        	Long trade_money = (Long)fee_command.get("trade_money");
        	boolean isNewAccount = true;
        	for(Map<String,String> payDetail : payDetailList){
        		if(account_type_id.toString().equals(payDetail.get("account_type_id"))){
        			Long tradeMoney = Long.parseLong(payDetail.get("trade_money"))+trade_money;
        			payDetail.put("trade_money", String.valueOf(tradeMoney));
        			isNewAccount = false;
        			break;
        		}
        	}
        	if(isNewAccount&&!Constant.ORDER_TYPE_DEPOSIT.equals(orderEntity.get("order_type"))){//充值订单，从余额出，手续费不属于支付详情
//        		Map<String,String> payDetail = new HashMap<String,String>();
//				payDetail.put("pay_order_id", String.valueOf(orderEntity.get("id")));
//				payDetail.put("pay_order_label", (String)orderEntity.get("orderNo"));
//				payDetail.put("account_type_id", String.valueOf(fee_command.get("account_type_id")));
//				payDetail.put("account_type_label", (String)fee_command.get("account_type_label"));
//				payDetail.put("trade_money", String.valueOf(fee_command.get("trade_money")));
//				payDetail.put("pay_channelNo", (String)fee_command.get("pay_channelNo"));
//				payDetail.put("pay_interfaceNo", (String)fee_command.get("pay_interfaceNo"));
        		
        		Map<String,String> payDetail = generatePayDetail(orderEntity, fee_command);
				
				payDetailList.add(payDetail);
        	}
        }
		for(Map<String,String> entity : payDetailList){
			DynamicEntityService.createEntity("AMS_PayDetail", entity, null);
		}
  	}
  	
  	/**
  	 * 设置payDetail
  	 * @param orderEntity
  	 * @param command
  	 * @return
  	 */
  	private static Map<String, String> generatePayDetail(Map<String, Object> orderEntity, Map<String, Object> command) {
  		Map<String, String> payDetailMap = new HashMap<String, String>();
  		
  		payDetailMap.put("pay_order_id", orderEntity.get("id") == null ? null : String.valueOf(orderEntity.get("id")));
  		payDetailMap.put("pay_order_label", (String)orderEntity.get("orderNo"));
  		payDetailMap.put("account_type_id", String.valueOf(command.get("account_type_id")));
  		payDetailMap.put("account_type_label", (String)command.get("account_type_label"));
  		payDetailMap.put("trade_money", command.get("trade_money").toString());
  		payDetailMap.put("pay_channelNo", (String)command.get("pay_channelNo"));
  		payDetailMap.put("pay_interfaceNo", (String)command.get("pay_interfaceNo"));
  		payDetailMap.put("accountNo", (String)command.get("accountNo"));
  		payDetailMap.put("accountNo_encrypt", (String)command.get("accountNo_encrypt"));
  		payDetailMap.put("bank_code", (String)command.get("bank_code"));
  		payDetailMap.put("bank_name", (String)command.get("bank_name"));
  		payDetailMap.put("trade_type", command.get("trade_type") == null ? null : String.valueOf(command.get("trade_type")));
  		payDetailMap.put("sub_trade_type", command.get("sub_trade_type") == null ? null : String.valueOf(command.get("sub_trade_type")));
  		payDetailMap.put("pay_mode", (String)command.get("pay_mode"));
  		payDetailMap.put("acct_validdate", (String)command.get("acct_validdate"));
  		payDetailMap.put("cvv2", (String)command.get("cvv2"));
  		payDetailMap.put("card_type", command.get("card_type") == null ? null : String.valueOf(command.get("card_type")));
  		payDetailMap.put("account_prop", command.get("account_prop") == null ? null : String.valueOf(command.get("account_prop")));
  		payDetailMap.put("account_codeNo", (String)command.get("account_codeNo"));
  		payDetailMap.put("pay_type", command.get("pay_type") == null ? null : String.valueOf(command.get("pay_type")));
  		
  		return payDetailMap;
  	}
    /**
     * 人工提现完成，先解冻，订单改成完成，把钱进去-自定义转账
     * @param orgNo
     * @throws Exception
     * @return List
     */
//    public static void afterArtificial(String orderNo, Session session)throws Exception{
//        //解冻
//        Map<String, Object> order = getOrder(orderNo, session);
//        Member member = new Member((String)order.get("member_uuid"));
//        Map<String, Object> user_mp = member.getUserInfo();
//        Long d = (Long)order.get("order_money");
//        Map<String, Object> temp = calculateFees(Constant.TRADE_TYPE_WITHDRAW, null, Constant.ACCOUNT_PROP_DEFAULT, null, Constant.ORG_NO_ALLINPAY, null, 
//                (Long)user_mp.get("member_type"), user_mp.get("isIdentity_checked") == null || !(Boolean)user_mp.get("isIdentity_checked") ? 0L : 1L, 
//                        d, session);
//        Long fee = (Long)temp.get("fee");
//        
//        Map<String, Object> unfreeze_param = new HashMap<String, Object>();
//        unfreeze_param.put("userId",            order.get("member_uuid").toString());    
//        unfreeze_param.put("bizid",             orderNo);
//        unfreeze_param.put("account_type_id",    (Long)order.get("account_type_id"));
//        unfreeze_param.put("unfreeze_money",     (Long)order.get("order_money")+fee);
//        unfreeze_param.put("remark",            "代付成功,解冻");
//        FreezeService.unfreezeMoney(unfreeze_param);
//      
//        //修改订单状态
//        Query query = session.createQuery("update AMS_Order set order_state =:order_state where orderNo=:orderNo");
//        query.setParameter("orderNo", orderNo);
//        query.setParameter("order_state", Constant.ORDER_STATE_SUCCESS);
//        query.executeUpdate();
//        
//        //把钱转过去
//        Member member2 = new Member(Constant.SYSTEM_USERID_ARTIFICIAL);
//        Map<String, Object> user = member2.getUserInfo();
//        Map<String, Object> param = new HashMap<String, Object>();
//        param.put("source_userId",      (String)order.get("member_uuid"));
//        param.put("target_userId",      Constant.SYSTEM_USERID_ARTIFICIAL);//系统会员
//        param.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
//        param.put("trade_money",        (Long)order.get("order_money"));
//        param.put("bizid",              orderNo);
//        param.put("remark",             (String)order.get("remark"));
//        param.put("account_type_id",    (Long)order.get("account_type_id"));
//        param.put("isMaster",           true);
//        param.put("source_memberNo",    (String)order.get("memberNo"));
//        param.put("target_memberNo",    (String)user.get("memberNo"));
//        param.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
//        param.put("source_member_name", (String)order.get("member_name"));
//        param.put("target_member_name", (String)user.get("name"));
//        
//        param.put("pay_channelNo",      Constant.PAY_CHANNEL_AMS);
//        TradeService.withdraw(param);
//        
//        //手续费转到手续费账户
//        if(fee > 0){
//            Map<String, Object> param_fee = new HashMap<String, Object>();
//            Member member3 = new Member(Constant.SYSTEM_USERID_FEE);
//            Map<String, Object> user_fee = member3.getUserInfo();
//            param_fee.put("source_userId",      (String)order.get("member_uuid"));
//            param_fee.put("target_userId",      Constant.SYSTEM_USERID_FEE);//手续费账户
//            param_fee.put("trade_type",         Constant.TRADE_TYPE_TRANSFER);
//            param_fee.put("sub_trade_type",       Constant.SUB_TRADE_TYPE_FEE);
//            param_fee.put("trade_money",        fee);
//            param_fee.put("bizid",              orderNo);
//            param_fee.put("remark",             (String)order.get("remark"));
//            param_fee.put("account_type_id",    (Long)order.get("account_type_id"));
//            param_fee.put("isMaster",           false);
//            param_fee.put("source_memberNo",    (String)order.get("memberNo"));
//            param_fee.put("target_memberNo",    (String)user_fee.get("memberNo"));
//            param_fee.put("pay_interfaceNo",    Constant.PAY_INTERFACE_AMS);
//            param_fee.put("source_member_name", (String)order.get("member_name"));
//            param_fee.put("target_member_name", (String)user_fee.get("name"));
//            param_fee.put("pay_channelNo",      Constant.PAY_CHANNEL_AMS);
//            TradeService.customTransfer(param_fee);
//        }
//    }
    
  	/**
  	 * 检测业务码
  	 * @param param
  	 * @throws Exception
  	 * @author xulai
  	 */
  	public static void checkTradeCode(Map<String,String> param)throws Exception{
  		logger.info("checkTradeCode start");
  		String orderType = (String)param.get("order_type");
  		String biz_trade_code = (String)param.get("biz_trade_code");
  		
  		//检查代收业务码
  		if((Constant.ORDER_TYPE_DAISHOU.toString()).equals(orderType)){
  			if(!("1001".equals(biz_trade_code) || "1002".equals(biz_trade_code) || "3001".equals(biz_trade_code) || "3002".equals(biz_trade_code) || "1003".equals(biz_trade_code)))
  				throw new Exception("非法业务码");
  		}
  		
  		//检查代付业务码，和代收业务码是否匹配
  		if((Constant.ORDER_TYPE_DAIFU.toString()).equals(orderType)||(Constant.ORDER_TYPE_BATCH_DAIFU.toString()).equals(orderType)){
  			logger.info("----代付---");
  			final Long application_id  = Long.valueOf(param.get("application_id"));
  			JSONArray collectPayList = new JSONArray(param.get("collectPayListClob"));
  			final List<String> bizOrderNoLsit = new ArrayList<String>();
  			for(int i = 0;i<collectPayList.length();i++){
  				JSONObject json = collectPayList.getJSONObject(i);
  				bizOrderNoLsit.add(json.optString("bizOrderNo"));
  			}
  			
  			List entityList = EntityManagerUtil.execute(new QueryWork<List>(){  				
				@Override
				public List doQuery(Session session)
						throws Exception {
					// TODO Auto-generated method stub
					Query query = session.createQuery("from AMS_Order where application_id = :application_id and bizOrderNo in (:bizOrderNo)");
					query.setParameterList("bizOrderNo", bizOrderNoLsit);
					query.setParameter("application_id", application_id);
					List list  = query.list();
					return list;
				}  				
  			});
  			for(int i = 0;i<entityList.size();i++){
  				Map<String,Object> temp = (Map<String,Object>)entityList.get(i);
  				String $biz_trade_code = (String)temp.get("biz_trade_code");
  				if("1001".equals($biz_trade_code) && !("2001".equals(biz_trade_code)))
  	  				throw new Exception("业务码有错");
  	  			if("1002".equals($biz_trade_code) && !("2002".equals(biz_trade_code)))
  	  				throw new Exception("业务码有错");
  	  			if("1003".equals($biz_trade_code) && !("2003".equals(biz_trade_code)))
	  				throw new Exception("业务码有错");
  	  			if("3001".equals($biz_trade_code) && !("4001".equals(biz_trade_code)))
  	  				throw new Exception("业务码有错");
  	  			if("3002".equals($biz_trade_code) && !("4002".equals(biz_trade_code)))
  	  				throw new Exception("业务码有错");
  			}
  		}
  	}
  	/**
	 * 检查分帐
	 * @param spliRuteList   	分帐列表
	 * @param sumAmount			分帐最大金额
	 * @param userId			分帐上级userId
	 * @throws Exception 
	 */
	public void checkSplitRule(JSONArray spliRuteList,Long sumAmount, String userId) throws Exception{
		logger.info("checkSplitRule start");
		logger.info("spliRuteList:"+spliRuteList.toString());
		logger.info("sumAmount:"+sumAmount);
		logger.info("userId:"+userId);
		Long amount = 0L;
		for( int i = 0; i < spliRuteList.length(); i++ ){
			logger.info("I:"+i);
			
			JSONObject spliRute = spliRuteList.getJSONObject(i);
			logger.info("spliRute:"+spliRute);
			String target_bizUserId = spliRute.getString("bizUserId");
			if( userId.equals(target_bizUserId) ){
				throw new BizException(ErrorCode.ORDER_ERROR,"分帐中不能包含自己!");
			}
			Long _amount = spliRute.getLong("amount");
			amount += _amount;
			if(spliRute.get("splitRuleList") != null){
				checkSplitRule(spliRute.getJSONArray("splitRuleList"),_amount,target_bizUserId);
			}
		}
		if( sumAmount < amount ){
			throw new BizException(ErrorCode.ORDER_ERROR,"分帐金额不能大于上级金额!");
		}
	}
	
	/**
	 * 获取交易日志
	 * @param tradeType         交易类型
	 * @param subTradeType      交易子类型
     * @param sourceUserId      sourceUserId
     * @param outBizno          outBizno
	 * @param session
	 * @throws Exception
	 */
	public static List getTradeLog(Long tradeType, Long subTradeType, String sourceUserId, String outBizno, Session session) throws Exception{
		logger.info("tradeType:" + tradeType + ",subTradeType:" + subTradeType + ",sourceUserId:" + sourceUserId + ",outBizno:" + outBizno);
		
		try{
			String queryStr = "from AMS_TradeLog where trade_type=:tradeType and sub_trade_type=:subTradeType and source_userId=:sourceUserId and out_bizno=:outBizno";
			Query query = session.createQuery(queryStr);
			query.setParameter("tradeType", tradeType);
			query.setParameter("subTradeType", subTradeType);
			query.setParameter("sourceUserId", sourceUserId);
			query.setParameter("outBizno", outBizno);
			return query.list();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 根据applicationId查询应用信息
	 * @param applicationId             应用ID
	 * @param session                   session
	 * @return Map<String, Object> 
	 * @throws Exception
	 */
	public static Map<String, Object> getApplicationById(Long applicationId, Session session) throws Exception{
		logger.info("applicationId:" + applicationId );
		try{
			String queryStr = "from AMS_Organization where id=:applicationId";
			Query query = session.createQuery(queryStr);
			query.setParameter("applicationId", applicationId);
			List list = query.list();
			if(list.isEmpty())
				return null;
			else				
				return (Map<String, Object>) list.get(0);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

    /**
     * 通过ID查询会员银行卡实体
     * @param bankId    会员银行卡ID
     * @param session   session
     * @return  Map<String,Object>
     */
    public static Map<String,Object> getBankById(Long bankId, Session session) throws Exception{
        logger.info("bankId:" + bankId );
        try{
            String queryStr = "from AMS_MemberBank where id=:bankId";
            Query query = session.createQuery(queryStr);
            query.setParameter("bankId", bankId);
            List list = query.list();
            if(list.isEmpty())
            	return null;
            else
            	return (Map<String, Object>) list.get(0);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查用户提现是否要求使用安全卡
     * @param applicationId     应用ID
     * @param bankId            会员ID
     * @param session           session
     */
	public static void checkSetSafeCard(Long applicationId,Long bankId, Session session )throws Exception{
        Map<String, Object> application = getApplicationById(applicationId, session);
        if ( application != null ) {
            Boolean is_withdraw_safe_card = application.get("is_withdraw_safe_card") == null ? false : (Boolean) application.get("is_withdraw_safe_card");
            if (Boolean.TRUE.equals(is_withdraw_safe_card)) {
                Map<String, Object> cardInfo = getBankById(bankId, session);
                logger.info("getBindBankCard返回：" + cardInfo);
                if (cardInfo == null || cardInfo.isEmpty()) {
                    throw new BizException(ErrorCode.CARD_BIND_LOG_NOTEXSIT, "此银行卡未绑定。");
                }
                Boolean memberSafeCard = cardInfo.get("is_safe_card")== null ? false : (Boolean) cardInfo.get("is_safe_card");
                if (!Boolean.TRUE.equals(memberSafeCard)) {
                    throw new BizException(ErrorCode.UN_SET_SAFE_CARD_NOT_WITHDRAW, "未设置安全卡不能提现");
                }
            }
        }else{
            throw new Exception("商户应用信息没有找到！");
        }

	}

    /**
     * 返回pos支付码
     * @return pos支付码
     * @throws Exception
     */
    public static String getPosCode()throws Exception{
        logger.info("--------getPosCode--");
        String _code = CodeUtil.getCode("1",8L,15);
        Long old_code = Long.parseLong(_code);
        Long new_code = old_code%999999;

        String s_old_code = String.valueOf(new_code);
        int zeroLong = 6 - s_old_code.length();
        String zero_s = "";
        for( int i = 0; i < zeroLong; i++ ){
            zero_s += "0";
        }
        logger.info("zero_s + s_old_code:"+zero_s + s_old_code);
        return zero_s + s_old_code;
    }

    /**
     * 创建批量支付批次量
     * @param entityMap 实体
     * @throws Exception
     */
    public static void createOrderByBatchDaiFu(final Map<String, String> entityMap) throws Exception {
        DynamicEntityService.createEntity("AMS_TLTBatchDaiFuOrder", entityMap, null);
    }

    /**
     * 修改批量支付批次状态
     * @param req_no    批次
     * @param state     状态
     * @param session   session
     * @throws Exception
     */
    public static void updateOrderByBatchDaiFu(String req_no, Long state, Session session)throws Exception{
        logger.info("Order.updateOrderByBatchDaiFu start");
        StringBuilder sb = new StringBuilder("update AMS_TLTBatchDaiFuOrder set state=:state where req_no=:req_no");
        Query query = session.createQuery(sb.toString());
        query.setParameter("state", state);
        query.setParameter("req_no", req_no);
        query.executeUpdate();
    }

}