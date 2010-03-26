package net.sourceforge.pmd.eclipse.ui.quickfix;

public abstract class AbstractFix implements Fix {

	private final String label;
	protected AbstractFix(String theLabel) {
		label = theLabel;
	}
	
    /**
     * @see net.sourceforge.pmd.eclipse.Fix#getLabel()
     */
    public String getLabel() {
        return label;
    }

}
