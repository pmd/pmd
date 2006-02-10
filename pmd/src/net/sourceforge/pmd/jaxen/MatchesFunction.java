package net.sourceforge.pmd.jaxen;

import org.apache.oro.text.perl.Perl5Util;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import java.util.List;

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
        String testString = attr.getValue();
        String regularExpression = '/' + (String) args.get(1) + '/';

        // see http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html#package_description
        Perl5Util regexp = new Perl5Util();
        if (regexp.match(regularExpression, testString)) {
            return context.getNodeSet();
        }
        return Boolean.FALSE;
    }
}
