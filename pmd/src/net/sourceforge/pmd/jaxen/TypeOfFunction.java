package net.sourceforge.pmd.jaxen;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.TypeNode;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import java.util.Arrays;
import java.util.List;

public class TypeOfFunction implements Function {

    public static void registerSelfInSimpleContext() {
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "typeof", new TypeOfFunction());
    }

    // TEST //ClassOrInterfaceType[typeof(@Image, 'java.lang.String')]
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.isEmpty()) {
            return Boolean.FALSE;
        }
        Node n = (Node) context.getNodeSet().get(0);
        if (n instanceof TypeNode) {
            List attributes = (List) args.get(0);
            Attribute attr = (Attribute) attributes.get(0);
            Class type = ((TypeNode) n).getType();
            String typeName = (String) args.get(1);
            String shortName = (args.size() > 2) ? (String) args.get(2) : "";
            if (type == null) {
                return typeName.equals(attr.getValue()) || shortName.equals(attr.getValue());
            }
            if (type.getName().equals(typeName) || type.getName().equals(attr.getValue())) {
                return Boolean.TRUE;
            }
            List<Class> implementors = Arrays.asList(type.getInterfaces());
            if (implementors.contains(type)) {
                return Boolean.TRUE;
            }
            Class superC = type.getSuperclass();
            while (superC != null && !superC.equals(Object.class)) {
                if (superC.getName().equals(typeName)) {
                    return Boolean.TRUE;
                }
                superC = superC.getSuperclass();
            }
        }
        return Boolean.FALSE;
    }
}
