package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;

import org.eclipse.swt.widgets.TabItem;


public interface RulePropertyManager {

    void tab(TabItem tab);
    boolean isActive();
    void manage(RuleSelection rules);
}
