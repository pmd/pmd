package net.sourceforge.pmd.eclipse.ui.filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Brian Remedios
 */
class FilterHolder {

	public String	pattern;
	public boolean  forPMD;
	public boolean	forCPD;
	public boolean	isInclude;
	
	public static final FilterHolder[] EMPTY_HOLDERS = new FilterHolder[0];
	
	public FilterHolder(String thePattern, boolean pmdFlag, boolean cpdFlag, boolean isIncludeFlag) {
		pattern = thePattern;
		forPMD = pmdFlag;
		forCPD = cpdFlag;
		isInclude = isIncludeFlag;
	}
	
	public interface Accessor {
		boolean boolValueFor(FilterHolder fh);
		String textValueFor(FilterHolder fh);
	}
	
	public static final Accessor ExcludeAccessor = new BasicAccessor() {
		public boolean boolValueFor(FilterHolder fh) { return !fh.isInclude; }
	};
	
	public static final Accessor IncludeAccessor = new BasicAccessor() {
		public boolean boolValueFor(FilterHolder fh) { return fh.isInclude; }
	};
	
	public static final Accessor PMDAccessor = new BasicAccessor() {
		public boolean boolValueFor(FilterHolder fh) { return fh.forPMD; }
	};
	
	public static final Accessor CPDAccessor = new BasicAccessor() {
		public boolean boolValueFor(FilterHolder fh) { return fh.forCPD; }
	};
	
	public static final Accessor PatternAccessor = new BasicAccessor() {
		public String textValueFor(FilterHolder fh) { return fh.pattern; }
	};
	
	public static Boolean boolValueOf(Collection<FilterHolder> holders, Accessor boolAccessor) {
		Set<Boolean> values = new HashSet<Boolean>();
		for (FilterHolder fh : holders) values.add(boolAccessor.boolValueFor(fh));
		int valueCount = values.size();
		return (valueCount == 2 || valueCount == 0) ? null : values.iterator().next(); 
	}
	
	public static String textValueOf(Collection<FilterHolder> holders, Accessor textAccessor) {
		Set<String> values = new HashSet<String>();
		for (FilterHolder fh : holders) values.add(textAccessor.textValueFor(fh));
		return (values.size() == 1) ? values.iterator().next() : ""; 
	}
}
