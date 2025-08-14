package org.irri.iric.portal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class AppContextFilterConfig implements Filter {

	@Override
	public void destroy() {
		
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		try {
			chain.doFilter(req, res);
		} catch (Exception ex) {
			
		}
	}
	
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		
		
		 if( AppContext.configloaded) return;
		
		 AppContext.defaultorganism = config.getInitParameter("defaultorganism");
		 AppContext.flatfilesdir = config.getInitParameter("flatfilesdir");
		 AppContext.hostdirectory = config.getInitParameter("hostdirectory");
		 AppContext.hostname =  config.getInitParameter("hostname");
		 
		 AppContext.pathtolocalblast =  config.getInitParameter("pathtolocalblast");
		 AppContext.pathtolocalblastdata =  config.getInitParameter("pathtolocalblastdata");
		 AppContext.webappdirectory =  config.getInitParameter("webappdirectory");
		 
		 
		 String deployment =  config.getInitParameter("deployment");
		 String deploymenttype =  config.getInitParameter("deploymenttype");
		 

		 if(deployment!=null) {

			 if(deployment.equals("localhost"))
				 AppContext.webserver=AppContext.WEBSERVER.LOCALHOST;
			 else if(deployment.equals("asti"))
				 AppContext.webserver=AppContext.WEBSERVER.ASTI;
			 else if(deployment.equals("aws"))
				 AppContext.webserver=AppContext.WEBSERVER.AWS;
			 else if(deployment.equals("awsdev"))
				 AppContext.webserver=AppContext.WEBSERVER.AWSDEV;
			 else if(deployment.equals("vmirri"))
				 AppContext.webserver=AppContext.WEBSERVER.VMIRRI;
			 else if(deployment.equals("pollux"))
				 AppContext.webserver=AppContext.WEBSERVER.POLLUX;
		 }
		 

		 //AppContext.compiletype = AppContext.COMPILETYPE_PROD;

		if(deploymenttype!=null) {
			 if(deploymenttype.equals("dev"))
				 AppContext.compiletype = AppContext.COMPILETYPE.DEV;
			 else if(deploymenttype.equals("test"))
				 AppContext.compiletype = AppContext.COMPILETYPE.TEST;
		 }
				 
		 AppContext.configloaded = true;
		 AppContext.debug("AppContext.configloaded done..." + deployment + "," + deploymenttype + "," + AppContext.hostname + "," +  AppContext.hostdirectory );
		 
	}

//	@Override
//	public void init(FilterConfig config) throws ServletException {
//		
//		
//		 if( AppContext.configloaded) return;
//		
//		 AppContext.defaultorganism = config.getInitParameter("defaultorganism");
//		 AppContext.flatfilesdir = config.getInitParameter("flatfilesdir");
//		 AppContext.hostdirectory = config.getInitParameter("hostdirectory");
//		 AppContext.hostname =  config.getInitParameter("hostname");
//		 
//		 AppContext.pathtolocalblast =  config.getInitParameter("pathtolocalblast");
//		 AppContext.pathtolocalblastdata =  config.getInitParameter("pathtolocalblastdata");
//		 AppContext.webappdirectory =  config.getInitParameter("webappdirectory");
//		 
//		 
//		 String deployment =  config.getInitParameter("deployment");
//		 String deploymenttype =  config.getInitParameter("deploymenttype");
//		 
//		 
//
//		 
//
//		 if(deployment!=null) {
//		 
//			 AppContext.isASTI = false;
//			 AppContext.isAWS = false;
//			 AppContext.isAWSdev = false;
//			 AppContext.isVMIRRI = false;
//			 AppContext.isPollux = false;
//			 AppContext.isLocalhost = false;
//
//			 if(deployment.equals("asti"))
//			 AppContext.isASTI=true;
//			 else if(deployment.equals("aws"))
//				 AppContext.isAWS=true;
//			 else if(deployment.equals("awsdev"))
//				 AppContext.isAWSdev=true;
//			 else if(deployment.equals("vmirri"))
//				 AppContext.isVMIRRI=true;
//			 else if(deployment.equals("pollux"))
//				 AppContext.isPollux=true;
//			 else if(deployment.equals("localhost"))
//				 AppContext.isLocalhost=true;
//		 }
//		 
//
//
//		if(deploymenttype!=null) {
//			 AppContext.isDev = false;
//			 AppContext.isTest = false;
//			 if(deploymenttype.equals("dev"))
//				 AppContext.isDev=true;
//			 else if(deploymenttype.equals("test"))
//				 AppContext.isTest=true;
//		 }
//		 
//		 /*
//		 AppContext.isASTI = Boolean.valueOf( config.getInitParameter("isASTI") );
//		 AppContext.isAWS = Boolean.valueOf( config.getInitParameter("isAWS") );
//		 AppContext.isAWSdev = Boolean.valueOf( config.getInitParameter("isAWSdev") );
//		 AppContext.isVMIRRI = Boolean.valueOf( config.getInitParameter("isVMIRRI") );
//		 AppContext.isPollux = Boolean.valueOf( config.getInitParameter("isPollux") );
//		 AppContext.isLocalhost = Boolean.valueOf( config.getInitParameter("isLocalhost") );
//		 AppContext.isDev = Boolean.valueOf( config.getInitParameter("isDev") );
//		 AppContext.isTest = Boolean.valueOf( config.getInitParameter("isTest") );
//		 */
//		 
//		 AppContext.configloaded = true;
//		 
//		 AppContext.debug("AppContext.configloaded done..." + deployment + "," + deploymenttype + "," + AppContext.hostname + "," +  AppContext.hostdirectory );
//		 
//		 
//	}

}
