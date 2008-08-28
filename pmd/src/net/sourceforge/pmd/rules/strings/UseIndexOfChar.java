package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.rules.AbstractPoorMethodCall;

/**
 */
public class UseIndexOfChar extends AbstractPoorMethodCall {

    private static final String targetTypeName = "String";
    private static final String[] methodNames = new String[] { "indexOf", "lastIndexOf" };
    
    public UseIndexOfChar() {
        super();
    }

    /**
     * Method targetTypeName.
     * @return String
     */
    protected String targetTypename() { 
        return targetTypeName;
    }

    /**
     * Method methodNames.
     * @return String[]
     */
    protected String[] methodNames() {
        return methodNames;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isViolationArgument(Node arg) {
        return ((ASTLiteral) arg).isSingleCharacterStringLiteral();
    }

}
