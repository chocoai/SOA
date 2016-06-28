package bps.triger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import bps.member.MemberServiceImpl;
import bps.process.Extension;
import ime.calendar.TrigerHandler;

public class CompanyPicDownloadTask implements TrigerHandler {
	private static Logger logger = Logger.getLogger(CompanyPicDownloadTask.class);
	private static List<String> picNameList = new ArrayList<String>();
	private static List<String> picNameAllList = new ArrayList<String>();
	
	static{
		
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

	@Override
	public void handle() {
		try{
			logger.info("开始下载企业认证图片。");

			//获取未审核成功的应用和应用下的企业用户
			MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
			List<Map<String, Object>> appCompanyList = memberServiceImpl.getAppCompany();
			logger.info("appCompanyList:" + appCompanyList);

//			Map<Long, Map<String, String>> companyPicInfo = Extension.companyPicService.getCompanyPic(appCompanyList);
//
//			logger.info("companyPicInfo:" + companyPicInfo);
//			//写入数据
//			memberServiceImpl.recordCompanyPic(companyPicInfo);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
}
