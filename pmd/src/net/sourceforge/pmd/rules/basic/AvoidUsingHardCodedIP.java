package net.sourceforge.pmd.rules.basic;

import java.net.InetAddress;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTLiteral;

public class AvoidUsingHardCodedIP extends AbstractJavaRule {

    private static final String IPv4_REGEXP = "^\"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";
    private static final String IPv6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9a-fA-F]+\"$";
    private static final String IPv4_MAPPED_IPv6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";

    private static final Pattern IPv4_PATTERM = Pattern.compile(IPv4_REGEXP);
    private static final Pattern IPv6_PATTERM = Pattern.compile(IPv6_REGEXP);
    private static final Pattern IPv4_MAPPED_IPv6_PATTERM = Pattern.compile(IPv4_MAPPED_IPv6_REGEXP);

    /**
     * This method checks if the Literal matches the pattern. If it does, a violation is logged.
     */
    public Object visit(ASTLiteral node, Object data) {
        String image = node.getImage();
        if (image == null || image.length() < 3 || image.charAt(0) != '"' ||
                image.charAt(image.length()-1) != '"') {
            return data;
        }
        
	/* Tests before calls to matches() ensure that the literal is '"[0-9:].*"' */
        char c = image.charAt(1);
        if ((Character.isDigit(c) || c == ':') &&
                (IPv4_PATTERM.matcher(image).matches() ||
                        IPv6_PATTERM.matcher(image).matches() ||
                        IPv4_MAPPED_IPv6_PATTERM.matcher(image).matches())) {
            try {
                // as patterns are not 100% accurate, test address
                InetAddress.getByName(image.substring(1, image.length()-1));
                
                // no error creating address object, pattern must be valid
                addViolation(data, node);
            } catch (Exception e) {
		// ignored: invalid format
            }
        }
        return data;
    }

}
