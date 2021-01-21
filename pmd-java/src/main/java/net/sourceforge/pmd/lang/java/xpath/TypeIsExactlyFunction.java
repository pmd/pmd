/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;


@InternalApi
@Deprecated
public class TypeIsExactlyFunction implements Function {

    public static void registerSelfInSimpleContext() {
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "typeIsExactly",
                new TypeIsExactlyFunction());
    }

    @Override
    public Object call(final Context context, final List args) throws FunctionCallException {
        if (args.size() != 1) {
            throw new IllegalArgumentException(
                    "typeIsExactly function takes a single String argument with the fully qualified type name to check against.");
        }
        final String fullTypeName = (String) args.get(0);
        final Node n = (Node) context.getNodeSet().get(0);

        return typeIsExactly(n, fullTypeName);
    }

    /**
     * Example XPath 1.0: {@code //ClassOrInterfaceType[typeIsExactly('java.lang.String')]}
     * <p>
     * Example XPath 2.0: {@code //ClassOrInterfaceType[pmd-java:typeIsExactly('java.lang.String')]}
     *
     * @param n The node on which to check for types
     * @param fullTypeName The fully qualified name of the class or any supertype
     * @return True if the type of the node matches, false otherwise.
     */
    public static boolean typeIsExactly(Node n, String fullTypeName) {
        if (n instanceof TypeNode) {
            fullTypeName = StringUtils.deleteWhitespace(fullTypeName);
            return TypeTestUtil.isExactlyA(fullTypeName, (TypeNode) n);
        } else {
            throw new IllegalArgumentException("typeIsExactly function may only be called on a TypeNode.");
        }
    }
}
