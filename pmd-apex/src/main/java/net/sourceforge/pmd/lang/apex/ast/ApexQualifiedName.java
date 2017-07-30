/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.QualifiedName;

import apex.jorje.semantic.symbol.type.TypeInfo;

/**
 * Qualified name of an apex class or method.
 *
 * @author Clément Fournier
 */
public class ApexQualifiedName implements QualifiedName {

    private static final Pattern FORMAT = Pattern.compile("(\\w+)__(\\w+)(.(\\w+))?(#(\\w+))?");

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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nameSpace).append("__");
        sb.append(classes[0]);

        if (classes.length > 1) {
            sb.append('.').append(classes[1]);
        }

        if (isOperation()) {
            sb.append("#").append(operation);
        }

        return sb.toString();
    }


    public static ApexQualifiedName ofString(String toParse) {
        return null;
    }


    @Override
    public int hashCode() {
        int result = nameSpace.hashCode();
        result = 31 * result + Arrays.hashCode(classes);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ApexQualifiedName
            && Objects.deepEquals(classes, ((ApexQualifiedName) obj).classes)
            && Objects.equals(operation, ((ApexQualifiedName) obj).operation)
            && Objects.equals(nameSpace, ((ApexQualifiedName) obj).nameSpace);

    }


    static ApexQualifiedName ofOuterClass(ASTUserClass astUserClass) {
        String ns = astUserClass.node.getDefiningType().getNamespace().toString();
        String[] classes = {astUserClass.getImage()};
        return new ApexQualifiedName(ns, classes, null);
    }


    static ApexQualifiedName ofNestedClass(ApexQualifiedName parent, ASTUserClass astUserClass) {

        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        classes[classes.length - 1] = astUserClass.getImage();
        return new ApexQualifiedName(parent.nameSpace, classes, null);
    }


    private static String getOperationString(ASTMethod node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getImage()).append('(');


        List<TypeInfo> paramTypes = node.getNode().getMethodInfo().getParameterTypes();

        if (paramTypes.size() > 0) {
            sb.append(paramTypes.get(0).getApexName());

            for (int i = 1; i < paramTypes.size(); i++) {
                sb.append(",").append(paramTypes.get(i).getTypeSignature());
            }

        }

        sb.append(')');

        return sb.toString();
    }


    static ApexQualifiedName ofMethod(ASTMethod node) {
        ApexQualifiedName parent = node.getFirstParentOfType(ASTUserClass.class).getQualifiedName();


        return new ApexQualifiedName(parent.nameSpace, parent.classes, getOperationString(node));
    }
}
