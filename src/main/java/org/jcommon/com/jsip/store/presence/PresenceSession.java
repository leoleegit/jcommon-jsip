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
package org.jcommon.com.jsip.store.presence;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

import org.apache.log4j.Logger;



/**
 * 
 * @author leoLee
 *
 */
public class PresenceSession extends TimerTask implements SipSession{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(this.getClass());  
	
	private Set<SipSession> _subscriptions = new HashSet<SipSession>();
	private SipSession session;

    private Timer timer = new Timer();
    
    public PresenceSession(Set<SipSession> _subscriptions,
    		SipSession session,
    		long expiry){
    	this._subscriptions = _subscriptions;
    	this.session        = session;
    	timer = new Timer();
		timer.schedule(this, (expiry * 1000));
    }
    
    public void removeSession(){
    	run();
    }
    
	
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return session.getCreationTime();
	}

	
	public String getId() {
		// TODO Auto-generated method stub
		return session.getId();
	}

	
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return session.getLastAccessedTime();
	}

	
	public void invalidate() {
		// TODO Auto-generated method stub
		session.invalidate();
	}

	
	public SipApplicationSession getApplicationSession() {
		// TODO Auto-generated method stub
		return session.getApplicationSession();
	}

	
	public String getCallId() {
		// TODO Auto-generated method stub
		return session.getCallId();
	}

	
	public Address getLocalParty() {
		// TODO Auto-generated method stub
		return session.getLocalParty();
	}

	
	public Address getRemoteParty() {
		// TODO Auto-generated method stub
		return session.getRemoteParty();
	}

	
	public State getState() {
		// TODO Auto-generated method stub
		return session.getState();
	}

	
	public SipServletRequest createRequest(String method) {
		// TODO Auto-generated method stub
		return session.createRequest(method);
	}

	
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return session.getAttribute(name);
	}

	
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return session.getAttributeNames();
	}

	
	public URI getSubscriberURI() throws IllegalStateException {
		// TODO Auto-generated method stub
		return session.getSubscriberURI();
	}

	
	public void setHandler(String name) throws ServletException {
		// TODO Auto-generated method stub
		session.setHandler(name);
	}

	
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		session.setAttribute(name, value);
	}

	
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		session.removeAttribute(name);
	}

	
	public boolean isValid() {
		// TODO Auto-generated method stub
		return session.isValid();
	}

	
	public void run() {
		// TODO Auto-generated method stub
		_subscriptions.remove(this);
		logger.debug("remove session :"+this);
		timer.cancel();
	}

	
	public boolean getInvalidateWhenReady() {
		// TODO Auto-generated method stub
		return session.getInvalidateWhenReady();
	}

	
	public SipApplicationRoutingRegion getRegion() {
		// TODO Auto-generated method stub
		return session.getRegion();
	}

	
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return session.getServletContext();
	}

	
	public boolean isReadyToInvalidate() {
		// TODO Auto-generated method stub
		return session.isReadyToInvalidate();
	}

	
	public void setInvalidateWhenReady(boolean arg0) {
		// TODO Auto-generated method stub
		session.setInvalidateWhenReady(arg0);
	}

	
	public void setOutboundInterface(InetAddress arg0) {
		// TODO Auto-generated method stub
		session.setOutboundInterface(arg0);
	}

	
	public void setOutboundInterface(InetSocketAddress arg0) {
		// TODO Auto-generated method stub
		session.setOutboundInterface(arg0);
	}	
}
