package com.eclat.mcws.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MCWSServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("MCWS ServletContextListener started");	
		
		System.setProperty("-Dcom.sun.management.jmxremote", "true");
		System.setProperty("-Djava.rmi.server.hostname", "54.166.64.175");
		System.setProperty("-Dcom.sun.management.jmxremote.port", "8999");
		System.setProperty("-Dcom.sun.management.jmxremote.ssl", "false");
		System.setProperty("-Dcom.sun.management.jmxremote.authenticate", "false");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("MCWS ServletContextListener destroyed");;
	}

}
