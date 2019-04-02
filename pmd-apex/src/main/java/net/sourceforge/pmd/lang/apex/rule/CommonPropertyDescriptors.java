/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author Clément Fournier
 */
public final class CommonPropertyDescriptors {

    private CommonPropertyDescriptors() {

    }

    public static GenericPropertyBuilder<Integer> reportLevelProperty() {
        return PropertyFactory.intProperty("reportLevel");
    }

}
