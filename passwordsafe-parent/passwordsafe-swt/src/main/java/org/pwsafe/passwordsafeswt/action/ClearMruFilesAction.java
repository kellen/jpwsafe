/*
 * $Id$
 * Copyright (c) 2008-2014 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.action;

import org.eclipse.jface.action.Action;
import org.pwsafe.passwordsafeswt.PasswordSafeJFace;
import org.pwsafe.passwordsafeswt.util.UserPreferences;

/**
 * Clears the list of recently/ most used password safes.
 * 
 * @author timmydog
 */
public class ClearMruFilesAction extends Action {

	public ClearMruFilesAction() {
		super(Messages.getString("ClearMruFilesAction.Label")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ClearMruFilesAction.Tooltip")); //$NON-NLS-1$
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		PasswordSafeJFace app = PasswordSafeJFace.getApp();

		UserPreferences.getInstance().clearMRUFiles();
		app.updateFileMenu();

	}

}