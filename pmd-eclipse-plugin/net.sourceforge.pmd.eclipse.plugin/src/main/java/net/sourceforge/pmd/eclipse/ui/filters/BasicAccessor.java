package net.sourceforge.pmd.eclipse.ui.filters;

import net.sourceforge.pmd.eclipse.ui.filters.FilterHolder.Accessor;

/**
 * 
 * @author Brian Remedios
 */
class BasicAccessor implements Accessor {
	
	public boolean boolValueFor(FilterHolder fh) { return false; }

	public String textValueFor(FilterHolder fh) { return null; }
}