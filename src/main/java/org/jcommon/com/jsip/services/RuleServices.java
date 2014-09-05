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

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.hss.Header;
import org.jcommon.com.jsip.hss.Rule;
import org.jcommon.com.jsip.utils.SipMethods;

public class RuleServices {
	private static Logger logger = Logger.getLogger(RuleServices.class);
	
	private final static Map<SipMethods, Vector<Rule>> services 
		= new Hashtable<SipMethods, Vector<Rule>>();
	
	public static void registerProcessor(SipMethods method, Rule rule){
		Vector<Rule> rules = services.get(method);
		if(null == rules){
			rules = new Vector<Rule>();
			services.put(method, rules);
		}
		
		if(!rules.contains(rule)){
			rules.add(rule);
			logger.info("register a Rule{method:"+method+";servlet:"+rule+"}");
		}
	}
	
	public static void unregisterProcessor(SipMethods method, Rule rule){
		Vector<Rule> rules = services.get(method);
		if(null == rules){
			rules = new Vector<Rule>();
			services.put(method, rules);
		}
		if(rules.contains(rule)){
			rules.remove(rule);
			logger.info("unregister a Rule{method:"+method+";servlet:"+rule+"}");
		}
	}
	
	public static boolean service(SipMethods method, SipServletRequest req, SipServletResponse resp)
		throws ServletException, IOException{
		Vector<Rule> rules = services.get(method);
		boolean run = false;
		String name;
		if(rules!=null){
			synchronized(rules){
				for(Rule rule : rules){
					boolean pass = true;
					for(Header rule_header : rule.getRule_header()){
						name = rule_header.getName();
						if(req.getHeader(name)==null){	
							pass = false;
							break;
						}else if(!rule_header.getValue().equals(req.getHeader(name))){
							pass = false;
							break;
						}
					}
					if(pass){
						rule.service(req, resp);
						run = true;
					}
				}
			}
			if(run)
				return true;
		}
		return false;
	}
}
