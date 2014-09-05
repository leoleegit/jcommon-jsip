package org.jcommon.com.jsip;

import org.jcommon.com.jsip.utils.PresenceType;

public interface PresenceListener {
	public void presenceChange(String user, PresenceType type);
}
