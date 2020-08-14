/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 *  Finds toString() call on String object.
 *
 *  <b>Note:</b> due to an issue with type resolution, this implementation doesn't detect cases when toString()
 *  call is chained to a method returning String which is not declared in the class having the call or the call
 *  arguments are not of the exact same type as method parameters are, excluding the case when method name and
 *  number of it's parameters is enough to identify the method. Example:
 *    <pre>{@code
 *    class A {
 *         public String str() {
 *            return "exampleStr";
 *         }
 *    }
 *    class B {
 *        public void foo() {
 *            String s = new A().str().toString(); // not detected because str() is from another class
 *            s = getString().toString(); // detected
 *            s = getData(new FileInputStream()).toString(); // detected because of argument type
 *            s = getData(new Integer(4), new Integer(5)).toString(); // detected because of unique args count
 *        }
 *        public String getString() {
 *            return "exampleStr";
 *        }
 *        public String getData(InputStream is) {
 *            return "argsResolutionIssueExample";
 *        }
 *        public int getData(String s) {
 *            return 0;
 *        }
 *        public String getData(Number a, Number b) {
 *            return "uniqueArgsCountExample";
 *        }
 *    }
 *    }</pre>
 */
public class StringToStringRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if ("toString".equals(node.getMethodName())
                && node.getArguments().size() == 0) {
            if (TypeHelper.symbolEquals(String.class, node.getQualifier())) {
                addViolation(data, node);
            }
        }

        return data;
    }

}
