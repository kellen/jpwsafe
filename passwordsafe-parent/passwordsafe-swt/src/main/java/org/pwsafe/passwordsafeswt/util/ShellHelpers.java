/*
 * Copyright (c) 2008-2014 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.util;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility routines related to shells (ie. windows).
 *
 * @author Glen Smith
 */
public class ShellHelpers {

	public static void centreShell(final Shell parent, final Shell shell) {
		final Rectangle bounds = parent.getBounds();
		final Rectangle rect = shell.getBounds();
		final int x = bounds.x + (bounds.width - rect.width) / 2;
		final int y = bounds.y + (bounds.height - rect.height) / 22 * 7;// approx Golden ratio
		shell.setLocation(x, y);
	}

}
