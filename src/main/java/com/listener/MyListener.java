package com.listener;

import com.model.tool.Quartz;
import com.service.tool.QuartzService;
import com.util.QuartzUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

//@Component
public class MyListener implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger log = Logger.getLogger(MyListener.class);
	
	//定时器服务层
	@Autowired
	private QuartzService quarzService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent cre) {
			log.info(".......................初始化任务调度.......................");
			ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
			Quartz qua = new Quartz();
			qua.setState("0");//运行中的任务
			List<Quartz> list = new ArrayList<Quartz>();
			try {
				list = quarzService.getAllList(qua);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//逐个启动各个任务
			for (Quartz quarz : list) {
				try {
					QuartzUtils.schedStart(quarz, servletContext);
				} catch (SchedulerException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
	}

}
