package net.sourceforge.pmd;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads an XML file containing information about a rule set and each rule within the rule set.
 * <p>
 * The SAXParser is used to parse the file.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetReader
{

    private String m_fileName;
    private RuleSet m_ruleSet;
    private InputStream m_inputStream;

    /**
     *****************************************************************************
     *
     */
    public RuleSetReader(InputStream inputStream)
    {
        m_inputStream = inputStream;
    }

    /**
     *****************************************************************************
     *
     * @param inputStream
     */
    public RuleSet read()
        throws PMDException
    {
        try
        {
            InputSource inputSource;
            MainContentHandler mainContentHandler;
            SAXParser parser;

            inputSource = new InputSource(m_inputStream);
            mainContentHandler = new MainContentHandler();
            parser = new SAXParser();

            parser.setContentHandler(mainContentHandler);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(inputSource);

            return m_ruleSet;
        }
        catch (IOException exception)
        {
            PMDException pmdException =  new PMDException("IOException was thrown.", exception);

            pmdException.fillInStackTrace();

            throw pmdException;
        }
        catch (SAXException exception)
        {
            Exception originalException = exception.getException();

            if (originalException instanceof PMDException)
            {
                throw (PMDException) originalException;
            }

            PMDException pmdException = new PMDException("SAXException was thrown.", exception);

            pmdException.fillInStackTrace();

            throw pmdException;
        }
        catch (Exception exception)
        {
            PMDException pmdException = new PMDException("Uncaught exception was thrown.", exception);

            pmdException.fillInStackTrace();

            throw pmdException;
        }
    }

    /**
     *****************************************************************************
     *****************************************************************************
     *****************************************************************************
     */
    private class MainContentHandler extends DefaultHandler
    {

        private StringBuffer m_buffer = new StringBuffer(500);
        private Rule m_rule;

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
            m_buffer.setLength(0);

            if (qualifiedName.equalsIgnoreCase("ruleset"))
            {
                String ruleSetName;

                m_ruleSet = new RuleSet();
                ruleSetName = attributes.getValue("name");
                ruleSetName = (ruleSetName == null) ? "Unknown" : ruleSetName.trim();

                m_ruleSet.setName(ruleSetName);
            }
            else if (qualifiedName.equalsIgnoreCase("rule"))
            {
                String ruleName;
                String message;
                String className;
                String include;

                ruleName = attributes.getValue("name");
                message = attributes.getValue("message");
                className = attributes.getValue("class");
                include = attributes.getValue("include");
                ruleName = (ruleName == null) ? "Unknown" : ruleName.trim();
                message = (message == null) ? "" : message.trim();
                className = (className == null) ? "" : className.trim();
                include = (include == null) ? "false" : include.trim();

                if (className.length() == 0)
                {
                    String template = "Missing class name for rule \"{0}\" in rule set \"{1}\".";
                    Object[] args = {ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg);
                    SAXException saxException = new SAXException("", pmdException);

                    pmdException.fillInStackTrace();

                    throw saxException;
                }

                try
                {
                    m_rule = (Rule) Class.forName(className).newInstance();
                }
                catch (ClassNotFoundException exception)
                {
                    String template = "Cannot find class \"{0}\" for rule \"{1}\" in rule set \"{2}\".";
                    Object[] args = {className, ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg, exception);
                    SAXException saxException = new SAXException("", pmdException);

                    pmdException.fillInStackTrace();

                    throw saxException;
                }
                catch (IllegalAccessException exception)
                {
                    String template = "Illegal access to class \"{0}\" for rule \"{1}\" in rule set \"{2}\".";
                    Object[] args = {className, ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg, exception);
                    SAXException saxException = new SAXException("", pmdException);

                    pmdException.fillInStackTrace();

                    throw saxException;
                }
                catch (InstantiationException exception)
                {
                    String template = "Cannot instantiate class \"{0}\" for rule \"{1}\" in rule set \"{2}\".";
                    Object[] args = {className, ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg, exception);
                    SAXException saxException = new SAXException("", pmdException);

                    pmdException.fillInStackTrace();

                    throw saxException;
                }

                m_rule.setName(ruleName);
                m_rule.setMessage(message);
                m_rule.setInclude(Boolean.getBoolean(include));
                m_ruleSet.addRule(m_rule);
            }
            else if (qualifiedName.equalsIgnoreCase("property"))
            {
                String name = attributes.getValue("name");
                String value = attributes.getValue("value");

                name = (name == null) ? "" : name.trim();
                value = (value == null) ? "" : value;

                if (name.length() > 0)
                {
                    m_rule.addProperty(name, value);
                }
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
            if (qualifiedName.equalsIgnoreCase("description"))
            {
                if (m_rule == null)
                {
                    m_ruleSet.setDescription(trim(m_buffer));
                }
                else
                {
                    m_rule.setDescription(trim(m_buffer));
                }
            }
            else if (qualifiedName.equalsIgnoreCase("message"))
            {
                m_rule.setMessage(trim(m_buffer));
            }
            else if (qualifiedName.equalsIgnoreCase("example"))
            {
                m_rule.setExample(trimExample(m_buffer));
            }
            else if (qualifiedName.equalsIgnoreCase("rule"))
            {
                m_rule = null;
            }
        }

        /**
         ***************************************************************************
         */
        private String trim(StringBuffer buffer)
        {
            if (buffer.length() > 0)
            {
                for (int n = 0; n < buffer.length(); n++)
                {
                    char theChar = buffer.charAt(n);

                    if (theChar == '\n')
                    {
                        buffer.deleteCharAt(n);

                        n--;
                    }
                    else if (n == 0)
                    {
                        if (theChar == ' ')
                        {
                            buffer.deleteCharAt(n);

                            n--;
                        }
                    }
                    else if ((theChar == ' ') && (buffer.charAt(n - 1) == ' '))
                    {
                        buffer.deleteCharAt(n);

                        n--;
                    }
                }

                int newLength = buffer.length();

                for (int n = buffer.length() - 1; n >= 0; n--)
                {
                    if (buffer.charAt(n) != ' ')
                    {
                        break;
                    }

                    newLength--;
                }

                buffer.setLength(newLength);
            }

            return buffer.toString();
        }

        /**
         ***************************************************************************
         */
        private String trimExample(StringBuffer buffer)
        {
            while ((buffer.length() > 0) && ((buffer.charAt(0) == '\n') || (buffer.charAt(0) == ' ')))
            {
                buffer.deleteCharAt(0);
            }

            for (int n = buffer.length() - 1; n >= 0; n--)
            {
                if ((buffer.charAt(n) != '\n') && (buffer.charAt(n) != ' '))
                {
                    buffer.setLength(n + 1);
                    break;
                }
            }

            return buffer.toString();
        }
    }
}