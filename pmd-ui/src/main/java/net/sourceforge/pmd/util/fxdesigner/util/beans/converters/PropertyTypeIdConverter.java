/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

import net.sourceforge.pmd.properties.PropertyTypeId;


/**
 * @author Clément Fournier
 * @since 6.1.0
 */
public class PropertyTypeIdConverter extends AbstractConverter {

    @Override
    protected String convertToString(Object value) {
        return ((PropertyTypeId) value).getStringId();
    }


    @Override
    protected Object convertToType(Class aClass, Object o) {
        return PropertyTypeId.lookupMnemonic(o.toString());
    }


    @Override
    protected Class getDefaultType() {
        return PropertyTypeId.class;
    }
}
