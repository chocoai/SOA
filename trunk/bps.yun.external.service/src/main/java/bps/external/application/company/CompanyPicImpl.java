package bps.external.application.company;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.kinorsoft.allinpay.interfaces.TranxServiceImpl;

import bps.external.application.Extension;
import bps.external.application.service.company.CompanyPicService;

public class CompanyPicImpl implements CompanyPicService {
//public class CompanyPicImpl {
	private static Logger logger = Logger.getLogger(CompanyPicImpl.class);
	private static FTPClient ftpClient = null;
	private static String dateStr = null;
	private static String url = null;
	private static int port = 0;
	private static String userName = null;
	private static String password = null;
	private static String localRootPath = null;
	private static String remoteRootPath = null;
	private static String picUrlPath = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private static List<String> picNameList = new ArrayList<String>();
	private static List<String> picNameAllList = new ArrayList<String>();
	
	static{
		url = Extension.paramMap.get("company.pic.ftp.url");
		port = Integer.parseInt(Extension.paramMap.get("company.pic.ftp.port"));
		userName = Extension.paramMap.get("company.pic.ftp.username");
		password = Extension.paramMap.get("company.pic.ftp.pwd");
		localRootPath = Extension.paramMap.get("company.pic.local.root.path");
		remoteRootPath = Extension.paramMap.get("company.pic.remote.root.path");
		picUrlPath = Extension.paramMap.get("company.pic.url.path");
		
//		url = "139.196.12.148";
//		port = 21;
//		userName = "ftproot";
//		password = "fadf454e65rq4e5w";
//		remoteRootPath = "/home/yun/companyPic";
//		picUrlPath = "http://139.196.12.148:8090/companyPic";
		
		picNameList.add("business_license");
		picNameList.add("organization_code");
		picNameList.add("tax_certificate");
		picNameList.add("bank_settlement");
		picNameList.add("org_credit_code");
		picNameList.add("icp");
		picNameList.add("industry_permit");
		picNameList.add("legal_cerificate_front");
		picNameList.add("legal_cerificate_back");
		
		picNameAllList.add("business_license.jpg");
		picNameAllList.add("organization_code.jpg");
		picNameAllList.add("tax_certificate.jpg");
		picNameAllList.add("bank_settlement.jpg");
		picNameAllList.add("org_credit_code.jpg");
		picNameAllList.add("icp.jpg");
		picNameAllList.add("industry_permit.jpg");
		picNameAllList.add("legal_cerificate_front.jpg");
		picNameAllList.add("legal_cerificate_back.jpg");
	}
	
//	public static void main(String[] args) throws Exception {
//		try{
//			System.out.println("start");
//			
//			List<Map<String, Object>> appCompanyList = new ArrayList<Map<String, Object>>();
//			
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("MEMBERID", 383L);
//			map.put("SYSID", "100000000002");
//			map.put("BIZ_USER_ID", "zxccy_company1");
//			appCompanyList.add(map);
//			
//			Map<Long, Map<String, String>> ret = getCompanyPic(appCompanyList);
//			System.out.println("ret=" + ret);
//		}catch(Exception e){
//			System.out.println("main exception=" + e.getMessage());
//		}
//	}
	
	@SuppressWarnings("finally")
	public Map<Long, Map<String, String>> getCompanyPic(List<Map<String, Object>> appCompanyList) {
		Map<Long, Map<String, String>> companyPicInfo = new HashMap<Long, Map<String, String>>();
		
		try{
			logger.info("开始下载企业认证图片。");
			
			//获取下载图片
				//获取连接
			ftpConnect(url, port, userName, password);
			
				//遍历下载
			dateStr = sdf.format(new Date());
			for(Map<String, Object> appCompany : appCompanyList){
				Long memberId = ((BigDecimal)appCompany.get("MEMBERID")).longValue();
//				Long memberId = (Long)appCompany.get("MEMBERID");
				String sysid = (String)appCompany.get("SYSID");
				String bizUserId = (String)appCompany.get("BIZ_USER_ID");
				
//				dateStr = "20160106";
				String remotePath = "/" + sysid + "/" + dateStr + "/" + bizUserId;
				String picUrlPathAll = picUrlPath + "/" + sysid + "/" + dateStr + "/" + bizUserId;
				
				Map<String, String> localFilePathMap = ftpDownload(remotePath, picUrlPathAll);

				//保存数据
				if(localFilePathMap != null){
					companyPicInfo.put(memberId, localFilePathMap);
				}
			}
			
			logger.info("companyPicInfo:" + companyPicInfo);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			try{
				ftpClose();
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
			
			return companyPicInfo;
		}
	}
	
	/**
	 * ftp连接
	 * @param url     
	 * @param port
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private void ftpConnect(String url, int port, String userName, String password) throws Exception{
		logger.info("1111ftpConnect参数url=" + url + ",port=" + port + ",userName=" + userName + ",password=" + password);
		
		try{
			ftpClient = new FTPClient();
			ftpClient.connect(url, port);
			ftpClient.login(userName, password);
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			int replyCode = ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)){
				throw new Exception("FTP连接失败。");
			}
			
			logger.info("FTP连接成功。");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			ftpClose();
			throw e;
		}
	}
	
	private Map<String, String> ftpDownload(String remotePath, String picUrlPathAll) throws Exception{
		logger.info("ftpDownload参数：remotePath=" + remotePath  + ",picUrlPathAll=" + picUrlPathAll);
		
		System.out.println("remotePath=" + remotePath  + ",picUrlPathAll=" + picUrlPathAll);
		try{
			Map<String, String> picPathMap = null;

			if(ftpClient.changeWorkingDirectory(remotePath)){
				FTPFile[] fs = ftpClient.listFiles();
				for(FTPFile ff : fs){
					try{
						String fileNameAll = ff.getName();
						logger.info("fileNameAll:" + fileNameAll);
						String fileName = fileNameAll.split("\\.")[0];
						
						//有匹配的文件
						if(picNameList.contains(fileName)){
							if(picPathMap == null){
								picPathMap = new HashMap<String, String>();
							}
							
							String picPathAll = picUrlPathAll + "/" + fileName + ".jpg";
							picPathMap.put(fileName, picPathAll);
						}
					}catch(Exception e){
						logger.error(e.getMessage());
					}
				}
				
				ftpClient.changeWorkingDirectory("../../../");
			}
			
			return picPathMap;
		}catch(Exception e){
			logger.error(e.getMessage());
			System.out.println("ftpDownload exception=" + e.getMessage());
			throw e;
		}
	}
	
	
	private void ftpClose() throws IOException{
		if(ftpClient != null){
			try {
				ftpClient.disconnect();
				
				logger.info("FTP断开连接");
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
	}

}
