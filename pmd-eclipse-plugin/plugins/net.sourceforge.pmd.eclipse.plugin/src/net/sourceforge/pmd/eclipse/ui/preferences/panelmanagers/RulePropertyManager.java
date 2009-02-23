package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;

import org.eclipse.swt.widgets.TabItem;


public interface RulePropertyManager {

    public void tab(TabItem tab);
    public void manage(RuleSelection rules);
}
