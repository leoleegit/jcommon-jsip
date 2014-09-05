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
package org.jcommon.com.jsip.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.MainSipServlet;
import org.jcommon.com.jsip.services.ProviderServices;
import org.jcommon.com.jsip.store.presence.PresenceInfo;
import org.jcommon.com.jsip.store.presence.PresenceStore;
import org.jcommon.com.jsip.utils.ProxyUtils;
import org.jcommon.com.jsip.utils.SipMethods;



/**
 * @author leoLee
 * Servlet implementation class PresenceServlet
 */
public class PresenceServlet extends SipServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());  
       
	private SipFactory factory;
	
	@SuppressWarnings("rawtypes")
	private static final Class fatherServlet = MainSipServlet.class;
    /**
     * @see SipServlet#SipServlet()
     */
    public PresenceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		factory = (SipFactory) getServletContext().getAttribute(SipServlet.SIP_FACTORY);
		 
		logger.info("PresenceServlet init....");
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.SUBSCRIBE, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.PUBLISH, this);
	}
	/*
	 * Saves the dialog in the helper object PresenceInfo based on TO header
	 */

	protected void doSubscribe(SipServletRequest req) throws ServletException,
			IOException {
		if(ProxyUtils.proxyToPersenceServer(factory, req))
			return;
		SipURI to = (SipURI)req.getTo().getURI();
		// ID for the hashmap is based on user@host
		StringBuilder id = new StringBuilder(to.getUser());
		id.append('@');
		id.append(to.getHost());
		String key = id.toString();
		
        synchronized( key ) {
	         PresenceInfo pi = (PresenceInfo)PresenceStore.instance().getPresenceInfo(key);
			
	         String expires = req.getHeader("Expires");
	         int exp = 3600;
	         if( expires != null && expires.length() > 0 ) {
	            exp = Integer.parseInt( expires );
	         }
	      
	         SipServletResponse resp = req.createResponse(200);
	         resp.setHeader("Expires",expires);
	         resp.send();
			
	         SipSession session = resp.getSession();
			
	         String event = req.getHeader("Event");
	         if( pi != null && pi.getLastPublish() != null ) {
	            if( event.equals("presence") ) {
	               pi.removeSipSession(session);
	               if( exp > 0 ) pi.addSipSession(session, exp);
	               else System.out.println("Removed SESSION = "+session);
	            }
	         }
	         else {
	            pi = new PresenceInfo(key,session, exp);
	         }
	         //Store the accept headers
	         ListIterator<String> li = req.getHeaders("Accept");
	         while ( li.hasNext() ) {
	            pi.getAccept().add(li.next().toString());
	         }
	      
	         pi.setInStorage();
			
	         //Send notify
	         SipServletRequest notify = session.createRequest("NOTIFY");
	         if( exp > 0 )
	         {
	            notify.setHeader("Subscription-State","active;expires="+expires);
	         }
	         else {
	            notify.setHeader("Subscription-State","terminated;deactivated");
	         }
	         notify.setHeader("Event",req.getHeader("Event"));
	         notify.setExpires(exp);
	         Object content = pi.getLastPublish();
	         String type = pi.getContentType();
	         if( event.equalsIgnoreCase("presence.winfo")) {
	            StringBuilder body = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><watcherinfo xmlns=\"urn:ietf:params:xml:ns:watcherinfo\" version=\"2\" state=\"partial\"><watcher-list resource=\"");
	            body.append(pi.getId());
	            body.append("\" package=\"presence\"></watcher-list></watcherinfo>");
	            notify.setContent( body.toString().getBytes(), "application/watcherinfo+xml" );
	         }
	         else if( content != null && type != null ) {
	            //Check Accept headers
	            if( pi.getAccept().isEmpty() || pi.getAccept().contains(type) ) {
	               notify.setContent( content, type );
	            }
	         }
	         sendNotify(notify);
        }
	}
	
	/*
	 * Create a PresenceInfo to store the last publish state
	 * After the 200 all subscriber should be notified. 
	 */
	
	protected void doPublish(SipServletRequest req) throws ServletException,
		IOException {
		if(ProxyUtils.proxyToPersenceServer(factory, req))
			return;
		
		SipURI to = (SipURI)req.getTo().getURI();
		byte [] body = req.getRawContent();
		StringBuilder id = new StringBuilder(to.getUser());
		id.append('@');
		id.append(to.getHost());
		String key = id.toString();
		String expires = req.getHeader("Expires");
		boolean expired = false;
        if( expires != null && expires.equals("0"))
        	expired = true;
        
        synchronized( key ) {
        	PresenceInfo pi = (PresenceInfo)PresenceStore.instance().getPresenceInfo(key);
        	
        	if( pi != null ) {
	            pi.setContentType(req.getContentType());
	             
	            pi.setLastPublish(body,expired);
	            Iterator<SipSession> i = pi.getSipSessions();
	            while( i.hasNext() ) {
	               SipSession s = i.next();
	               if(!s.isValid()){
	            	   logger.error(this.getClass().getName()+ ":Invalid session:"+s);
	            	   continue;
	               }
	            	   
	               // Send notify
	               try{
	            	   SipServletRequest notify = s.createRequest("NOTIFY");
		               //s.getApplicationSession().getTimers()
		               notify.setHeader("Subscription-State","active;expires="+3600);
		               notify.setHeader("Event",req.getHeader("Event"));
		               notify.setContent( pi.getLastPublish(), pi.getContentType() );
		               notify.setExpires(3600);
		               sendNotify(notify);
	               }catch(IllegalStateException e){
	            	   logger.error(this.getClass().getName()+ ":Invalid session:"+s , e);
	            	   pi.addSipSession(s, 0);
	            	   //e.printStackTrace();
	            	   continue;
	               }
	              
	            }
	            pi.setLastPublish( body, expired );
	         }
	         else {
	            pi = new PresenceInfo(key, req.getContentType());
	            pi.setLastPublish( body, expired );
	         }
        	 pi.setInStorage();
	      }
	        
		  if( expires == null ) expires = "3600";
		  SipServletResponse resp = req.createResponse(200);
		  resp.setHeader("Expires",expires);
		  resp.send();
	      SipApplicationSession appsess = req.getApplicationSession(false);
	      if (appsess != null)
	      {
	         appsess.invalidate();
	      }

	}
	
	protected void sendNotify(SipServletRequest notify) throws IOException, ServletException{	
		logger.info("send notify:\n"+notify.toString());
		notify.send();
	}
	
    @SuppressWarnings("rawtypes")
	public static Class getFatherServlet(){return fatherServlet;}
}
