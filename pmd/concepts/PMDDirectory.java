package net.sourceforge.pmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sourceforge.pmd.swingui.Preferences;
import net.sourceforge.pmd.swingui.Resources;

/**
 * Defines and provides access to PMD's directory structure.  The user defines the location
 * of the root PMD directory, e.g., /users/userA/PMD.  The PMD directory structure provides
 * the following:
 * <ul>
 * <li>Organization to simplify PMD's access to files.</li>
 * <li>Eliminates dependence of updating the Java classpath.</li>
 * <li>Permits adding and removing rule sets without updating lists.</li>
 * </ul>
 * <pre>
 * The directory structure and contents are the following:
 * <code>
 *    PMD
 *       pmd.properties
 *       rulesets
 *          basic_rules
 *             basic_rules.xml
 *             EmptyCatchBlock.class
 *             IfElseStmtsMustUseBracesRule.class
 *             WhileLoopsMustUseBracesRule.class
 *          design_rules
 *             design_rules.xml
 *             LooseCouplingRule.class
 *             SimplifyBooleanReturnsRule.class
 *          import_rules
 *             import_rules.xml
 *             DontImportJavaLang.class
 *             DuplicateImports.class
 * </code>
 * </pre>
 * The <b>PMD</b> directory is the root directory of all PMD files.
 * <p>
 * The <b>pmd.properties</b> file contains information such as a list of rule sets that are
 * included when the rules are analyzed.  Other properties are to be defined.
 * <p>
 * The <b>rulesets</b> directory contains the rule set directories.
 * <p>
 * A <b>rule set</b> directory contains one XML file and one or more Java class files.  The
 * XML file contains descriptive information about the rule set and its rules.  This information
 * is displayed and maintained in the PMD Viewer.  The rule class files are called by PMD
 * to perform the analysis.
 * <p>
 * <b>NOTE:</b> The user's home directory will contain a PMD directory with a user.preferences
 * file.  An entry in the user's preferences will be the PMD root directory
 *
 * @author Donald A. Leckie
 * @since September 19, 2002
 * @version $Revision$, $Date$
 */

public class PMDDirectory
{

    private String m_rootDirectory;
    private Properties m_properties;

    // Constants
    private final String RULE_SET_LIST = "ruleSetList";
    private final String PROPERTIES_FILE_NAME = "pmd.properties";

