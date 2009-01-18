package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * A custom control intended to display and accept Type values. New values are validated
 * when the widget loses focus, if the text represents a recognized class then it is
 * re-rendered with its full package name. If it isn't recognized or is a disallowed
 * primitive then the entry is cleared.
 * 
 * @author Brian Remedios
 */
public class TypeText extends Composite {

    private Text    text;
    private boolean acceptPrimitives;
    
    public TypeText(Composite parent, int style, boolean primitivesOK) {
        super(parent, SWT.None);
        
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;     layout.horizontalSpacing = 0;
        layout.marginHeight = 0;        layout.marginWidth = 0;
        this.setLayout(layout);
        
        text = new Text(this, style);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        acceptPrimitives = primitivesOK;
    }
        
    public void addListener(int eventType, Listener listener) {
        text.addListener(eventType, listener);
    }
    
    public void removeListener(int eventType, Listener listener) {
        text.removeListener(eventType, listener);
    }
    
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return text.computeSize(wHint, hHint, changed);
    }
    
    public void setType(Class<?> cls) {
        
        if (cls == null) {
            text.setText("");
            return;
        }
        
        if (cls.isPrimitive() && !acceptPrimitives) {
            setType(null);
            return;
        }
        
        text.setText(cls.getName());
    }
    
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        text.setEnabled(flag);
    }
    
    public void setEditable(boolean flag) {
        text.setEditable(flag);
    }
    
    public Class<?> getType(boolean doCleanup) {
        
        String typeStr = text.getText().trim();
        if (StringUtil.isEmpty(typeStr)) {
            if (doCleanup) setType(null);
            return null;
        }
        
        Class<?> cls = ClassUtil.getTypeFor(typeStr);
        if (cls.isPrimitive() && !acceptPrimitives) {
            cls = null;
        }
        
        if (cls != null) {
            if (doCleanup) setType(cls);
            return cls;
        }
        
        try {
            return Class.forName(typeStr);
        } catch (Exception ex) {
            if (doCleanup) setType(null);
            return null;
        }
    }
}
