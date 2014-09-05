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
package org.jcommon.com.jsip.callflow;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.utils.SipMethods;

public class CallflowRequest implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());   
	private Vector<Object> listeners;
	private SipServletRequest req;
	private SipServletResponse resp;
	private SipMethods method;
	
	public CallflowRequest(Vector<Object> listeners,
			SipServletRequest req,
			SipServletResponse resp,
			SipMethods method){
		this.listeners = listeners;
		this.req       = req;
		this.resp      = resp;
		this.method    = method;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		if(listeners==null)return;
		synchronized(listeners){
			for(Object o : listeners){
				CallflowListener cListener = (CallflowListener) o;
				try {
					switch(method){
						case REGISTER:cListener.doRegister(req);	
							break;
						case INVITE:cListener.doInvite(req);	
							break;
						case BYE:cListener.doBye(req);	
							break;
						case ACK:cListener.doAck(req);	
							break;
						case CANCEL:cListener.doCancel(req);	
							break;
						case MESSAGE:cListener.doMessage(req);	
							break;
						case SUBSCRIBE:cListener.doSubscribe(req);	
							break;
						case NOTIFY:cListener.doNotify(req);	
							break;
						case PUBLISH:cListener.doPublish(req);	
							break;
						case REFER:cListener.doRefer(req);	
							break;
						case RESPONSE:cListener.doResponse(resp);	
							break;
						case REQUEST:cListener.doRequest(req);	
							break;
					}
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					logger.error("", e); 
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("", e); 
					continue;
				}
			}
		}
	}

}
