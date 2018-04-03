/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

import net.sourceforge.pmd.RulePriority;


/**
 * @author Clément Fournier
 * @since 6.1.0
 */
public class RulePriorityConverter extends AbstractConverter {

    @Override
    protected String convertToString(Object value) {
        return Integer.toString(((RulePriority) value).getPriority());
    }


    @Override
    protected Object convertToType(Class aClass, Object o) {
        return RulePriority.valueOf(Integer.parseInt(o.toString()));
    }


    @Override
    protected Class getDefaultType() {
        return RulePriority.class;
    }
}
