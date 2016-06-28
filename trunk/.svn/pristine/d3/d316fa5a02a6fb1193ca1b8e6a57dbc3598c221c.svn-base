package qs.spring.controller;

import ime.security.entity.Principal;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apf.util.EntityManagerUtil;
import apf.work.QueryWork;
import apf.work.TransactionWork;

/**
 * 
 * @author hongh
 *
 */
@Controller
public class HomeController {
	
	@RequestMapping("/spring")
	public String index(Model m) throws Exception {
		Object ret = EntityManagerUtil.execute(new QueryWork<Object>() {
			@Override
			public Object doQuery(final Session session) throws Exception {
				Query query = session.createQuery("from Principal");
	    		
	    		@SuppressWarnings("rawtypes")
				List list = query.list();
				
	    		if (list.size() > 0) {
	    			return list.get(0);
	    		}
				return null;
			}
		});
		m.addAttribute("user", ret);
		
		m.addAttribute("name", "Spring");
		m.addAttribute("value", 1000);
		return "index";
	}
	@RequestMapping("/test")
	public String test(@RequestParam final boolean doWork, Model m) throws Exception {
		Object ret = EntityManagerUtil.execute(new TransactionWork<Principal>(){
			private Principal user = null;
			@Override
			public boolean before(final Session session) throws Exception {
				Query query = session.createQuery("from Principal");
	    		
	    		@SuppressWarnings("rawtypes")
				List list = query.list();
				
	    		if (list.size() > 0) {
	    			user = (Principal) list.get(0);
	    		}
	    		if (!doWork || user == null) {
	    			return false;
	    		}
	    		return true;
			}
			@Override
			public Principal doTransaction(Session session, Transaction tx) throws Exception{
				user.setNote(Long.valueOf(System.currentTimeMillis()).toString());
				session.merge(user);
				return user;
			}
		});
		m.addAttribute("user", ret);
		
		m.addAttribute("name", "Spring");
		m.addAttribute("value", 1000);
		return "index";
	}
}