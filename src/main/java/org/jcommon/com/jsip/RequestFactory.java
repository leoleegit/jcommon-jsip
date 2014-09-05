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

import java.io.UnsupportedEncodingException;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.utils.PresenceType;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.jsip.utils.SipUtil;
/**
 * 
 * @author leoLee
 */
public class RequestFactory {
	private Logger logger = Logger.getLogger(this.getClass());  
	
	private static final RequestFactory instance = new RequestFactory();
	public static RequestFactory instance(){return instance;}
	public static SipFactory sipFactory;
	
	public SipServletRequest createMessageRequest(SipFactory factory, String from, String to, String body){
		try {
			SipServletRequest req = factory.createRequest(factory.createApplicationSession(), SipMethods.MESSAGE.toString(),
			        from, to);
			String uri = "<" + to + ">";
            req.addHeader("P-Preferred-Identity", uri);
            req.setExpires(3600);
            req.setMaxForwards(70);
            req.setContent(body.getBytes("utf-8"), "text/plain");
            return req;
		} catch (ServletParseException e) {
			// TODO Auto-generated catch block
			logger.error("create message request ServletParseException:", e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("create message request UnsupportedEncodingException:", e);
			e.printStackTrace();
		}
		return null;
	}
	
	public SipServletRequest createPublishRequest(SipFactory factory, String from, String presence, String personalMessage){
		try {
			SipServletRequest req = factory.createRequest(factory.createApplicationSession(), SipMethods.PUBLISH.toString(),
			        from, from);
			req.setHeader("Event", "presence");
            req.setHeader("Content-Type", "application/pidf+xml");
            req.setContent(getPublishContent(from, presence, personalMessage).getBytes("utf-8"), "application/pidf+xml");
            req.setHeader("Allow", "REFER,INVITE,ACK,BYE,CANCEL,OPTIONS,PRACK,MESSAGE,SUBSCRIBE,NOTIFY,UPDATE,INFO");
            req.setHeader("Allow-Events", "presence, presence.winfo");
            req.setExpires(3600);
            req.setMaxForwards(70);
            return req;
		} catch (ServletParseException e) {
			// TODO Auto-generated catch block
			logger.error("create message request ServletParseException:", e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("create message request UnsupportedEncodingException:", e);
			e.printStackTrace();
		}
		return null;
	}
	
	public SipServletRequest createSubscribeRequest(SipFactory factory, String from, String to){
		return createSubscribeRequest(factory, from, to,3600);
	}
	
	public SipServletRequest createSubscribeRequest(SipFactory factory, String from, String to, int expires){
		try {
			SipServletRequest req = factory.createRequest(factory.createApplicationSession(), SipMethods.SUBSCRIBE.toString(),
			        from, to);
			req.setHeader("Event", "presence");
            req.setHeader("Content-Type", "application/pidf+xml");
            req.setHeader("Allow", "REFER,INVITE,ACK,BYE,CANCEL,OPTIONS,PRACK,MESSAGE,SUBSCRIBE,NOTIFY,UPDATE,INFO");
            req.setHeader("Allow-Events", "presence, presence.winfo");
            req.setExpires(expires);
            return req;
		} catch (ServletParseException e) {
			// TODO Auto-generated catch block
			logger.error("create message request ServletParseException:", e);
			e.printStackTrace();
		} 
		return null;
	}
	
	private String getPublishContent(String from, String presence, String personalMessage){
		String temp = SipUtil.Publish2Type(presence).toString(PresenceType.type);

		String content = "<?xml version='1.0' encoding='UTF-8'?>" +
				"<presence xmlns='urn:ietf:params:xml:ns:pidf' xmlns:dm='urn:ietf:params:xml:ns:pidf:data-model' xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid' xmlns:c='urn:ietf:params:xml:ns:pidf:cipid' entity='"+from+"'>" +
				"<tuple id='"+SipUtil.generateRandom(20)+"'>" +
				"<status><basic>"+(isOffline(presence)?"close":"open")+"</basic></status>" +
				"</tuple>" +
				"<dm:person id='"+SipUtil.generateRandom(20)+"'>" +
				"<rpid:activities xmlns:rpid='urn:ietf:params:xml:ns:pidf:rpid'>"+
				temp +
				"</rpid:activities>"+
				"<dm:note>"+personalMessage+"</dm:note>" +
				"</dm:person>" +
				"</presence>";
		return content;
	}
	
	private boolean isOffline(String presence){
		if((PresenceType.unavailable.toString()).equals(presence))
			return true;
		else
			return false;
	}
	
	public SipServletRequest createReferRequest(SipFactory factory, String from, String to){
		try {
			SipServletRequest req = factory.createRequest(factory.createApplicationSession(), SipMethods.REFER.toString(),
			        from, to);
            return req;
		} catch (ServletParseException e) {
			// TODO Auto-generated catch block
			logger.error("create message request ServletParseException:", e);
			e.printStackTrace();
		} 
		return null;
	}
}
