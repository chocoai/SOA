package bps.order.orderWithoutConfirm;


import bps.member.Member;
import bps.order.Order;
import bps.order.OrderServiceImpl;
import bps.order.WithdrawOrder;
import org.apache.log4j.Logger;
import org.hibernate.Session;


import java.util.*;

/**
 * Created by fff on 2016/5/9.
 */
public class WithdrawOrderWithoutConfirm  extends Order{

    private static Logger logger = Logger.getLogger(WithdrawOrderWithoutConfirm.class.getName());
    WithdrawOrder  withdrawOrder = new WithdrawOrder();
    @Override
    public Map<String,Object> applyOrder(Map<String, Object> param) throws Exception {
        try{
            //订单申请
            OrderServiceImpl orderService = new OrderServiceImpl();
            Map<String,Object> applyMap = withdrawOrder.applyOrder(param);

            Long member_id = (Long) param.get("memberId");
            Member member = new Member(member_id);
            String phone = member.getPhone();
            String orderNo = (String)applyMap.get("orderNo");
            //订单确认
            Map<String, Object> payResponseMap = orderService.confirmPay(member_id, orderNo, null, "0.0.0.0", phone, null);
            return payResponseMap;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }

    }
    @Override
    public void completePay(Map<String, Object> order_entity, Session session) throws Exception {
        // TODO Auto-generated method stub
        withdrawOrder.completeOrder(order_entity,null,session);
    }




}
