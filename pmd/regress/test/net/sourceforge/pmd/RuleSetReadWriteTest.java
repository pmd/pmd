package test.net.sourceforge.pmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetReader;
import net.sourceforge.pmd.RuleSetWriter;


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
        catch (PMDException pmdException)
        {
            pmdException.getOriginalException().printStackTrace();
        }
    }

    /**
     ********************************************************************************
     *
     */
    private void loadTestFile()
    {
        m_inputStream = getClass().getClassLoader().getResourceAsStream("RuleSetReadWriteTest.xml");
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
        assertEquals("Rule set descriptions are equal.", m_ruleSetIn.getDescription(), m_ruleSetOut.getDescription());

        Set rulesIn = m_ruleSetIn.getRules();
        Set rulesOut = m_ruleSetOut.getRules();
        int rulesInCount = rulesIn.size();
        int rulesOutCount = rulesOut.size();

        assertEquals("Rule counts are equal.", rulesInCount, rulesOutCount);

        Rule[] rulesOutArray = new Rule[rulesOutCount];

        rulesOut.toArray(rulesOutArray);

        HashMap rulesOutMap = new HashMap((int)(rulesInCount / 0.75));

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
                assertEquals("Rule descriptions are equal.", ruleIn.getDescription(), ruleOut.getDescription());
                assertEquals("Rule examples are equal.", ruleIn.getExample(), ruleOut.getExample());

                Properties propertiesIn = ruleIn.getProperties();
                Properties propertiesOut = ruleOut.getProperties();
                int propertiesInCount = propertiesIn.size();
                int propertiesOutCount = propertiesOut.size();

                assertEquals("Properties counts are equal.", propertiesInCount, propertiesOutCount);

                Enumeration property = propertiesIn.keys();

                while (property.hasMoreElements())
                {
                    String propertyName = (String) property.nextElement();
                    String propertyInValue = propertiesIn.getProperty(propertyName);
                    String propertyOutValue = propertiesOut.getProperty(propertyName);

                    assertNotNull("\"" + propertyName + "\" exists in output rule properties.", propertyOutValue);

                    String msg = "Rule property \"" + propertyName + "\" values are equal.";

                    assertEquals(msg, propertyInValue, propertyOutValue);
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
