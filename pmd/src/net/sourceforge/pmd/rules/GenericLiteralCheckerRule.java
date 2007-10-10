/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 *
 */
package net.sourceforge.pmd.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.properties.StringProperty;


/**
 * This class allow to match a Literal (most likely a string) with a regex pattern.
 * Obviously, there is many application to it ( such as the AvoidUsingHardCodedIPRule)
 *
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class GenericLiteralCheckerRule extends AbstractRule {

	private static final String PROPERTY_NAME = "pattern";
	private static final String DESCRIPTION = "Regular Expression";
	private String stringPattern = null;
	private static 	Pattern pattern;

	/**
	 * Default constructor. Retrive the regex and compile it.
	 *
	 */
	public GenericLiteralCheckerRule() {
		init();
	}

	private void init() {
		// Retrieving the pattern setted by user
		PropertyDescriptor property = new StringProperty(PROPERTY_NAME,DESCRIPTION,"", 1.0f);
		stringPattern = super.getStringProperty(property);
		// if the pattern is not empty, we compile it once in for all
		if ( isPatternOk() ) {
			pattern = Pattern.compile(stringPattern);
		}
	}

	private boolean isPatternOk() {
		if ( stringPattern != null && stringPattern.length() > 0 && ! "".equals(stringPattern) ) {
			return true;
		}
		return false;
	}

	/**
	 * This method check if the Literal match the pattern. If it is, a violation is logged.
	 */
	public Object visit(ASTLiteral node, Object data) {
		if ( isPatternOk() ) { //otherwise, this is pointless
			String image = node.getImage();
			if ( image.length() > 0 && ! "".equals(image) && isAMatch(image) ) {
				addViolation(data, node);
			}
		}
		return data;
	}

	private boolean isAMatch(String image) {
        Matcher matcher = pattern.matcher(image);
        if (matcher.find()) {
            return true;
        }
		return false;
	}
}