    /**
     ********************************************************************************
     *
     * Creates the information about the PMD directory structure that will be required
     * for accessing the PMD files.
     *
     * @param pathToPMD The full path to the PMD directory, but excludes the PMD directory.
     */
    public PMDDirectory(String pathToPMD)
    throws PMDException
    {
        String propertiesFileName;
        FileInputStream inputStream;

        m_rootDirectory = pathToPMD + File.separator + "PMD";
        propertiesFileName = m_rootDirectory + File.separator + PROPERTIES_FILE_NAME;
        m_properties = new Properties();
        inputStream = null;

        try
        {
            File file = new File(propertiesFileName);

            if (file.exists() == false)
            {
                file.mkdirs();
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
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     * Gets a rule set containing only the rule sets and rules to be included for running
     * the analysis.
     *
     * @return  A rule containing only included rules.
     */
    public RuleSet getIncludedRules()
    throws PMDException
    {
        RuleSet includedRules = new RuleSet();
        List includedRuleSetFileNames = getIncludedRuleSetFileNames();
        List ruleSetDirectoryNames = getRuleSetDirectoryNames();
        Iterator directories = ruleSetDirectoryNames.iterator();
        String ruleSetsDirectory = m_rootDirectory + File.separator + "rulesets";

        while (directories.hasNext())
        {
            String directoryName = (String) directories.next();
            String ruleSetDirectory = ruleSetsDirectory + File.separator + directoryName;
            String ruleSetFileName = getRuleSetFileName(ruleSetDirectory);
            boolean includeRuleSet = false;
            Iterator includedRuleSets = includedRuleSetFileNames.iterator();

            while (includedRuleSets.hasNext())
            {
                String includedRuleSetFileName = (String) includedRuleSets.next();

                if (ruleSetFileName.equalsIgnoreCase(includedRuleSetFileName))
                {
                    includeRuleSet = true;

                    break;
                }
            }

            if (includeRuleSet)
            {
                RuleSet ruleSet = getRuleSet(ruleSetDirectory);
                Iterator allRules = ruleSet.getRules().iterator();

                while (allRules.hasNext())
                {
                    Rule rule = (Rule) allRules.next();

                    if (rule.isInclude())
                    {
                        includedRules.addRule(rule);
                    }
                }
            }
        }

        return includedRules;
    }

    /**
     ********************************************************************************
     *
     * Gets a list of the rule set directory names in the <i>ruleset</i> directory.
     *
     * @return A list of rule set directory names.
     */
    public List getRuleSetDirectoryNames()
    {
        File ruleSetDirectory = new File(m_rootDirectory + File.separator + "rulesets");
        List directoryNamesList = new ArrayList();
        File[] directoryNames = ruleSetDirectory.listFiles();

        for (int n = 0; n < directoryNames.length; n++)
        {
            if (directoryNames[n].isDirectory())
            {
                directoryNamesList.add(directoryNames[n].getName());
            }
        }

        return directoryNamesList;
    }

    /**
     ********************************************************************************
     *
     * Gets the rule set for the given rule set directory name.  All rules in the directory
     * are stored in the rule set regardless of their <i>include</i> state.
     *
     * @param ruleSetDirectoryName The directory name of the desired rule set.
     *
     * @return A rule set containing all of its rules.
     *
     * @throws PMDException
     */
    public RuleSet getRuleSet(String ruleSetDirectoryName)
    throws PMDException
    {
        String directoryPath = m_rootDirectory
                             + File.separator
                             + "rulesets"
                             + File.separator
                             + ruleSetDirectoryName;
        File directory = new File(directoryPath);

        if (directory.exists() == false)
        {
            String message = "Rule set directory \"" + directoryPath + "\" does not exist.";
            PMDException pmdException = new PMDException(message);
            pmdException.fillInStackTrace();

            throw pmdException;
        }

        File[] files = directory.listFiles();
        File ruleSetFile = null;

        // Get rule set file whose name ends with ".xml".
        for (int n = 0; n < files.length; n++)
        {
            if (files[n].getName().endsWith(".xml"))
            {
                ruleSetFile = files[n];

                break;
            }
        }

        if (ruleSetFile == null)
        {
            String message = "There is no XML rule set file in \"" + directoryPath + "\".";
            PMDException pmdException = new PMDException(message);
            pmdException.fillInStackTrace();

            throw pmdException;
        }

        RuleSetReader reader = new RuleSetReader();
        RuleSet ruleSet = reader.read(ruleSetFile);

        return ruleSet;
    }

    /**
     ********************************************************************************
     *
     * Gets a list of rule set file names that define the rule sets to be included during
     * analysis.  The file names are stored as a string in the preferences.
     *
     * @return List of rule set names.
     */
    public List getIncludedRuleSetFileNames()
    {
        String ruleSetFileNameList = m_properties.getProperty(RULE_SET_LIST);
        List ruleSetFileNames = new ArrayList();

        if (ruleSetFileNameList != null)
        {
            StringTokenizer parser = new StringTokenizer(ruleSetFileNameList, ",");

            while (parser.hasMoreTokens())
            {
                String ruleSetFileName = parser.nextToken().trim();

                if (ruleSetFileName.length() > 0)
                {
                    ruleSetFileNames.add(ruleSetFileName);
                }
            }
        }

        return ruleSetFileNames;
    }

    /**
     ********************************************************************************
     *
     * Stores the list of file names that define the rule sets to be included during analysis.
     * The list is stored as a string of file names in the preferences.
     *
     * @param ruleSetFileNames
     */
    public final void saveIncludedRuleSetFileNames(List ruleSetFileNames)
    {
        if (ruleSetFileNames != null)
        {
            StringBuffer buffer = new StringBuffer(100);
            Iterator iterator = ruleSetFileNames.iterator();

            while (iterator.hasNext())
            {
                String ruleSetFileName = ((String) iterator.next()).trim();

                if (ruleSetFileName.length() > 0)
                {
                    buffer.append(ruleSetFileName);
                    buffer.append(',');
                }
            }

            if (buffer.length() > 0)
            {
                buffer.setLength(buffer.length() - 1);
            }

            m_properties.setProperty(RULE_SET_LIST, buffer.toString());
        }
    }

    /**
     ********************************************************************************
     *
     * @param ruleSetDirectory
     *
     * @return
     */
    private String getRuleSetFileName(String ruleSetDirectory)
    {
        File directory = new File(ruleSetDirectory);
        String[] fileNames = directory.list();

        for (int n = 0; n < fileNames.length; n++)
        {
            if (fileNames[n].toLowerCase().endsWith(".xml"))
            {
                return fileNames[n];
            }
        }

        return null;
    }

    /**
     ********************************************************************************
     *
     */
    private void savePropertiesFile()
    throws PMDException
    {
        FileOutputStream outputStream;
        String propertiesFileName;
        File file;

        outputStream = null;
        propertiesFileName = m_rootDirectory + File.separator + PROPERTIES_FILE_NAME;
        file = new File(propertiesFileName);

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
                    exception.printStackTrace();
                }
            }
        }
    }
}