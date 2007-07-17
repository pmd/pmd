package test.net.sourceforge.pmd.rules.naming;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class NamingRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("naming", "AbstractNaming"));
        rules.add(findRule("naming", "AvoidDollarSigns"));
        rules.add(findRule("naming", "AvoidFieldNameMatchingMethodName"));
        rules.add(findRule("naming", "AvoidFieldNameMatchingTypeName"));
        rules.add(findRule("naming", "BooleanGetMethodName"));
        rules.add(findRule("naming", "ClassNamingConventions"));
        rules.add(findRule("naming", "LongVariable"));
        rules.add(findRule("naming", "MethodNamingConventions"));
        rules.add(findRule("naming", "MethodWithSameNameAsEnclosingClass"));
        rules.add(findRule("naming", "MisleadingVariableName"));
        rules.add(findRule("naming", "NoPackage"));
        rules.add(findRule("naming", "PackageCase"));
        rules.add(findRule("naming", "ShortMethodName"));
        rules.add(findRule("naming", "ShortVariable"));
        rules.add(findRule("naming", "SuspiciousConstantFieldName"));
        rules.add(findRule("naming", "SuspiciousEqualsMethodName"));
        rules.add(findRule("naming", "SuspiciousHashcodeMethodName"));
        rules.add(findRule("naming", "VariableNamingConventions"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(NamingRulesTest.class);
    }
}
