package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NonThreadSafeSingletonTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "NonThreadSafeSingleton");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "OK, method is synchronized", 0, rule),
            new TestDescriptor(TEST3, "OK, in synchronized block", 0, rule),
            new TestDescriptor(TEST4, "OK, in returning non-static data", 0, rule),
            new TestDescriptor(TEST5, "failure case, two if statements", 1, rule),
            new TestDescriptor(TEST6, "failure case, compound if statement", 1, rule),
            new TestDescriptor(TEST7, "failure case 2", 1, rule),
            new TestDescriptor(TEST8, "From defect 1573591", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private static List buz;" + PMD.EOL +
            " public static List bar() {" + PMD.EOL +
            "  if (buz == null) buz = new ArrayList();" + PMD.EOL +
            "  return buz;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " private static List buz;" + PMD.EOL +
            " public static synchronized List bar() {" + PMD.EOL +
            "  if (buz == null) buz = new ArrayList();" + PMD.EOL +
            "  return buz;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " private static List buz;" + PMD.EOL +
            " public static List bar() {" + PMD.EOL +
            "  synchronized (baz) {" + PMD.EOL +
            "   if (buz == null) buz = new ArrayList();" + PMD.EOL +
            "   return buz;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " private static Locale locale;" + PMD.EOL +
        " public static List bar() {" + PMD.EOL +
        "  if (locale==null) return Locale.getDefault();" + PMD.EOL +
        "  return locale;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " private static List buz;" + PMD.EOL +
        " private static boolean b = false;" + PMD.EOL +
        " public static List bar(String foo) {" + PMD.EOL +
        "  if (buz == null) {" + PMD.EOL +
        "     buz = new ArrayList();" + PMD.EOL +
        "     if(foo == null){b = true;}" + PMD.EOL +
        "  }" + PMD.EOL +
        "  return buz;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " private static List list;" + PMD.EOL +
        " public static List bar(String param) {" + PMD.EOL +
        "     if( list == null || !param.equals(\"foo\")) {" + PMD.EOL +
        "        list = new ArrayList();" + PMD.EOL +
        "        param = \"x\";" + PMD.EOL +
        "     }" + PMD.EOL +
        "     return list;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " private static List buz = null;" + PMD.EOL +
        " private static List bar() {" + PMD.EOL +
        "  if (buz == null) { " + PMD.EOL +
        "     buz = Collections.get(Integer.MAX_SIZE); " + PMD.EOL +
        "}" + PMD.EOL +
        "  return buz;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    public static final String TEST8 = 
    	"public class A {" + PMD.EOL +
    	"public final static String FOO = \"0\";" + PMD.EOL +
    	"private String bar;" + PMD.EOL +
    	"public void bla() {" + PMD.EOL +
    	"if (this.bar == null) {" + PMD.EOL +
    	"this.bar = FOO;" + PMD.EOL +
    	"}" + PMD.EOL +
    	"}" + PMD.EOL +
    	"}";

}

