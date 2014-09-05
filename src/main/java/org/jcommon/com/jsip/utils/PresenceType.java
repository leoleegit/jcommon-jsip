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

/**
 * 
 * @author leoLee
 *
 */
public enum PresenceType {
	 /**
     * Available 
     */
    Available,

    /**
     * Away
     */
    away,

    /**
     * XA (extended away)
     */
    xa,

    /**
     * DND (do not disturb)
     */
    dnd,

    /**
     * Chat (free to chat)
     */
    chat,

    /**
     * Unavailable (offline)
     */
    unavailable,

    /**
     * Unknown
     */
    unknown;
    
    @Override
    public String toString(){
    	switch(this){
    		case Available  :
    		case chat       : return "NLN";
    		case away       :
    		case xa         : return "AWY";
    		case dnd        : return "BSY";
    		case unavailable:
    		default         : return "FLN";
    		
    	}
    }

    public static final String spotlight = "spotlight";
    public static final String xlite     = "xlite";
    public static String type = spotlight;
    
    public String toString(String client){
    	if(spotlight.equals(client)){
        	switch(this){
    		case Available  :
    		case chat       : return "<rpid:reachable/>";
    		case away       :
    		case xa         : return "<rpid:away/>";
    		case dnd        : return "<rpid:not_reachable/>";
    		case unavailable: 
    		default         : return "";
    		
    	}
    	}else if(xlite.equals(client)){
        	switch(this){
    		case Available  :
    		case chat       : return "<rpid:unknown/>";
    		case away       :
    		case xa         : return "<rpid:away/>";
    		case dnd        : return "<rpid:busy/>";
    		case unavailable:
    		default         : return "<rpid:unknown/>";
    		
        	}
    	}else return "unkown";
    }
}
