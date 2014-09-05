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
package org.jcommon.com.jsip.services;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.util.system.SystemListener;

/**
 * 
 * @author leoLee
 *
 */
public class ProviderServices implements SystemListener{
	private Logger logger = Logger.getLogger(this.getClass());   
	
	private Map<SipMethods, Vector<SipServlet>> methodProcessorTable
		= new Hashtable<SipMethods, Vector<SipServlet>>();	
	
	private static Map<String, ProviderServices> services 
		= new Hashtable<String, ProviderServices>();
	
	@SuppressWarnings("rawtypes")
	public static ProviderServices instance(Class c){
		ProviderServices instance;
		String key = c.getName();		
		if(!services.containsKey(key)){
			instance = new ProviderServices();
			services.put(key, instance);
		}
		else
			instance   = services.get(key);
		return instance;
	}
	
	public ProviderServices(){
		
	}
	
	public void registerProcessor(SipMethods method, SipServlet servlet){
		Vector<SipServlet> listener = methodProcessorTable.get(method);
		if(null == listener){
			listener = new Vector<SipServlet>();
			methodProcessorTable.put(method, listener);
		}
		
		if(!listener.contains(servlet)){
			listener.add(servlet);
			logger.info("register a servlet{method:"+method+";servlet:"+servlet+"}");
		}
	}
	
	public void unregisterProcessor(SipMethods method, SipServlet servlet){
		Vector<SipServlet> listener = methodProcessorTable.get(method);
		if(null == listener){
			listener = new Vector<SipServlet>();
			methodProcessorTable.put(method, listener);
		}
		if(listener.contains(servlet)){
			listener.remove(servlet);
			logger.info("unregister a servlet{method:"+method+";servlet:"+servlet+"}");
		}
	}
	
	public boolean service(SipMethods method, SipServletRequest req, SipServletResponse resp)
		throws ServletException, IOException{
		Vector<SipServlet> listener = methodProcessorTable.get(method);
		boolean run = false;
		if(listener!=null){
			synchronized(listener){
				for(SipServlet servlet : listener){
					servlet.service(req, resp);
					run = true;
				}
			}
			if(run)
				return true;
		}
		return false;
	}
	
    @Override
    protected void finalize() throws Throwable {
    	methodProcessorTable.clear();
    }

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		methodProcessorTable.clear();
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSynchronized() {
		// TODO Auto-generated method stub
		return false;
	}
}
