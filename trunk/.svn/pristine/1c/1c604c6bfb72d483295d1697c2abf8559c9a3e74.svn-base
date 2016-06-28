package bps.check.limit;

import bps.common.BizException;
import bps.common.Constant;
import bps.common.ErrorCode;
import bps.member.Member;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.formula.functions.Error;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 * 检查账户
 */
public class AccountCheck {
    private static Logger logger = Logger.getLogger(AccountCheck.class);

    /**
     * 判断账户集类型是否和应用匹配
     * @param userId
     * @param accountTypeId
     * @param session
     * @throws Exception
     */
    public static void isAccountTypeBelongApp(String userId, Long accountTypeId, Session session) throws Exception {
        logger.info("isAccountTypeBelongApp 参数：userId = " + userId + ",accountTypeId=" + accountTypeId);

        try{
            Member member = new Member(userId);
            Long memberType = member.getMember_type();
            //如果是系统会员，则不检查
            //因为系统会员没有设置应用
            if(Constant.MEMBER_TYPE_SYSTEM == memberType){
                return;
            }
            Long applicationId = member.getApplicationId();

//            String sqlStr = "from AMS_AccountType where codeNo=:codeNo and application_id=:applicationId";
//            Query query = session.createQuery(sqlStr);
//            query.setParameter("codeNo", accountSetNo);
//            query.setParameter("applicationId", applicationId);
//            List accountTypeList = query.list();
//            if(accountTypeList == null || accountTypeList.size() == 0){
//                throw new Exception("账户集错误。");
//            }
            
            String sqlStr = "from AMS_AccountType where id=:id and application_id=:applicationId";
            Query query = session.createQuery(sqlStr);
            query.setParameter("id", accountTypeId);
            query.setParameter("applicationId", applicationId);
            List accountTypeList = query.list();
            if(accountTypeList == null || accountTypeList.size() == 0){
                throw new Exception("账户集错误。");
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

}
