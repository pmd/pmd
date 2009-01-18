package net.sourceforge.pmd.eclipse.ui.preferences.editors;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A general purpose selection widget that deals with methods. Once the user types in a valid type
 * in the left-most text field, all methods that are part of it are listed in the combobox on the
 * right. 
 * 
 * Note: Uses the default class loader to lookup the class, we'll probably want to supply an
 * external one in the future?
 * 
 * @author Brian Remedios
 */
public class MethodPicker extends Composite {

    private TypeText    typeText;
    private Combo       methodList;    
    private Method[]    methods;    
    private String[]    unwantedPrefixes;
    
    public MethodPicker(Composite parent, int style, String[] theUnwantedPrefixes) {
        super(parent, SWT.None);
        
        unwantedPrefixes = theUnwantedPrefixes == null ?
            StringUtil.EMPTY_STRINGS : theUnwantedPrefixes;
        
        GridLayout layout = new GridLayout(2, true);
        layout.verticalSpacing = 0;     layout.horizontalSpacing = 0;
        layout.marginHeight = 0;        layout.marginWidth = 0;
        this.setLayout(layout);
        
        typeText = new TypeText(this, style, false);
        typeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeText.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event) {
                reviseMethodListFor(typeText.getType(true));
            }
        });
        methodList = new Combo(this, style);
        methodList.setLayoutData(new GridData(GridData.FILL_BOTH));
    }
        
    private void reviseMethodListFor(Class<?> cls) {
        
        if (cls == null) {
            methodList.removeAll();
            methodList.setEnabled(false);
            return;
        }
        
        methodList.setEnabled(true);
        methods = cls.getMethods();
        Arrays.sort(methods, Util.MethodNameComparator);
        String[] items = new String[methods.length];
        for (int i=0; i<methods.length; i++) items[i] = Util.signatureFor(methods[i], unwantedPrefixes);
        
        methodList.setItems(items);
        methodList.select(0);
    }
    
    public Point computeSize(int wHint, int hHint, boolean changed) {
        Point pt = typeText.computeSize(wHint, hHint, changed);
        pt.x *= 2;
        pt.y += 2;  // !@#$ combobox is taller than text box, need to avoid cropping it
        return pt;
    }
    
    public void setType(Class<?> cls) {        
        typeText.setType(cls);
    }
    
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        typeText.setEnabled(flag);
        methodList.setEnabled(flag);
    }
    
    public void setEditable(boolean flag) {
        typeText.setEditable(flag);
    }
    
    public Class<?> getType(boolean doCleanup) {        
        return typeText.getType(doCleanup);
    }
    
    private int indexOf(Method method) {
        if (methods == null) return -1;
        for (int i=0; i<methods.length; i++) {
            if (methods[i].equals(method)) return i;
        }
        return -1;
    }
    
    public void setMethod(Method method) {
        
        if (method == null) {
            typeText.setType(null);
            return;
        }
        Class<?> cls = method.getDeclaringClass();
        typeText.setType(cls);
        reviseMethodListFor(cls);
        methodList.select(indexOf(method));
    }
    
    public Method getMethod() {
        
        return methods == null ? 
            null :
            methods[methodList.getSelectionIndex()];
    }
    
    public void addSelectionListener(SelectionListener listener) {
        methodList.addSelectionListener(listener);  
    }
}
