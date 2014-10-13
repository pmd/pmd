/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

/**
 * @since Created on 14.07.2004
 * @author raik
 */
public class VariableAccess {

    public static final int DEFINITION = 0;
    public static final int REFERENCING = 1;
    public static final int UNDEFINITION = 2;

    private int accessType;
    private String variableName;

    public VariableAccess(int accessType, String varName) {
        this.accessType = accessType;
        int dotPos = varName.indexOf('.');
        variableName = dotPos < 0 ? 
        	varName :
        	varName.substring(0, dotPos);
    }

    // TODO completely encapsulate this somehow?
    public int getAccessType() {
        return accessType;
    }

    public boolean accessTypeMatches(int otherType) {
        return accessType == otherType;
    }

    public boolean isDefinition() {
        return this.accessType == DEFINITION;
    }

    public boolean isReference() {
        return this.accessType == REFERENCING;
    }

    public boolean isUndefinition() {
        return this.accessType == UNDEFINITION;
    }

    public String getVariableName() {
        return variableName;
    }

    public String toString() {
        if (isDefinition()) {
            return "Definition(" + variableName + ")";
        }
        if (isReference()) {
            return "Reference(" + variableName + ")";
        }
        if (isUndefinition()) {
            return "Undefinition(" + variableName + ")";
        }
        throw new RuntimeException("Access type was never set");
    }
}
