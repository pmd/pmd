package net.sourceforge.pmd.swingui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.pmd.PMDException;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class Preferences
{

    private Properties m_preferences = new Properties();
    private String m_defaultUserRuleSetDirectory;
    private String m_defaultSharedRuleSetDirectory;
    private String m_defaultCurrentRuleSetDirectory;

    // Constants
    protected static final String USER_RULE_SET_DIRECTORY = "user_rule_set_directory";
    protected static final String SHARED_RULE_SET_DIRECTORY = "shared_rule_set_directory";
    protected static final String CURRENT_RULE_SET_DIRECTORY = "current_rule_set_directory";
    private static final String UNIVERSAL_SEPARATOR = "&US;";
    private static final String RULE_SET_DIRECTORY_MARK = "&RSDM;";


    /**
     *******************************************************************************
     *
     * @return
     */
    protected Preferences()
    {
        String path;

        //
        // Default user rule set directory.
        //
        path = System.getProperty("user.home") + File.separator + "pmd" + File.separator + "rulesets";
        m_defaultUserRuleSetDirectory = path;

        setRuleSetDirectory(USER_RULE_SET_DIRECTORY, path);

        //
        // Current rule set directory.
        //
        m_defaultCurrentRuleSetDirectory = path;

        setRuleSetDirectory(CURRENT_RULE_SET_DIRECTORY, path);

        //
        // Default shared rule set directory.
        //
        path = System.getProperty("user.dir") + File.separator + "pmd" + File.separator + "rulesets";
        m_defaultSharedRuleSetDirectory = path;

        setRuleSetDirectory(SHARED_RULE_SET_DIRECTORY, path);
    }


    /**
     *******************************************************************************
     *
     * @return
     */
    protected boolean load(PMDViewer pmdViewer)
    {
        String path = System.getProperty("user.home") + File.separator + "pmd" + File.separator + "pmd.preferences";
        File file = new File(path);

        if (file.exists() == false)
        {
            boolean tryWorkingDirectory = false;
            File directory = file.getParentFile();

            try
            {
                directory.mkdirs();
                file.createNewFile();
            }
            catch (IOException exception)
            {
                String template = "Could not create file \"{0}\" in your home directory \"{1}\".  Will try your working directory.";
                Object[] args = {"pmd.preferences", directory};
                String message = MessageFormat.format(template, args);

                MessageDialog.show(pmdViewer, message, exception);

                tryWorkingDirectory = true;
            }

            if (tryWorkingDirectory)
            {
                path = System.getProperty("user.dir") + File.separator + "pmd" + File.separator + "pmd.preferences";
                file = new File(path);
                directory = file.getParentFile();

                try
                {
                    directory.mkdirs();
                    file.createNewFile();
                }
                catch (IOException exception)
                {
                    String template = "Could not create file \"{0}\" in your working directory \"{1}\".";
                    Object[] args = {"pmd.preferences", directory};
                    String message = MessageFormat.format(template, args);

                    MessageDialog.show(pmdViewer, message, exception);

                    return false;
                }
            }
        }

        FileInputStream inputStream = null;

        try
        {
            inputStream = new FileInputStream(file);

            m_preferences.load(inputStream);

            if (m_preferences.containsKey(USER_RULE_SET_DIRECTORY) == false)
            {
                m_preferences.setProperty(USER_RULE_SET_DIRECTORY, m_defaultUserRuleSetDirectory);
            }

            if (m_preferences.containsKey(SHARED_RULE_SET_DIRECTORY) == false)
            {
                m_preferences.setProperty(SHARED_RULE_SET_DIRECTORY, m_defaultSharedRuleSetDirectory);
            }

            if (m_preferences.containsKey(CURRENT_RULE_SET_DIRECTORY) == false)
            {
                m_preferences.setProperty(CURRENT_RULE_SET_DIRECTORY, m_defaultCurrentRuleSetDirectory);
            }

            return true;
        }
        catch (FileNotFoundException exception)
        {
            // Should not reach here because the file was created above.
            exception.printStackTrace();

            return false;
        }
        catch (IOException exception)
        {
            String template = "Could not load file \"{0}\" from directory \"{1}\".";
            Object[] args = {"pmd.preferences", path};
            String message = MessageFormat.format(template, args);

            MessageDialog.show(pmdViewer, message, exception);

            return false;
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     *******************************************************************************
     *
     */
    private void savePreferences()
        throws PMDException
    {
        FileOutputStream outputStream = null;
        String path = null;

        try
        {
            path = System.getProperty("user.home") + File.separator + "pmd.preferences";
            outputStream = new FileOutputStream(path);

            m_preferences.store(outputStream, null);
        }
        catch (FileNotFoundException exception)
        {
            try
            {
                (new File(path)).createNewFile();
            }
            catch (IOException ioException)
            {
                String template = "Could not find your \"{0}\" file in your home directory \"{1}\".";
                Object[] args = {"pmd.preferences", path};
                String message = MessageFormat.format(template, args);
                PMDException pmdException = new PMDException(message, ioException);

                pmdException.fillInStackTrace();

                throw pmdException;
            }
        }
        catch (IOException exception)
        {
            String template = "Could not save your \"{0}\" file in your home directory \"{1}\".";
            Object[] args = {"pmd.preferences", path};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);

            pmdException.fillInStackTrace();

            throw pmdException;
        }
        finally
        {
            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected List getRuleSetDirectoryNames()
    {
        Enumeration values = m_preferences.elements();
        List list = new ArrayList();

        while (values.hasMoreElements())
        {
            String value = (String) values.nextElement();
            String ruleSetName = decodeRuleSetName(value);

            if (ruleSetName != null)
            {
                list.add(ruleSetName);
            }
        }

        return list;
    }

    /**
     *******************************************************************************
     *
     * @param newName
     * @param newDirectory
     */
    protected boolean setRuleSetDirectory(String name, String directory)
    {
        name = trim(name);
        directory = trim(directory);

        if ((name.length() == 0) || (directory.length() == 0))
        {
            return false;
        }

        String key;

        key = name.toLowerCase();
        directory = encodeRuleSetNameAndDirectory(name, directory);

        m_preferences.put(key, directory);

        return true;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private String encodeRuleSetNameAndDirectory(String name, String directory)
    {
        if (directory != null)
        {
            StringBuffer buffer = new StringBuffer(directory.length() + 50);

            buffer.append(name);
            buffer.append(RULE_SET_DIRECTORY_MARK);
            buffer.append(directory);

            for (int n = 0; n < buffer.length(); n++)
            {
                if (buffer.charAt(n) == File.separatorChar)
                {
                    buffer.replace(n, n + 1, UNIVERSAL_SEPARATOR);
                }
            }

            directory = buffer.toString();
        }

        return directory;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private String decodeRuleSetName(String value)
    {
        if (value != null)
        {
            int endIndex = value.indexOf(RULE_SET_DIRECTORY_MARK);

            if (endIndex < 0)
            {
                value = null;
            }
            else
            {
                value = value.substring(0, endIndex);
            }
        }

        return value;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private String decodeRuleSetDirectory(String value)
    {
        if (value != null)
        {
            int beginIndex = value.indexOf(RULE_SET_DIRECTORY_MARK);

            if (beginIndex < 0)
            {
                value = null;
            }
            else
            {
                StringBuffer buffer;
                int universalSeparatorLength;

                beginIndex += RULE_SET_DIRECTORY_MARK.length();
                value = value.substring(beginIndex);
                buffer = new StringBuffer(value);
                universalSeparatorLength = UNIVERSAL_SEPARATOR.length();

                for (int n = 0; n < buffer.length(); n++)
                {
                    if (buffer.charAt(n) == '&')
                    {
                        if ((n + universalSeparatorLength) <= value.length())
                        {
                            if (buffer.charAt(n + 1) == 'U')
                            {
                                if (buffer.charAt(n + 2) == 'S')
                                {
                                    if (buffer.charAt(n + 3) == ';')
                                    {
                                        buffer.delete(n, n + universalSeparatorLength);
                                        buffer.insert(n, File.separatorChar);
                                    }
                                }
                            }
                        }
                    }
                }

                value = buffer.toString();
            }
        }

        return value;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private boolean isDeletable(String key)
    {
        return (SHARED_RULE_SET_DIRECTORY.equalsIgnoreCase(key) == false) &&
               (USER_RULE_SET_DIRECTORY.equalsIgnoreCase(key) == false) &&
               (CURRENT_RULE_SET_DIRECTORY.equalsIgnoreCase(key) == false);
    }

    /**
     *******************************************************************************
     *
     * @param name
     */
    protected void removeRuleSetDirectory(String name)
    {
        String key = trim(name).toLowerCase();

        if (isDeletable(key))
        {
            m_preferences.remove(key);
        }
    }

    /**
     *******************************************************************************
     *
     * @param name
     *
     * @return
     */
    protected String getRuleSetDirectory(String name)
    {
        String key = trim(name).toLowerCase();
        String directory = decodeRuleSetDirectory(m_preferences.getProperty(key));

        if (directory == null)
        {
            directory = "";
        }

        return directory;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getCurrentRuleSetDirectory()
    {
        return getRuleSetDirectory(CURRENT_RULE_SET_DIRECTORY);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getUserRuleSetDirectory()
    {
        return getRuleSetDirectory(USER_RULE_SET_DIRECTORY);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getSharedRuleSetDirectory()
    {
        return getRuleSetDirectory(SHARED_RULE_SET_DIRECTORY);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getPreference(String name)
    {
        String key = trim(name).toLowerCase();
        String value = m_preferences.getProperty(key);

        if (value == null)
        {
            value = "";
        }

        return value;
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param value
     */
    protected boolean setPreference(String name, String value)
    {
        name = trim(name);

        if (name.length() == 0)
        {
            return false;
        }

        if (value == null)
        {
            value = "";
        }

        String key = name.toLowerCase();

        if (key.equals(USER_RULE_SET_DIRECTORY) && (value.length() == 0))
        {
            return false;
        }

        if (key.equals(SHARED_RULE_SET_DIRECTORY) && (value.length() == 0))
        {
            return false;
        }

        if (key.equals(CURRENT_RULE_SET_DIRECTORY) && (value.length() == 0))
        {
            return false;
        }

        m_preferences.put(key, value);

        return true;
    }


    /**
     *************************************************************************
     *
     * @param text
     *
     * @return
     */
    private String trim(String text)
    {
        if (text == null)
        {
            text = "";
        }
        else
        {
            text = text.trim();

            if (text.length() == 0)
            {
                text = "";
            }
        }

        return text;
    }
}