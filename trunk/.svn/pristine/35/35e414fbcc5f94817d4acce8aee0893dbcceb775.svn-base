package bps.goods;

import org.directwebremoting.util.Logger;

public class GoodsFactory {
	private static Logger logger = Logger.getLogger(GoodsFactory.class);
	
	public static Goods GetGoods(Long goodsType) throws Exception{
		try{
			//标的
			if(goodsType == 1){
				return new Subject();
			}else{
				throw new Exception("商品类型不正确");
			}
		}catch(Exception e){
			 logger.error(e.getMessage(), e);
			 throw e;
		}
		
	}
}
