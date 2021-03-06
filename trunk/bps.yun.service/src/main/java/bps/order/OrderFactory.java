package bps.order;

import bps.common.Constant;
import bps.order.orderWithoutConfirm.DepositOrderWithoutConfirm;
import bps.order.orderWithoutConfirm.ShoppingOrderWithoutConfirm;
import bps.order.orderWithoutConfirm.WithdrawOrderWithoutConfirm;

/**
 * Order类对象工厂
 * @author Administrator
 *
 */
public class OrderFactory {
	
	public static Order getOrder(Long trade_type, Long sub_trade_type) throws Exception{
		Order order = null;
		if (Constant.TRADE_TYPE_DEPOSIT.equals(trade_type)&& Constant.SUB_TRADE_TYPE_DEPOSIT_WITHOUT_CONFIRM.equals(sub_trade_type)) {
			order = new DepositOrderWithoutConfirm();//无验证充值订单
		} else if(Constant.TRADE_TYPE_DEPOSIT.equals(trade_type)) {
			order = new DepositOrder();//充值订单
		} else if(Constant.TRADE_TYPE_WITHDRAW.equals(trade_type) && Constant.SUB_TRADE_TYPE_WITHDRAW_WITHOUT_CONFIRM.equals(sub_trade_type)){
			order = new WithdrawOrderWithoutConfirm();//无验证提现订单
		} else if(Constant.TRADE_TYPE_WITHDRAW.equals(trade_type)) {
			order = new WithdrawOrder();//提现订单
		}else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_SHOPPING_WITHOUT_CONFIRM.equals(sub_trade_type)) {
			order = new ShoppingOrderWithoutConfirm();//无验证消费
		} else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_SHOPPING.equals(sub_trade_type)) {
			order = new ShoppingOrder();//购物
		} else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_DAISHOU.equals(sub_trade_type)) {
			order = new DaiShouOrder();//代收
		} else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_DAIFU.equals(sub_trade_type)) {
			order = new DaiFuOrder(); //代付
		} else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_BATCH_DAIFU.equals(sub_trade_type)){
			order = new BatchDaiFuOrder(); //批量代付
		} else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_CROSS_APP.equals(sub_trade_type)){
			order = new AppCrossOrder();
		}else if(Constant.TRADE_TYPE_TRANSFER.equals(trade_type) && Constant.SUB_TRADE_TYPE_APPLICATION_TRANSFER.equals(sub_trade_type)){
			order = new ApplicationTransferOrder();  //平台转账
		} else if(Constant.TRADE_TYPE_REFUNDMENT.equals(trade_type) && Constant.SUB_TRADE_TYPE_BATCH_REFUND.equals(sub_trade_type)){
			order = new BatchRefundOrder(); //批量退款
		} else if(Constant.TRADE_TYPE_REFUNDMENT.equals(trade_type)) {
			order = new RefundOrderNew();
		} else {
			throw new Exception("该订单不存在");
		}
		return order;
	}
}
