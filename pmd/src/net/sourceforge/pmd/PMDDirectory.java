package net.sourceforge.pmd;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEventListener;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetEvent;
import net.sourceforge.pmd.swingui.event.RuleSetEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * Defines and provides access to PMD's directory structure.  The user defines the location
 * of the root PMD directory, e.g., /users/userA/PMD.  The PMD directory structure provides
 * the following:
 * <ul>
 * <li>Organization to simplify PMD's access to files.</li>
 * <li>Eliminates dependence of manually updating the Java classpath.</li>
 * <li>Permits adding and removing rule sets without updating lists.</li>
 * </ul>
 * <pre>
 * The directory structure and contents are the following:
 * <code>
 *    PMD
 *       pmd.properties
 *       rulesets
 *          basic.xml
 *          design.xml
 *          import.xml
 *          com
 *             myCompany
 *                pmd
 *                   rules
 *                      myRule01.class
 *                      myRule02.class
 *                      myRule03.class
 *          net
 *             sourceforge
 *                pmd
 *                   rules
 *                      myNewExperimentalRule.class
 * </code>
 * </pre>
 * The <b>PMD</b> directory is the root directory of all PMD files.
 * <p>
 * The <b>pmd.properties</b> file contains various information to be defined.
 * <p>
 * The <b>rulesets</b> directory contains the rule set files and rule class file directories.
 * <p>
 * A <b>rule set file</b> is a XML file that describes the rule set and its rules.  This
 * information is displayed and maintained in the PMD Viewer.  The rule class files are called by PMD
 * to perform the analysis.
 * <p>
 * All rule classes, other than the rule classes in pmd.jar, are stored in directory paths
 * defined by each rule's class name.  The Java classpath is appended with the rulesets
 * directory so that the rule class and any supporting class files may be found.
 * <p>
 * <b>NOTE:</b> The user's home directory will contain a PMD directory with a user.preferences
 * file.  An entry in the user's preferences will be the path to the PMD root directory
 * described above.
 *
 * @author Donald A. Leckie
 * @since September 19, 2002
 * @version $Revision$, $Date$
 */

public class PMDDirectory
{

    private String m_pmdDirectoryPath;
    private String m_ruleSetsDirectoryPath;
    private Properties m_properties;
    private PMDDirectoryRequestEventHandler m_pmdDirectoryRequestEventHandler;
    private RuleSetEventHandler m_ruleSetEventHandler;
    private static PMDDirectory m_pmdDirectoryInstance;

    // Constants
    private final String PROPERTIES_FILE_NAME = "pmd.properties";

    /**
     ********************************************************************************
     *
     * Creates the information about the PMD directory structure that will be required
     * for accessing the PMD files.
     *
     * @param pathToPMD The full path to the PMD directory, but excludes the PMD directory.
     */
    private PMDDirectory(String pathToPMD) throws PMDException
    {
        String classpath;
        String key;

        m_pmdDirectoryRequestEventHandler = new PMDDirectoryRequestEventHandler();
        m_ruleSetEventHandler = new RuleSetEventHandler();
        ListenerList.addListener( m_pmdDirectoryRequestEventHandler);
        ListenerList.addListener( m_ruleSetEventHandler);
        m_pmdDirectoryPath = pathToPMD + File.separator + "PMD";
        m_ruleSetsDirectoryPath = m_pmdDirectoryPath + File.separator + "rulesets";
        key = "java.class.path";
        classpath = System.getProperty(key);
        classpath = classpath + ";" + m_ruleSetsDirectoryPath;
        System.setProperty(key, classpath);
        loadPropertiesFile();
    }

