package bps.common;

import java.io.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.xml.XMLSerializer;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.HashMap;
import java.util.Map;

/**
 * JDOM 方式操作XML
 * 
 * @author Watson Xu
 * @date 2011-5-3 下午02:20:49
 */
public class ReadXML {
	
	/**
	 * DOCUMENT格式化输出保存为XML
	 * 
	 * @param doc JDOM的Document
	 * @param filePath 输出文件路径
	 * @throws Exception
	 */
	public static void doc2XML(Document doc, String filePath) throws Exception{
		Format format = Format.getCompactFormat(); 
		format.setEncoding("UTF-8"); //设置XML文件的字符为UTF-8
		format.setIndent("     ");//设置缩进 
	
        XMLOutputter outputter = new XMLOutputter(format);//定义输出 ,在元素后换行，每一层元素缩排四格 
        FileWriter writer = new FileWriter(filePath);//输出流
        outputter.output(doc, writer);
        writer.close();
	}
	
	/**
	 * 字符串转换为DOCUMENT
	 * 
	 * @param xmlStr 字符串
	 * @return doc JDOM的Document
	 * @throws Exception
	 */
	public static Document string2Doc(String xmlStr) throws Exception {
		java.io.Reader in = new StringReader(xmlStr);
		Document doc = (new SAXBuilder()).build(in);       
        return doc;
	}

	/**
	 * Document转换为字符串
	 * 
	 * @param xmlFilePath XML文件路径
	 * @return xmlStr 字符串
	 * @throws Exception
	 */
	public static String doc2String(Document doc) throws Exception {
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");// 设置xml文件的字符为UTF-8，解决中文问题
		XMLOutputter xmlout = new XMLOutputter(format);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		xmlout.output(doc, bo);
		return bo.toString();
	}

	/**
	 * XML转换为Document
	 * 
	 * @param xmlFilePath XML文件路径
	 * @return doc Document对象
	 * @throws Exception
	 */
	public static Document xml2Doc(String xmlFilePath) throws Exception {
		File file = new File(xmlFilePath);
		return (new SAXBuilder()).build(file);
	}
	
	
	public static Map<String, String> getParamMap() {
		String xmls="";
		try{
			Document doc = xml2Doc(ReadXML.class.getClassLoader().getResource("/").getPath() + "params.xml");
//			System.out.println(doc);
			xmls=doc2String(doc);
//			doc = string2Doc(doc2String(doc));
//			doc2XML(doc, "1.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSON json=new XMLSerializer().read(xmls);
		
		net.sf.json.JSONObject jsonObj=net.sf.json.JSONObject.fromObject(json);
		JSONArray jsonAry=JSONArray.fromObject(jsonObj.get("properties")) ;
		Map<String, String> paramMap=new HashMap<String, String>();
		for (Object elem : jsonAry) {
			String key=net.sf.json.JSONObject.fromObject(elem).get("@name").toString();
			String value=net.sf.json.JSONObject.fromObject(elem).get("@value").toString();
			paramMap.put(key, value);
		}
		return paramMap;
	}
	
	public static void main(String[] args) {
		
		System.out.println(getParamMap());
		
	}
}