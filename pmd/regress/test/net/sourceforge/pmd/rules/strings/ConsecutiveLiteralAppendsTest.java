/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;


public class ConsecutiveLiteralAppendsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "ConsecutiveLiteralAppends");
    }

    public void testAll() {

        // first run the legal tests
        runTests(new TestDescriptor[]{

            new TestDescriptor(TEST1, "1, Single append, should be ok", 0, rule),
            new TestDescriptor(TEST3, "3, Appends broken up by variable", 0, rule),
            new TestDescriptor(TEST7, "7, Appends, then a variable", 0, rule),
            new TestDescriptor(TEST8, "8, Appends, then a while", 0, rule),
            new TestDescriptor(TEST12, "12, Two loops, not concurrent appends though", 0, rule),
            new TestDescriptor(TEST13, "13, A bunch of loops, but nothing concurrent", 0, rule),
            new TestDescriptor(TEST15, "15, A bunch of loops, none concurrent, separated by else", 0, rule),
            new TestDescriptor(TEST16, "16, Additive Expression 1", 0, rule),
            new TestDescriptor(TEST17, "17, Additive Expression 2", 0, rule),
            new TestDescriptor(TEST18, "18, End with literal append", 0, rule),
            new TestDescriptor(TEST19, "19, A bunch of appends", 0, rule),
            new TestDescriptor(TEST21, "21, Appends separated by an if", 0, rule),
            new TestDescriptor(TEST22, "22, calls to methods in append", 0, rule),
            new TestDescriptor(TEST24, "24, Appends from within switch statement", 0, rule),
            new TestDescriptor(TEST25, "25, Appends from within several different ifs", 0, rule),
            new TestDescriptor(TEST26, "26, One append in if, one in else", 0, rule),
            new TestDescriptor(TEST28, "28, Additive inside an if statement", 0, rule),
            new TestDescriptor(TEST29, "29, Adding two strings only", 0, rule),
            new TestDescriptor(TEST30, "30, Method call in append", 0, rule),
            new TestDescriptor(TEST34, "34, Additive in the constructor", 0, rule),
            new TestDescriptor(TEST35, "35, For block without braces", 0, rule),
            new TestDescriptor(TEST36, "36, Appends broken up by method call", 0, rule),
            new TestDescriptor(TEST39, "39, Buffer as class variable, accessed in 2 methods", 0, rule),
        });

        // Then run the failure tests
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST2_FAIL, "2, Back to back append, not ok", 1, rule),
            new TestDescriptor(TEST4_FAIL, "4, Appends with literal appends", 1, rule),
            new TestDescriptor(TEST5_FAIL, "5, Appends broken up by while loop", 1, rule),
            new TestDescriptor(TEST6_FAIL, "6, Appends, then a variable", 1, rule),
            new TestDescriptor(TEST9_FAIL, "9, Multiple appends in same while", 1, rule),
            new TestDescriptor(TEST10_FAIL, "10, Multiple appends in same while, with multiple outside that while", 2, rule),
            new TestDescriptor(TEST11_FAIL, "11, Multiple appends in same while, none outside the loop", 1, rule),
            new TestDescriptor(TEST14_FAIL, "14, A bunch of loops, one concurrent", 1, rule),
            new TestDescriptor(TEST20_FAIL, "20, Suffix append follwed by real append", 1, rule),
            new TestDescriptor(TEST23_FAIL, "23, force 2 failures on 3 lines", 2, rule),
            new TestDescriptor(TEST27_FAIL, "27, Concurrent Appends from within switch statement", 2, rule),
            new TestDescriptor(TEST31_FAIL, "31, Adding two strings together then another append", 1, rule),
            new TestDescriptor(TEST32_FAIL, "32, Including the constructor's string", 1, rule),
            new TestDescriptor(TEST33_FAIL, "33, Additive in the constructor", 1, rule),
            new TestDescriptor(TEST37_FAIL, "37, Intervening method call not related to append", 1, rule),
            new TestDescriptor(TEST38_FAIL, "38, Intervening method call not related to append", 1, rule),
        });

        // test threshold
        rule.addProperty("threshold", "2");
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST2_FAIL, "2, re-running with threshold", 0, rule),
            new TestDescriptor(TEST23_FAIL, "23, re-running with threshold", 1, rule),

        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
            "   public void bar() {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer(15);" + PMD.EOL +
            "       sb.append(\"foo\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"Hello\";" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  sb.append(foo);" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST4_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\").append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"Hello\";" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            "  sb.append(foo);" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "   sb.append(foo);" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST9_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST10_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST11_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST12 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  while(true){" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            "  for(int ix = 0; ix < 2; ix++){" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "       sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST13 =
            "public class Foo {" + PMD.EOL +
            "   public void bar(List l) {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "       sb.append(\"Hello cruel world\");" + PMD.EOL +
            "       Iterator iter = l.iterator();" + PMD.EOL +
            "       while (iter.hasNext()) {" + PMD.EOL +
            "           List innerList = (List) iter.next();" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "           for (Iterator ixtor = innerList.iterator(); ixtor.hasNext();) {" + PMD.EOL +
            "               Integer integer = (Integer) ixtor.next();" + PMD.EOL +
            "               sb.append(\"\");" + PMD.EOL +
            "               if (ixtor.hasNext()) {" + PMD.EOL +
            "                   sb.append(\",\");" + PMD.EOL +
            "               }" + PMD.EOL +
            "           }" + PMD.EOL +
            "           sb.append(\"foo\");" + PMD.EOL +
            "       }" + PMD.EOL +
            "   }" + PMD.EOL +
            "}";

    private static final String TEST14_FAIL =
            "public class Foo {" + PMD.EOL +
            "   public void bar(List l) {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "       sb.append(\"Hello cruel world\");" + PMD.EOL +
            "       Iterator iter = l.iterator();" + PMD.EOL +
            "       while (iter.hasNext()) {" + PMD.EOL +
            "           List innerList = (List) iter.next();" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "           for (Iterator ixtor = innerList.iterator(); ixtor.hasNext();) {" + PMD.EOL +
            "               Integer integer = (Integer) ixtor.next();" + PMD.EOL +
            "               sb.append(\"\");" + PMD.EOL +
            "               if (ixtor.hasNext()) {" + PMD.EOL +
            "                   sb.append(\",\");" + PMD.EOL +
            "                   sb.append(\",\");" + PMD.EOL +
            "               }" + PMD.EOL +
            "           }" + PMD.EOL +
            "           sb.append(\"foo\");" + PMD.EOL +
            "       }" + PMD.EOL +
            "   }" + PMD.EOL +
            "}";

    private static final String TEST15 =
            "public class Foo {" + PMD.EOL +
            "   public void bar(List l) {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "       sb.append(\"Hello cruel world\");" + PMD.EOL +
            "       Iterator iter = l.iterator();" + PMD.EOL +
            "       while (iter.hasNext()) {" + PMD.EOL +
            "           List innerList = (List) iter.next();" + PMD.EOL +
            "           sb.append(\",\");" + PMD.EOL +
            "           for (Iterator ixtor = innerList.iterator(); ixtor.hasNext();) {" + PMD.EOL +
            "               Integer integer = (Integer) ixtor.next();" + PMD.EOL +
            "               sb.append(\"\");" + PMD.EOL +
            "               if (ixtor.hasNext()) {" + PMD.EOL +
            "                   sb.append(\",\");" + PMD.EOL +
            "               } " +
            "           }" + PMD.EOL +
            "           sb.append(\"foo\");" + PMD.EOL +
            "       }" + PMD.EOL +
            "   }" + PMD.EOL +
            "}";

    private static final String TEST16 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"World\";" + PMD.EOL +
            "  sb.append(\"Hello\" + foo);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST17 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"World\";" + PMD.EOL +
            "  sb.append(\"Hello\" + foo);" + PMD.EOL +
            "  sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST18 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"Hello\";" + PMD.EOL +
            "  sb.append(\"Hello\");" + PMD.EOL +
            "  sb.append(foo);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST19 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"World\";" + PMD.EOL +
            "  sb.append(foo).append(\"World\");" + PMD.EOL +
            "  sb.append(foo).append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST20_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"AAA\";" + PMD.EOL +
            "  sb.append(foo).append(\"BBB\");" + PMD.EOL +
            "  sb.append(\"CCC\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST21 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"BBB\");" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  sb.append(\"DDD\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST22 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(String.valueOf(\"2\"));" + PMD.EOL +
            "  sb.append(String.valueOf(\"3\"));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST23_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  String somevar = \"\";" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"<bignode>\");" + PMD.EOL +
            "  sb.append(\"<somenode>\").append(somevar).append(\"</somenode>\");" + PMD.EOL +
            "  sb.append(\"</bignode>\");" + PMD.EOL +
            "  sb.append(\"</bignode>\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST24 =
            "public class Foo {" + PMD.EOL +
            "    public String foo(int in) {" + PMD.EOL +
            "        StringBuffer retval = new StringBuffer();" + PMD.EOL +
            "        for (int i = 0; i < in; i++) {" + PMD.EOL +
            "          switch (in){" + PMD.EOL +
            "             case 0 :" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 1:" + PMD.EOL +
            "                retval.append(\"0\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 2:" + PMD.EOL +
            "                retval.append(\"1\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 3:" + PMD.EOL +
            "                retval.append(\"2\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             default:" + PMD.EOL +
            "                retval.append(\"3\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "          }" + PMD.EOL +
            "        }" + PMD.EOL +
            "        return retval.toString();" + PMD.EOL +
            "     }" + PMD.EOL +
            "}";
    private static final String TEST25 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  }" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    private static final String TEST26 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  sb.append(\"CCC\");" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  } else if (sb.length() == 2){" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  } else {" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST27_FAIL =
            "public class Foo {" + PMD.EOL +
            "    public String foo(int in) {" + PMD.EOL +
            "        StringBuffer retval = new StringBuffer();" + PMD.EOL +
            "        for (int i = 0; i < in; i++) {" + PMD.EOL +
            "          switch (in){" + PMD.EOL +
            "             case 0 :" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 1:" + PMD.EOL +
            "                retval.append(\"0\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 2:" + PMD.EOL +
            "                retval.append(\"1\");" + PMD.EOL +
            "                retval.append(\"1\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             case 3:" + PMD.EOL +
            "                retval.append(\"2\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "             default:" + PMD.EOL +
            "                retval.append(\"3\");" + PMD.EOL +
            "                retval.append(\"3\");" + PMD.EOL +
            "                continue;" + PMD.EOL +
            "          }" + PMD.EOL +
            "        }" + PMD.EOL +
            "        return retval.toString();" + PMD.EOL +
            "     }" + PMD.EOL +
            "}";


    private static final String TEST28 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  String foo = \"blah\";" + PMD.EOL +
            "  int count = 0;" + PMD.EOL +
            "  if(true){" + PMD.EOL +
            "   sb.append(\"CCC\" + (++count) + \"Ffalsd\");" + PMD.EOL +
            "  }else if(foo.length() == 2){" + PMD.EOL +
            "   sb.append(\"CCC\" + (++count) + \"Ffalsd\");" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST29 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "   sb.append(\"CCC\" + \"Ffalsd\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    private static final String TEST30 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "  int count = 0;" + PMD.EOL +
            "   sb.append(\"CCC\" + String.valueOf(count++));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST31_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "   sb.append(\"CCC\" + \"Ffalsd\");" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST32_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(\"CCC\");" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST33_FAIL =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(foo + \"CCC\");" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST34 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(\"CCC\" + foo);" + PMD.EOL +
            "   sb.append(\"CCC\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST35 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
            " sb.append(\"World\");" + PMD.EOL +
            "  for(int ix = 0; ix < 2; ix++)" + PMD.EOL +
            "     sb.append(\"World\");" + PMD.EOL +
            " " + PMD.EOL +
            " sb.append(\"World\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST36 =
            "public class Foo {" + PMD.EOL +
            "    public void bar() {" + PMD.EOL +
            "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "        foo(sb);" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST37_FAIL =
            "public class Foo {" + PMD.EOL +
            "    public void bar() {" + PMD.EOL +
            "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "        sb.toString();" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST38_FAIL =
            "public class Foo {" + PMD.EOL +
            "    public void bar() {" + PMD.EOL +
            "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "        foo(sb.toString());" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST39 =
            "public class Foo {" + PMD.EOL +
            "    StringBuffer sb = new StringBuffer();" + PMD.EOL +
            "    public void foo() {" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "    }" + PMD.EOL +
            "    public void bar() {" + PMD.EOL +
            "        sb.append(\"World\");" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

}