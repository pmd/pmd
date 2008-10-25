package net.sourceforge.pmd.eclipse.ui.preferences.br;

import org.eclipse.swt.widgets.Tree;

public interface CellPainterBuilder {

	void addPainterFor(Tree tree, int columnIndex, RuleFieldAccessor getter);
}
