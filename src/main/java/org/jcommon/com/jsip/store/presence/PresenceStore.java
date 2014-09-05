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

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.PresenceMonitor;
import org.jcommon.com.jsip.utils.PresenceType;
import org.jcommon.com.jsip.utils.SipUtil;
import org.jcommon.com.util.collections.MapStore;


public class PresenceStore extends MapStore{
	private Logger logger = Logger.getLogger(this.getClass());  
	
	private static final PresenceStore INSTANCE = new PresenceStore();

	public static PresenceStore instance(){return INSTANCE;}
	
	protected void setPresenceInfo(String key, PresenceInfo user){
		if(super.addOne(key, user))
			logger.info("add sip PresenceInfo :"+key);
		try{
			if(PresenceMonitor.instance()==null)return;
			PresenceType presence = SipUtil.getStatus(new String(user.getLastPublish(),"utf-8"));
			PresenceMonitor.instance().addProperties(key, presence.toString());
		}catch(Exception e){logger.error("", e);}
	}
	
	protected void removePresenceInfo(String key){
		if(super.removeOne(key)!=null){
			logger.info("remove sip PresenceInfo :"+key);
		}
		try{
			if(PresenceMonitor.instance()==null)return;
			PresenceMonitor.instance().removeProperties(key);
		}catch(Exception e){logger.error("", e);}
	}
	
	public PresenceInfo getPresenceInfo(String key){
		return (PresenceInfo) super.getOne(key);
	}
}
