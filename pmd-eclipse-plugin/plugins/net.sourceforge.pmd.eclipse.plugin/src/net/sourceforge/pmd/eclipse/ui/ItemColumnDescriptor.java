package net.sourceforge.pmd.eclipse.ui;


import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Brian Remedios
 */
public class ItemColumnDescriptor<T extends Object, V extends Object> extends AbstractColumnDescriptor {
	
	private final ItemFieldAccessor<T, V> accessor;
	
	public ItemColumnDescriptor(String theId, String labelKey, int theAlignment, int theWidth, boolean resizableFlag, ItemFieldAccessor<T, V> theAccessor) {
		super(theId, labelKey, theAlignment, theWidth, resizableFlag, null);
		
		accessor = theAccessor;
	}

    public T valueFor(V item) {
        return accessor.valueFor(item);
    }
    
    public Image imageFor(V item) {
        return accessor.imageFor(item);
    }

}
