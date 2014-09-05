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
package org.jcommon.com.jsip.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jcommon.com.jsip.config.SipConfig;

public class SipUtil {
	private static Logger logger = Logger.getLogger(SipUtil.class);
	
	public static SipURI cleanURI(SipURI original) {
        SipURI copy = (SipURI) original.clone();
        Iterator<String> headers = copy.getHeaderNames();
        if (headers.hasNext()) {
            headers.next();
            headers.remove();
        }
        Iterator<String> parameters = copy.getParameterNames();
        if (parameters.hasNext()) {
            String param = (String) parameters.next();
            copy.removeParameter(param);
        }
        return copy;
    }
	
	public static SipURI cloneURI(SipURI contact1, SipURI contact2){
		Iterator<String> it = contact1.getParameterNames();
		for(;it.hasNext();){
			String p = it.next();
			contact2.setParameter(p, contact1.getParameter(p));
		}
		return contact2;
	}
	
    /**
     * get the content of SipServletRequest , always read MESSAGE request
     */
	public static  String getContents(SipServletRequest request) throws IOException {
        String body = "";
        Object message = request.getContent();
        
        if (message != null) {
            if (message instanceof String) {
                body = (String) message;
            } else if (message instanceof byte[]) {
            	System.out.println("messages"+message);
                body = new String((byte[]) message, "utf-8");
            } else {
                body = message.toString();
            }
        }
        return body;
    }
	
	public static PresenceType getStatus(SipServletRequest request) throws IOException{
		String body = getContents(request);
		return getStatus(body);
	}
	
	public static PresenceType getStatus(String body) throws IOException{
		String presence = "";

		if(body==null)
			return PresenceType.unavailable;
		try{
			Document doc = DocumentHelper.parseText(body);
			Element root = doc.getRootElement();
			if(root != null){			
				root = root.element("person");
				if(root != null){
					root = root.element("activities");
					if(root != null){
						Iterator<?> it = root.elementIterator();
						for(;it.hasNext();){
							Object e = it.next();
							presence = ((Element)e).asXML();
						}
					}
				}
			}
			
		}catch (Throwable t) {
			logger.error("parse to doc error :\n"+body, t);
			t.printStackTrace();
		}

		if(presence.indexOf(" ")!=-1)
			presence = presence.substring(0, presence.indexOf(" ")) + "/>";
		return notify2Type(presence);
	}
	
	public static String getPresences(String str) throws IOException{
		String presence = "";
		try{
			if(str==null)return presence;
			Document doc = DocumentHelper.parseText(str);
			Element root = doc.getRootElement();
			if(root != null){			
				root = root.element("person");
				if(root != null){
					root = root.element("note");
					
					if(root != null){
						presence = root.getTextTrim();
					}
				}
			}
			
		}catch (Throwable t) {
			logger.error("parse to doc error :\n"+str, t);
			t.printStackTrace();
		}
		return presence;
	}
	
	public static String getPresences(SipServletRequest request) throws IOException{
		String body = getContents(request);

		if(body==null)
			return null;
		return getPresences(body);
	}
	
	public static String generateRandom(int length) {
		Random random = new Random();
		byte[] byteArray = new byte[length];
		int i;
		random.nextBytes(byteArray);
		for (i = 0; i < byteArray.length; i++) {
			if (byteArray[i] < 0)
				byteArray[i] *= -1;

			while (!((byteArray[i] >= 65 && byteArray[i] <= 90)
					|| (byteArray[i] >= 97 && byteArray[i] <= 122) || (byteArray[i] <= 57 && byteArray[i] >= 48))) {

				if (byteArray[i] > 122)
					byteArray[i] -= random.nextInt(byteArray[i]);
				if (byteArray[i] < 48)
					byteArray[i] += random.nextInt(5);
				else
					byteArray[i] += random.nextInt(10);
			}
		}
		return new String(byteArray);
	}
	
   public static boolean isWriting(String content) {
		// TODO Auto-generated method stub
		if(content.startsWith("<?xml") &&
				content.indexOf("<isComposing")!=-1)
			return true;
		return false;
	}
   
	public static SocketAddress CreateServerAddress(String address){
		if(address!=null)
			return new SocketAddress(address);
		else
			return new SocketAddress(IpAddress.getLocalHostAddress().toString());
	}
	
    public static PresenceType Publish2Type(String sipStatus){
        if (sipStatus.equals(PresenceType.Available.toString())) {
            return PresenceType.Available;
            
        } else if (sipStatus.equals(PresenceType.away.toString())) {
            return PresenceType.away;
            
        } else if (sipStatus.equals(PresenceType.dnd.toString())) {
            return PresenceType.dnd;
            
        } else if (sipStatus.equals(PresenceType.unavailable.toString())) {
            return PresenceType.unavailable;
        } else {
            return PresenceType.unavailable;
        }
    }
    
    public static PresenceType notify2Type(String sipStatus){
        if (sipStatus.equals(PresenceType.Available.toString(PresenceType.type))) {
            return PresenceType.Available;
            
        } else if (sipStatus.equals(PresenceType.away.toString(PresenceType.type))) {
            return PresenceType.away;
            
        } else if (sipStatus.equals(PresenceType.dnd.toString(PresenceType.type))) {
            return PresenceType.dnd;
            
        } else if (sipStatus.equals(PresenceType.unavailable.toString(PresenceType.type))) {
            return PresenceType.unavailable;
        } else {
            return PresenceType.unavailable;
        }
    }
    
