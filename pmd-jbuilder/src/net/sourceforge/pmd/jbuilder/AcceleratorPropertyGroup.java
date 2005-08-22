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


package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.*;

import java.awt.event.KeyEvent;


/**
 * put your documentation comment here
 */
public class AcceleratorPropertyGroup
        implements PropertyGroup {

    public static AcceleratorPropertyGroup currentInstance = null;
    static GlobalIntegerProperty PROP_CHECKFILE_KEY;
    static GlobalIntegerProperty PROP_CHECKFILE_MOD;
    static GlobalIntegerProperty PROP_CHECKPROJ_KEY;
    static GlobalIntegerProperty PROP_CHECKPROJ_MOD;
    static GlobalBooleanProperty PROP_KEYS_ENABLED;

    static {
        PROP_CHECKFILE_KEY = new GlobalIntegerProperty(Constants.RULESETS, "checkfilekey", 'P');
        PROP_CHECKFILE_MOD = new GlobalIntegerProperty(Constants.RULESETS, "checkfilemod", KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
        PROP_CHECKPROJ_KEY = new GlobalIntegerProperty(Constants.RULESETS, "checkprojkey", 'J');
        PROP_CHECKPROJ_MOD = new GlobalIntegerProperty(Constants.RULESETS, "checkprojmod", KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
        PROP_KEYS_ENABLED = new GlobalBooleanProperty(Constants.RULESETS, "keysenabled", false);
    }

    /**
     * Standard Constructor
     */
    public AcceleratorPropertyGroup() {
        currentInstance = this;
    }


    /**
     * Called by JBuilder
     */
    public void initializeProperties() {
    }

    /**
     * Create the panel that will go in the property page
     *
     * @param topic Topic of page (represented as a tab in the property page)
     * @return Factory for creating property pages - this factory will create RuleSetPropertyPages
     */
    public PropertyPageFactory getPageFactory(Object topic) {
        if (topic == Constants.RULESETS_TOPIC) {
            return new PropertyPageFactory("PMD HotKey Condiguration", "Set the HotKeys for PMD") {

                public PropertyPage createPropertyPage() {
                    return new AcceleratorPropertyPage();
                }
            };
        }
        return null;
    }
}



