<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" errorPage="" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.unionpay.acp.sdk.AcpService" %>
<%@page import="bps.common.Constant"%>
<%@ page import="bps.external.soa.process.Extension" %>
<%@ page import="com.unionpay.acp.sdk.LogUtil" %>

<%!
    public static final String encoding = "UTF-8";
    public static final String TXN_TYPE_DAIKOU = "11";           //代扣
    public static final String TXN_TYPE_CONSUME_UNDO = "31";    //消费撤销
    public static final String TXN_TYPE_REFUND = "04";           //退货
    public static Logger logger = Logger.getLogger("unionDaikouBack.jsp");


    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                //System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }
%>

<%
    try{
        //开始接收银联代收后台通知
        request.setCharacterEncoding(encoding);

        logger.info("================================代扣后台通知开始============================");

        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(request);
        logger.info(reqParam);
        System.out.println(reqParam);

        Map<String, String> valideData = null;
        if (null != reqParam && !reqParam.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
            valideData = new HashMap<String, String>(reqParam.size());
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                value = new String(value.getBytes(encoding), encoding);
                valideData.put(key, value);
            }
        }

        logger.info("代扣返回参数=" + valideData);

        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(valideData, encoding)) {
            logger.error("代扣验签验证签名结果");
        } else {//获取返回结果成功
            String txnType = valideData.get("txnType"); //交易类型
            String respCode = valideData.get("respCode"); //响应码
            String commandNo = valideData.get("orderId"); //商户订单号
            String payAmount = valideData.get("txnAmt"); //代扣金额
            String outTradeId = valideData.get("queryId"); //代扣金额

            if(TXN_TYPE_DAIKOU.equals(txnType)){//处理代扣异步通知
                if("00".equals(respCode)){  //代扣成功
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("orderNo", commandNo);
                    param.put("orderMoney", Long.valueOf(payAmount));
                    param.put("outTradeId", outTradeId);
                    param.put("payChannelNo", Constant.PAY_CHANNEL_UNION_DAIKOU);
                    param.put("payInterfaceNo", Constant.PAY_INTERFACE_UNION_DAIKOU);
                    param.put("tradeTime", new Date());

                    Extension.orderService.payChannelDeposit(param);
                }else if(("03").equals(respCode)||  //进行中，目前不进行处理
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){

                }else{  //代扣失败
                    Extension.orderService.closeCommandOrder(commandNo, null);
                }
            }else if(TXN_TYPE_CONSUME_UNDO.equals(txnType)){    //消费撤销

            }else if(TXN_TYPE_REFUND.equals(txnType)){  //退货异步消息通知

            }

        }

        logger.info("================================代扣后台通知结束============================");
    }catch(Exception e){
        logger.error("代扣后台通知错误：" + e.getMessage(), e);
    }

%>