package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractColumnDescriptor implements ColumnDescriptor {

	private final String id;
	private final String label;
	private final String tooltip;
	private final int alignment;
	private final int width;
	private final boolean isResizable;
	private final String imagePath;
	
	public static final String DescriptorKey = "descriptor";
	
	public AbstractColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, boolean resizableFlag, String theImagePath) {
		super();
		
		id = theId;
        label = SWTUtil.stringFor(labelKey);
        tooltip = SWTUtil.tooltipFor(labelKey);
        alignment = theAlignment;
        width = theWidth;
        isResizable = resizableFlag;
        imagePath = theImagePath;
	}

	public String id() { return id; };
	
	public String label() { return label; }

	public String tooltip() { return tooltip;  }

    protected TreeColumn buildTreeColumn(Tree parent) {

        final TreeColumn tc = new TreeColumn(parent, alignment);
        loadCommon(tc);
        tc.setWidth(width);
        tc.setResizable(isResizable);
        tc.setToolTipText(tooltip);
        
        return tc;
    }
    
    public TableColumn buildTableColumn(Table parent) {

        final TableColumn tc = new TableColumn(parent, alignment);
        loadCommon(tc);
    	tc.setText(label);
        tc.setWidth(width);
        tc.setResizable(isResizable);
        tc.setToolTipText(tooltip);
        
        return tc;
    }
    
    private void loadCommon(Item column) {
    	column.setData(DescriptorKey, this);
    	if (imagePath != null) column.setImage(ResourceManager.imageFor(imagePath));
    }
}