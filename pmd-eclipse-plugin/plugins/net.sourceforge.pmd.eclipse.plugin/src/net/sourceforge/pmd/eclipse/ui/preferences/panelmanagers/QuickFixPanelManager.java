package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.quickfix.Fix;
import net.sourceforge.pmd.eclipse.ui.quickfix.PMDResolutionGenerator;
import net.sourceforge.pmd.eclipse.util.ResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class QuickFixPanelManager extends AbstractRulePanelManager {

    private org.eclipse.swt.widgets.List    fixerList;
    private ListManager						listManager;
    
	public QuickFixPanelManager(ValueChangeListener theListener) {
		super(theListener);
	}
    
	protected List<Fix> commonFixes() {
		// TODO finish this
		List<Rule> theRules = rules.allRules();
		List<Fix> fixes = new ArrayList<Fix>();
		
		Fix[] fixSet = PMDResolutionGenerator.fixesFor(theRules.get(0));
		if (fixSet != null) {
			for (Fix fix : fixSet) fixes.add(fix);
		}
		
		return fixes;
	}
	
	@Override
	protected void adapt() {
		 
		fixerList.removeAll();
		
		List<Fix> fixes = commonFixes();
		 
		for (Fix fix : fixes) fixerList.add(fix.getLabel());
	}

    protected boolean canManageMultipleRules() { return true; }

	@Override
	protected void clearControls() {
		fixerList.removeAll();
	}

    protected void setVisible(boolean flag) {        
        fixerList.setVisible(flag);
    }

    public static <T> List<T> shift(List<T> items, int[] indices, int shiftAmt) {
    	
    	int[] indexArr = new int[items.size()];
    	
    	int currentIdx = 0;
    	for (int i=0; i<items.size(); i++) {
    		if (currentIdx < indices.length && i == indices[currentIdx] + shiftAmt) {
    			indexArr[i] = indices[currentIdx++];
    		} else {
    			indexArr[i] = i - currentIdx;
    		}
    	}
    	
    	List<T> out = new ArrayList<T>(indexArr.length);
    	for (int i=0; i<indexArr.length; i++) {
    		out.add( items.get(indexArr[i])  );
    	}
    	
    	return out;
    }
    
    public static <T> List<T> shift(List<T> items, int index, int shiftAmt) {
    	
    	int start = Math.min(index, index + shiftAmt);
    	int end = Math.max(index, index + shiftAmt);
    	
    	Collections.rotate(items.subList(start, end), shiftAmt);
    	
    	return items;
    }
    
	 public Control setupOn(Composite parent) {
	        
	        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	        
	        Composite panel = new Composite(parent, 0);
	        GridLayout layout = new GridLayout(2, false);
	        panel.setLayout(layout);
	        
	        fixerList = new org.eclipse.swt.widgets.List(panel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL); 
	        gridData = new GridData(GridData.FILL_BOTH);
	        gridData.grabExcessHorizontalSpace = true;
	        gridData.horizontalSpan = 1;
	        fixerList.setLayoutData(gridData);    

	        Composite buttonPanel = new Composite(panel, 0);
	        layout = new GridLayout(1, false);
	        buttonPanel.setLayout(layout);
	        gridData = new GridData();
	        gridData.horizontalSpan = 1;
	        gridData.grabExcessHorizontalSpace = false;
	        buttonPanel.setLayoutData(gridData);        
	        	        
	        Button shiftUpButton = new Button(buttonPanel, SWT.PUSH);
	        shiftUpButton.setToolTipText("Shift up");
	        shiftUpButton.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_UPARROW));
	        
	        Button addButton = new Button(buttonPanel, SWT.PUSH);
	        addButton.setToolTipText("Add");
	        addButton.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_ADD));
	        
	        Button removeButton = new Button(buttonPanel, SWT.PUSH);
	        removeButton.setToolTipText("Remove");
	        removeButton.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_DELETE));
	        
	        Button shiftDownButton = new Button(buttonPanel, SWT.PUSH);
	        shiftDownButton.setToolTipText("Shift down");
	        shiftDownButton.setImage(ResourceManager.imageFor(PMDUiConstants.ICON_BUTTON_DOWNARROW));
	        
	        listManager = new ListManager(fixerList, shiftUpButton, shiftDownButton, removeButton);
	        
	        return panel;    
	    }
	 
	 public static void main(String[] args) {
		 
		 List<Integer> numbers = new ArrayList<Integer>();
		 Collections.addAll(numbers, 0,1,2,3,4,5,6,7,8,9,10);
//		 int[] shiftSet = new int[] { 4, 6 };
		 
		 List<Integer> newNumbers = shift(numbers, 5, 3);
		 
		 System.out.println(newNumbers);
	 }
}