    /**
     ********************************************************************************
     *
     * @param pathToPMD The full path to the PMD directory, but excludes the PMD directory.
     */
    public static final void open(String pathToPMD) throws PMDException
    {
        m_pmdDirectoryInstance = new PMDDirectory(pathToPMD);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    public static final PMDDirectory getDirectory()
    {
        return m_pmdDirectoryInstance;
    }

    /**
     ********************************************************************************
     *
     * Gets a rule set containing only the rule sets and rules to be included for running
     * the analysis.
     *
     * @return  A rule containing only included rules.
     */
    public RuleSet getIncludedRules(int lowestPriorityForAnalysis)
    throws PMDException
    {
        RuleSet includedRules = new RuleSet();
        Iterator ruleSetFiles = getRuleSetFiles().iterator();

        while (ruleSetFiles.hasNext())
        {
            File ruleSetFile = (File) ruleSetFiles.next();
            RuleSet ruleSet = getRuleSet(ruleSetFile, true);

            if ((ruleSet != null) && ruleSet.include())
            {
                Iterator allRules = ruleSet.getRules().iterator();

                while (allRules.hasNext())
                {
                    Rule rule = (Rule) allRules.next();

                    if (rule.include())
                    {
                        if (rule.getPriority() <= lowestPriorityForAnalysis)
                        {
                            includedRules.addRule(rule);
                        }
                    }
                }
            }
        }

        return includedRules;
    }

    /**
     ********************************************************************************
     *
     * Gets the rule set for the given rule set file.  All rules in the rule set file
     * are stored in the rule set regardless of their <i>include</i> state.
     *
     * @param ruleSetFile The file of the desired rule set.
     *
     * @return A rule set containing all of its rules.
     *
     * @throws PMDException
     */
    public RuleSet getRuleSet(File ruleSetFile)
    throws PMDException
    {
        return getRuleSet(ruleSetFile, false);
    }

    /**
     ********************************************************************************
     *
     * Gets the rule set for the given rule set File.  All rules in the rule set file
     * are stored in the rule set according of their <i>include</i> state and the <i>onlyIfIncluded</i>
     * flag.
     *
     * @param ruleSetFile The file of the desired rule set.
     *
     * @return A rule set containing all of its rules.
     *
     * @throws PMDException
     */
    public RuleSet getRuleSet(File ruleSetFile, boolean onlyIfIncluded)
    throws PMDException
    {
        if (ruleSetFile == null)
        {
            String message = "Rule set file parameter is missing.";
            PMDException exception = new PMDException(message);
            exception.fillInStackTrace();
            throw exception;
        }

        FileInputStream inputStream = null;
        RuleSet ruleSet = null;

        try
        {
            RuleSetReader reader;
            inputStream = new FileInputStream(ruleSetFile);
            reader = new RuleSetReader();
            ruleSet = reader.read(inputStream, ruleSetFile.getName(), onlyIfIncluded);
        }
        catch (FileNotFoundException exception)
        {
            String template = "Rule set \"{0}\" was not found.";
            String[] args = {ruleSetFile.getPath()};
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
                }
            }
        }

