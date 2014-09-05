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

import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.util.thread.ThreadManager;

public class CallflowManager extends org.jcommon.com.util.collections.CollectionStore implements CallflowListener{
	private static CallflowManager instance = new CallflowManager();
	
	public static CallflowManager instance(){return instance;}
	
	public boolean addCallflowListener(CallflowListener listener){
		return super.addOne(listener);
	}
	
	public boolean removeCallflowListener(CallflowListener listener){
		return super.removeOne(listener);
	}

	public void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.REGISTER);
		ThreadManager.instance().execute(request);	
	}

	public void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.INVITE);
		ThreadManager.instance().execute(request);
	}

	public void doMessage(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.MESSAGE);
		ThreadManager.instance().execute(request);
	}

	public void doAck(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.ACK);
		ThreadManager.instance().execute(request);
	}

	public void doBye(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.BYE);
		ThreadManager.instance().execute(request);
	}

	public void doRefer(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.REFER);
		ThreadManager.instance().execute(request);
	}

	public void doNotify(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.NOTIFY);
		ThreadManager.instance().execute(request);
	}

	public void doPublish(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.PUBLISH);
		ThreadManager.instance().execute(request);
	}

	public void doSubscribe(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.SUBSCRIBE);
		ThreadManager.instance().execute(request);
	}

	public void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.CANCEL);
		ThreadManager.instance().execute(request);
	}

	public void doSuccessResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		Vector<Object> listeners = (Vector<Object>) super.getAll();
//		synchronized(listeners){
//			for(Object o : listeners){
//				CallflowListener cListener = (CallflowListener) o;
//				cListener.doSuccessResponse(resp);
//			}
//		}
	}

	public void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),req,null,SipMethods.REQUEST);
		ThreadManager.instance().execute(request);
	}

	public void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		CallflowRequest request = new CallflowRequest((Vector<Object>)super.getAll(),null,resp,SipMethods.RESPONSE);
		ThreadManager.instance().execute(request);
	}

	public void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		Vector<Object> listeners = (Vector<Object>) super.getAll();
//		synchronized(listeners){
//			for(Object o : listeners){
//				CallflowListener cListener = (CallflowListener) o;
//				cListener.doErrorResponse(resp);
//			}
//		}
	}
}
