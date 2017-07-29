/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;

import net.sourceforge.pmd.lang.ast.QualifiedName;

/**
 * Qualified name of an apex class or method.
 *
 * @author Clément Fournier
 */
public class ApexQualifiedName implements QualifiedName {

    private final String nameSpace;
    private final String[] classes;
    private final String operation;


    private ApexQualifiedName(String nameSpace, String[] classes, String operation) {
        this.nameSpace = nameSpace;
        this.operation = operation;
        this.classes = classes;
    }


    @Override
    public String getOperation() {
        return operation;
    }


    @Override
    public String[] getClasses() {
        return Arrays.copyOf(classes, classes.length);
    }


    /**
     * Gets the namespace prefix of this resource.
     *
     * @return The namespace prefix
     */
    public String getNameSpace() {
        return nameSpace;
    }


    @Override
    public boolean isClass() {
        return operation == null;
    }


    @Override
    public boolean isOperation() {
        return operation != null;
    }


    static ApexQualifiedName ofOuterClass(ASTUserClass astUserClass) {
        String ns = astUserClass.node.getDefiningType().getNamespace().toString();
        String[] classes = {astUserClass.getImage()};
        return new ApexQualifiedName(ns, classes, null);
    }


    static ApexQualifiedName ofNestedClass(ApexQualifiedName parent, ASTUserClass astUserClass) {

        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length);
        classes[classes.length - 1] = astUserClass.getImage();
        return new ApexQualifiedName(parent.nameSpace, classes, null);
    }


    static ApexQualifiedName ofMethod(ASTMethod node) {
        ApexQualifiedName parent = node.getFirstParentOfType(ASTUserClass.class).getQualifiedName();

        return new ApexQualifiedName(parent.nameSpace, parent.classes, node.getImage());
    }
}
