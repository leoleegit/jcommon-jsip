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
package org.jcommon.com.jsip.config;

public class SipConfig {
	private static final SipConfig instance = new SipConfig();
	
	public static SipConfig instance(){return instance;}
	
	private String sipServer;
	private String persenceServer;
	private int  sessionExpired = 0;
	
	public void setSipServer(String sipServer) {
		this.sipServer = sipServer;
	}
	public String getSipServer() {
		return sipServer;
	}
	public void setPersenceServer(String persenceServer) {
		this.persenceServer = persenceServer;
	}
	public String getPersenceServer() {
		return persenceServer;
	}
	public void setSessionExpired(int sessionExpired) {
		this.sessionExpired = sessionExpired;
	}
	public int getSessionExpired() {
		return sessionExpired;
	}
}
