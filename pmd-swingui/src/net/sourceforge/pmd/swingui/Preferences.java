package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;

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
class Preferences {

    private Properties m_properties = new Properties();
    private String m_defaultUserPathToPMD;
    private String m_defaultSharedPathToPMD;
    private String m_defaultCurrentPathToPMD;
    private String m_defaultAnalysisResultsPath;
    private String m_preferencesPath;
    private static Preferences m_preferences;

    // Constants
    private final String USER_PATH_TO_PMD = "user_path_to_pmd";
    private final String SHARED_PATH_TO_PMD = "shared_path_to_pmd";
    private final String CURRENT_PATH_TO_PMD = "current_path_to_pmd";
    private final String LOWEST_PRIORITY_FOR_ANALYSIS = "lowest_priority_for_analysis";
    private final String ANALYSIS_RESULTS_PATH = "analysis_results_path";
    private final String UNIVERSAL_SEPARATOR = "&US;";
    private final String PREFERENCES_FILE_NAME = "user.preferences";
    private final String PMD_DIRECTORY = "pmd";
    private final String ANALYSIS_RESULTS_DIRECTORY = "Analysis_Results";
    private final int LOWEST_RULE_PRIORITY = Rule.LOWEST_PRIORITY;


    /**
     *******************************************************************************
     *
     * @return
     */
    private Preferences() throws PMDException {
        //
        // Default user rule set directory.
        //
        m_defaultUserPathToPMD = System.getProperty("user.home");
        setPath(USER_PATH_TO_PMD, m_defaultUserPathToPMD);

        //
        // Current rule set directory.
        //
        m_defaultCurrentPathToPMD = m_defaultUserPathToPMD;
        setPath(CURRENT_PATH_TO_PMD, m_defaultCurrentPathToPMD);

        //
        // Default shared rule set directory.
        //
        m_defaultSharedPathToPMD = System.getProperty("user.dir");
        setPath(SHARED_PATH_TO_PMD, m_defaultSharedPathToPMD);

        //
        // Default analysis results path.
        //
        m_defaultAnalysisResultsPath = m_defaultUserPathToPMD + File.separator + PMD_DIRECTORY + File.separator + ANALYSIS_RESULTS_DIRECTORY;
        setPath(ANALYSIS_RESULTS_PATH, m_defaultAnalysisResultsPath);

        //
        // Preferences path.
        //
        getPreferencesPath();
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected static final Preferences getPreferences() throws PMDException {
        if (m_preferences == null) {
            m_preferences = new Preferences();
            m_preferences.load();
        }

        return m_preferences;
    }

    /**
     *******************************************************************************
     *
     * @return
     *
     * @throws PMDException
     */
    protected void getPreferencesPath() throws PMDException {
        m_preferencesPath = System.getProperty("user.home") + File.separator + PMD_DIRECTORY + File.separator + PREFERENCES_FILE_NAME;

        File file = new File(m_preferencesPath);

        if (file.exists() == false) {
            File directory = file.getParentFile();

            try {
                directory.mkdirs();
                file.createNewFile();
            } catch (IOException exception) {
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
    protected boolean load() throws PMDException {
        File file = new File(m_preferencesPath);
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);

            m_properties.load(inputStream);

            if (m_properties.containsKey(USER_PATH_TO_PMD) == false) {
                m_properties.setProperty(USER_PATH_TO_PMD, m_defaultUserPathToPMD);
            }

            if (m_properties.containsKey(SHARED_PATH_TO_PMD) == false) {
                m_properties.setProperty(SHARED_PATH_TO_PMD, m_defaultSharedPathToPMD);
            }

            if (m_properties.containsKey(CURRENT_PATH_TO_PMD) == false) {
                m_properties.setProperty(CURRENT_PATH_TO_PMD, m_defaultCurrentPathToPMD);
            }

            if (m_properties.containsKey(ANALYSIS_RESULTS_PATH) == false) {
                m_properties.setProperty(ANALYSIS_RESULTS_PATH, m_defaultAnalysisResultsPath);
            }

            return true;
        } catch (FileNotFoundException exception) {
            String template = "Could not find file \"{0}\" in directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        } catch (IOException exception) {
            String template = "Could not load file \"{0}\" from directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     *******************************************************************************
     *
     */
    protected void save() throws PMDException {
        FileOutputStream outputStream = null;

        try {
            File file = new File(m_preferencesPath);

            if (file.exists() == false) {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(m_preferencesPath);

            m_properties.store(outputStream, null);
        } catch (FileNotFoundException exception) {
            String template = "Could not find your \"{0}\" file in your home directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        } catch (IOException exception) {
            String template = "Could not save your \"{0}\" file in your home directory \"{1}\".";
            Object[] args = {PREFERENCES_FILE_NAME, m_preferencesPath};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException exception) {
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
    protected void setCurrentPathToPMD(String path) {
        setPath(CURRENT_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param path
     */
    protected void setUserPathToPMD(String path) {
        setPath(USER_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param path
     */
    protected void setSharedPathToPMD(String path) {
        setPath(SHARED_PATH_TO_PMD, path);
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param directory
     */
    private boolean setPath(String name, String directory) {
        name = trim(name);
        directory = trim(directory);

        if ((name.length() == 0) || (directory.length() == 0)) {
            return false;
        }

        String key;

        key = name.toLowerCase();
        directory = encodePath(directory);

        m_properties.put(key, directory);

        return true;
    }

    /**
     *******************************************************************************
     *
     * @param directory
     */
    protected void setAnalysisResultPath(String directory) {
        directory = encodePath(trim(directory));

        m_properties.put(ANALYSIS_RESULTS_PATH, directory);
    }

    /**
     *******************************************************************************
     *
     * @param priority
     */
    protected void setLowestPriorityForAnalysis(int priority) {
        if (priority < 0) {
            priority = 0;
        } else if (priority > LOWEST_RULE_PRIORITY) {
            priority = LOWEST_RULE_PRIORITY;
        }

        m_properties.put(LOWEST_PRIORITY_FOR_ANALYSIS, String.valueOf(priority));
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected int getLowestPriorityForAnalysis() {
        int priority;

        try {
            priority = Integer.parseInt((String) m_properties.get(LOWEST_PRIORITY_FOR_ANALYSIS));
        } catch (NumberFormatException exception) {
            priority = LOWEST_RULE_PRIORITY;
        }

        return priority;
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param directory
     *
     * @return
     */
    private String encodePath(String directory) {
        if (directory != null) {
            StringBuffer buffer = new StringBuffer(directory.length() + 50);

            buffer.append(directory);

            for (int n = 0; n < buffer.length(); n++) {
                if (buffer.charAt(n) == File.separatorChar) {
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
     * @param value
     *
     * @return
     */
    private String decodePath(String value) {
        if (value != null) {
            StringBuffer buffer = new StringBuffer(value);
            int universalSeparatorLength = UNIVERSAL_SEPARATOR.length();

            for (int n = 0; n < buffer.length(); n++) {
                if (buffer.charAt(n) == '&') {
                    if ((n + universalSeparatorLength) <= buffer.length()) {
                        if (buffer.charAt(n + 1) == 'U') {
                            if (buffer.charAt(n + 2) == 'S') {
                                if (buffer.charAt(n + 3) == ';') {
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
    protected String getAnalysisResultsPath() {
        String path = decodePath(m_properties.getProperty(ANALYSIS_RESULTS_PATH));

        if (path == null) {
            path = m_defaultAnalysisResultsPath;
        }

        (new File(path)).mkdirs();

        return path;
    }

    /**
     *******************************************************************************
     *
     * @param pathName
     *
     * @return
     */
    private String getPathToPMD(String pathName) {
        String key = trim(pathName).toLowerCase();
        String directory = decodePath(m_properties.getProperty(key));

        if (directory == null) {
            directory = "";
        }

        return directory;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getCurrentPathToPMD() {
        return getPathToPMD(CURRENT_PATH_TO_PMD);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getUserPathToPMD() {
        return getPathToPMD(USER_PATH_TO_PMD);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected String getSharedPathToPMD() {
        return getPathToPMD(SHARED_PATH_TO_PMD);
    }

    /**
     *************************************************************************
     *
     * @param text
     *
     * @return
     */
    private String trim(String text) {
        if (text == null) {
            text = "";
        } else {
            text = text.trim();

            if (text.length() == 0) {
                text = "";
            }
        }

        return text;
    }
}