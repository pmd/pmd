package net.sourceforge.pmd;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.swingui.IConstants;

/**
 * Reads an XML file containing information about a rule set and each rule within the rule set.
 * <p>
 * The SAXParser is used to parse the file.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetReader implements IConstants
{

    private RuleSet m_ruleSet;
    private boolean m_onlyIfIncluded;

    // Constants
    private final String REJECT_NOT_INCLUDED = "Reject not included";

    /**
     *****************************************************************************
     *
     */
    public RuleSetReader()
    {
    }

    /**
     *****************************************************************************
     *
     * @param inputStream
     * @param ruleSetFileName
     *
     * @return
     */
    public RuleSet read(InputStream inputStream, String ruleSetFileName)
    throws PMDException
    {
        return read(inputStream, ruleSetFileName, false);
    }

    /**
     *****************************************************************************
     *
     * @param inputStream
     * @param ruleSetFileName
     * @param onlyIfIncluded
     *
     * @return
     */
    public RuleSet read(InputStream inputStream, String ruleSetFileName, boolean onlyIfIncluded)
    throws PMDException
    {
        if (inputStream == null)
        {
            String message = "Missing input stream.";
            PMDException pmdException = new PMDException(message);
            pmdException.fillInStackTrace();
            throw pmdException;
        }

        if (ruleSetFileName == null)
        {
            String message = "Missing rule set file name.";
            PMDException pmdException = new PMDException(message);
            pmdException.fillInStackTrace();
            throw pmdException;
        }

        m_onlyIfIncluded = onlyIfIncluded;

        try
        {
            InputSource inputSource;
            MainContentHandler mainContentHandler;
            SAXParser parser;

            inputSource = new InputSource(inputStream);
            mainContentHandler = new MainContentHandler();
            parser = new SAXParser();

            parser.setContentHandler(mainContentHandler);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(inputSource);
            m_ruleSet.setFileName(ruleSetFileName);

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
            if (exception.getMessage() == REJECT_NOT_INCLUDED)
            {
                // Return a null rule set to indicate that it should not be included.
                return null;
            }

            Exception originalException = exception.getException();

            if (originalException instanceof PMDException)
            {
                throw (PMDException) originalException;
            }

            String message = "SAXException was thrown.";
            PMDException pmdException = new PMDException(message, exception);
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
                String name;
                String include;

                m_ruleSet = new RuleSet();
                name = attributes.getValue("name");
                name = (name == null) ? "Unknown" : name.trim();
                include = attributes.getValue("include");
                include = (include == null) ? "true" : include.trim();

                m_ruleSet.setName(name);
                m_ruleSet.setInclude(include.equalsIgnoreCase("true"));
            }
            else if (qualifiedName.equalsIgnoreCase("rule"))
            {
                String ruleName;
                String message;
                String className;
                String includeText;
                boolean include;

                ruleName = attributes.getValue("name");
                message = attributes.getValue("message");
                className = attributes.getValue("class");
                includeText = attributes.getValue("include");
                ruleName = (ruleName == null) ? "Unknown" : ruleName.trim();
                message = (message == null) ? EMPTY_STRING : message.trim();
                className = (className == null) ? EMPTY_STRING : className.trim();
                includeText = (includeText == null) ? "true" : includeText.trim();
                include = includeText.equalsIgnoreCase("true");

                if (m_onlyIfIncluded && (include == false))
                {
                    SAXException exception = new SAXException(REJECT_NOT_INCLUDED);
                    throw exception;
                }

                if (className.length() == 0)
                {
                    String template = "Missing class name for rule \"{0}\" in rule set \"{1}\".";
                    Object[] args = {ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg);
                    SAXException saxException = new SAXException(EMPTY_STRING, pmdException);
                    pmdException.fillInStackTrace();
                    throw saxException;
                }

                try
                {
                    Class ruleClass;

                    ruleClass = Class.forName(className);
                    m_rule = (Rule) ruleClass.newInstance();
                }
                catch (ClassNotFoundException classNotFoundException)
                {
                    try
                    {
                        Class ruleClass;

                        className = "net.sourceforge.pmd.UnknownRule";
                        ruleClass = Class.forName(className);
                        m_rule = (Rule) ruleClass.newInstance();
                    }
                    catch (Exception exception)
                    {
                        String template = "Cannot find class \"{0}\" on the classpath.";
                        Object[] args = {className};
                        String msg = MessageFormat.format(template, args);
                        PMDException pmdException = new PMDException(msg, exception);
                        SAXException saxException = new SAXException(EMPTY_STRING, pmdException);
                        pmdException.fillInStackTrace();
                        throw saxException;
                    }
                }
                catch (IllegalAccessException exception)
                {
                    String template = "Illegal access to class \"{0}\" for rule \"{1}\" in rule set \"{2}\".";
                    Object[] args = {className, ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg, exception);
                    SAXException saxException = new SAXException(EMPTY_STRING, pmdException);
                    pmdException.fillInStackTrace();
                    throw saxException;
                }
                catch (InstantiationException exception)
                {
                    String template = "Cannot instantiate class \"{0}\" for rule \"{1}\" in rule set \"{2}\".";
                    Object[] args = {className, ruleName, m_ruleSet.getName()};
                    String msg = MessageFormat.format(template, args);
                    PMDException pmdException = new PMDException(msg, exception);
                    SAXException saxException = new SAXException(EMPTY_STRING, pmdException);
                    pmdException.fillInStackTrace();
                    throw saxException;
                }

                m_rule.setName(ruleName);
                m_rule.setMessage(message);
                m_rule.setInclude(include);
                m_ruleSet.addRule(m_rule);
            }
            else if (qualifiedName.equalsIgnoreCase("property"))
            {
                String name = attributes.getValue("name");
                String value = attributes.getValue("value");
                String type = attributes.getValue("type");

                name = (name == null) ? EMPTY_STRING : name.trim();
                value = (value == null) ? EMPTY_STRING : value;
                type = (type == null) ? EMPTY_STRING : type;

                if (name.length() > 0)
                {
                    m_rule.getProperties().setValue(name, value);
                    m_rule.getProperties().setValueType(name, type);
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