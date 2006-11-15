
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
 package test.net.sourceforge.pmd.rules.strings;
 
 import net.sourceforge.pmd.PMD;
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class InefficientStringBufferingTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("strings", "InefficientStringBuffering");
     }
 
     public void testAll() {
        runTests(new TestDescriptor[] {
                new TestDescriptor(TEST1, "concatenating a literal to a method return value", 1, rule),
                new TestDescriptor(TEST2, "same as TEST1, but in SB constructor", 1, rule),
                new TestDescriptor(TEST3, "chained appends", 0, rule),
                new TestDescriptor(TEST4, "concatenating two literals in SB constructor", 0, rule),
                new TestDescriptor(TEST5, "concatenating two literals post-construction", 0, rule),
                new TestDescriptor(TEST6, "case where concatenation is not a child of a BlockStatement, but instead is a child of an ExplicitConstructorInvocation", 0, rule),
                new TestDescriptor(TEST7, "don't error out on array instantiation", 0, rule),
                new TestDescriptor(TEST8, "usage of the StringBuffer constructor that takes an int", 0, rule),
                new TestDescriptor(TEST9, "nested", 0, rule),
                new TestDescriptor(TEST10, "looking up too high", 0, rule),
                new TestDescriptor(TEST11, "looking too deep", 0, rule),
                new TestDescriptor(TEST12, "concatenating two non-literals", 1, rule),
                new TestDescriptor(TEST13, "concatenating method + int", 0, rule),
                new TestDescriptor(TEST14, "JTextArea.append", 0, rule),
                new TestDescriptor(TEST15, "don't get thrown off by a buried literal", 1, rule),
                new TestDescriptor(TEST16, "sb.delete shouldn't trigger it", 0, rule),
                new TestDescriptor(TEST17, "skip additions involving static finals, compiler will do constant folding for these", 0, rule),
                new TestDescriptor(TEST18, "for statement without braces", 1, rule),
                new TestDescriptor(TEST19, "if statement without braces", 1, rule),
                new TestDescriptor(TEST20, "3 args version of StringBuffer.append", 0, rule),
                new TestDescriptor(TEST21, "compile-time concats are ok", 0, rule),
                new TestDescriptor(TEST22, "compile-time concats are ok, v2", 0, rule),
        });
     }
 
    private static final String TEST1 =
     "public class Foo {" + PMD.EOL +
     " private void baz() {" + PMD.EOL +
     "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
     "  sb.append(\"hello\"+ world()); "+
     " }" + PMD.EOL +
     "}";
 
    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " private void baz() {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(\"hello\"+ world());" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " private void baz() {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "  sb.append(\"hello\").append(world()); "+
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " private void baz() {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(\"hello\"+ \"world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " private void baz() {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "  sb.append(\"hello\"+\"world\"); "+
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public Foo() {" + PMD.EOL +
        "  super(\"CauseMsg:\" + ex.getMessage(), ex); " + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
     private static final String TEST7 =
         "public class Foo {" + PMD.EOL +
         " public void bar() {" + PMD.EOL +
         "  int t[] = new int[x+y+1];" + PMD.EOL +
         " }" + PMD.EOL +
         "}";
 
 
    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public int foor() {return 2;}" + PMD.EOL +
        " public void bar(int x) {" + PMD.EOL +
        "  StringBuffer buf = new StringBuffer(1 + foo());" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int x) {" + PMD.EOL +
        "  StringBuffer buf = new StringBuffer(x);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST10 =
        "public class Foo {" + PMD.EOL +
        " public void bar() {" + PMD.EOL +
        "  if (foo) {" + PMD.EOL +
        "   StringBuffer buf = new StringBuffer();" + PMD.EOL +
        "   buf.append(\"hello\");" + PMD.EOL +
        "   Object x = a(\"world\" + x, buf.toString());" + PMD.EOL +
        "  }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST11 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int i) {" + PMD.EOL +
        "  StringBuffer buf = new StringBuffer();" + PMD.EOL +
        "  buf.append(getFoo(getBar(i + \"hi\")));" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST12 =
        "public class Foo {" + PMD.EOL +
        " public void bar(String a, String b) {" + PMD.EOL +
        "  StringBuffer buf = new StringBuffer();" + PMD.EOL +
        "  buf.append(a + b);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST13 =
        "public class Foo {" + PMD.EOL +
        " public void bar(Date a) {" + PMD.EOL +
        "  StringBuffer buf = new StringBuffer();" + PMD.EOL +
        "  buf.append(a.getYear() + 1900);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
    private static final String TEST14 =
        "public class Foo {" + PMD.EOL +
        " public void bar(JTextArea jta) {" + PMD.EOL +
        "  jta.append(f + \"hi\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
 
     private static final String TEST15 =
         "public class Foo {" + PMD.EOL +
         " private void baz() {" + PMD.EOL +
         "  StringBuffer sb = new StringBuffer(\"hello\"+ System.getProperty(\"blah\"));" + PMD.EOL +
         " }" + PMD.EOL +
         "}";
 
     private static final String TEST16 =
         "public class Foo {" + PMD.EOL +
         " public void bar(StringBuffer sb) {" + PMD.EOL +
         "  sb.delete(x, y+z);" + PMD.EOL +
         " }" + PMD.EOL +
         "}";
 
     private static final String TEST17 =
         "public class Foo {" + PMD.EOL +
         " public static final String FOO = \"bar\";" + PMD.EOL +
         " public void bar(StringBuffer sb) {" + PMD.EOL +
         "  sb.append(\"foo\" + FOO);" + PMD.EOL +
         " }" + PMD.EOL +
         "}";
    
     private static final String TEST18 =
         "public class Foo {" + PMD.EOL +
         " private void baz() {" + PMD.EOL +
         "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
         "  for(int ix = 0; ix < 100; ix++) "+
         "      sb.append(\"hello\"+ world()); "+
         " }" + PMD.EOL +
         "}";
 
     private static final String TEST19 =
         "public class Foo {" + PMD.EOL +
         " private void baz() {" + PMD.EOL +
         "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
         "  if(true) "+
         "      sb.append(\"hello\"+ world()); "+
         " }" + PMD.EOL +
         "}";
 
     private static final String TEST20 =
         "public class Foo {" + PMD.EOL +
         " private void baz(StringBuffer s, char[] chars, int start, int end) {" + PMD.EOL +
         "  s.append(chars, start, start - end);" + PMD.EOL +
         " }" + PMD.EOL +
         "}";
 
 
     private static final String TEST21 =
             "public class Foo {" + PMD.EOL +
             " private void baz() {" + PMD.EOL +
             "StringBuffer buffer = new StringBuffer(" + PMD.EOL +
             "\"a\" + \"b\" + \"c\");" + PMD.EOL +
             "} }";
 
     private static final String TEST22 =
             "public class Foo {" + PMD.EOL +
             "static final String BAR = \"foo\";" + PMD.EOL +
             " private void baz() {" + PMD.EOL +
             "StringBuffer buffer = new StringBuffer(" + PMD.EOL +
             "\"a\" + BAR + \"b\" + BAR);" + PMD.EOL +
             "} }";
 }
