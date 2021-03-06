/*
 * Copyright (c) 2008-2014 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.action;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.pwsafe.passwordsafeswt.PasswordSafeJFace;

/**
 * Save command.
 * 
 * @author Glen Smith
 */
public class SaveFileAction extends Action {

	public SaveFileAction() {
		super(Messages.getString("SaveFileAction.Label")); //$NON-NLS-1$
		setAccelerator(SWT.MOD1 | 'S');
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("org/pwsafe/passwordsafeswt/images/tool_newbar_save.gif"))); //$NON-NLS-1$
		setToolTipText(Messages.getString("SaveFileAction.Tooltip")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		PasswordSafeJFace app = PasswordSafeJFace.getApp();
		try {
			app.saveFile();
			// TODO: more use for status line
			// app.setStatusMessage("file saved successfully");
		} catch (IOException e1) {
			app.displayErrorDialog(
					Messages.getString("SaveFileAction.ErrorDialog.Title"), e1.getMessage(), e1); //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
			app.displayErrorDialog(
					Messages.getString("SaveFileAction.ErrorDialog.Title"), e.getMessage(), e); //$NON-NLS-1$
		}

	}

}