/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xpath;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

// FIXME Can this function be extended to work on non-AST attributes?
public class MatchesFunction implements Function {

    public static void registerSelfInSimpleContext() {
        // see http://jaxen.org/extensions.html
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "matches", new MatchesFunction());
    }

    public Object call(Context context, List args) throws FunctionCallException {
        if (args.isEmpty()) {
            return Boolean.FALSE;
        }
        List attributes = (List) args.get(0);
        Attribute attr = (Attribute) attributes.get(0);

        for(int i = 1; i < args.size(); i++) {
            Pattern check = Pattern.compile((String) args.get(i));
            Matcher matcher = check.matcher(attr.getStringValue());
            if (matcher.find()) {
                return context.getNodeSet();
            }
        }
        return Boolean.FALSE;
    }

    public static boolean matches(String s, String... patterns) {
	for (String pattern: patterns) {
            Pattern check = Pattern.compile(pattern);
            Matcher matcher = check.matcher(s);
            if (matcher.find()) {
        	return true;
            }
        }
        return false;
    }
}