    //"leo"<sip:1001@192.168.2.23> to leo
    public static String disName(String sipName){
    	if(sipName.indexOf("<")!=-1){
    		sipName = sipName.substring(0, sipName.indexOf("<"));
    	}
    	if(sipName.startsWith("\""))
    		sipName = sipName.substring(1);
    	if(sipName.endsWith("\""))
    		sipName = sipName.substring(0,sipName.lastIndexOf("\""));
    	return sipName;
    }
    
    
    public static String getContact(String sipName){
    	String domain = getDomain(sipName);
    	sipName = sipName.replaceAll(IpAddress.getLocalHostAddress().toString(), domain);
    	return sipName;
    }
    
    //"sip:1001@192.168.2.170" to 1001 
    public static String getUsername(String sipName){
    	if(sipName.indexOf("sip:")!=-1){
    		sipName = sipName.substring(sipName.indexOf("sip:")+4);
    	}
    	if(sipName.indexOf("@")!=-1){
    		sipName = sipName.substring(0, sipName.indexOf("@"));
    	}
    	return sipName;
    }
    
    //"sip:1001@192.168.2.170" to 192.168.2.170
    public static String getDomain(String sipName){
    	if(sipName.indexOf("sip:")!=-1){
    		sipName = sipName.substring(sipName.indexOf("sip:")+4);
    	}
    	if(sipName.indexOf("@")!=-1){
    		sipName = sipName.substring(sipName.indexOf("@")+1, sipName.length());
    	}
    	return sipName;
    }
    
    public static String getBuddyAddr(String sipName){
    	if(sipName==null)return null;
    	String sipServer = SipConfig.instance().getSipServer();
    	if(sipName.indexOf("sip:")!=-1){
    		sipName = sipName.substring(sipName.indexOf("sip:")+4);
    	}
    	if(sipName.indexOf("@")==-1){
    		sipName = sipName + "@" + sipServer;
    	}
    	return sipName;
    }
    
    //"1001" to sip:1001@192.168.2.170 or "1001" <sip:1001@192.168.2.170>; to sip:1001@192.168.2.170
    public static String getSipAddr(String name){
    	if(name==null)return null;
    	String sipServer = SipConfig.instance().getSipServer();
    	if(sipServer!=null && sipServer.indexOf(":")!=-1)
    		sipServer = sipServer.split(":")[0];
    	
    	if(name.indexOf("sip:")==-1){
    		name = "sip:"+name;
    	}else{
    		name = name.substring(name.indexOf("sip:"));
    	}
    	if(name.indexOf(">")!=-1){
    		name = name.substring(0, name.indexOf(">"));
    	}
    	if(name.indexOf("@")==-1){
    		name = name + "@" + sipServer;
    	}
    	return name;
    }
    
	public static String getNickName(String sipAdd){
		if(sipAdd==null)return null;
		if(sipAdd.startsWith("<"))return getUsername(sipAdd);
		if(sipAdd.startsWith("sip:"))return getUsername(sipAdd);
		if(sipAdd.indexOf("\"")!=-1){
			sipAdd = sipAdd.substring(1);
		}
		if(sipAdd.indexOf("<")!=-1){
			sipAdd = sipAdd.substring(0,sipAdd.indexOf("<"));
		}
		if(sipAdd.lastIndexOf("\"")!=-1){
			sipAdd = sipAdd.substring(0,sipAdd.indexOf("\""));
		}
		return sipAdd.trim();
	}
    
    public static String getSipAddr(int name){
    	String sipName = null;
    	try{
    		sipName = String.valueOf(name);
    	}catch(Exception e){return null;}
    	return getSipAddr(sipName);
    }
    
    private static final String subscriber_ = "subscriber_";
    public static String getSubscriber(String sipName){
    	String name        = getUsername(sipName);
    	String subscriber  = subscriber_ + name;
    	return sipName.replaceAll(name, subscriber);
    }
    
    public static SipURI getLocalContact(ArrayList<SipURI> contacts){
    	for(SipURI uri : contacts){
    		String localHost = IpAddress.getLocalHostAddress().toString();
    		if(localHost.equals(uri.getHost()))
    			return uri;
    	}
    	return null;
    }
    
    public static PresenceType getPresence(Map<String, String> status)
	{
		// TODO Auto-generated method stub
    	for(String sipStatus : status.values()){
    		if (sipStatus.equals(PresenceType.Available.toString()))
    			return PresenceType.Available;
    	}
    	for(String sipStatus : status.values()){
   		    if (sipStatus.equals(PresenceType.dnd.toString()))
   			    return PresenceType.dnd;
    	}
    	for(String sipStatus : status.values()){
   		    if (sipStatus.equals(PresenceType.away.toString()))
   			    return PresenceType.away;
    	}
		return PresenceType.unavailable;
	}
    
	public static String getPhoto_url(String url){
		if(url==null)return null;
		if(url.indexOf("ws://")!=-1){
			url = url.replaceAll("ws://", "http://");
		}
		else if(url.indexOf("wss://")!=-1)
			url = url.replaceAll("wss://", "https://");
		
		url = url.substring(0, indexOf(url,"/",4));
		url = url + "UploadServlet";
		return url;
	}
	
	public static int indexOf(String str, String regex, int no){
		int index = 0;
		for(int i=0; i<no; i++){
			if(str.indexOf(regex, index)!=-1){
				index = str.indexOf(regex, index)+1;
			}else
				break;
		}
		return index;
	}
}
