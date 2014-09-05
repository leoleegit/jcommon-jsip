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
package org.jcommon.com.jsip;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import org.apache.log4j.Logger;
import org.jcommon.com.jsip.services.LocalServices;
import org.jcommon.com.jsip.utils.PresenceType;
import org.jcommon.com.jsip.utils.ProxyUtils;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.jsip.utils.SipUtil;

/**
 * 
 * @author leoLee
 *
 */
public class SipClient{
	private Logger logger = Logger.getLogger(this.getClass()); 
	
	private String displayName;
	
	private String sipUser;
	
	private SipURI fromURI;
	
	private SipFactory factory;
	
	private String contact;
	
	private Map<String, String> status = new HashMap<String, String>();
	private Map<String, String> presences = new HashMap<String, String>();
	
    /** Expires time (in seconds). */
    public int expires = 3600;
    
    private Task  task;
    
    private Timer timer;
    
    private int id;
    
    private java.util.Vector<Integer> NTypes = new java.util.Vector<Integer>();
    
    private String presence = PresenceType.unavailable.toString();
    private String personalMessage = "personalMessage";
    
	public SipClient(String displayName, String sipUser, SipFactory factory){
		this.displayName = displayName;
		this.sipUser = sipUser;
		this.factory = factory;	
		init();
	}
	
	public void init(){
		fromURI = factory
			.createSipURI(SipUtil.getUsername(sipUser), SipUtil.getDomain(sipUser));
		if(displayName!=null)
			fromURI.setUserParam(displayName);
//		contact = "\""+SipUtil.getUsername(sipUser)  + 
//			"\"<sip:"+SipUtil.getUsername(sipUser)+"@" +
//			IpAddress.getLocalHostAddress().toString() +
//			";transport=udp>";
		contact = SipUtil.getContact(fromURI.toString());
	}
	
	boolean isRegister;
	public void loopRegister()throws ServletParseException, IOException{
		if(isRegister){
			logger.info("client "+sipUser + " keep alive");
			
			this.task   = new Task();
			try{if(timer!=null)
				timer.cancel();}catch(Exception e){}
			timer = new Timer(sipUser);
			timer.schedule(task, (1000*expires)/2, (1000*expires)/2);
		}
		else{
			if(time>0)
				logger.info("client "+sipUser + " register again");
			this.task   = new Task();
			try{if(timer!=null)
				timer.cancel();}catch(Exception e){}
			timer = new Timer(sipUser);
			timer.schedule(task, 3*1000);
		}
	}
	
	private void registerServer() throws ServletParseException, IOException{
		SipServletRequest req = factory.createRequest(factory.createApplicationSession(), 
				SipMethods.REGISTER.toString(), fromURI, fromURI);
		
		try {
			req.setHeader("Contact", contact + ";expires=3600");
			//ProviderServices.instance(NewsClient.class).service(SipMethods.REGISTER, req, null);
			ProxyUtils.sendToSipServer(factory, req);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
		}
		
		logger.info("client "+sipUser + " send register request");
	}
	
	public void unregisterServer() throws ServletParseException, IOException{
		SipServletRequest req = factory.createRequest(factory.createApplicationSession(), 
				SipMethods.REGISTER.toString(), fromURI, fromURI);
		
		req.setHeader("Contact", contact + ";expires=0");
		try {
			ProxyUtils.sendToSipServer(factory, req);
			//ProviderServices.instance(NewsClient.class).service(SipMethods.REGISTER, req, null);		
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
		}
		if(timer!=null)
			timer.cancel();
		logger.info("client "+sipUser + " send unregister request");
	}
	
	public void subscribe(){
	    logger.info(sipUser+ " subscribe");
		try {
			String from = SipUtil.cleanURI(fromURI).toString();
			for(String to : status.keySet()){
				SipServletRequest subscribe = RequestFactory.instance().createSubscribeRequest(factory, from, to, (expires)/2);
				if(subscribe == null){
					logger.error("create subscribeRequest Failure to "+to);
					continue;
				}
				ProxyUtils.sendToSipServer(factory, subscribe);
			}
			
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void publish(){
		try {
			String from = SipUtil.cleanURI(fromURI).toString();
			SipServletRequest publish = RequestFactory.instance().createPublishRequest(factory, from, presence, personalMessage);
			if(publish == null){ 
				logger.error("create publishRequest Failure");
				return;
			}
			ProxyUtils.sendToSipServer(factory,publish);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean setPresence(String presence,
			String personalMessage){
		if(this.presence.equals(presence))return false;
		this.presence = presence;
		this.personalMessage = personalMessage;
		return true;
	}
	
	public void registerLocal(SipServlet sipServlet){
		LocalServices.instance().registerLocalClient(
				this, sipServlet);
	}

	int time;
	public void doResponse(SipServletResponse resp) throws ServletException,IOException {
		logger.info("SipccSipClient "+sipUser+" response:\n"+resp.toString()+"\n\n");
		String method = resp.getMethod();
    	int code      = resp.getStatus();
    	
		if(SipMethods.REGISTER.toString().equals(method)){
			if(code==SipServletResponse.SC_OK){
				isRegister = true;
				loopRegister();
			}else{
				time++;
				if(time<3)
					loopRegister();
				else
					logger.info("SipccSipClient "+sipUser+" register timeout.");
			}			
		}else
			logger.info("SipccSipClient "+sipUser+" method "+method);
	}
	
	public void doErrorResponse(SipServletResponse resp)throws ServletException, IOException {
		logger.info("SipccSipClient "+sipUser+" ErrorResponse:\n"+resp.toString()+"\n\n");
	}
	
	class Task extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				registerServer();
			} catch (ServletParseException e) {
				// TODO Auto-generated catch block
				logger.error("SipccSipClient{"+sipUser+"} send register request exception:", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("SipccSipClient{"+sipUser+"} send register request exception:", e);
			}
		}
	}
	public boolean updateStatus(String key, String value){
        if(status.containsKey(key) && value.equals(status.get(key))){
        	return false;
        }

		status.put(key, value);
		return true;
	}
	
	public boolean updatePresence(String key, String value){
		if(presences.containsKey(key) && value.equals(presences.get(key))){
        	return false;
        }

		presences.put(key, value);
		return true;
	}
	
	public String getPresence(String key){
		if(presences.containsKey(key))
			return presences.get(key);
		return null;
	}
	
	public Map<String, String> getStatus(){
		return status;
	}
	
	public String getStatus(String key){
		if(key==null)return null;
		if(status.containsKey(key))
			return status.get(key);
		return null;
	}
	
	public String getKey(){
		return SipUtil.cleanURI(fromURI).toString();
	}
	
	public void addNType(int NType){
		if(!NTypes.contains(NType)){
			NTypes.add(NType);
		}
	}
	
	public java.util.Vector<Integer> getNTypes(){
		return NTypes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
