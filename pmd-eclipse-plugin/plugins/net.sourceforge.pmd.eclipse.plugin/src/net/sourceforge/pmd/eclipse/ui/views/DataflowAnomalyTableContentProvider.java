package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.java.rule.controversial.DaaRuleViolation;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provides the Content for the DataflowAnomalyTable
 * 
 * @author SebastianRaffel  ( 07.06.2005 )
 */
public class DataflowAnomalyTableContentProvider implements IStructuredContentProvider {
	private static final int MAX_ROWS = 200;
    
	/* @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object) */
	public Object[] getElements(Object inputElement) {
        Object[] result = new Object[0];
	    if (inputElement instanceof Iterator) {
            final Iterator violationsIterator = (Iterator)inputElement;
            final List violations = new ArrayList();
            for (int count = 0; violationsIterator.hasNext() && count < MAX_ROWS; count++) {
                final DaaRuleViolation violation = (DaaRuleViolation) violationsIterator.next();
                if (!violationIsInList(violation, violations)) {
                    violations.add(violation);
                }
            }
            
            result = violations.toArray(new DaaRuleViolation[violations.size()]);
        }
        return result;
	}
	
	/* @see org.eclipse.jface.viewers.IContentProvider#dispose() */
	public void dispose() {
        // do nothing
	}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }
    
    private boolean violationIsInList(DaaRuleViolation newViolation, List list) {
        
        final Iterator violationIterator = list.iterator();
        while (violationIterator.hasNext()) {
            
            final DaaRuleViolation violation = (DaaRuleViolation) violationIterator.next();
            if (violation.getVariableName().equals(newViolation.getVariableName())
                    && violation.getType().equals(newViolation.getType())
                    && violation.getBeginLine() == newViolation.getBeginLine()
                    && violation.getEndLine() == newViolation.getEndLine()) {
                return true;              
            }
        }
        return false;
    }

}
