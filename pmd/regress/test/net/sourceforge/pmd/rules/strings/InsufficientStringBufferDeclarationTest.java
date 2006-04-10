/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class InsufficientStringBufferDeclarationTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "InsufficientStringBufferDeclaration");
    }

    public void testAll() {

        // first run the legal tests
        runTests(new TestDescriptor[] {
                new TestDescriptor(TEST32, "32, Constructor from math", 0, rule),
        });

        runTests(new TestDescriptor[] {
                new TestDescriptor(TEST1, "1, StringBuffer allocated with enough space", 0, rule),
                new TestDescriptor(TEST3, "3, StringBuffer allocated with space", 0, rule),
                new TestDescriptor(TEST4, "4, StringBuffer allocated from variable", 0, rule),
                new TestDescriptor(TEST5, "5, creating a new StringBuffer", 0, rule),
                new TestDescriptor(TEST6, "6, Initialize with a specific String", 1, rule),
                new TestDescriptor(TEST7, "7, appends inside if statements", 0, rule),
                new TestDescriptor(TEST8, "8, Field level variable", 0, rule),
                new TestDescriptor(TEST10, "10, Appending non-literals", 0, rule),
                new TestDescriptor(TEST11, "11, Initialized to null", 0, rule),
                new TestDescriptor(TEST12, "12, Passed in as parameter", 0, rule),
                new TestDescriptor(TEST14, "14, Compound append, presized just fine", 0, rule),
                new TestDescriptor(TEST16, "16, Append int, properly presized", 0, rule),
                new TestDescriptor(TEST18, "18, Append char, properly presized", 0, rule),
                new TestDescriptor(TEST22, "22, appends inside if/else if/else statements", 0, rule),
                new TestDescriptor(TEST23, "23, appends inside if/else if/else statements", 0, rule),
                new TestDescriptor(TEST24, "24, appends inside if/else if/else statements", 1, rule),
                new TestDescriptor(TEST25, "25, Compound ifs", 0, rule),
                new TestDescriptor(TEST27, "27, Switch statement doesn't exceed 16 characters", 0, rule),
                new TestDescriptor(TEST29, "29, Appending from a cast", 0, rule),
                new TestDescriptor(TEST30, "30, Appending chars", 0, rule),
                new TestDescriptor(TEST31, "31, Appending from a cast in ifs", 0, rule),
                new TestDescriptor(TEST32, "32, Constructor from math", 0, rule),
       });
    
       // Then run the failure tests
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST2_FAIL, "2, StringBuffer not allocated with enough space", 1, rule),
               new TestDescriptor(TEST9_FAIL, "9, Field level variable", 1, rule),
               new TestDescriptor(TEST13_FAIL, "13, compound append", 1, rule),
               new TestDescriptor(TEST15_FAIL, "15, Append int, incorrect presize", 1, rule),
               new TestDescriptor(TEST17_FAIL, "17, Append char, incorrect presize", 1, rule),
               new TestDescriptor(TEST19_FAIL, "19, String concatenation, incorrect presize", 1, rule),
               new TestDescriptor(TEST20_FAIL, "20, String concatenation with non-literal, incorrect presize", 1, rule),
               new TestDescriptor(TEST21_FAIL, "21, Incorrectly presized twice", 2, rule),
               new TestDescriptor(TEST26_FAIL, "26, Compound if, pushed over the edge", 1, rule),
               new TestDescriptor(TEST28_FAIL, "28, Compound if, pushed over the edge", 1, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer(16);" + PMD.EOL +
        "       sb.append(\"foo\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST2_FAIL =
        "public class Foo {" + PMD.EOL +
        " public void bar() {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"World\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(l.size());" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"World\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  int x = 3;" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(x);" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"World\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  int x = 3;" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(5);" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb = new StringBuffer(23);" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  int x = 3;" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(\"Initialize With A String\");" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "            sb.append(\"1234567890\");" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            sb.append(\"123456789\");" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(200);" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST9_FAIL =
        "public class Foo {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST10 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(0);" + PMD.EOL +
        "  sb.append(l.get(2));" + PMD.EOL +
        "  sb.append(l.toString());" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST11 =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  StringBuffer sb = null;" + PMD.EOL +
        "  sb = new StringBuffer(20);" + PMD.EOL +
        "  sb.append(l.toString());" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    
    private static final String TEST12 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer param) {" + PMD.EOL +
        "  param.append(\"Append something\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST13_FAIL =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "       sb.append(\"foo\").append(\"this will make it long\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST14 =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer(30);" + PMD.EOL +
        "       sb.append(\"foo\").append(\"this is presized just right\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST15_FAIL =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "       sb.append(12345678901234567890);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST16 =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "       sb.append(12345);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST17_FAIL =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer(2);" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST18 =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer(3);" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        "       sb.append('a');" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST19_FAIL =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar() {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "       sb.append(\"This string\" + \" \" + \"isn't nice, but valid\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST20_FAIL =
        "public class Foo {" + PMD.EOL +
        "   private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Foo.class);" + PMD.EOL +
        "   public void bar(String x) {" + PMD.EOL +
        "       StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "       sb.append(\"This string\" + x + \"isn't nice, but valid\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST21_FAIL =
        "public class Foo {" + PMD.EOL +
        " public void bar(List l) {" + PMD.EOL +
        "  int x = 3;" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer(2);" + PMD.EOL +
        "  sb.append(\"Hello\");" + PMD.EOL +
        "  sb = new StringBuffer(5);" + PMD.EOL +
        "  sb.append(\"How are you today world\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST22 =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "            sb.append(\"1234567890\");" + PMD.EOL +
        "        } else if( l.size() == 5){" + PMD.EOL +
        "            sb.append(\"1234567890\");" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            sb.append(\"1234567890\");" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST23 =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        } else if( l.size() == 5){" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        }" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        } else if( l.size() == 5){" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            sb.append(\"12345\");" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";


    private static final String TEST24 =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "            sb.append(\"This should use\");" + PMD.EOL +
        "        } else if( l.size() == 5){" + PMD.EOL +
        "            sb.append(\"The longest if\");" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            sb.append(\"statement for its violation, which is this one\");" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST25 =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "           if(true){" + PMD.EOL +
        "                sb.append(\"More\");" + PMD.EOL +
        "           } else if( l.size() == 5){" + PMD.EOL +
        "                sb.append(\"Compound\");" + PMD.EOL +
        "           } else {" + PMD.EOL +
        "               sb.append(\"If\");" + PMD.EOL +
        "           }" + PMD.EOL +
        "       } else {" + PMD.EOL +
        "           sb.append(\"A compound if\");" + PMD.EOL +
        "       } " + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST26_FAIL =
        "public class Foo {" + PMD.EOL +
        "    public void bar(List l) {" + PMD.EOL +
        "        StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "        if(true){" + PMD.EOL +
        "           if(true){" + PMD.EOL +
        "                sb.append(\"More\");" + PMD.EOL +
        "           } else if( l.size() == 5){" + PMD.EOL +
        "                sb.append(\"Compound\");" + PMD.EOL +
        "           } else {" + PMD.EOL +
        "               sb.append(\"If\");" + PMD.EOL +
        "           }" + PMD.EOL +
        "       } else {" + PMD.EOL +
        "           sb.append(\"A compound if\");" + PMD.EOL +
        "       } " + PMD.EOL +
        "       sb.append(\"Push\");" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST27 =
        "public class Foo {" + PMD.EOL +
        " public void bar(String str) {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "    switch(str.charAt(0)){" + PMD.EOL +
        "        case 'a':" + PMD.EOL +
        "            sb.append(\"Switch block\");" + PMD.EOL +
        "        break;" + PMD.EOL +
        "        case 'b':" + PMD.EOL +
        "            sb.append(\"Doesn't exceed\");" + PMD.EOL +
        "        break;" + PMD.EOL +
        "        default:" + PMD.EOL +
        "            sb.append(\"16 chars\");" + PMD.EOL +
        "    }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST28_FAIL =
        "public class Foo {" + PMD.EOL +
        " public void bar(String str) {" + PMD.EOL +
        "  StringBuffer sb = new StringBuffer();" + PMD.EOL +
        "    switch(str.charAt(0)){" + PMD.EOL +
        "        case 'a':" + PMD.EOL +
        "            sb.append(\"Switch block\");" + PMD.EOL +
        "        break;" + PMD.EOL +
        "        default:" + PMD.EOL +
        "            sb.append(\"The default block exceeds 16 characters and will fail\");" + PMD.EOL +
        "    }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";    
    
    private static final String TEST29 =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(1);" + PMD.EOL +
            "   sb.append((char) 0x0041);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    
    private static final String TEST30 =
            "public class Foo {" + PMD.EOL +
            " public void bar(char longnamedchar) {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(1);" + PMD.EOL +
            "   sb.append(longnamedchar);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    
    private static final String TEST31 =
            "public class Foo {" + PMD.EOL +
            " public void bar(int i) {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(1);" + PMD.EOL +
            "   if(i == 1){" + PMD.EOL +
            "       sb.append((char) 0x0041);" + PMD.EOL +
            "   } else if(i == 2){" + PMD.EOL +
            "       sb.append((char) 0x0041);" + PMD.EOL +
            "   } else if(i == 19){" + PMD.EOL +
            "       sb.append((char) 0x0041);" + PMD.EOL +
            "   } else {" + PMD.EOL +
            "       sb.append((char) 0x0041);" + PMD.EOL +
            "   } " + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    
    
    private static final String TEST32 =
            "public class Foo {" + PMD.EOL +
            " public void bar(char longnamedchar) {" + PMD.EOL +
            "  StringBuffer sb = new StringBuffer(132+42);" + PMD.EOL +
            "   sb.append(\"Some string. At this point I'm not doing anything to count characters, assuming developer knows what they're doing\");" + PMD.EOL +
            "  StringBuffer sb1 = new StringBuffer(132*42);" + PMD.EOL +
            "   sb1.append(\"Some string. At this point I'm not doing anything to count characters, assuming developer knows what they're doing\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    
    
}