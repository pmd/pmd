/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractPoorMethodCall;

/**
 */
public class UseIndexOfCharRule extends AbstractPoorMethodCall {

    private static final String TARGET_TYPE_NAME = "String";
    private static final String[] METHOD_NAMES = new String[] { "indexOf", "lastIndexOf" };

    /**
     * Method targetTypeName.
     * @return String
     */
    @Override
    protected String targetTypename() {
        return TARGET_TYPE_NAME;
    }

    /**
     * Method methodNames.
     * @return String[]
     */
    @Override
    protected String[] methodNames() {
        return METHOD_NAMES;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isViolationArgument(Node arg) {
        return ((ASTLiteral) arg).isSingleCharacterStringLiteral();
    }

}
