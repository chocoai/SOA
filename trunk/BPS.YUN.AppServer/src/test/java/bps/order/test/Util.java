package bps.order.test;

import bps.common.MD5Util;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 * Created by yyy on 2016/3/31.
 */
public class Util {
    private static Logger logger = Logger.getLogger("pos");
    private static Provider prvd = null;
    private static final Util.SSLHandler simpleVerifier = new Util.SSLHandler();
    private static SSLSocketFactory sslFactory;

    static String charsetName = "UTF-8";

    /**
     * 发送http请求
     * @param url   请求地址
     * @param xml   发送内容
     * @return  请求结果
     * @throws Exception
     */
    public static String httpSend(String url, String xml)throws Exception{
        OutputStream reqStream = null;
        InputStream resStream = null;
        URLConnection request = null;
        String respText = null;

        try {
            byte[] postData = xml.getBytes(charsetName);
            request = createRequest(url, "POST");
            request.setRequestProperty("Content-type", "application/tlt-notify");
            request.setRequestProperty("Content-length", String.valueOf(postData.length));
            request.setRequestProperty("Keep-alive", "false");
            reqStream = request.getOutputStream();
            System.out.println("------------reqStream"+reqStream);
            reqStream.write(postData);
            reqStream.close();
            ByteArrayOutputStream ex = null;
            resStream = request.getInputStream();
            ex = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];

            int count;
            while((count = resStream.read(buf, 0, buf.length)) > 0) {
                ex.write(buf, 0, count);
            }

            resStream.close();
            respText = new String(ex.toByteArray(), charsetName);
            return respText;
        } catch (Exception var13) {
            throw var13;
        } finally {
            close(reqStream);
            close(resStream);
        }
    }
    private static URLConnection createRequest(String strUrl, String strMethod) throws Exception {
        URL url = new URL(strUrl);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if(conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpConn = (HttpsURLConnection)conn;
            httpConn.setRequestMethod(strMethod);
            httpConn.setSSLSocketFactory(getSSLSF());
            httpConn.setHostnameVerifier(getVerifier());
        } else if(conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn1 = (HttpURLConnection)conn;
            httpConn1.setRequestMethod(strMethod);
        }

        return conn;
    }
    private static void close(InputStream c) {
        try {
            if(c != null) {
                c.close();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    private static void close(OutputStream c) {
        try {
            if(c != null) {
                c.close();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static HostnameVerifier getVerifier() {
        return simpleVerifier;
    }

    public static synchronized SSLSocketFactory getSSLSF() throws Exception {
        if(sslFactory != null) {
            return sslFactory;
        } else {
            SSLContext sc = prvd == null?SSLContext.getInstance("SSLv3"):SSLContext.getInstance("SSLv3");
            sc.init((KeyManager[])null, new TrustManager[]{simpleVerifier}, (SecureRandom)null);
            sslFactory = sc.getSocketFactory();
            return sslFactory;
        }
    }

    private static class SSLHandler implements X509TrustManager, HostnameVerifier {
        private SSLHandler() {
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    /**
     * String转Doc
     * @param xmlDoc    xml字符串
     * @return  Document
     */
    public Document StringToXmlDoc(String xmlDoc){
        logger.info("xmlDoc:" + xmlDoc);
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();

        try {
            //通过输入源构造一个Document
            return sb.build(source);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * xmlDoc转Map
     * @param root          xmlDoc
     * @param returnValue   返回值
     * @return returnValue
     */
    public Map<String,Object> xmlElementsObject(Element root,Map<String, Object> returnValue) {
        logger.info("root:" + root);
        //创建一个新的字符串

        try {
            //通过输入源构造一个Document
            logger.info(root.getName());//输出根元素的名称
            System.out.println(root.getName());
            //得到根元素所有子元素的集合
            List<Element> jiedian = root.getChildren();

            //Element et;

            for (Element et : jiedian) {
                //et = aJiedian;//循环依次得到子元素
                List<Element> detail = et.getChildren();

                String value = et.getText();
                String key = et.getName();
                if ( detail.isEmpty() ){
                    returnValue.put(key, value);
                }else{
                    xmlElementsObject(et,returnValue);
                }
            }
        }catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        logger.info("返回的Map<String, Object>----------------------");

        logger.info(returnValue);

        logger.info("-------------end-<object>--------");
        return returnValue;
    }

    /**
     * 读hpptIO数据流
     * @param is            输入流
     * @param contentLen    长度
     * @return byte[]
     */
    public static final byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;

            int readLengthThisTime = 0;

            byte[] message = new byte[contentLen];

            try {

                while (readLen != contentLen) {

                    readLengthThisTime = is.read(message, readLen, contentLen
                            - readLen);

                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }

                    readLen += readLengthThisTime;
                }

                return message;
            } catch (IOException e) {
                // Ignore
                // e.printStackTrace();
            }
        }

        return new byte[] {};
    }
    public static void main(String[] args) throws Exception {
        String xmls = "<?xml version=\"1.0\" encoding=\"GBK\"?><transaction><transaction_header>\n" +
                "<transaction_type>MP0001</transaction_type>\n" +
                "<requester>ALP</requester>\n" +
                "<response_time>20150413150510</response_time>\n" +
                "<resp_code>00</resp_code>\n" +
                "<resp_msg>成功</resp_msg>\n" +
                "</transaction_header><transaction_body>\n" +
                "<order_no>5110110000005128</order_no>\n" +
                "<amt>1188.00</amt>\n" +
                "<remark>13991980423</remark>\n" +
                "</transaction_body>\n" +
                "<sign_type>MD5</sign_type>\n" +
                "<sign>285d9f0ef5ea4f7ae07e50a3aea5a408469eb</sign>\n" +
                "</transaction>";

        //Map<String, Object> returnValue = new HashMap<>();
        //Util dc =new Util();
        //Document root = dc.StringToXmlDoc(xmls);
        //System.out.println(dc.xmlElementsObject(root.getRootElement(), returnValue));
        String str = "MP0001ALLINPAY20150413150430511011000000512800000004309610148168003";
        System.out.println(MD5Util.MD5(str));
    }
}
