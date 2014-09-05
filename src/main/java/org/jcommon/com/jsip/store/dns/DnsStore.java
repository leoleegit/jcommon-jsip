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
package org.jcommon.com.jsip.store.dns;

import org.apache.log4j.Logger;
import org.jcommon.com.jsip.utils.SocketAddress;
import org.jcommon.com.util.collections.MapStore;

/**
 * 
 * @author leoLee
 *
 */
public class DnsStore extends MapStore{
	private Logger logger = Logger.getLogger(this.getClass());  
	
	private static DnsStore instance = new DnsStore();

	public static DnsStore instance(){
		return instance;
	}
	
	public void addDns(String key, DNS dns){
		if(super.addOne(key, dns))
			logger.info("DNS{"+key+";	"+dns.getAddress().getAddress().toString()+"}");
	}
	
	public void removeDns(String key){
		super.removeOne(key);
	}
	
	public DNS getDns(String key){
		return (DNS)super.getOne(key);
	}
	
	public SocketAddress getAddress(SocketAddress key){
		if(getDns(key.getAddress().toString())!=null)
			return getDns(key.getAddress().toString()).getAddress();
		else
			return key;
	}
	
	public SocketAddress getAddress(String domain){
		if(getDns(domain)!=null)
			return getDns(domain).getAddress();
		else
			return null;
	}
}
