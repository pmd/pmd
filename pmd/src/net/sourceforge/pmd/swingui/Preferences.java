package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class Preferences
{

    private Properties m_preferences = new Properties();
    private String m_defaultUserPathToPMD;
    private String m_defaultSharedPathToPMD;
    private String m_defaultCurrentPathToPMD;
    private String m_preferencesPath;

    // Constants
    private final String USER_PATH_TO_PMD = "user_path_to_pmd";
    private final String SHARED_PATH_TO_PMD = "shared_path_to_pmd";
    private final String CURRENT_PATH_TO_PMD = "current_path_to_pmd";
    private final String UNIVERSAL_SEPARATOR = "&US;";
    private final String PREFERENCES_FILE_NAME = "user.preferences";


    /**
     *******************************************************************************
     *
     * @return
     */
    protected Preferences()
    throws PMDException
    {
        //
        // Default user rule set directory.
        //
        m_defaultUserPathToPMD = System.getProperty("user.home");
        setPathToPMD(USER_PATH_TO_PMD, m_defaultUserPathToPMD);

        //
        // Current rule set directory.
        //
        m_defaultCurrentPathToPMD = m_defaultUserPathToPMD;
        setPathToPMD(CURRENT_PATH_TO_PMD, m_defaultCurrentPathToPMD);

        //
        // Default shared rule set directory.
        //
        m_defaultSharedPathToPMD = System.getProperty("user.dir");
        setPathToPMD(SHARED_PATH_TO_PMD, m_defaultSharedPathToPMD);

        //
        // Preferences path.
        //
        getPreferencesPath();
    }


    /**
     *******************************************************************************
     *
     * @return
     *
     * @throws PMDException
     */
    protected void getPreferencesPath()
    throws PMDException
    {
        m_preferencesPath = System.getProperty("user.home")
                          + File.separator
                          + "pmd"
                          + File.separator
                          + PREFERENCES_FILE_NAME;

        File file = new File(m_preferencesPath);

        if (file.exists() == false)
        {
            File directory = file.getParentFile();

            try
            {
                directory.mkdirs();
                file.createNewFile();
            }
            catch (IOException exception)
            {
                String template = "Could not create file \"{0}\" in your home directory \"{1}\".";
                Object[] args = {PREFERENCES_FILE_NAME, directory};
                String message = MessageFormat.format(template, args);
                PMDException pmdException = new PMDException(message, exception);
                pmdException.fillInStackTrace();

                throw pmdException;
            }
        }
    }


    /**
     *******************************************************************************
     *
     * @return
     */
    protected boolean load()
    throws PMDException
    {
        File file = new File(m_preferencesPath);
        FileInputStream inputStream = null;

        try
        {
            inputStream = new FileInputStream(file);

            m_preferences.load(inputStream);

            if (m_preferences.containsKey(USER_PATH_TO_PMD) == false)
            {
                m_preferences.setProperty(USER_PATH_TO_PMD, m_defaultUserPathToPMD);
            }

            if (m_preferences.containsKey(SHARED_PATH_TO_PMD) == false)
            {
                m_preferences.setProperty(SHARED_PATH_TO_PMD, m_defaultSharedPathToPMD);
            }

            if (m_preferences.containsKey(CURRENT_PATH_TO_PMD) == false)
            {
                m_preferences.setProperty(CURRENT_PATH_TO_PMD, m_defaultCurrentPathToPMD);
            }

            return true;
        }
        catch (FileNotFoundException exception)
        {
            String template = "Could not find file \"{0}\" in directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        }
        catch (IOException exception)
        {
            String template = "Could not load file \"{0}\" from directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
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
    protected void save()
    throws PMDException
    {
        FileOutputStream outputStream = null;

        try
        {
            File file = new File(m_preferencesPath);

            if (file.exists() == false)
            {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(m_preferencesPath);

            m_preferences.store(outputStream, null);
        }
        catch (FileNotFoundException exception)
        {
            String template = "Could not find your \"{0}\" file in your home directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        }
        catch (IOException exception)
        {
            String template = "Could not save your \"{0}\" file in your home directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
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
     * @param path
     */
    protected void setCurrentPathToPMD(String path)
    {
        setPathToPMD(CURRENT_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param path
     */
    protected void setUserPathToPMD(String path)
    {
        setPathToPMD(USER_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param path
     */
    protected void setSharedPathToPMD(String path)
    {
        setPathToPMD(SHARED_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param directory
     */
    private boolean setPathToPMD(String name, String directory)
    {
        name = trim(name);
        directory = trim(directory);

        if ((name.length() == 0) || (directory.length() == 0))
        {
            return false;
        }

        String key;

        key = name.toLowerCase();
        directory = encodePathToPMD(directory);

        m_preferences.put(key, directory);

        return true;
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param directory
     *
     * @return
     */
    private String encodePathToPMD(String directory)
    {
        if (directory != null)
        {
            StringBuffer buffer = new StringBuffer(directory.length() + 50);

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
    private String decodePathToPMD(String value)
    {
        if (value != null)
        {
            StringBuffer buffer = new StringBuffer(value);
            int universalSeparatorLength = UNIVERSAL_SEPARATOR.length();

            for (int n = 0; n < buffer.length(); n++)
            {
                if (buffer.charAt(n) == '&')
                {
                    if ((n + universalSeparatorLength) <= buffer.length())
                    {
                        if (buffer.charAt(n + 1) == 'U')
                        {
                            if (buffer.charAt(n + 2) == 'S')
                            {
                                if (buffer.charAt(n + 3) == ';')
                                {
                                    buffer.replace(n, n + universalSeparatorLength, File.separator);
                                }
                            }
                        }
                    }
                }
            }

            value = buffer.toString();
        }

        return value;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    /*
    private boolean isDeletable(String key)
    {
        return (SHARED_PATH_TO_PMD.equalsIgnoreCase(key) == false) &&
               (USER_PATH_TO_PMD.equalsIgnoreCase(key) == false) &&
               (CURRENT_PATH_TO_PMD.equalsIgnoreCase(key) == false);
    }
    */

    /**
     *******************************************************************************
     *
     * @param pathName
     *
     * @return
     */
    private String getPathToPMD(String pathName)
    {
        String key = trim(pathName).toLowerCase();
        String directory = decodePathToPMD(m_preferences.getProperty(key));

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
    protected String getCurrentPathToPMD()
    {
        return getPathToPMD(CURRENT_PATH_TO_PMD);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getUserPathToPMD()
    {
        return getPathToPMD(USER_PATH_TO_PMD);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getSharedPathToPMD()
    {
        return getPathToPMD(SHARED_PATH_TO_PMD);
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

        if (key.equals(USER_PATH_TO_PMD) && (value.length() == 0))
        {
            return false;
        }

        if (key.equals(SHARED_PATH_TO_PMD) && (value.length() == 0))
        {
            return false;
        }

        if (key.equals(CURRENT_PATH_TO_PMD) && (value.length() == 0))
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