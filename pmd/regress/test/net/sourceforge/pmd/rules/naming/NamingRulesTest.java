package test.net.sourceforge.pmd.rules.naming;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class NamingRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("naming", "AbstractNaming");
        addRule("naming", "AvoidDollarSigns");
        addRule("naming", "AvoidFieldNameMatchingMethodName");
        addRule("naming", "AvoidFieldNameMatchingTypeName");
        addRule("naming", "BooleanGetMethodName");
        addRule("naming", "ClassNamingConventions");
        addRule("naming", "LongVariable");
        addRule("naming", "MethodNamingConventions");
        addRule("naming", "MethodWithSameNameAsEnclosingClass");
        addRule("naming", "MisleadingVariableName");
        addRule("naming", "NoPackage");
        addRule("naming", "PackageCase");
        addRule("naming", "ShortMethodName");
        addRule("naming", "ShortVariable");
        addRule("naming", "SuspiciousConstantFieldName");
        addRule("naming", "SuspiciousEqualsMethodName");
        addRule("naming", "SuspiciousHashcodeMethodName");
        addRule("naming", "VariableNamingConventions");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(NamingRulesTest.class);
    }
}
