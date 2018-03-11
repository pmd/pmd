/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;


/**
 * @author Clément Fournier
 * @since 6.1.0
 */
public class LanguageVersionConverter extends AbstractConverter {

    @Override
    protected String convertToString(Object value) {
        return ((LanguageVersion) value).getTerseName();
    }


    @Override
    protected Object convertToType(Class aClass, Object o) {
        return LanguageRegistry.findLanguageVersionByTerseName(o.toString());
    }


    @Override
    protected Class getDefaultType() {
        return LanguageVersion.class;
    }
}
