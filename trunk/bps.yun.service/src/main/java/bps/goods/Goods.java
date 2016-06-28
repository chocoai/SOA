package bps.goods;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;

import bps.common.BizException;
import bps.common.Util;


public class Goods {
	 public Map<String, Object> enter(Long applicationId, Long borrowerId, Long goodsType, String bizGoodsNo, String goodsName, String goodsDescription, String goodsParams, String showUrl, String extendInfo) throws BizException{
		 return null;
	 }
	 
	 public Map<String,Object> getGoods(Session session, Long applicationId, String bizSubjectNo)throws Exception{
		 return null;
	 }
	 
	 public void modifyGoods(Session session,Map<String,Object>param)throws Exception{
		 
	 }
	 //产生商品编号
	 public static String generateGoodsNo(Long goodsId) throws Exception{
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			String str_date = sdf.format(new Date());
			
			String number  = "";
			String goodsIdStr = goodsId.toString();
			for(int i=0; i<10-goodsIdStr.length(); i++){
				number += Util.getRandom();
			}
			String goodsNo = str_date + number + goodsIdStr;
			return goodsNo;
		}
}
