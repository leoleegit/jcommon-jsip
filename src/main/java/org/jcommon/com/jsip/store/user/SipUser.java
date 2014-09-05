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
package org.jcommon.com.jsip.store.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.sip.SipURI;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * @author leoLee
 *
 */
public class SipUser implements Serializable {
    public final static String USERID = "USERID";
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** "Contact" Header of the user. */
    private ArrayList<SipURI>  contactURI  = new ArrayList<SipURI>();
    
    private String account;

    private SipUserType type;

    private long expiry;
    private long leftTime;
    private long loginTime;
    
    private Timer timer = new Timer();
    private Task  task;
    
    public SipUser(String account){
    	this.account = account;
    }
    
    public void addContactUri(SipURI contact){ 
    	if(!contactURI.contains(contact))
    		contactURI.add(contact);
    }
    
    public void clearContactUri(){
    	contactURI.clear();
    }
    
	public String getaccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public SipUserType getType() {
		return type;
	}

	public void setType(SipUserType type) {
		this.type = type;
	}

	public ArrayList<SipURI> getContactURI() {
		return contactURI;
	}

	public void setContactURI(ArrayList<SipURI> contactURI) {
		this.contactURI = contactURI;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
		this.task   = new Task();
		try{timer.cancel();}catch(Exception e){}
		timer = new Timer();
		timer.schedule(task, (expiry * 1000));
		if(expiry!=0)
			setLoginTime(new Date().getTime());
	}

	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	
	public void setInStorage(){
		SipUserStorage.instance().setSipUser(this.account, this);
	}
	
	class Task extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			SipUserStorage.instance().removeSipUser(account);
			timer.cancel();
		}
		
	}
	
    public Element getElement() {
    	Document doc     = DocumentHelper.createDocument();
		Element  element = doc.addElement(this.getClass().getSimpleName());
		Element  msg     = element.addElement("SipAccount");
		
		msg.addElement("account").setText(account);	
		msg.addElement("loginTime").setText(String.valueOf(loginTime));				
		msg.addElement("expiry").setText(String.valueOf(expiry));
		msg.addElement("ip").setText(contactURI.size()!=0?contactURI.get(0).getHost():"miss_ip");
		
		return element;
	}
}
