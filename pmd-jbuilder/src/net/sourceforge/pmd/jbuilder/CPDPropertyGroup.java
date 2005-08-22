package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.GlobalIntegerProperty;
import com.borland.primetime.properties.PropertyGroup;
import com.borland.primetime.properties.PropertyPage;
import com.borland.primetime.properties.PropertyPageFactory;

/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 *
 * @author David Craine
 * @version 1.0
 */

public class CPDPropertyGroup implements PropertyGroup {
    static GlobalIntegerProperty PROP_MIN_TOKEN_COUNT = new GlobalIntegerProperty(Constants.RULESETS, "mintokencount", 30);

    public void initializeProperties() {
    }

    public PropertyPageFactory getPageFactory(Object topic) {
        if (topic == Constants.RULESETS_TOPIC) {
            return new PropertyPageFactory("CPD Properties", "Configure the CPD RuleSets") {

                public PropertyPage createPropertyPage() {
                    return new CPDPropertyPage();
                }
            };
        }
        return null;
    }
}