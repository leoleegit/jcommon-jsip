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
package org.jcommon.com.jsip.hss;

import java.util.*;

import javax.servlet.sip.SipServlet;

public class Rule extends SipServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Header> rule_header = new ArrayList<Header>();
	
	public Rule(){
		
	}
	
	public Rule(List<Header> rule_header){
		this.rule_header = rule_header;
	}

	public void setRule_header(List<Header> rule_header) {
		this.rule_header = rule_header;
	}
	public List<Header> getRule_header() {
		return rule_header;
	}
}
