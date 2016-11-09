package org.pwsafe.passwordsafeswt.model.comparator;

import java.util.Comparator;

public class EqualsIgnoreCasecomparator implements Comparator<String> {

	@Override
	public int compare(final String o1, final String o2) {
		return o1.compareToIgnoreCase(o2);
	}

}
