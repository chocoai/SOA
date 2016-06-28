<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@page import="bps.common.Util"%>
<%@page import="bps.common.Constant"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="bps.external.soa.process.Extension"%>
<%@page import="bps.common.ErrorCode"%>
<%@page import="bps.common.BizException"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.JSONObject"%>
<%@page import="bps.external.soa.SoaServiceUtil"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.text.SimpleDateFormat"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
</head>

<body>
<script>
	<%
        try{
            logger.info("======================gatewayFront start============================");

            sysid = request.getParameter("ext1");
            frontUrl = request.getParameter("ext2");
            String commandNo = request.getParameter("orderNo");//商户订单号
            String payResult= request.getParameter("payResult");
            logger.info("orderNo:"+commandNo);
            logger.info("payResult:"+payResult);

            int index = commandNo.indexOf(Constant.COMMAND_SPLIT_SIGN);

            Map<String,Object> orderEntity = null;
            String orderNo = commandNo.substring(0, index);

            if(index >= 0) {
                orderEntity = Extension.orderService.getOrder(orderNo);

                if(orderEntity == null || orderEntity.isEmpty()){
                    throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
                }
            }else{
                logger.error("支付指令号不正确。");
                throw new BizException(ErrorCode.OTHER_ERROR, "支付指令号不正确");
            }

            logger.info("order_entity:"+orderEntity);

            String payCompleteFrontUrl = (String)orderEntity.get("frontUrl");

            //有前台回调
            if(!StringUtils.isBlank(payCompleteFrontUrl)){
                String params = "";
                //网关支付成功
                if("1".equals(payResult)){
                    //查询订单是否成功
                    int orderState = checkOrderIsSuc(orderNo);

                    orderEntity = Extension.orderService.getOrder(orderNo);
                    switch(orderState){
                        case 1:
                            params = sucOpe(orderEntity, sdf);
                            break;
                        case 3:
    %>
	window.location.href = '../../gateway/error/500.html';
	<%
                            break;
                    }

                }else{ //网关支付失败
    %>
	window.location.href = '../../gateway/error/500.html';
	<%
                }

                String payCompleteFrontUrlAll = payCompleteFrontUrl + "?" + params;
                logger.info("payCompleteFrontUrlAll=" + payCompleteFrontUrlAll);
    %>

	window.location.href = '<%= payCompleteFrontUrlAll%>';

	<%
            }else{ //无前台回调
                logger.info("无前台回调。");
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);

    %>
	window.location.href = '../../gateway/error/500.html';
	<%
        }

        logger.info("======================gatewayFront end============================");
    %>
</script>

<%!
	Logger logger = Logger.getLogger("gatewayFront.jsp");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String sysid = null;
	String frontUrl = null;

	//查询订单是否成功。1：成功。2：失败。3： 超时。
	public int checkOrderIsSuc(String orderNo) throws BizException{
		try{
			int count = Integer.parseInt(Extension.paramMap.get("gateway.front.query.order.state.count"));
			int interVal = Integer.parseInt(Extension.paramMap.get("gateway.front.query.order.state.interVal"));
			for(int i = 0; i < count; i++){
				logger.info("查询订单是否成功第" + i + "次");
				Map<String, Object> orderEntity = Extension.orderService.getOrder(orderNo);
				if(orderEntity == null || orderEntity.isEmpty()){
					throw new BizException(ErrorCode.ORDER_NOTEXSIT, "订单不存在。");
				}

				Long orderState = (Long)orderEntity.get("order_state");
				logger.info("订单状态" + orderState);

				//成功
				if(Constant.ORDER_STATE_SUCCESS.equals(orderState)){
					return 1;
				}

				Thread.sleep(interVal);
			}

			//超时
			return 3;
		}catch(BizException bizE){
			throw bizE;
		}catch(Exception e){
			throw new BizException(ErrorCode.OTHER_ERROR, "支付失败。");
		}
	}

	//成功操作,返回参数
	public String sucOpe(Map<String, Object> orderEntity, SimpleDateFormat sdf) throws BizException{
		logger.info("==========================sucOpe start==========================");
		logger.info("sucOpe参数：orderEntity=" + orderEntity + ",,sysid=" + sysid);

		try{
			Long buyerUserId = (Long)orderEntity.get("member_id");
			Map<String, Object> buyerUserEntity = Extension.memberService.getUserInfo(buyerUserId);

			String timestamp = sdf.format(new Date());
			String v = Extension.paramMap.get("version");
			String service = "OrderService";
			String method = "pay";
			String orderNo = (String)orderEntity.get("orderNo");
			String bizOrderNo = (String)orderEntity.get("bizOrderNo");
			Long orderMoney = (Long)orderEntity.get("order_money");
			String payDatetime = sdf.format((Date)orderEntity.get("confirm_time"));
			String buyerBizUserId = (String)buyerUserEntity.get("biz_user_id");
			String extendInfo = (String)orderEntity.get("extend_info");

			JSONObject returnValue = new JSONObject();
			returnValue.put("orderNo", orderNo);
			returnValue.put("bizOrderNo", bizOrderNo);
			returnValue.put("amount", orderMoney);
			returnValue.put("payDatetime", payDatetime);
			returnValue.put("buyerBizUserId", buyerBizUserId);
			returnValue.put("extendInfo", extendInfo);

			JSONObject rps = new JSONObject();
			rps.put("service", service);
			rps.put("method", method);
			rps.put("returnValue", returnValue);
			rps.put("status", "OK");

			//签名
			String signStr = sysid + rps.toString() + timestamp;
			String sign = SoaServiceUtil.rsaSign(sysid, signStr);

			StringBuilder sb = new StringBuilder();
			sb.append("sysid=").append(sysid).append("&");
			sb.append("sign=").append(java.net.URLEncoder.encode(sign)).append("&");
			sb.append("timestamp=").append(timestamp).append("&");
			sb.append("v=").append(v).append("&");
			sb.append("rps=").append(rps.toString());

			logger.info("ret:" + sb.toString());
			logger.info("==========================sucOpe end==========================");
			return sb.toString();
		}catch(BizException bizE){
			logger.error(bizE.getMessage(), bizE);
			throw bizE;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new BizException(ErrorCode.OTHER_ERROR, "其他错误。");
		}
	}
%>

</body>
</html>

