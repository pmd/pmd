/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;


/**
 * This class allow to match a Literal (most likely a String) with a regex pattern.
 * Obviously, there are many applications of it (such as basic.xml/AvoidUsingHardCodedIP).
 *
 * @author Romain PELISSE, belaran@gmail.com
 */
//FUTURE This is not referenced by any RuleSet?
public class GenericLiteralCheckerRule extends AbstractJavaRule {

	private Pattern pattern;
	
	private static final String PROPERTY_NAME = "regexPattern";
		
	private static final PropertyDescriptor regexProperty = new StringProperty(PROPERTY_NAME,"Regular expression","", 1.0f);

    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(regexProperty);

    protected Map<String, PropertyDescriptor> propertiesByName() {
        return PROPERTY_DESCRIPTORS_BY_NAME;
    }
	
	private void init() {
		if (pattern == null) {
			// Retrieve the regex pattern set by user			
			String stringPattern = super.getStringProperty(regexProperty);
			// Compile the pattern only once
			if ( stringPattern != null && stringPattern.length() > 0 ) {
				pattern = Pattern.compile(stringPattern);
			} else {
				throw new IllegalArgumentException("Must provide a value for the '" + PROPERTY_NAME + "' property.");
			}
		}
	}

	/**
	 * This method checks if the Literal matches the pattern. If it does, a violation is logged.
	 */
	@Override
	public Object visit(ASTLiteral node, Object data) {
		init();
		String image = node.getImage();
		if ( image != null && image.length() > 0 && RegexHelper.isMatch(this.pattern,image) ) {
			addViolation(data, node);
		}
		return data;
	}
}
