/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 11:39:30 AM
 */
package net.sourceforge.pmd.symboltable;

public class Kind {

    // see JLS 6.1 for all declaration entities
    public static final Kind LOCAL_VARIABLE = new Kind("Local variable");
    public static final Kind FIELD = new Kind("Field");

    public static final Kind UNKNOWN = new Kind("Unknown");

    private String name;

    private Kind(String name) {
        this.name= name;
    }

    public String toString() {
        return name;
    }


}
