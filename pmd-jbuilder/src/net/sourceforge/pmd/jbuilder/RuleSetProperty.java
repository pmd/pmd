package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.GlobalProperty;
import net.sourceforge.pmd.RuleSet;

/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class RuleSetProperty {
    private  GlobalProperty prop;
    private RuleSet ruleSet;

    public RuleSetProperty(GlobalProperty prop, RuleSet ruleSet) {
        this.prop = prop;
        this.ruleSet = ruleSet;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public GlobalProperty getGlobalProperty() {
        return prop;
    }
}