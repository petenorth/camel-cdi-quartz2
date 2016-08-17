package org.wildfly.camel.examples.quartz2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.inject.Produces;

import org.apache.camel.component.quartz2.QuartzComponent;
import org.quartz.SchedulerException;

public class Factory {
	
//	@Produces
//	public QuartzComponent createQuartzComponent() throws SchedulerException{
//		
//		QuartzComponent quartzComponent = new QuartzComponent();
//		
//		quartzComponent.setAutoStartScheduler(true);
//		Properties props = new Properties();
//		try (InputStream in = new FileInputStream(System.getProperty("quartz.properties"))){
//		  props.load(in);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		} 
//		quartzComponent.setProperties(props);
//		return quartzComponent;
//	}

}
