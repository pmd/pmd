/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  net.sourceforge.pmd.jbuilder;

import  com.borland.primetime.properties.PropertyGroup;
import  com.borland.primetime.properties.PropertyPageFactory;
import  com.borland.primetime.properties.GlobalProperty;
import  com.borland.primetime.properties.PropertyPage;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSet;
import java.util.Iterator;
import java.util.HashMap;
import com.borland.primetime.ide.MessageCategory;
import com.borland.primetime.ide.Browser;
import java.util.Enumeration;



/**
 * put your documentation comment here
 */
public class AcceleratorPropertyGroup
        implements PropertyGroup {

    public static AcceleratorPropertyGroup currentInstance = null;

    /**
    * Standard Constructor
    */
    public AcceleratorPropertyGroup () {
        currentInstance = this;
    }


    /**
     * Called by JBuilder
     */
    public void initializeProperties () {
    }

    /**
     * Create the panel that will go in the property page
     * @param topic Topic of page (represented as a tab in the property page)
     * @return Factory for creating property pages - this factory will create RuleSetPropertyPages
     */
    public PropertyPageFactory getPageFactory (Object topic) {
        if (topic == Constants.RULESETS_TOPIC) {
            return  new PropertyPageFactory("PMD HotKey Condiguration", "Set the HotKeys for PMD") {

                public PropertyPage createPropertyPage () {
                    return  new AcceleratorPropertyPage();
                }
            };
        }
        return  null;
    }
}



