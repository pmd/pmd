package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetReader;
import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.util.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
public class RuleSetReadWriteTest extends TestCase
{
    private InputStream m_inputStream;
    private RuleSet m_ruleSetIn;
    private RuleSet m_ruleSetOut;

    /**
     ********************************************************************************
     *
     */
    public RuleSetReadWriteTest()
    {
        super("Rule Set Read/Write Test");
    }

    /**
     ********************************************************************************
     *
     */
    public void testReadWrite()
    {
        try
        {
            loadTestFile();
            m_ruleSetIn = (new RuleSetReader()).read(m_inputStream, "foo");
            write();
            m_ruleSetOut = (new RuleSetReader()).read(m_inputStream, "foo");
            compare();
        }
        catch (PMDException pmdException) {
            pmdException.printStackTrace();
        }
    }

    /**
     ********************************************************************************
     *
     */
    private void loadTestFile()
    {
        m_inputStream = ResourceLoader.loadResourceAsStream("test-data/RuleSetReadWriteTest.xml");
	assertNotNull("Could not retrieve RuleSetReadWriteTest.xml", m_inputStream);
    }

    /**
     ********************************************************************************
     *
     */
    private void write()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        (new RuleSetWriter(outputStream)).write(m_ruleSetIn);

        m_inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     ********************************************************************************
     *
     */
    private void compare()
    {
        assertEquals("Rule set names are equal.", m_ruleSetIn.getName(), m_ruleSetOut.getName());
        //assertEquals("Rule set descriptions are equal.", m_ruleSetIn.getDescription(), m_ruleSetOut.getDescription());

        Set rulesIn = m_ruleSetIn.getRules();
        Set rulesOut = m_ruleSetOut.getRules();
        int rulesInCount = rulesIn.size();
        int rulesOutCount = rulesOut.size();

        assertEquals("Rule counts are equal.", rulesInCount, rulesOutCount);

        Rule[] rulesOutArray = new Rule[rulesOutCount];

        rulesOut.toArray(rulesOutArray);

        Map rulesOutMap = new HashMap((int)(rulesInCount / 0.75));

        for (int n = 0; n < rulesOutCount; n++)
        {
            String key = rulesOutArray[n].getName();

            rulesOutMap.put(key, rulesOutArray[n]);
        }

        Iterator iterator = rulesIn.iterator();

        while (iterator.hasNext())
        {
            Rule ruleIn = (Rule) iterator.next();
            String key = ruleIn.getName();
            Rule ruleOut = (Rule) rulesOutMap.get(key);

            assertNotNull("\"" + key + "\" exists in output rules.", ruleOut);

            if (ruleOut != null)
            {
                assertEquals("Rule messages are equal.", ruleIn.getMessage(), ruleOut.getMessage());
                assertEquals("Rule class are equal.", ruleIn.getClass().getName(), ruleOut.getClass().getName());
                assertEquals("Rule includes are equal.", ruleIn.include(), ruleOut.include());
          //      assertEquals("Rule descriptions are equal.", ruleIn.getDescription(), ruleOut.getDescription());
                assertEquals("Rule examples are equal.", ruleIn.getExample(), ruleOut.getExample());

                RuleProperties propertiesIn = ruleIn.getProperties();
                RuleProperties propertiesOut = ruleOut.getProperties();

                assertEquals("Properties counts are equal.", propertiesIn.size(), propertiesOut.size());

                Enumeration property = propertiesIn.keys();

                while (property.hasMoreElements())
                {
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
    public static void main(String[] args)
    {
        (new RuleSetReadWriteTest()).testReadWrite();
    }
}
