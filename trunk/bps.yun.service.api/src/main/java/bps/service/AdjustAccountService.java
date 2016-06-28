package bps.service;

import bps.common.BizException;

public interface AdjustAccountService {
	public void adjustAccount(String bizNo, String sysid, Long sourceMemberId, Long sourceAccountTypeId, Long targetMemberId, Long targetAccountTypeId, Long amount, String remark) throws BizException;
}
