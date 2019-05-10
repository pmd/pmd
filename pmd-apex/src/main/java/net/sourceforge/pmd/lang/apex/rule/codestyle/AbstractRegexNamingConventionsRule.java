/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.regexProperty;

import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.properties.PropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public abstract class AbstractRegexNamingConventionsRule extends AbstractApexRule {
    protected static final String CAMEL_CASE = "[a-z][a-zA-Z0-9]*";
    protected static final String PASCAL_CASE = "[A-Z][a-zA-Z0-9]*";

    abstract String displayName(String name);

    protected void checkMatches(PropertyDescriptor<Pattern> propertyDescriptor, ApexNode<?> node, Object data) {
        Pattern regex = getProperty(propertyDescriptor);
        String name = node.getImage();
        if (!regex.matcher(name).matches()) {
            String displayName = displayName(propertyDescriptor.name());
            addViolation(data, node, new Object[] { displayName, name, regex.toString() });
        }
    }

    protected static PropertyBuilder.RegexPropertyBuilder prop(String name, String displayName, Map<String, String> descriptorToDisplayNames) {
        descriptorToDisplayNames.put(name, displayName);
        return regexProperty(name).desc("Regex which applies to " + displayName + " names");
    }
}
