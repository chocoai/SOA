package qs.spring.controller;

import java.io.IOException;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent>{
	
	
	public void onApplicationEvent(ContextRefreshedEvent event) {
	    if (event.getApplicationContext().getParent() == null) {
			//????dubbo????
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] { "dubbo-provider.xml" });
			context.start();
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			} 
        }
	}
	
	public static void main(String[] args) {
		//????dubbo????
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
		new String[] { "dubbo-provider.xml" });
		context.start();
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
