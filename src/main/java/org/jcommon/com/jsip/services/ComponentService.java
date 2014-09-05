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
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.hss.Component;
import org.jcommon.com.jsip.utils.SipMethods;
import org.jcommon.com.jsip.utils.SipUtil;

public class ComponentService {	
    private static Logger logger = Logger.getLogger(ComponentService.class);
	
	private final static Map<SipMethods, Vector<Component>> services 
		= new Hashtable<SipMethods, Vector<Component>>();
	
	public static void registerProcessor(SipMethods method, Component component){
		Vector<Component> components = services.get(method);
		if(null == components){
			components = new Vector<Component>();
			services.put(method, components);
		}
		
		if(!components.contains(component)){
			components.add(component);
			logger.info("register a Component{method:"+method+";servlet:"+component+"}");
		}
	}
	
	public static void unregisterProcessor(SipMethods method, Component component){
		Vector<Component> components = services.get(method);
		if(null == components){
			components = new Vector<Component>();
			services.put(method, components);
		}
		if(components.contains(component)){
			components.remove(component);
			logger.info("unregister a Component{method:"+method+";servlet:"+component+"}");
		}
	}
	
	public static boolean service(SipMethods method, SipServletRequest req, SipServletResponse resp)
		throws ServletException, IOException{
		Vector<Component> components = services.get(method);
		boolean run = false;
		SipURI to   = null;
		if(req!=null){
			to   = SipUtil.cleanURI((SipURI) req.getTo().getURI());	
		}else if(resp!=null){
			to   = SipUtil.cleanURI((SipURI) resp.getTo().getURI());	
		}else
			return false;
		
		if(components!=null){
			synchronized(components){
				for(Component component : components){
					String name = getComponentName(to);
					if(name==null)continue;
					if(component.getName().equals(name)){
						component.service(req, resp);
						run = true;
					}
				}
			}
			if(run)
				return true;
		}
		return false;
	}
	
	private static String getComponentName(SipURI to){
		String s = to.toString();
		return SipUtil.getDomain(s);
	}
}
