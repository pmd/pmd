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
	((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "typeof",
		new TypeOfFunction());
    }

	public Object call(Context context, List args) throws FunctionCallException {
		
		String nodeTypeName = null;
		String fullTypeName = null;
		String shortTypeName = null;
		Attribute attr = null;
		for (int i = 0; i < args.size(); i++) {
		    if (args.get(i) instanceof List) {
			if (attr == null) {
			    attr = ((List<Attribute>) args.get(i)).get(0);
			    nodeTypeName = attr.getStringValue();
			} else {
			    throw new IllegalArgumentException(
				    "typeof function can take only a single argument which is an Attribute.");
			}
		    } else {
			if (fullTypeName == null) {
			    fullTypeName = (String) args.get(i);
			} else if (shortTypeName == null) {
			    shortTypeName = (String) args.get(i);
			} else {
			    break;
			}
		    }
		}
		if (fullTypeName == null) {
		    throw new IllegalArgumentException(
			    "typeof function must be given at least one String argument for the fully qualified type name.");
			}
		Node n = (Node) context.getNodeSet().get(0);
		return typeof(n, nodeTypeName, fullTypeName, shortTypeName);
    }

    // TEST //ClassOrInterfaceType[typeof(@Image, 'java.lang.String')]
    public static boolean typeof(Node n, String nodeTypeName, String fullTypeName, String shortTypeName) {
	if (n instanceof TypeNode) {
	    Class<?> type = ((TypeNode) n).getType();
	    if (type == null) {
		return nodeTypeName != null
			&& (nodeTypeName.equals(fullTypeName) || nodeTypeName.equals(shortTypeName));
	    }
	    if (type.getName().equals(fullTypeName)) {
		return true;
	    }
	    List<Class<?>> implementors = Arrays.asList(type.getInterfaces());
	    if (implementors.contains(type)) {
		return true;
	    }
	    Class<?> superC = type.getSuperclass();
	    while (superC != null && !superC.equals(Object.class)) {
		if (superC.getName().equals(fullTypeName)) {
		    return true;
		}
		superC = superC.getSuperclass();
	    }
	} else {
	    throw new IllegalArgumentException("typeof function may only be called on a TypeNode.");
	}
	return false;
    }
}
