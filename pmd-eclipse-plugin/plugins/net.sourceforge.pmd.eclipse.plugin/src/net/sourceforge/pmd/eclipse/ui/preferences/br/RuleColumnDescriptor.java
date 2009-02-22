package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * @author Brian Remedios
 */
public interface RuleColumnDescriptor {

    String label();
    RuleFieldAccessor accessor();
    Image imageFor(Rule rule);
    String stringValueFor(Rule rule);
    TreeColumn newTreeColumnFor(Tree parent, int columnIndex, RuleSortListener sortListener, Map<Integer, List<Listener>> paintListeners);
}