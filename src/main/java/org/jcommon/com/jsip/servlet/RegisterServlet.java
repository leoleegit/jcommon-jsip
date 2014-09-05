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
import java.util.ListIterator;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.sip.Address;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.MainSipServlet;
import org.jcommon.com.jsip.services.LocalServices;
import org.jcommon.com.jsip.services.ProviderServices;
import org.jcommon.com.jsip.store.user.SipUser;
import org.jcommon.com.jsip.store.user.SipUserStorage;
import org.jcommon.com.jsip.utils.ProxyUtils;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.jsip.utils.SipUtil;

/**
 * 
 * @author leoLee
 *
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends SipServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());   

	private SipFactory factory;
	public static final String invalid_content = ".invalid";
	@SuppressWarnings("rawtypes")
	private final static Class fatherServlet = MainSipServlet.class;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see Servlet#init(ServletConfig)
	 */
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	logger.info("RegisterServlet init....");
    	factory = (SipFactory) getServletContext().getAttribute(SipServlet.SIP_FACTORY);
    	
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.REGISTER, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.INVITE, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.MESSAGE, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.NOTIFY, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.REFER, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.ACK, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.BYE, this);
    	ProviderServices.instance(fatherServlet).registerProcessor(SipMethods.CANCEL, this);
    }
    
    @Override
    protected void doRegister(SipServletRequest req) throws ServletException, IOException{
    	if(!ProxyUtils.isSipServer(req)){
    		ProxyUtils.proxyToSipServer(factory, req);
    		if(ProviderServices.instance(this.getClass()).service(SipMethods.REGISTER, req, null))
	    		return;
    		else super.doRegister(req);
    	} 

        SipServletResponse response = req.createResponse(SipServletResponse.SC_OK);
		try{
			SipURI to = SipUtil.cleanURI((SipURI) req.getTo().getURI());
	        int expires = 0;
	
	        SipUser sipUser = SipUserStorage.instance().getSipUser(to.toString());
	        
	        req.getSession().setAttribute(SipUser.USERID, to.toString());
	        if ( sipUser == null ){
	            sipUser = new SipUser(to.toString());
	        }
	        sipUser.clearContactUri();
	        ListIterator<Address> li = req.getAddressHeaders("Contact");
	        while (li.hasNext()){
	            Address na = li.next();
	            SipURI contact = (SipURI) na.getURI();
	            String expiresString = na.getParameter("expires");
	            if (expiresString == null) { // Check the Expires header
	                    expiresString = req.getHeader("Expires");
	            }
	            if (expiresString == null) {//Missing expires value in request!
	                    expiresString = "3600";
	            }
	            expires = Integer.parseInt(expiresString);
	            String host = contact.getHost();
	            if(host!=null && host.indexOf(invalid_content)!=-1){
	            	SipURI contact_ = (SipURI) contact.clone();
	            	contact = factory.createSipURI(contact.getUser(), req.getRemoteHost());
	            	contact = SipUtil.cloneURI(contact_, contact);
	            	contact.setPort(req.getRemotePort());
	            }
	            logger.info("contact clone : "+contact.toString());
	            sipUser.addContactUri(contact);
	            response.setHeader("Contact",na.toString() + ";expires="+expiresString);
	        }
	        if(expires!=0)
	        	sipUser.setInStorage();
	        sipUser.setExpiry(expires); 
	        response.send();
	        logger.info(Level.FINE.toString()+ "Sent 200 response.");
	        if(ProviderServices.instance(this.getClass()).service(SipMethods.REGISTER, req, null))
	    		return;
		} catch(Exception e) {
            logger.error("Sent 500 response:"+e.getMessage());
            response.setStatus(500);
            response.send();
        }
        SipApplicationSession appsess = req.getApplicationSession(false);
        if (appsess != null) {
            appsess.invalidate();
        }
	}
	
	protected void doInvite(SipServletRequest req) throws ServletException, IOException{
    	if(!proxyTo(req)){
           	if(!ProviderServices.instance(this.getClass()).service(SipMethods.INVITE, req, null)){
           		if(!ProxyUtils.proxyToSipServer(factory, req)){
           			SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
           			response.send();
           		}         			
           	}
        }
	}
	
    @Override
    protected void doMessage(SipServletRequest req) throws ServletException, IOException{
    	SipURI from = SipUtil.cleanURI((SipURI) req.getFrom().getURI()); 
    	try {
			if(SipUtil.isWriting(SipUtil.getContents(req))){
				logger.info(from+" is writing...");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("doMessage:", e);
		}
        
    	if(!proxyTo(req)){
        	if(!ProviderServices.instance(this.getClass()).service(SipMethods.MESSAGE, req, null)){
	            SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
	            response.send();
            }
        }
    }
    
    @Override
    protected void doRefer(SipServletRequest req) throws ServletException, IOException{
    	if(!proxyTo(req)){
        	if(!ProviderServices.instance(this.getClass()).service(SipMethods.REFER, req, null)){
	            SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
	            response.send();
            }
        }
    }
    
    @Override
	protected void doAck(SipServletRequest req) throws ServletException, IOException{
    	if(!proxyTo(req)){
        	if(!ProviderServices.instance(this.getClass()).service(SipMethods.ACK, req, null)){
	            SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
	            response.send();
            }
        }
	}
    
    @Override
	protected void doBye(SipServletRequest req) throws ServletException, IOException{
    	if(!proxyTo(req)){
        	if(!ProviderServices.instance(this.getClass()).service(SipMethods.BYE, req, null)){
	            SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
	            response.send();
            }
        }
    }
    
    @Override
    protected void doCancel(SipServletRequest req) throws ServletException, IOException{
    	if(!proxyTo(req)){
        	if(!ProviderServices.instance(this.getClass()).service(SipMethods.CANCEL, req, null)){
	            SipServletResponse response = req.createResponse(SipServletResponse.SC_NOT_FOUND);
	            response.send();
            }
        }
    }
    
    @Override
    protected void doNotify(SipServletRequest req) throws ServletException, IOException{
    	SipURI to = SipUtil.cleanURI((SipURI) req.getTo().getURI());
    	SipURI from = SipUtil.cleanURI((SipURI) req.getFrom().getURI());
    	
    	if(to.equals(from)){
    		logger.info("ignore the notify request that to myself:\n");
    	    SipServletResponse response = req.createResponse(SipServletResponse.SC_OK);
    	    response.send();
    		return;
    	}
    	
    	if(!proxyTo(req)){
	        if(!ProviderServices.instance(this.getClass()).service(SipMethods.NOTIFY, req, null)){
		        super.doNotify(req);
	        }
        }
    	
    }
    
    @Override
	protected void doRequest(SipServletRequest req) throws ServletException,
			IOException {
    	SipURI request = SipUtil.cleanURI((SipURI) req.getRequestURI()); 
    	if(request!=null && request.toString().indexOf(invalid_content)!=-1){
    		SipURI request_ = (SipURI) request.clone();
    		request = factory.createSipURI(request.getUser(), req.getRemoteHost());
    		request = SipUtil.cloneURI(request_, request);
    		request.setPort(req.getRemotePort());
    		req.setRequestURI(request);
    	}
		super.doRequest(req);
	}
    
	@Override
	protected void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		logger.info("response:\n"+resp.toString()+"\n\n");	
        super.doResponse(resp);
	}
	
	@Override
	protected void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doErrorResponse:\n"+resp.toString()+"\n\n");
		super.doErrorResponse(resp);
	}
    
    private boolean proxyTo(SipServletRequest req) throws ServletException, IOException{
    	SipURI to = SipUtil.cleanURI((SipURI) req.getTo().getURI());    
    	
    	//local clients
    	if(LocalServices.instance().hasRegister(to.toString())){
    		LocalServices.instance().service(to.toString(), req, null);
    		return true;
    	}
    		
        SipUser sipUser = SipUserStorage.instance().getSipUser(to.toString());     
        if (sipUser != null){       
            Proxy proxy = req.getProxy();
            proxy.setRecordRoute(true);
            proxy.setSupervised(true);
            proxy.proxyTo(sipUser.getContactURI());
            return true;
        }

    	return false;
    }
    
    @SuppressWarnings("rawtypes")
	public static Class getFatherServlet(){return fatherServlet;}
}
