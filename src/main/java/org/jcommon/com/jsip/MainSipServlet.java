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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSessionListener;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.callflow.CallflowManager;
import org.jcommon.com.jsip.config.SipConfig;
import org.jcommon.com.jsip.services.ComponentService;
import org.jcommon.com.jsip.services.LocalServices;
import org.jcommon.com.jsip.services.ProviderServices;
import org.jcommon.com.jsip.services.RuleServices;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.jsip.utils.SipUtil;

/**
 * @author leoLee
 * Servlet implementation class SipServlet
 */
public class MainSipServlet extends SipServlet implements SipApplicationSessionListener,SipSessionListener{
	private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(this.getClass());   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainSipServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		RequestFactory.sipFactory = (SipFactory) getServletContext().getAttribute(SipServlet.SIP_FACTORY);
		logger.info("sip-server init....");	
	}
	
	
	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		logger.info("sip-server destroy....");
	}
	
	@Override
    protected void doRegister(SipServletRequest req) throws ServletException, IOException{
		if(!ComponentService.service(SipMethods.REGISTER, req, null))
		if(!RuleServices.service(SipMethods.REGISTER, req, null))
		if(!ProviderServices.instance(this.getClass()).service(SipMethods.REGISTER, req, null))
			super.doRegister(req);
		CallflowManager.instance().doRegister(req);
	}
	
	 @Override
	protected void doInvite(SipServletRequest req) throws ServletException, IOException{
		 if(!ComponentService.service(SipMethods.INVITE, req, null))
		 if(!RuleServices.service(SipMethods.INVITE, req, null))
		if(!ProviderServices.instance(this.getClass()).service(SipMethods.INVITE, req, null))
			super.doInvite(req);
		CallflowManager.instance().doInvite(req);
	}
	
    @Override
    protected void doMessage(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.MESSAGE, req, null))
    	if(!RuleServices.service(SipMethods.MESSAGE, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.MESSAGE, req, null))
    		super.doMessage(req);
    	CallflowManager.instance().doMessage(req);
    }
    
    @Override
	protected void doAck(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.ACK, req, null))
    	if(!RuleServices.service(SipMethods.ACK, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.ACK, req, null))
			super.doAck(req);
		CallflowManager.instance().doAck(req);
	}
	
    @Override
	protected void doBye(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.BYE, req, null))
    	if(!RuleServices.service(SipMethods.BYE, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.BYE, req, null))
			super.doBye(req);  
		CallflowManager.instance().doBye(req);
	}
    
    @Override
    protected void doRefer(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.REFER, req, null))
    	if(!RuleServices.service(SipMethods.REFER, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.REFER, req, null))
    		super.doRefer(req);
    	CallflowManager.instance().doRefer(req);
    }
      
    @Override
    protected void doNotify(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.NOTIFY, req, null))
    	if(!RuleServices.service(SipMethods.NOTIFY, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.NOTIFY, req, null))
    		super.doNotify(req);
    	CallflowManager.instance().doNotify(req);
    }
    
    @Override
    protected void doPublish(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.PUBLISH, req, null))
    	if(!RuleServices.service(SipMethods.PUBLISH, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.PUBLISH, req, null))
    		super.doPublish(req);
    	CallflowManager.instance().doPublish(req);
    }
    
    @Override
    protected void doSubscribe(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.SUBSCRIBE, req, null))
    	if(!RuleServices.service(SipMethods.SUBSCRIBE, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.SUBSCRIBE, req, null))
    		super.doSubscribe(req);
    	CallflowManager.instance().doSubscribe(req);
    }
    
    @Override
    protected void doCancel(SipServletRequest req) throws ServletException, IOException{
    	if(!ComponentService.service(SipMethods.CANCEL, req, null))
    	if(!RuleServices.service(SipMethods.CANCEL, req, null))
    	if(!ProviderServices.instance(this.getClass()).service(SipMethods.CANCEL, req, null))
    		super.doCancel(req);
    	CallflowManager.instance().doCancel(req);
    }
    
    @Override
    protected void doSuccessResponse(SipServletResponse resp) throws ServletException, IOException {
    	if(!ComponentService.service(SipMethods.RESPONSE, null, resp))
        if(!RuleServices.service(SipMethods.RESPONSE, null, resp))
    	if(resp.getMethod().equals(SipMethods.INVITE.toString())){
            super.doSuccessResponse(resp);
        }
        CallflowManager.instance().doSuccessResponse(resp);
    }

	@Override
	protected void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		logger.info("request:\n"+req.toString()+"\n\n");
		super.doRequest(req);
		CallflowManager.instance().doRequest(req);
	}

	@Override
	protected void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		logger.info("response:\n"+resp.toString()+"\n\n");
		SipURI from = SipUtil.cleanURI((SipURI) resp.getFrom().getURI()); 
		if(!RuleServices.service(SipMethods.RESPONSE, null, resp))
		if(LocalServices.instance().hasRegister(from.toString())){
    		LocalServices.instance().service(from.toString(), null, resp);
    	}else{
    		super.doResponse(resp);
    	}
		CallflowManager.instance().doResponse(resp);
	}
	
	@Override
	protected void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		logger.info("doErrorResponse:\n"+resp.toString()+"\n\n");
		SipURI from = SipUtil.cleanURI((SipURI) resp.getFrom().getURI()); 
		if(LocalServices.instance().hasRegister(from.toString())){
    		LocalServices.instance().service(from.toString(), null, resp);
    	}else
    		super.doErrorResponse(resp);	
		CallflowManager.instance().doErrorResponse(resp);
	}

	@Override
	public void sessionCreated(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionExpired(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		try
	    {
	      SipApplicationSession sa = arg0.getApplicationSession();
	      sa.setExpires(SipConfig.instance().getSessionExpired());
	    }
	    catch (Exception e) {
	      this.logger.error("", e);
	    }
	}

	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionCreated(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionReadyToInvalidate(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
