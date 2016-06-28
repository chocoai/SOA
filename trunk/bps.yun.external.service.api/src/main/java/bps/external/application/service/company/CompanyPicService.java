package bps.external.application.service.company;

import java.util.List;
import java.util.Map;

public interface CompanyPicService {
	public Map<Long, Map<String, String>> getCompanyPic(List<Map<String, Object>> appCompanyList) throws Exception;
}
