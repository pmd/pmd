/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.xpath;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.java.ast.TypeNode;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

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
            Attribute attr = null;
            String typeName = null;
            String shortName = null;
            for (int i = 0; i < args.size(); i++) {
        	if (args.get(i) instanceof List) {
        	    if (attr == null) {
        		attr = (Attribute)((List)args.get(i)).get(0);
        	    } else {
        		throw new IllegalArgumentException("typeof function can take only a single argument which is an Attribute.");
        	    }
        	}
        	else {
        	    if (typeName == null) {
        		typeName = (String)args.get(i);
        	    } else if (shortName == null) {
        		shortName = (String)args.get(i);
        	    } else {
        		break;
        	    }
        	}
            }
            if (typeName == null) {
        	throw new IllegalArgumentException("typeof function must be given at least one String argument for the type name.");
            }
            Class type = ((TypeNode) n).getType();
            if (type == null) {
                return attr != null && (typeName.equals(attr.getValue()) || (shortName != null && shortName.equals(attr.getValue())));
            }
            if (type.getName().equals(typeName) || (attr != null && type.getName().equals(attr.getValue()))) {
                return Boolean.TRUE;
            }
            List<Class> implementors = Arrays.asList(type.getInterfaces());
            if (implementors.contains(type)) {
                return Boolean.TRUE;
            }
            Class<?> superC = type.getSuperclass();
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
