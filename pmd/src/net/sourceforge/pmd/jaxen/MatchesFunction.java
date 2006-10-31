package net.sourceforge.pmd.jaxen;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

        Pattern check = Pattern.compile((String) args.get(1));
        Matcher matcher = check.matcher(attr.getValue());
        if (matcher.find()) {
            return context.getNodeSet();
        }
        return Boolean.FALSE;
    }
}
