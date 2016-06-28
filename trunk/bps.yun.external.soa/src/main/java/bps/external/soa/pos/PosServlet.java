package bps.external.soa.pos;

import bps.common.MD5Util;
import bps.external.soa.process.Extension;
import org.apache.log4j.Logger;
import org.jdom.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱成
 * pos交互类
 * Created by yyy on 2016/3/30.
 */
@WebServlet(name = "PosServlet", urlPatterns = {"/PosServlet.do"})
public class PosServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger("pos");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String key = "xQ4mm0YD7o5YBolR5om0";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Do some work
        doService(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doService(request, response);
    }

    /**
     * 执行服务
     * @param request   request（传入）
     * @param response  response（传出）
     * @throws IOException
     */
    static void doService(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        logger.info("------------------PosService begin------------------");
        getIpAddress(request);
        logger.info("request:"+request);
        logger.info("response:"+response);
        logger.info("request.getRemotePort():"+request.getRemotePort());
        //PrintWriter out = response.getWriter();

        request.setCharacterEncoding("UTF-8");
        int size = request.getContentLength();
        logger.info("size:"+size);

        InputStream is = request.getInputStream();

        byte[] reqBodyBytes = Util.readBytes(is, size);

        String res = new String(reqBodyBytes);
        logger.info("res:"+res);

        Map<String, Object> returnValue = new HashMap<>();
        Util dc =new Util();
        Document root = dc.StringToXmlDoc(res);
        dc.xmlElementsObject(root.getRootElement(),returnValue);

        String transaction_type = (String) returnValue.get("transaction_type");

        logger.info("type:"+transaction_type);

        if( "MP0001".equals(transaction_type)){
            query(returnValue, response);
        }else if("MP0002".equals(transaction_type)){
            pay(returnValue, response);
        }

    }

    /**
     * 订单查询
     * @param returnValue   传入参数
     * @param response      返回
     * @throws IOException
     */
    public static void query(Map<String, Object> returnValue, HttpServletResponse response) throws IOException {
        boolean isTrue = true;

        String state_code = "99";
        String state_msg = "其他问题";

        String transaction_type = (String) returnValue.get("transaction_type");
        String requester = (String) returnValue.get("requester");
        //String request_time = (String) returnValue.get("request_time");

        String pay_code = (String) returnValue.get("paycode");
        String terminal_id = (String) returnValue.get("terminal_id");
        String mcht_cd = (String) returnValue.get("mcht_cd");

        String str = "<transaction_header>" +
                "<transaction_type>" + transaction_type + "</transaction_type>" +
                "<requester>" + requester + "</requester>" +
                //"<response_time>" + request_time + "</response_time>" +
                "</transaction_header>" +
                "<transaction_body>" +
                "<paycode>" + pay_code + "</paycode>" +
                "<mcht_cd>" + mcht_cd + "</mcht_cd>" +
                "<terminal_id>" + terminal_id + "</terminal_id>" +
                "</transaction_body>" + key;
        String old_md5 = null;
        try {
            old_md5 = MD5Util.MD5(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sign = (String) returnValue.get("sign");
        sign = sign.toUpperCase();

        logger.info("str:" + str);
        logger.info("sign:" + sign);
        logger.info("old_md5:" + old_md5);

        if (!sign.equals(old_md5)) {
            state_code = "02";
            state_msg = "验签失败";
            isTrue = false;
        }
        Map<String, Object> order;
        String outString;

        //String response_time = sdf.format(new Date());

        String amt = "";
        String merchantname = "";
        String name = "";
        logger.info("aaaaaaaaaaaaaa");
        if (isTrue){
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("paycode", pay_code);
                param.put("terminal_id", terminal_id);
                param.put("mcht_cd", mcht_cd);
                logger.info("bbbbbbbbbb");
                Extension.orderService.setPosOrder(param);
                logger.info("cccccccccccccc");
                order = Extension.orderService.getOrderByPos(pay_code);
                logger.info("dddddddddddddd");
                amt = (String) order.get("amt");
                merchantname = (String) order.get("merchantname");
                name = (String) order.get("name");
                state_code = "00";
                state_msg = "成功";
                if (order == null) {
                    state_code = "01";
                    state_msg = "没有支付码对应的订单";
                }


            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                state_code = "99";
                state_msg = "其他问题：" + e.getMessage();
            }
    }
        logger.info("eeeeeeeeeeeeeee");
        str = "<transaction_header>" +
                "<transaction_type>"+transaction_type+"</transaction_type>" +
                "<requester>ALP</requester>" +
                //"<response_time>"+response_time+"</response_time>"+
                "<resp_code>"+state_code+"</resp_code>"+
                "<resp_msg>"+state_msg+"</resp_msg>"+
                "</transaction_header>" +
                "<transaction_body>" +
                "<paycode>"+pay_code+"</paycode>"+
                "<amt>"+amt+"</amt>"+
                "<merchantname>"+merchantname+"</merchantname>"+
                "<name>"+name+"</name>" +
                "</transaction_body>"+key;
        String md5 = null;
        logger.info("ffffffffffff");
        try {
            md5 = MD5Util.MD5(str);
        } catch (Exception e) {
            state_code = "99";
            state_msg = "其他问题："+e.getMessage();
            logger.error(e.getMessage(),e);
        }

        logger.info("str:"+str);
        logger.info("md5:"+md5);

        outString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        outString +="<transaction>\n" +
                "\t<transaction_header>\n" +
                "\t\t<transaction_type>"+transaction_type+"</transaction_type>\n" +
                "\t\t<requester>ALP</requester>\n" +
                //"\t\t<response_time>"+response_time+"</response_time>\n" +
                "\t\t<resp_code>"+state_code+"</resp_code>\n" +
                "\t\t<resp_msg>"+state_msg+"</resp_msg>\n" +
                "\t</transaction_header>\n" +
                "\t<transaction_body>\n" +
                "\t\t<paycode>"+pay_code+"</paycode>\n" +
                "\t\t<amt>"+amt+"</amt>\n" +
                "\t\t<merchantname>"+merchantname+"</merchantname>\n" +
                "\t\t<name>"+name+"</name>\n" +
                "\t</transaction_body>\n" +
                "\t<sign_type>MD5</sign_type>\n" +
                "\t<sign>"+md5+"</sign>\n" +
                "</transaction>";
        logger.info("outString:"+outString);
        logger.info("l:"+outString.getBytes().length);
//        response.setContentType("application/x-www-form-urlencoded");
        response.setContentLength(outString.getBytes().length);
        response.setCharacterEncoding("UTF-8");
//        response.getOutputStream().write(outString.getBytes("UTF-8"));
//        response.flushBuffer();

        response.getWriter().print(outString);
        response.flushBuffer();
    }

    /**
     * 支付确认
     * @param returnValue   传入参数
     *                      paycode         支付码
     *                      pay_type        付款方式：1：刷卡 2：现金
     *                      trace_no        凭证号（pos终端流水号）
     *                      refer_no        通联系统交易流水号
     *                      amt             订单金额 保留两位小数
     *                      bank_card_no    银行卡号，前六后四位,中间用星号替换；（现金交易此参数不传）
     *                      bank_code       银行代码
     *                      terminal_id     终端号，pos机具编号
     *                      mcht_cd         商户编号
     * @param response      返回
     * @throws IOException
     */
    public static void pay(Map<String, Object> returnValue, HttpServletResponse response) throws IOException {

        boolean isTrue = true;

        String state_code = "99";
        String state_msg = "其他问题";
        String outString;
        String md5 = null;

        String transaction_type = (String) returnValue.get("transaction_type");
        String requester = (String) returnValue.get("requester");

        String pay_code = (String) returnValue.get("paycode");      //支付码
        String pay_type = (String) returnValue.get("pay_type");     //付款方式：01：刷卡 02：现金

        String trace_no = (String) returnValue.get("trace_no");     //凭证号（pos终端流水号）

        String refer_no = (String) returnValue.get("refer_no");     //通联系统交易流水号
        String amt = (String) returnValue.get("amt");               //订单金额 保留两位小数
        String bank_card_no = (String) returnValue.get("bank_card_no");     //银行卡号，前六后四位,中间用星号替换；（现金交易此参数不传）
        String bank_code = (String) returnValue.get("bank_code");           //银行代码
        String terminal_id = (String) returnValue.get("terminal_id");       //终端号，pos机具编号
        String mcht_cd = (String) returnValue.get("mcht_cd");               //商户编号

        try {
//        <transaction_header><transaction_type>MP0002</transaction_type><requester>ALLINPAY</requester>
//        </transaction_header><transaction_body><paycode>000115</paycode><pay_type>01</pay_type>
//        <mcht_cd>821610115200000</mcht_cd><terminal_id>00000001</terminal_id><amt>0.10</amt>
//        <trace_no>000312</trace_no><refer_no>000000172562</refer_no>
//        <bank_card_no>622848*********9819</bank_card_no><bank_code>01030000</bank_code></transaction_body>

            String str = "<transaction_header>" +
                    "<transaction_type>" + transaction_type + "</transaction_type>" +
                    "<requester>" + requester + "</requester>" +
                    //"<request_time>"+request_time+"</request_time>" +
                    "</transaction_header>" +
                    "<transaction_body>" +
                    "<paycode>" + pay_code + "</paycode>" +
                    "<pay_type>" + pay_type + "</pay_type>" +
                    "<mcht_cd>" + mcht_cd + "</mcht_cd>" +
                    "<terminal_id>" + terminal_id + "</terminal_id>" +
                    "<amt>" + amt + "</amt>" +
                    "<trace_no>" + trace_no + "</trace_no>" +
                    "<refer_no>" + refer_no + "</refer_no>" +
                    "<bank_card_no>" + bank_card_no + "</bank_card_no>" +
                    "<bank_code>" + bank_code + "</bank_code>" +
                    "</transaction_body>" + key;
            //返回

            Map<String, Object> order = null;

            String old_md5 = MD5Util.MD5(str);

            String sign = (String) returnValue.get("sign");
            sign = sign.toUpperCase();

            logger.info("str:" + str);
            logger.info("sign:" + sign);
            logger.info("old_md5:" + old_md5);

            if (!sign.equals(old_md5)) {
                state_code = "02";
                state_msg = "验签失败";
                isTrue = false;
            }
            try {
                order = Extension.orderService.getOrderByPos(pay_code);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            if (order == null) {
                state_code = "01";
                state_msg = "没有支付码对应的订单";
                isTrue = false;
            }
//            if (isTrue) {
//                String order_money = (String) order.get("amt");
//                Double _order_money =  Double.parseDouble(order_money) * 100;
//                Double _amt = Double.parseDouble(amt) * 100;
//                logger.info("order_money:" + _order_money);
//                logger.info("amt:" + _amt);
//
//                if (!(_order_money.longValue()).equals(_amt.longValue()) ) {
//                    state_code = "99";
//                    state_msg = "其他问题：交易金额不一致!";
//                    isTrue = false;
//                }
//            }

            if (isTrue) {
                try {
                    Map<String, Object> param = new HashMap<>();
                    param.put("paycode", pay_code);
                    param.put("terminal_id", terminal_id);
                    param.put("mcht_cd", mcht_cd);

                    param.put("pay_type", pay_type);
                    param.put("trace_no", trace_no);
                    param.put("refer_no", refer_no);
                    param.put("amt", amt);
                    param.put("bank_card_no", bank_card_no);
                    param.put("bank_code", bank_code);

                    Extension.orderService.setPosOrderAndOver(param);

                    state_code = "00";
                    state_msg = "成功";
                } catch (Exception e) {
                    state_code = "99";
                    state_msg = "其他问题：" + e.getMessage();
                    logger.error(e.getMessage(), e);
                }
            }

            str = "<transaction_header>" +
                    "<transaction_type>" + transaction_type + "</transaction_type>" +
                    "<requester>ALP</requester>" +
                    //"<response_time>"+response_time+"</response_time>" +
                    "<resp_code>" + state_code + "</resp_code>" +
                    "<resp_msg>" + state_msg + "</resp_msg>" +
                    "</transaction_header>" +
                    "<transaction_body>" +
                    "<paycode>" + pay_code + "</paycode>" +
                    "</transaction_body>" + key;

            md5 = MD5Util.MD5(str);

            logger.info("str:" + str);
            logger.info("md5:" + md5);


        }catch (Exception e){
            state_code = "99";
            state_msg = "其他问题：" + e.getMessage();
            logger.error(e.getMessage(), e);
        }

        outString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        outString += "<transaction>\n" +
                "<transaction_header>\n" +
                "<transaction_type>" + transaction_type + "</transaction_type>\n" +
                "<requester>ALP</requester>\n" +
                //"<response_time>"+response_time+"</response_time>\n" +
                "<resp_code>" + state_code + "</resp_code>\n" +
                "<resp_msg>" + state_msg + "</resp_msg>\n" +
                "</transaction_header>\n" +
                "<transaction_body>\n" +
                "<paycode>" + pay_code + "</paycode>\n" +
                "</transaction_body>\n" +
                "<sign_type>MD5</sign_type>\n" +
                "<sign>" + md5 + "</sign>\n" +
                "</transaction>";

        logger.info("outString:"+outString);
        logger.info("l:"+outString.getBytes().length);
        response.setContentLength(outString.getBytes().length);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(outString);
        response.flushBuffer();

    }
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            logger.info("Proxy-Client-IP:"+ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.info("WL-Proxy-Client-IP:"+ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            logger.info("HTTP_CLIENT_IP:"+ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            logger.info("HTTP_X_FORWARDED_FOR:"+ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.info("getRemoteAddr:"+ip);
        }
        logger.info("ip:"+ip);
        return ip;
    }
}
