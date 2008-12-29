package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

public interface CellPainterBuilder {

	void addPainterFor(Tree tree, int columnIndex, RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode);
}
