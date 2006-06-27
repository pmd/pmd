package net.sourceforge.pmd.rules.strings;

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
     * Method isViolationArgument.
     * @param argIndex int
     * @param arg String
     * @return boolean
     */
    protected boolean isViolationArgument(int argIndex, String arg) {
        
        return isSingleCharAsString(arg);
    }

}
