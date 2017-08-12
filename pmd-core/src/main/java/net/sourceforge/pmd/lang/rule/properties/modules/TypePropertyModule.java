/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.modules;

import java.util.List;

/**
 * @author Clément Fournier
 */
public class TypePropertyModule extends PackagedPropertyModule<Class> {

    public TypePropertyModule(String[] legalPackageNames, List<Class> defaults) {
        super(legalPackageNames, defaults);
    }


    @Override
    protected String packageNameOf(Class item) {
        return item.getName();
    }


    @Override
    protected String itemTypeName() {
        return "type";
    }

}
