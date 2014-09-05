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

import java.util.*;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.utils.PresenceType;
import org.jcommon.com.jsip.utils.SipUtil;
import org.jcommon.com.util.jmx.Monitor;

public class PresenceMonitor extends Monitor {
	protected static final Logger logger = Logger.getLogger(PresenceMonitor.class);
	protected static Monitor instance;
	public static PresenceMonitor instance(){return (PresenceMonitor) instance;}
	
	public List<PresenceListener> listeners = new ArrayList<PresenceListener>();
	
	public PresenceMonitor(){
		super("PresenceMonitor");
		PresenceMonitor.instance = this;
	}
	
	public void addPresenceListener(PresenceListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removePresenceListener(PresenceListener listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	public void addProperties(String key, String value){
		String value_ = super.getProperties(key);
		try{
			super.addProperties(key, value);
			if(value_==null || value.equals(value_))
				onChange(key, value);
		}catch(Exception e){logger.error("",e);}
	}
	
	public void removeProperties(String key){
		String value_ = super.getProperties(key);
		try{
			super.removeProperties(key);
			if(value_!=null && !PresenceType.unavailable.toString().equals(value_))
				onChange(key, PresenceType.unavailable.toString());
		}catch(Exception e){logger.error("",e);}
	}
	
	private void onChange(String key, String value){
		PresenceType  type = SipUtil.Publish2Type(value);
		synchronized(listeners){
			for(PresenceListener listener : listeners)
				listener.presenceChange(key, type);
		}
	}
}
