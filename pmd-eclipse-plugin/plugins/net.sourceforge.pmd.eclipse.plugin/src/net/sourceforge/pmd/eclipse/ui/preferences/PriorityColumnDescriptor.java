package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.AbstractColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.PriorityFieldAccessor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Brian Remedios
 */
public class PriorityColumnDescriptor extends AbstractColumnDescriptor {
	
	private PriorityFieldAccessor<?> accessor;
	
	public PriorityColumnDescriptor(String labelKey, int theAlignment, int theWidth, boolean resizableFlag, PriorityFieldAccessor<?> theAccessor) {
		super(labelKey, theAlignment, theWidth, resizableFlag, null);
		
		accessor = theAccessor;
	}

    protected Object valueFor(RulePriority priority) {
        return accessor.valueFor(priority);
    }
    
    protected Image imageFor(RulePriority priority) {
        return accessor.imageFor(priority);
    }

	public static final PriorityColumnDescriptor name   		= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_NAME, SWT.LEFT, 25, true, PriorityFieldAccessor.name);
	public static final PriorityColumnDescriptor value  		= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_VALUE, SWT.RIGHT, 25, true, PriorityFieldAccessor.value);
//	public static final PriorityColumnDescriptor size  			= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_SIZE, SWT.RIGHT, 25, true, PriorityFieldAccessor.size);
	public static final PriorityColumnDescriptor image  		= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_SHAPE, SWT.CENTER, 25, true, PriorityFieldAccessor.image);
//	public static final PriorityColumnDescriptor color  		= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_COLOR, SWT.RIGHT, 25, true, PriorityFieldAccessor.color);
	public static final PriorityColumnDescriptor description	= new PriorityColumnDescriptor(StringKeys.PRIORITY_COLUMN_DESC, SWT.LEFT, 25, true, PriorityFieldAccessor.description);
    
	public static final PriorityColumnDescriptor[] VisibleColumns = new PriorityColumnDescriptor[] { image, name, value, description };
}
