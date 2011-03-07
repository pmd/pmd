package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.ColumnDescriptor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 *
 * @author Brian Remedios
 */
public interface RuleColumnDescriptor extends ColumnDescriptor {

    RuleFieldAccessor accessor();
    Image imageFor(Rule rule);
    Image imageFor(RuleCollection collection);
    String stringValueFor(Rule rule);
    String stringValueFor(RuleCollection collection);
    String detailStringFor(Rule rule);
    String detailStringFor(RuleGroup group);
    TreeColumn newTreeColumnFor(Tree parent, int columnIndex, SortListener sortListener, Map<Integer, List<Listener>> paintListeners);
}