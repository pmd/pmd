/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;


public class ClassNode extends AbstractReportNode {

    private String className;

    public ClassNode(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public boolean equalsNode(AbstractReportNode arg0) {
        if (!(arg0 instanceof ClassNode)) {
            return false;
        }
        return ((ClassNode) arg0).getClassName().equals(className);
    }

}
