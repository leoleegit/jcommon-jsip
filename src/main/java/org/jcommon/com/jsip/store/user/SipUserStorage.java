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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.UsersMonitor;
import org.jcommon.com.util.DateUtils;
import org.jcommon.com.util.collections.MapStore;

public class SipUserStorage extends MapStore{
	private Logger logger = Logger.getLogger(this.getClass());   
	
	private static final SipUserStorage INSTANCE = new SipUserStorage();

	
	public static SipUserStorage instance(){return INSTANCE;}

	protected void setSipUser(String key, SipUser user){
		if(super.addOne(key, user)){
			logger.info("add sip user :"+key+"("+user+")" );
		}else{
			updateOne(key,user);
			logger.info("update sip user :"+key+"("+user+")" );
		}
		try{
			if(UsersMonitor.instance()!=null)
				UsersMonitor.instance().addProperties(key, DateUtils.getNowSinceYear());
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected void removeSipUser(String key){
		if(super.removeOne(key)!=null){
			if(UsersMonitor.instance()!=null)
				UsersMonitor.instance().removeProperties(key);
			logger.info("remove sip user :"+key);
		}		
	}
	
	public boolean containsKey(String key){
		return super.hasKey(key);
	}
	
	public SipUser getSipUser(String key){
		return (SipUser)super.getOne(key);
	}
	
	public Map<String, SipUser> getSipUserMap(){
		Map<String, SipUser> store = new HashMap<String, SipUser>();
		synchronized(super.getAll()){
			for(Object key : super.getAll().keySet()){
				store.put((String)key, (SipUser)super.getAll().get(key));
			}
		}
		return store;
	}
}
