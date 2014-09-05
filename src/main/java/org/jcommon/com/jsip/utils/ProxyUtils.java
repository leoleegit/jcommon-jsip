// ========================================================================
// Copyright 2012 leolee<workspaceleo@gmail.com>
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================
package org.jcommon.com.jsip.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.config.SipConfig;


public class ProxyUtils {
	private static Logger logger = Logger.getLogger(ProxyUtils.class);   
	
	private static SocketAddress sipServer;
	private static SocketAddress persenceServer;
	
    public static boolean proxyToSipServer(SipFactory factory, SipServletRequest req) throws IOException, ServletException{
		if(isSipServer(req))
			return false;
		SipURI uriSipURI = (SipURI) factory.createURI("sip:"+sipServer.toString());
		req.getProxy().proxyTo(uriSipURI);
		return true;
	}
    
    public static void sendToSipServer(SipFactory factory, SipServletRequest req) throws IOException, ServletException{
		if(sipServer == null)
			sipServer = SipUtil.CreateServerAddress(SipConfig.instance().getSipServer());
		SipURI uriSipURI = (SipURI) factory.createURI("sip:"+sipServer.toString());		
		uriSipURI.setLrParam(true);
		req.pushRoute(uriSipURI);	
		req.send();
		//logger.info("send a request to "+sipServer.toString() + "\n"+req.toString());
	}
	
    public static SocketAddress getSipAddress(){
		if(sipServer == null)
			sipServer = SipUtil.CreateServerAddress(SipConfig.instance().getSipServer());
		return sipServer;
    }
    
    public static boolean isSipServer(SipServletRequest req){
//    	if(sipServer == null)
//			sipServer = SipUtil.CreateServerAddress(Config.instance().getSipServer());
//		String localHost = IpAddress.getLocalHostAddress().toString();
//		String sServer   = sipServer.getAddress().toString();
//		logger.info("localHost:"+localHost +"\nsipServer:"+sServer);
//		if(localHost.equals(sServer)){
//			return true;
//		}
//		return false;
    	return true;
    }
	
	public static boolean proxyToPersenceServer(SipFactory factory, SipServletRequest req) throws IOException, ServletException{
		if(SipConfig.instance().getPersenceServer()==null)return false;
		if(isLocalHost(SipConfig.instance().getPersenceServer()))return false;
		if(persenceServer == null)
			persenceServer = SipUtil.CreateServerAddress(SipConfig.instance().getPersenceServer());
		String localHost = IpAddress.getLocalHostAddress().toString();
		String pServer   = persenceServer.getAddress().toString();
		logger.info("localHost:"+localHost +"\npServer:"+pServer);
		if(localHost.equals(pServer)){
			return false;
		}
		SipURI uriSipURI = (SipURI) factory.createURI("sip:"+persenceServer.toString());
		req.getProxy().proxyTo(uriSipURI);
		return true;
	}
	
	private static boolean isLocalHost(String host){
		return "127.0.0.1".equals(host) || "localhost".equalsIgnoreCase(host);
	}
}
