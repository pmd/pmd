package net.sourceforge.pmd.lang.java.rule.basic;

import java.net.InetAddress;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidUsingHardCodedIPRule extends AbstractJavaRule {

    private static final String IPV4_REGEXP = "^\"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";
    private static final String IPV6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9a-fA-F]+\"$";
    private static final String IPV4_MAPPED_IPV6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";

    private static final Pattern IPV4_PATTERM = Pattern.compile(IPV4_REGEXP);
    private static final Pattern IPV6_PATTERM = Pattern.compile(IPV6_REGEXP);
    private static final Pattern IPV4_MAPPED_IPV6_PATTERM = Pattern.compile(IPV4_MAPPED_IPV6_REGEXP);

    /**
     * This method checks if the Literal matches the pattern. If it does, a violation is logged.
     */
    @Override
    public Object visit(ASTLiteral node, Object data) {
        if (!node.isStringLiteral()) {
            return data;
        }
        String image = node.getImage();

	/* Tests before calls to matches() ensure that the literal is '"[0-9:].*"' */
        char c = image.charAt(1);
        if ((Character.isDigit(c) || c == ':') &&
                (IPV4_PATTERM.matcher(image).matches() ||
                        IPV6_PATTERM.matcher(image).matches() ||
                        IPV4_MAPPED_IPV6_PATTERM.matcher(image).matches())) {
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
