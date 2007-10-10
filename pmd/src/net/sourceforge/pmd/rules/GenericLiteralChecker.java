/**
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
 * @author rpelisse
 *
 */
public class GenericLiteralChecker extends AbstractRule {

	private static final String PROPERTY_NAME = "pattern";
	private static final String DESCRIPTION = "Regular Expression";
	private String stringPattern = null;

	public GenericLiteralChecker() {
		init();
	}

	private void init() {
		PropertyDescriptor property = new StringProperty(PROPERTY_NAME,DESCRIPTION,"", 1.0f);
		stringPattern = super.getStringProperty(property);
	}

	/**
	 * Look here for pattern.
	 */
	public Object visit(ASTLiteral node, Object data) {
		if ( stringPattern != null && stringPattern.length() > 0 && ! "".equals(stringPattern) ) { //otherwise, this is pointless
			String image = node.getImage();
			if ( image.length() > 0 && ! "".equals(image) && isAMatch(image) ) {
				addViolation(data, node);
			}
		}
		return data;
	}

	private boolean isAMatch(String image) {
		//FIXME: do a singleton on Pattern
		Pattern pattern = Pattern.compile(stringPattern );
        Matcher matcher = pattern.matcher(image);
        if (matcher.find()) {
            return true;
        }
		return false;
	}
}
