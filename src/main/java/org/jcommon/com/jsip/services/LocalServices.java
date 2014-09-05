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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;
import org.jcommon.com.util.collections.MapStore;
import org.jcommon.com.jsip.SipClient;

/**
 * 
 * @author leoLee
 *
 */
public class LocalServices extends MapStore {
	private Logger logger = Logger.getLogger(this.getClass());   
	
	private static LocalServices instance = new LocalServices();
	
	private Map<String, SipServlet> clientProcessorTable
		= new HashMap<String, SipServlet>();
	
	public static LocalServices instance(){return instance;}
	
	public boolean hasRegister(String sipUser){
		//logger.info("\n"+clientProcessorTable.toString());
		if(clientProcessorTable.containsKey(sipUser))
			return true;
		return false;
	}
	
	public void registerLocalClient(SipClient sipUser, SipServlet sipServlet){
		if(!clientProcessorTable.containsKey(sipUser.getKey())){
			clientProcessorTable.put(sipUser.getKey(), sipServlet);
			super.addOne(sipUser.getKey(), sipUser);
			logger.info(sipUser.getKey()+ " register a LocalServlet{servlet:"+sipServlet+"}");
		}
	}
	
	public void unregisterLocalClient(String sipUser){
		if(!clientProcessorTable.containsKey(sipUser)){
			clientProcessorTable.remove(sipUser);
			super.removeOne(sipUser);
			logger.info(sipUser+ " unregister LocalServlet");
		}
	}
	
	public void service(String sipUser, SipServletRequest req, SipServletResponse resp) 
		throws ServletException, IOException{
		if(hasRegister(sipUser)){
			clientProcessorTable.get(sipUser).service(req, resp);
		}
	}
	
	public void clear() {
		// TODO Auto-generated method stub
		Map<Object, Object> store = getAllClient();
		synchronized(store){
			for(Object sc: store.values()){
				try {
					if(sc instanceof SipClient)
						((SipClient)sc).unregisterServer();
				} catch (ServletParseException e) {
					// TODO Auto-generated catch block
					logger.error(((SipClient)sc).getKey()+" unRegister failure ", e);
					continue;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(((SipClient)sc).getKey()+" unRegister failure ", e);
					continue;
				}
			}
		}
		super.clear();
		clientProcessorTable.clear();
	}

	public void registerToServer(){
		new Thread(){
			public void run(){
			    try {
			    	sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Map<Object, Object> store = getAllClient();
				synchronized(store){
					for(Object sc: store.values()){
						try {
							((SipClient)sc).loopRegister();
						} catch (ServletParseException e) {
							// TODO Auto-generated catch block
							logger.error(((SipClient)sc).getKey()+" Register failure ", e);
							continue;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error(((SipClient)sc).getKey()+" Register failure ", e);
							continue;
						}
					}
				}
			}
		}.start();
	}
	
	/**
	 * 
	 * @return Map<String, SipClient>
	 */
	public Map<Object, Object> getAllClient(){
		return super.getAll();
	}
	
	public SipClient getClient(String key){
		return (SipClient)super.getOne(key);
	}
}
