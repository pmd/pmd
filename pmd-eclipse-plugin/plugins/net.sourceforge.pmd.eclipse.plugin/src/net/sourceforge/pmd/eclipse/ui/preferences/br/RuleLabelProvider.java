package net.sourceforge.pmd.eclipse.ui.preferences.br;

import org.eclipse.swt.graphics.Image;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.AbstractTableLabelProvider;

/**
 * 
 * @author Brian Remedios
 */
public class RuleLabelProvider extends AbstractTableLabelProvider {
	
    private RuleColumnDescriptor[] columnDescriptors;
    
    /**
     * Constructor for RuleLabelProvider.
     * @param columns RuleColumnDescriptor[]
     */
    public RuleLabelProvider(RuleColumnDescriptor[] columns) {
    	columnDescriptors = columns;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
       
        if (element instanceof RuleGroup) {
        	RuleGroup rg = (RuleGroup)element;
        	if (columnIndex == 0) {
        		String label = rg.label();
        		return standardized(label, rg.ruleCount());
        	}
        	return "";
        }
                
        if (element instanceof Rule) {
        	Rule rule = (Rule) element;        	
        	return columnDescriptors[columnIndex].stringValueFor(rule);      
        }

        return "??";
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
       
        if (element instanceof RuleGroup) {
            return null;
        }
                
        if (element instanceof Rule) {
            Rule rule = (Rule) element;
            return columnDescriptors[columnIndex].imageFor(rule);
        }

        return null;		// should never get here
    }
    
    /**
     * @param rawLabel String
     * @param count int
     * @return String
     */
    private String standardized(String rawLabel, int count) {
    
    	int rulesPos = rawLabel.indexOf(" Rules");
    	String filteredLabel = rulesPos > 0 ?
    			rawLabel.substring(0, rulesPos) : rawLabel;
    			
    	return filteredLabel + "  (" + count + ")";
    }
    
}
