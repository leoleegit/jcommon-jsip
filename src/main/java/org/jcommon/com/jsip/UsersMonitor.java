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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jcommon.com.util.jmx.Monitor;


public class UsersMonitor extends Monitor{
	protected static final Logger logger = Logger.getLogger(UsersMonitor.class);

	protected static Monitor instance;

	public static UsersMonitor instance(){return  (UsersMonitor) instance;}
	
	public List<UserListener> listeners = new ArrayList<UserListener>();
	
	public UsersMonitor(){
		super("UserMonitor");	
		UsersMonitor.instance = this;
	}
	
	
	public void addUserListener(UserListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void removeUserListener(UserListener listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	public void addProperties(String key, String value){
		try{
			if(!super.hasProperties(key))
				onLogin(key);
			super.addProperties(key, value);
		}catch(Exception e){logger.error("",e);}
	}
	
	public void removeProperties(String key){
		try{
			super.removeProperties(key);
			onLogout(key);
		}catch(Exception e){logger.error("",e);}
	}
	
	private void onLogin(String key){
		synchronized(listeners){
			for(UserListener listener : listeners)
				listener.userLogin(key);
		}
	}
	
	private void onLogout(String key){
		synchronized(listeners){
			for(UserListener listener : listeners)
				listener.userLogout(key);
		}
	}
}
