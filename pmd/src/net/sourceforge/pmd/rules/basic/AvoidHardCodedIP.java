/**
 *
 */
package net.sourceforge.pmd.rules.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLiteral;

/**
 * A very simple rule, that checks that there is no IP adresses
 * that are "hard coded" into the java code. This kind of bad
 * habit may render an app quite difficult to deploy...
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class AvoidHardCodedIP extends AbstractRule {

	private static final int MAX_IP_LENGTH = 16; // 3 * 4 (for number) + 3 * 1 (one for a dot)

	/**
	 * Look for "hard coded ip".
	 */
	public Object visit(ASTLiteral node, Object data) {
		String image = node.getImage();
		if ( image.length() > 0 && image.length() <= MAX_IP_LENGTH &&
			 isIPAdress(image)	) {
			addViolation(data, node);
		}
		return data;
	}

	private boolean isIPAdress(String image) {
		Pattern pattern =
        Pattern.compile("[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}");
        Matcher matcher = pattern.matcher(image);
        while (matcher.find()) {
            return true;
        }
		return false;
	}

}
