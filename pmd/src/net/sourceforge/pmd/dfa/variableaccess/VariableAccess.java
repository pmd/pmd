/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

/**
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
        if (varName.indexOf(".") == -1) {
            this.variableName = varName;
        } else {
            this.variableName = varName.substring(0, varName.indexOf("."));
        }
    }

    public int getAccessType() {
        return accessType;
    }

    public String getVariableName() {
        return variableName;
    }

    public String toString() {
        return "AccessType: " + this.accessType + " | VariableName: " + this.variableName;
    }
}
