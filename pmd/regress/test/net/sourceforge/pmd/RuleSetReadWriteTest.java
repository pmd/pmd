package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.cpd.CPD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A test for reading and writing a rule set file.  The registered rule sets
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetReadWriteTest extends TestCase {
    private InputStream m_inputStream;
    private RuleSet m_ruleSetIn;
    private RuleSet m_ruleSetOut;

    /**
     ********************************************************************************
     *
     */
    public RuleSetReadWriteTest() {
        super("Rule Set Read/Write Test");
    }

    /**
     ********************************************************************************
     *
     */
    public void testReadWrite() {
/*
        try {
            loadTestFile();
            m_ruleSetIn = (new RuleSetReader()).read(m_inputStream, "foo");
            write();
            m_ruleSetOut = (new RuleSetReader()).read(m_inputStream, "foo");
            compare();
        } catch (PMDException pmdException) {
            pmdException.printStackTrace();
        }
*/
    }

    /**
     ********************************************************************************
     *
     */
    private void loadTestFile() {
        m_inputStream = new StringBufferInputStream(TEST1);
    }

    /**
     ********************************************************************************
     *
     */
    private void write() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        (new RuleSetWriter(outputStream)).write(m_ruleSetIn);

        m_inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     ********************************************************************************
     *
     */
    private void compare() {
        assertEquals("Rule set names are equal.", m_ruleSetIn.getName(), m_ruleSetOut.getName());
        //assertEquals("Rule set descriptions are equal.", m_ruleSetIn.getDescription(), m_ruleSetOut.getDescription());

        Set rulesIn = m_ruleSetIn.getRules();
        Set rulesOut = m_ruleSetOut.getRules();
        int rulesInCount = rulesIn.size();
        int rulesOutCount = rulesOut.size();

        assertEquals("Rule counts are equal.", rulesInCount, rulesOutCount);

        Rule[] rulesOutArray = new Rule[rulesOutCount];

        rulesOut.toArray(rulesOutArray);

        Map rulesOutMap = new HashMap((int) (rulesInCount / 0.75));

        for (int n = 0; n < rulesOutCount; n++) {
            String key = rulesOutArray[n].getName();

            rulesOutMap.put(key, rulesOutArray[n]);
        }

        Iterator iterator = rulesIn.iterator();

        while (iterator.hasNext()) {
            Rule ruleIn = (Rule) iterator.next();
            String key = ruleIn.getName();
            Rule ruleOut = (Rule) rulesOutMap.get(key);

            assertNotNull("\"" + key + "\" exists in output rules.", ruleOut);

            if (ruleOut != null) {
                assertEquals("Rule messages are equal.", ruleIn.getMessage(), ruleOut.getMessage());
                assertEquals("Rule class are equal.", ruleIn.getClass().getName(), ruleOut.getClass().getName());
                assertEquals("Rule includes are equal.", ruleIn.include(), ruleOut.include());
                //      assertEquals("Rule descriptions are equal.", ruleIn.getDescription(), ruleOut.getDescription());
                assertEquals("Rule examples are equal.", ruleIn.getExample(), ruleOut.getExample());

                RuleProperties propertiesIn = ruleIn.getProperties();
                RuleProperties propertiesOut = ruleOut.getProperties();

                assertEquals("Properties counts are equal.", propertiesIn.size(), propertiesOut.size());

                Enumeration property = propertiesIn.keys();

                while (property.hasMoreElements()) {
                    String propertyName = (String) property.nextElement();
                    String propertyInValue = propertiesIn.getValue(propertyName);
                    String propertyOutValue = propertiesOut.getValue(propertyName);

                    assertNotNull("\"" + propertyName + "\" exists in output rule properties.", propertyOutValue);

                    String msg = "Rule property \"" + propertyName + "\" values are equal.";

                    assertEquals(msg, propertyInValue, propertyOutValue);

                    String propertyInValueType = propertiesIn.getValueType(propertyName);
                    String propertyOutValueType = propertiesOut.getValueType(propertyName);

                    assertNotNull("\"" + propertyName + "\" exists in output rule properties.", propertyOutValueType);

                    msg = "Rule property \"" + propertyName + "\" value types are equal.";

                    assertEquals(msg, propertyInValueType, propertyOutValueType);
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     * @param args
     */
    public static void main(String[] args) {
        (new RuleSetReadWriteTest()).testReadWrite();
    }

    private static final String TEST1 =
    "<?xml version=\"1.0\"?>" + CPD.EOL +
    "<ruleset name=\"Basic Rules\">" + CPD.EOL +
    "  <description>" + CPD.EOL +
    "The Basic Ruleset contains a collection of good practice rules which everyone should follow." + CPD.EOL +
    "  </description>" + CPD.EOL +
    "  <rule name=\"EmptyCatchBlock\"" + CPD.EOL +
    "        message=\"Avoid empty catch blocks\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.EmptyCatchBlockRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "Empty Catch Block finds instances where an exception is caught," + CPD.EOL +
    "but nothing is done.  In most circumstances, this swallows an exception" + CPD.EOL +
    "which should either be acted on or reported." + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  public void doSomething() {" + CPD.EOL +
    "    try {" + CPD.EOL +
    "      FileInputStream fis = new FileInputStream(\"/tmp/bugger\");" + CPD.EOL +
    "    } catch (IOException ioe) {" + CPD.EOL +
    "        // not good" + CPD.EOL +
    "    }" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"EmptyIfStmt\"" + CPD.EOL +
    "        message=\"Avoid empty 'if' statements\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.EmptyIfStmtRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "Empty If Statement finds instances where a condition is checked but nothing is done about it." + CPD.EOL +
    "  </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  if (absValue < 1) {" + CPD.EOL +
    "     // not good" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "     </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"EmptyWhileStmt\"" + CPD.EOL +
    "        message=\"Avoid empty 'while' statements\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.EmptyWhileStmtRule\">" + CPD.EOL +
    "     <description>" + CPD.EOL +
    "Empty While Statement finds all instances where a while statement" + CPD.EOL +
    "does nothing.  If it is a timing loop, then you should use Thread.sleep() for it; if" + CPD.EOL +
    "it's a while loop that does a lot in the exit expression, rewrite it to make it clearer." + CPD.EOL +
    "     </description>" + CPD.EOL +
    "     <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "while (a == b) {" + CPD.EOL +
    "  // not good" + CPD.EOL +
    "}" + CPD.EOL +
    "]]>" + CPD.EOL +
    "     </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"IfElseStmtsMustUseBracesRule\"" + CPD.EOL +
    "        message=\"Avoid using 'if...else' statements without curly braces\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.IfElseStmtsMustUseBracesRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "    Avoid using if..else statements without using curly braces" + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  public void doSomething() {" + CPD.EOL +
    "    // this is OK" + CPD.EOL +
    "    if (foo) x++;" + CPD.EOL +
    "" + CPD.EOL +
    "    // but this is not" + CPD.EOL +
    "    if (foo)" + CPD.EOL +
    "        x=x+1;" + CPD.EOL +
    "    else" + CPD.EOL +
    "        x=x-1;" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"UnnecessaryConversionTemporaryRule\"" + CPD.EOL +
    "        message=\"Avoid unnecessary temporaries when converting primitives to Strings\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "    Avoid unnecessary temporaries when converting primitives to Strings" + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  public String convert(int x) {" + CPD.EOL +
    "    // this wastes an object" + CPD.EOL +
    "    String foo = new Integer(x).toString();" + CPD.EOL +
    "    // this is better" + CPD.EOL +
    "    return Integer.toString(x);" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"OverrideBothEqualsAndHashcodeRule\"" + CPD.EOL +
    "        message=\"Ensure you override both equals() and hashCode()\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.OverrideBothEqualsAndHashcodeRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "Override both public boolean Object.equals(Object other), and public int Object.hashCode(), or override neither.  Even if you are inheriting a hashCode() from a parent class, consider implementing hashCode and explicitly delegating to your superclass." + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "// this is bad" + CPD.EOL +
    "public class Bar {" + CPD.EOL +
    "    public boolean equals(Object o) {" + CPD.EOL +
    "        // do some comparison" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "// and so is this" + CPD.EOL +
    "public class Baz {" + CPD.EOL +
    "    public int hashCode() {" + CPD.EOL +
    "        // return some hash value" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "// this is OK" + CPD.EOL +
    "public class Foo {" + CPD.EOL +
    "    public boolean equals(Object other) {" + CPD.EOL +
    "        // do some comparison" + CPD.EOL +
    "    }" + CPD.EOL +
    "    public int hashCode() {" + CPD.EOL +
    "        // return some hash value" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"EmptyTryBlock\"" + CPD.EOL +
    "        message=\"Avoid empty try blocks\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.EmptyTryBlockRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "Avoid empty try blocks - what's the point?" + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "// this is bad" + CPD.EOL +
    "public void bar() {" + CPD.EOL +
    "    try {" + CPD.EOL +
    "    } catch (Exception e) {" + CPD.EOL +
    "        e.printStackTrace();" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"EmptyFinallyBlock\"" + CPD.EOL +
    "        message=\"Avoid empty finally blocks\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.EmptyFinallyBlockRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "Avoid empty finally blocks - these can be deleted." + CPD.EOL +
    "    </description>" + CPD.EOL +
    "" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "// this is bad" + CPD.EOL +
    "public void bar() {" + CPD.EOL +
    "    try {" + CPD.EOL +
    "        int x=2;" + CPD.EOL +
    "    } finally {" + CPD.EOL +
    "    }" + CPD.EOL +
    "}" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"WhileLoopsMustUseBracesRule\"" + CPD.EOL +
    "        message=\"Avoid using 'while' statements without curly braces\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.WhileLoopsMustUseBracesRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "    Avoid using 'while' statements without using curly braces" + CPD.EOL +
    "    </description>" + CPD.EOL +
    "" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  public void doSomething() {" + CPD.EOL +
    "    while (true)" + CPD.EOL +
    "        x++;" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  <rule name=\"ForLoopsMustUseBracesRule\"" + CPD.EOL +
    "        message=\"Avoid using 'for' statements without curly braces\"" + CPD.EOL +
    "        class=\"net.sourceforge.pmd.rules.ForLoopsMustUseBracesRule\">" + CPD.EOL +
    "    <description>" + CPD.EOL +
    "    Avoid using 'for' statements without using curly braces" + CPD.EOL +
    "    </description>" + CPD.EOL +
    "    <example>" + CPD.EOL +
    "<![CDATA[" + CPD.EOL +
    "  public void foo() {" + CPD.EOL +
    "    for (int i=0; i<42;i++)" + CPD.EOL +
    "        foo();" + CPD.EOL +
    "  }" + CPD.EOL +
    "]]>" + CPD.EOL +
    "    </example>" + CPD.EOL +
    "  </rule>" + CPD.EOL +
    "  </ruleset>" + CPD.EOL +
    "";

}
