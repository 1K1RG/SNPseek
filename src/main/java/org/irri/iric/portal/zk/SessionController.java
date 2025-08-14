package org.irri.iric.portal.zk;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Executions;

/**
 * Manages sessions
 */
public class SessionController {
	/**
	 * Returns the session object
	 *
	 * @return The session
	 */
	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

	/**
	 * Gets a object from the current session
	 *
	 * @param name
	 *            The name of the object to get.
	 * @return The object
	 */
	public Object getSessionObject(String name) {
		HttpSession session = getSession();
		return session.getAttribute(name);
	}

	/**
	 * Returns true if the session instance is new. False, if not.
	 *
	 * @return The status of the session
	 */
	public boolean sessionIsNew() {
		HttpSession session = getSession();
		return session.isNew();
	}

	/**
	 * Sets a new session object
	 *
	 * @param name
	 *            The name of the object.
	 * @param object
	 *            The object
	 */
	public void setSessionObject(String name, Object object) {
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(name, object);
		}
	}
}