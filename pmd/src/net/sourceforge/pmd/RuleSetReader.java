package net.sourceforge.pmd;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
class RuleSetReader
{

    private String m_fileName;

    /**
     *****************************************************************************
     *
     * @param fileName
     */
    public RuleSetReader(String fileName)
        throws PMDException
    {
        FileReader reader = null;

        try
        {
            InputSource inputSource;
            MainContentHandler mainContentHandler;
            SAXParser parser;

            reader = new FileReader(fileName);
            inputSource = new InputSource(reader);
            mainContentHandler = new MainContentHandler();
            parser = new SAXParser();

            parser.setContentHandler(mainContentHandler);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(inputSource);
        }
        catch (FileNotFoundException exception)
        {
            String template = "Could not read file \"{0}\".  The file does not exist or the path may be incorrect.";
            Object[] args = {fileName};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);

            pmdException.fillInStackTrace();
            throw pmdException;
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
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
            }
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
        private RuleSet m_ruleSet;
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
                Rule rule;

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
                    rule = (Rule) Class.forName(className).newInstance();
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

                rule.setName(ruleName);
                rule.setMessage(message);
                rule.setInclude(Boolean.getBoolean(include));
                m_ruleSet.addRule(m_rule);
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
                    m_ruleSet.setDescription(m_buffer.toString());
                }
                else
                {
                    m_rule.setDescription(m_buffer.toString());
                }
            }
            else if (qualifiedName.equalsIgnoreCase("example"))
            {
                m_rule.setExample(m_buffer.toString());
            }
            else if (qualifiedName.equalsIgnoreCase("rule"))
            {
                m_rule = null;
            }
        }
    }
}