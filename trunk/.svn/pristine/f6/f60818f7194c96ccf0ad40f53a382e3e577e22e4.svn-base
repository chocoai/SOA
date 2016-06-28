package bps.external.application.increment.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import bps.external.application.Extension;


public class SendMail extends Thread {
	private static Logger logger = Logger.getLogger(SendMail.class.getName());
	
	private String _to_user;
	
	private String _subject;
	
	private String _content;
	
	public SendMail(String to_user, String subject, String content){
		_to_user = to_user;
		_subject = subject;
		_content = content;
	}
	
	public void run() {
		
		String serverHost = Extension.paramMap.get("mail.server.host");
		String serverPort = Extension.paramMap.get("mail.server.port");
		String fromAddres = Extension.paramMap.get("mail.from.address");
		String password = Extension.paramMap.get("mail.from.password");
		String userName = Extension.paramMap.get("mail.from.userName");
/*	    String serverHost = "smtp.qq.com";
        String serverPort = "25";
        String fromAddres = "419328324@qq.com";
        String password = "";
        String userName = "";*/
		
		MailSenderInfo mailInfo = new MailSenderInfo();    
		mailInfo.setMailServerHost(serverHost);    
		mailInfo.setMailServerPort(serverPort);    
		mailInfo.setValidate(true);    
		mailInfo.setUserName(userName);    
		mailInfo.setPassword(password);//您的邮箱密码    
		mailInfo.setFromAddress(fromAddres);    
		mailInfo.setToAddress(_to_user);   
		try
        {
            mailInfo.setSubject(MimeUtility.encodeText(_subject, "UTF-8", "B"));
        }
        catch (UnsupportedEncodingException e)
        {
            mailInfo.setSubject(_subject);
        }    
		mailInfo.setContent(_content);    
		logger.info("serverHost:"+serverHost+"---serverPort:"+serverPort+"---fromAddres:"+fromAddres+"---userName:"+userName);
		if(!SimpleMailSender.sendHtmlMail(mailInfo))
			logger.info("发送邮件失败" + _to_user + " 主题:" + _subject);
		else
			logger.info("邮件发送成功");
	}
}