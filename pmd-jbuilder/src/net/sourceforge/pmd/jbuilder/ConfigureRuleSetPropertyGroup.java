package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.PropertyGroup;
import com.borland.primetime.properties.PropertyPageFactory;
import  com.borland.primetime.properties.PropertyPage;
import com.borland.primetime.ide.MessageCategory;

/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class ConfigureRuleSetPropertyGroup implements PropertyGroup {
    public ConfigureRuleSetPropertyGroup() {
    }
    public void initializeProperties() {
    }

    public PropertyPageFactory getPageFactory(Object topic) {
        if (topic == ActiveRuleSetPropertyGroup.RULESETS_TOPIC) {
           return  new PropertyPageFactory("PMD RuleSet Properties", "Configure the PMD RuleSets") {

               public PropertyPage createPropertyPage () {
                   return  new ConfigureRuleSetPropertyPage();
               }
           };
       }
        return  null;
    }
}