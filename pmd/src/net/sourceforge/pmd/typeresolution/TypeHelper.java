package net.sourceforge.pmd.typeresolution;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.TypeNode;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class TypeHelper {

    public static boolean isA(TypeNode n, Class clazz) {
        return subclasses(n, clazz);
    }

    public static boolean isA(VariableNameDeclaration vnd, Class clazz) {
        Class type = vnd.getType();
        return ((type != null && type.equals(clazz)) || (type == null && (clazz.getSimpleName().equals(vnd.getTypeImage()) || clazz.getName().equals(vnd.getTypeImage()))));
    }

    public static boolean subclasses(TypeNode n, Class clazz) {
        Class type = n.getType();
        if (type == null) {
            return (clazz.getSimpleName().equals(((SimpleNode) n).getImage()) || clazz.getName().equals(((SimpleNode) n).getImage()));
        }

        if (type.equals(clazz)) {
            return Boolean.TRUE;
        }

        List<Class> implementors = Arrays.asList(type.getInterfaces());
        if (implementors.contains(clazz)) {
            return Boolean.TRUE;
        }
        Class superC = type.getSuperclass();
        while (superC != null && !superC.equals(Object.class)) {
            if (superC.equals(clazz)) {
                return Boolean.TRUE;
            }
            superC = superC.getSuperclass();
        }
        return Boolean.FALSE;
    }
}