        return ruleSet;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private List getRuleSetFiles()
    {
        List ruleSetFiles = new ArrayList();
        File directory = new File(m_ruleSetsDirectoryPath);

        if (directory.exists() == false)
        {
            directory.mkdirs();
        }

        File[] files = directory.listFiles(new XMLFileNameFilter());

        for (int n = 0; n < files.length; n++)
        {
            ruleSetFiles.add(files[n]);
        }

        return ruleSetFiles;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    public List getRegisteredRuleSets()
    {
        List ruleSetList = new ArrayList();

        try
        {
            Iterator ruleSets = (new RuleSetFactory()).getRegisteredRuleSets();

            while (ruleSets.hasNext())
            {
                RuleSet ruleSet;
                Iterator rules;

                ruleSet = (RuleSet) ruleSets.next();
                ruleSet.setInclude(true);
                rules = ruleSet.getRules().iterator();

                while (rules.hasNext())
                {
                    ((Rule) rules.next()).setInclude(true);
                }

                ruleSetList.add(ruleSet);
            }
        }
        catch (RuleSetNotFoundException exception)
        {
            // This should not happen because the registered rule sets are resources in pmd.jar.
            System.out.println(exception.getMessage());
        }

        return ruleSetList;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    public List getRuleSets() throws PMDException
    {
        List ruleSetList;
        List ruleSetFilesList = getRuleSetFiles();

        if (ruleSetFilesList.size() == 0)
        {
            ruleSetList = getRegisteredRuleSets();
        }
        else
        {
            Iterator ruleSetFiles;

            ruleSetList = new ArrayList();
            ruleSetFiles = ruleSetFilesList.iterator();

            while (ruleSetFiles.hasNext())
            {
                File ruleSetFile = (File) ruleSetFiles.next();
                RuleSet ruleSet = getRuleSet(ruleSetFile);

                ruleSetList.add(ruleSet);
            }
        }

        return ruleSetList;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    public String getPMDDirectoryPath()
    {
        return m_pmdDirectoryPath;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    public String getRuleSetsDirectoryPath()
    {
        return m_ruleSetsDirectoryPath;
    }

    /**
     ********************************************************************************
     *
     * @param ruleSetList
     */
    protected void saveRuleSets(List ruleSetList)
    {
        Iterator ruleSets = ruleSetList.iterator();

        while (ruleSets.hasNext())
        {
            RuleSet ruleSet = (RuleSet) ruleSets.next();
            String ruleSetFileName = ruleSet.getFileName();
            String path = m_ruleSetsDirectoryPath + File.separator + ruleSetFileName;
            File file = new File(path);
            FileOutputStream outputStream = null;

            if (file.exists())
            {
                file.delete();
            }

            try
            {
                RuleSetWriter writer;

                outputStream = new FileOutputStream(file);
                writer = new RuleSetWriter(outputStream);
                writer.write(ruleSet);
            }
            catch (FileNotFoundException exception)
            {
                // Should not reach here because the rule set file has been deleted if it
                // existed and the directories all exist.
                exception = null;
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
                        exception = null;
                    }
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     * @param pathToPMD
     */
    private void loadPropertiesFile()
    throws PMDException
    {
        String propertiesFileName;
        FileInputStream inputStream;

        propertiesFileName = m_pmdDirectoryPath + File.separator + PROPERTIES_FILE_NAME;
        m_properties = new Properties();
        inputStream = null;

        try
        {
            File file = new File(propertiesFileName);

            if (file.exists() == false)
            {
                File directory = file.getParentFile();

                directory.mkdirs();
                file.createNewFile();
            }

            inputStream = new FileInputStream(propertiesFileName);
            m_properties.load(inputStream);
        }
        catch (FileNotFoundException exception)
        {
            String template = "Could not find the file \"{0}\".";
            String[] args = {propertiesFileName};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        }
        catch (IOException exception)
        {
            String template = "Unable to read the file \"{0}\".";
            String[] args = {propertiesFileName};
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
                    exception = null;
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     */
    public void savePropertiesFile()
    throws PMDException
    {
        FileOutputStream outputStream = null;
        String propertiesFileName = m_pmdDirectoryPath + File.separator + PROPERTIES_FILE_NAME;
        File file = new File(propertiesFileName);
        if (file.exists())
        {
            file.delete();
        }

        try
        {
            m_properties.store(outputStream, null);
        }
        catch (FileNotFoundException exception)
        {
            String template = "Could not find the file \"{0}\".";
            String[] args = {propertiesFileName};
            String message = MessageFormat.format(template, args);
            PMDException pmdException = new PMDException(message, exception);
            pmdException.fillInStackTrace();
            throw pmdException;
        }
        catch (IOException exception)
        {
            String template = "Unable to read the file \"{0}\".";
            String[] args = {propertiesFileName};
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
                    exception = null;
                }
            }
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RuleSetEventHandler implements RuleSetEventListener
    {

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void saveRuleSets(RuleSetEvent event)
        {
            List ruleSetList = event.getRuleSetList();
            PMDDirectory.this.saveRuleSets(ruleSetList);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class PMDDirectoryRequestEventHandler implements PMDDirectoryRequestEventListener
    {

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void requestRuleSetPath(PMDDirectoryRequestEvent event)
        {
            PMDDirectoryReturnedEvent.notifyReturnedRuleSetPath(this, getRuleSetsDirectoryPath());
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void requestAllRuleSets(PMDDirectoryRequestEvent event) throws PMDException
        {
            PMDDirectoryReturnedEvent.notifyReturnedAllRuleSets(this, getRuleSets());
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void requestDefaultRuleSets(PMDDirectoryRequestEvent event)
        {
            PMDDirectoryReturnedEvent.notifyReturnedDefaultRuleSets(this, getRegisteredRuleSets());
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void requestIncludedRules(PMDDirectoryRequestEvent event) throws PMDException
        {
            int priority = event.getLowestPriorityForAnalysis();
            PMDDirectoryReturnedEvent.notifyReturnedIncludedRules(this, getIncludedRules(priority));
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class XMLFileNameFilter implements FilenameFilter
    {

        /**
         ***************************************************************************
         *
         * @param directory
         * @param fileName
         *
         * @return
         */
        public boolean accept(File directory, String fileName)
        {
            return fileName.toLowerCase().endsWith(".xml");
        }
    }
}
