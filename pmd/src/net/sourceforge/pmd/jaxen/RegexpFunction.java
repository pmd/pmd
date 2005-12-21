package net.sourceforge.pmd.jaxen;

import org.jaxen.Function;
import org.jaxen.Context;
import org.jaxen.FunctionCallException;
import org.apache.oro.text.perl.Perl5Util;

import java.util.List;

public class RegexpFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.isEmpty()) {
            return Boolean.FALSE;
        }
        List attributes = (List)args.get(0);
        Attribute attr = (Attribute)attributes.get(0);
        String testString = attr.getValue();
        String regularExpression = '/' + (String)args.get(1) + '/';

        // see http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html#package_description
        Perl5Util regexp = new Perl5Util();
        if (regexp.match(regularExpression, testString)) {
            return context.getNodeSet();
        }
        return Boolean.FALSE;
    }
}
