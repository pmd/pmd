package net.sourceforge.pmd.eclipse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.CellPainterBuilder;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleCollection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractCellPainterBuilder implements CellPainterBuilder {

	private Font 					standardFont;
	private Font 					hasIssueFont;
	
	private static ColourManager 	colourManager;
	
	private static final int IssueFontStyle = SWT.ITALIC;
	
    public static void addListener(Control control, int eventType, Listener listener, Map<Integer, List<Listener>> listenersByEventCode) {
        
        Integer eventCode = Integer.valueOf(eventType);
        
        control.addListener(eventType, listener);
        if (!listenersByEventCode.containsKey(eventCode)) {
            listenersByEventCode.put(eventCode, new ArrayList<Listener>());
            }
        
        listenersByEventCode.get(eventCode).add(listener);
    }
	
    protected static ColourManager colorManager() {

        if (colourManager != null) return colourManager;

        colourManager = ColourManager.managerFor(Display.getCurrent());
        return colourManager;
    }
    
    
    public void dispose() {
    	colorManager().dispose();
    }
    
    
    protected int widthOf(int columnIndex, Tree tree) {
    	return tree.getColumn(columnIndex).getWidth();
    }
    
    protected static Font deriveHasIssueFontFrom(Font sourceFont, Display display) {
    	
    	FontData fd = sourceFont.getFontData()[0];
    	return new Font(display, new FontData( fd.getName(), fd.getHeight(), IssueFontStyle));
    }
    
    protected void setFontsFrom(Control control) {
    	
    	standardFont = control.getFont();
    	hasIssueFont = deriveHasIssueFontFrom(standardFont, control.getDisplay());
    }
    
    protected Font fontFor(Control control, Rule rule) {
    	
    	if (standardFont == null) setFontsFrom(control);
    	
    	return rule.dysfunctionReason() != null ?
    			hasIssueFont :
    			standardFont;
    }

    protected Rule ruleFrom(TreeItem tItem) {
    	Object item = tItem.getData();
    	return item instanceof Rule ? (Rule)item : null;
    }
    
    protected Object valueFor(TreeItem tItem, RuleFieldAccessor getter) {
    
            Object item = tItem.getData();
            
            if (item instanceof Rule) {
            	return getter.valueFor((Rule) item);
            }
            
            if (item instanceof RuleCollection) {
            	return getter.valueFor((RuleCollection)item);
            }
            
            return null;
    }
    
    protected String textFor(TreeItem tItem, RuleFieldAccessor getter) {
        
        Object item = tItem.getData();
        
        if (item instanceof Rule) {
        	return getter.labelFor((Rule) item);
        }
        
        if (item instanceof RuleCollection) {
        	return String.valueOf(
        			getter.valueFor((RuleCollection)item)
        			);
        }
        
        return null;
}
}
