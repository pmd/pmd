package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 *
 */
public abstract class AbstractColumnDescriptor implements ColumnDescriptor {

	private final String label;
	private final String tooltip;
	private final int alignment;
	private final int width;
	private final boolean isResizable;
	private final String imagePath;
	
	public AbstractColumnDescriptor(String labelKey, int theAlignment, int theWidth, boolean resizableFlag, String theImagePath) {
		super();
		
        label = SWTUtil.stringFor(labelKey);
        tooltip = SWTUtil.tooltipFor(labelKey);
        alignment = theAlignment;
        width = theWidth;
        isResizable = resizableFlag;
        imagePath = theImagePath;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.eclipse.ui.preferences.br.ColumnDescriptor#label()
	 */
	public String label() { return label; }

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.eclipse.ui.preferences.br.ColumnDescriptor#tooltip()
	 */
	public String tooltip() { return tooltip;  }

    protected TreeColumn buildTreeColumn(Tree parent, final RuleSortListener sortListener) {

        final TreeColumn tc = new TreeColumn(parent, alignment);
        tc.setWidth(width);
        tc.setResizable(isResizable);
        tc.setToolTipText(tooltip);
        if (imagePath != null) tc.setImage(ResourceManager.imageFor(imagePath));

        return tc;
    }
}