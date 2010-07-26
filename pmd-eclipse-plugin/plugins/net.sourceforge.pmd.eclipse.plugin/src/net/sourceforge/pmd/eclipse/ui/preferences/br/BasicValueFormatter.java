package net.sourceforge.pmd.eclipse.ui.preferences.br;

/**
 * 
 * @author Brian Remedios
 */
public class BasicValueFormatter implements ValueFormatter {
	
	/**
	 * Override in subclasses
	 */
	public String format(Object value) {
		StringBuilder sb = new StringBuilder();
		format(value, sb);
		return sb.toString();
	}
	
	/**
	 * Override in subclasses
	 */
	public void format(Object value, StringBuilder target) {
		target.append(value);
	}

}
