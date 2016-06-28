/*
 * Created on 2005-5-23
 *
 */
package bps.common;

/**
 * @author honghao
 *
 */
public class BizException extends Exception {
	private static final long serialVersionUID = 3162074173226275651L;
	private String errorCode;
	
	public BizException(String errcode, String message){
		super(message);
		this.errorCode = errcode;
	}
	
	/**
	 * @return Returns the errcode.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	public String getFullMessage(){
	    String strMessage;
	    strMessage = errorCode + ":" + super.getMessage();
	    return strMessage;
	}
}
