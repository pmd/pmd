package net.sourceforge.pmd;

// import org.apache.xerces.parsers.SAXParser;
import net.sourceforge.pmd.util.ResourceLoader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Loads the PMD <b>project.xml</b> file and stores the contents in a Properties object.
 * The property keys are the case-insensitive path starting below the root <project> down
 * to the element.  For example:
 * <ul>
 * <li>currentVersion</li>
 * <li>organization/name</li>
 * <li>versions/version/name</li>
 * </ul>
 * When an element has repeated values, e.g., developer names, one property is created and
 * the values are separated by <b>&amp;vs;</b>.  The <i>vs</i> stands for <i>value separator</i>.
 * For example:
 * <ul>
 * <li>Tom Copeland&amp;vs;David Dixon-Peugh&amp;vs;David Craine</li>
 * <li>tom@infoether.com&amp;vs;ddp@apache.org&amp;vs;dave@infoether.com</li>
 * </ul>
 * When there is a collection of repeating values, an empty space will reserve the position
 * of a missing value.  This is so that the collection can be parsed on position.
 *
 * @author Donald A. Leckie
 * @since September 10, 2002
 * @version $Revision$, $Date$
 */
public class ProjectFile
{

    private static Properties PROPERTIES;
    private static Exception PARSE_EXCEPTION;
    private static final String VALUE_SEPARATOR = "&vs;";

    /**
     *****************************************************************************
     *
     * @param key
     *
     * @return
     */
    public static final String getProperty(String key)
    {
        key = (key == null) ? "" : key.trim().toLowerCase();

        if (PROPERTIES == null)
        {
            (new ProjectFile()).loadProperties();
        }

        String value = PROPERTIES.getProperty(key);

        return (value == null) ? "" : value;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public static final Enumeration getPropertyKeys()
    {
        return PROPERTIES.propertyNames();
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public static final int getPropertyCount()
    {
        int count = 0;
        Enumeration keys = PROPERTIES.propertyNames();

        while (keys.hasMoreElements())
        {
            keys.nextElement();
            count++;
        }

        return count;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public static final String[] toArray(String propertyValue)
    {
        String[] values = new String[0];

        if (propertyValue != null)
        {
            StringTokenizer parser;
            int valueCount;
            int index;

            parser = new StringTokenizer(propertyValue, VALUE_SEPARATOR);
            valueCount = parser.countTokens();
            values = new String[valueCount];
            index = 0;

            while (parser.hasMoreTokens())
            {
                values[index] = parser.nextToken();
                index++;
            }
        }

        return values;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public static final Exception getException()
    {
        return PARSE_EXCEPTION;
    }

    /**
     *****************************************************************************
     *
     */
    private void loadProperties()
    {
        InputStream inputStream;
        InputSource inputSource;

        PROPERTIES = new Properties();
        inputStream = ResourceLoader.loadResourceAsStream("project.xml");
        inputSource = new InputSource(inputStream);

        try
        {
            MainContentHandler mainContentHandler;
	    SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);

            SAXParser parser = factory.newSAXParser();

            mainContentHandler = new MainContentHandler();

	    parser.parse(inputSource, mainContentHandler);
        }
        catch (Exception exception)
        {
            PARSE_EXCEPTION = exception;
        }
    }

    /**
     *****************************************************************************
     *****************************************************************************
     *****************************************************************************
     */
    private class MainContentHandler extends DefaultHandler
    {

        private StringBuffer m_buffer = new StringBuffer(100);
        private Stack m_nameStack = new Stack();
        private final String PROJECT = "project";

        /**
         *************************************************************************
         */
        private MainContentHandler()
        {
            super();
        }

        /**
         *************************************************************************
         *
         * @param namespace
         * @param localName
         * @param qualifiedName
         * @param attributes
         *
         * @throws SAXException
         */
        public void startElement(String namespace,
                                 String localName,
                                 String qualifiedName,
                                 Attributes attributes)
            throws SAXException
        {
            if (qualifiedName.equalsIgnoreCase(PROJECT) == false)
            {
                m_nameStack.push(qualifiedName);
            }
        }

        /**
         *************************************************************************
         *
         * @param chars
         * @param beginIndex
         * @param length
         *
         * @throws PMDException
         */
        public void characters(char[] chars, int beginIndex, int length)
        {
            m_buffer.append(chars, beginIndex, length);
        }

        /**
         *************************************************************************
         *
         * @param namespace
         * @param localName
         * @param qualifiedName
         *
         * @throws SAXException
         */
        public void endElement(String namespace, String localName, String qualifiedName)
            throws SAXException
        {
            String value = m_buffer.toString().replace('\n', ' ').trim();
            String key = buildKey();
            String existingValue = PROPERTIES.getProperty(key);

            if (existingValue != null)
            {
                value = existingValue + VALUE_SEPARATOR + value;
            }

            PROPERTIES.setProperty(key, value);
            m_buffer.setLength(0);
            m_nameStack.pop();
        }

        /**
         *************************************************************************
         *
         * @return
         */
        private String buildKey()
        {
            StringBuffer name = new StringBuffer(100);
            Iterator iterator = m_nameStack.iterator();

            while (iterator.hasNext())
            {
                name.append(iterator.next());
                name.append('/');
            }

            if (name.length() > 0)
            {
                name.setLength(name.length() - 1);
            }

            return name.toString().toLowerCase();
        }
    }
}
