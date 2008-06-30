package net.sourceforge.pmd.lang.java.rule.strings;

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
    protected String targetTypename() { 
        return TARGET_TYPE_NAME;
    }

    /**
     * Method methodNames.
     * @return String[]
     */
    protected String[] methodNames() {
        return METHOD_NAMES;
    }

    /**
     * Method isViolationArgument.
     * @param argIndex int
     * @param arg String
     * @return boolean
     */
    protected boolean isViolationArgument(int argIndex, String arg) {
        
        return isSingleCharAsString(arg);
    }

}
