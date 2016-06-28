package bps.external.application.service.trade;

import java.util.Map;

public interface UserManageService {
	/**
	 * 保存协议
	 * @param param
	 */
	public Map saveContract(Map<String, String> param) throws Exception;
	
	/**
	 * 重复签约
	 * @param param
	 */
	public Map repeatContract(Map<String, String> param) throws Exception;
	
	/**
     * 解除签约
     * @param param
     */
    public void removeContract(Map<String, String> param) throws Exception;
	
    /**
     * 查询签约
     * @param param
     */
    public Map selectContract(Map<String, String> param) throws Exception;
}
