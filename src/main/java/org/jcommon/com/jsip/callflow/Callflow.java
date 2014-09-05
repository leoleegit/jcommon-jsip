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
package org.jcommon.com.jsip.callflow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

public abstract class Callflow implements CallflowListener {

	@Override
	public void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMessage(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doAck(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doBye(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doRefer(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doNotify(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPublish(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSubscribe(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSuccessResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doErrorResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
