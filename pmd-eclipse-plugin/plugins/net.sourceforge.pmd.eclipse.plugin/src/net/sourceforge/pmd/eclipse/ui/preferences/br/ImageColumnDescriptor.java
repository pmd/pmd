package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 */
public class ImageColumnDescriptor extends AbstractRuleColumnDescriptor {

    private String             imagePath;
    private CellPainterBuilder painterBuilder;
    
    public static final RuleColumnDescriptor filterExpression  = new ImageColumnDescriptor("Filters", SWT.LEFT, 25, RuleFieldAccessor.violationRegex, false, PMDUiConstants.ICON_FILTER, Util.regexBuilderFor(16, 16));

    
    public ImageColumnDescriptor(String labelKey, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath, CellPainterBuilder thePainterBuilder) {
        super(labelKey, theAlignment, theWidth, theAccessor, resizableFlag);
        
        imagePath = theImagePath;
        painterBuilder = thePainterBuilder;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSortListener, java.util.Map)
     */
    public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, final RuleSortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
        TreeColumn tc = buildTreeColumn(parent, sortListener);
        tc.setToolTipText(label());       
        if (imagePath != null) tc.setImage(ResourceManager.imageFor(imagePath));        
        if (painterBuilder != null) painterBuilder.addPainterFor(tc.getParent(), columnIndex, accessor(), paintListeners);        
        return tc;
    }

    public String stringValueFor(Rule rule) {
        return "";
    }
    
    public Image imageFor(Rule rule) {
        return null;
    }
}
