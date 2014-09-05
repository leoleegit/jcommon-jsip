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

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright (c) Ericsson AB, 2004-2007. All rights reserved.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.sip.SipSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

/**
 * @author leoLee
 *
 * This class is intended to hold the presence state for a simple
 * presence servlet implementation
 */
public class PresenceInfo {
	private Logger logger = Logger.getLogger(this.getClass());  
	 
	private String _id;
	private Set<SipSession> _subscriptions = new HashSet<SipSession>();
	private byte[] _lastPublish;
	private String _contentType = "application/presence";
	private Set<String> _accept = new HashSet<String>();

	private String _entity = null;
	private String _xmlns = null;
 	private Map<String,Element> _tuples = new HashMap<String,Element> ();
	private Map<String,Element> _globalState = new HashMap<String,Element> ();

	private Date date_lastPublish;
	
	public PresenceInfo(String id, SipSession subscription, long expiry) {
		_id = id;
		addSipSession( subscription, expiry );
	}
	
	public PresenceInfo(String id, String contentType ) {
		_id = id;
		_contentType = contentType;
	}
	
	public String getId() { return _id; }
	public void setId( String id ) { _id = id; }
   
	public void addSipSession(SipSession session, long expiry){
		if(expiry != 0)
			addSipSession(new PresenceSession(_subscriptions, session, expiry));		
		else
			removeSipSession(session);
	}
	
	private void addSipSession(SipSession session){
		_subscriptions.add(session);
		logger.debug("add session :"+ session +" status:"+session.getState());
	}
	
	public void removeSipSession(SipSession session){
		if(_subscriptions.contains(session)){
			((PresenceSession)session).removeSession();
			logger.info(this.getClass().getName()+"remove _subscriptions:"+session);
		}
	}
	
	public Iterator<SipSession> getSipSessions(){return _subscriptions.iterator();}
	
	
    public Set<String> getAccept() { return _accept; }
    public void setAccept( Set<String> a ) { _accept = a; }
	
	public byte[] getLastPublish() throws ServletException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
			
			DOMImplementation impl = builder.getDOMImplementation();
			Document doc = builder.newDocument();
			Element presence = doc.createElement("presence");
			presence.setAttribute("entity",_entity);
			presence.setAttribute("xmlns",_xmlns);
			presence.setAttribute("xmlns:hs", "http://www.hotsip.com/presence-1.0");
			Iterator<Element> tuples = _tuples.values().iterator();
			while( tuples.hasNext() ) { presence.appendChild( doc.adoptNode( tuples.next() ) ); }
			
			Iterator<Element> globalState = _globalState.values().iterator();
			while( globalState.hasNext() ) 
			{ 
				Element e = globalState.next();
				e.removeAttribute("xmlns:hs");
				presence.appendChild( doc.adoptNode( e ) );
			}			
			doc.appendChild( presence );
			
			DOMImplementationLS ls = (DOMImplementationLS)impl.getFeature("LS","3.0");
			LSSerializer serializer = ls.createLSSerializer();
			
			LSOutput out = ls.createLSOutput();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			out.setByteStream( baos );
			out.setEncoding("UTF-8");
			serializer.write(doc,out);
			_lastPublish   = baos.toByteArray();
			return _lastPublish;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ServletException("Parsing problem",e);
		}
	}
	
	public void setLastPublish( byte[] body, boolean expired ) throws ServletException {
		//_lastPublish = body;
		//System.out.println(new String(body));
		if(body==null)
			return;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			DOMImplementationLS ls = (DOMImplementationLS)impl.getFeature("LS","3.0");
			LSParser lsparser = ls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			LSInput input = ls.createLSInput();
			input.setByteStream(new ByteArrayInputStream(body));
			Document doc = lsparser.parse(input);
			
			//String name = doc.getLocalName();
			Element e = doc.getDocumentElement();
			if( e.getNodeName().equals("presence") ) {
				_entity = e.getAttribute("entity");
				_xmlns = e.getAttribute("xmlns");
				NodeList nl = e.getChildNodes();
				for(int i = 0;i<nl.getLength();i++) {
					Node n = nl.item(i);
					if( n.getNodeType() == Node.ELEMENT_NODE ) {
						Element e2 = (Element) n;
						if(e2.getNodeName().equals("tuple")) {
							String id = e.getAttribute("id");
							if( expired ) { _tuples.remove(id); }
							else { _tuples.put(id,e2); }
						}
						else { // Hard state nodes
							String name2 = e2.getNodeName();
							//if( !name2.equals("note") )
								_globalState.put(name2,e2);
							//System.out.println("name2 = "+name2);
						}
					}
					//System.out.println("n = "+n);
				}
			}
			setDate_lastPublish(new Date());
		}
		catch(Exception e){ throw new ServletException("Parsing problem",e);}
	}
	
	public String getContentType() { return _contentType; }
	public void setContentType( String contentType ) { _contentType = contentType; }

	private void setDate_lastPublish(Date date_lastPublish)
	{
		this.date_lastPublish = date_lastPublish;
	}

	public Date getDate_lastPublish()
	{
		return date_lastPublish;
	}
	
	public void setInStorage(){
		 PresenceStore.instance().setPresenceInfo( _id, this );
	}
}
