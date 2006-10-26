/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Adding this test to validate current working code doesn't break I've been
 * trying to locate the article referenced. The below code stresses the NPath
 * rule, and according to its current style, runs 2 tests, one pass and one
 * fail.
 * 
 * @author Allan Caplan
 * 
 */
public class UseCollectionIsEmptyTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UseCollectionIsEmpty");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1, "fail, == 0", 1, rule),
                new TestDescriptor(TEST2, "ok, isEmpty", 0, rule),
                new TestDescriptor(TEST3, "fail, != 0", 1, rule),
                new TestDescriptor(TEST4, "ok, !isEmpty", 0, rule),
                new TestDescriptor(TEST5, "fail, != 0", 1, rule),
                new TestDescriptor(TEST6, "ok, !isEmpty", 0, rule),
                new TestDescriptor(TEST7, "fail, 0 ==", 1, rule),
        });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst) {" + PMD.EOL +
        "        if(lst.size() == 0){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst) {" + PMD.EOL +
        "        if(lst.isEmpty()){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    
    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst) {" + PMD.EOL +
        "        if(lst.size() != 0){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst) {" + PMD.EOL +
        "        if(!lst.isEmpty()){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    
    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst, boolean b) {" + PMD.EOL +
        "        if(lst.size() == 0 && b){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    
    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst, boolean b) {" + PMD.EOL +
        "        if(lst.isEmpty() && b){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        "    public static boolean bar(List lst) {" + PMD.EOL +
        "        if(0 == lst.size()){" + PMD.EOL +
        "            return true;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return false;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";}

