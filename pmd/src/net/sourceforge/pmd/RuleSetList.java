package net.sourceforge.pmd;

import net.sourceforge.pmd.swingui.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Reads and writes a list of included rule sets.  Used by the PMD Viewer to select the
 * rule sets to be used during analysis.  The PMD Viewer provides the editing capability
 * to include or exclude rule sets.
 *
 * @author Donald A. Leckie
 * @since September 11, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetList
{

    private static final String RULE_SET_LIST_FILE_NAME = "Included_Rule_Set_Names.txt";

    /**
     ********************************************************************************
     *
     * @param directoryPath
     *
     * @return
     */
    public static final String[] getIncludedRuleSetNames(String directoryPath)
    throws PMDException
    {
        String[] ruleSetNames = new String[0];

        if (directoryPath != null)
        {
            File file;

            directoryPath = directoryPath.trim();
            file = new File(directoryPath + File.separator + RULE_SET_LIST_FILE_NAME);

            if (file.exists())
            {
                BufferedReader inputStream = null;

                try
                {
                    String ruleSetNameList;
                    StringTokenizer parser;
                    int index;

                    inputStream = new BufferedReader(new FileReader(file));
                    ruleSetNameList = inputStream.readLine();
                    parser = new StringTokenizer(ruleSetNameList, ",");
                    ruleSetNames = new String[parser.countTokens()];
                    index = 0;

                    while (parser.hasMoreTokens())
                    {
                        ruleSetNames[index] = parser.nextToken().trim();
                        index++;
                    }

                }
                catch (FileNotFoundException exception)
                {
                    // Should not reach here because the file was already tested for existence.
                    String message;
                    PMDException pmdException;

                    message = Resources.getString("RESOURCE_RuleSetListFileNotFound");
                    pmdException = new PMDException(message, exception);
                    pmdException.fillInStackTrace();
                    throw pmdException;
                }
                catch (IOException exception)
                {
                    String message;
                    PMDException pmdException;

                    message = Resources.getString("RESOURCE_RuleSetListFileIOError");
                    pmdException = new PMDException(message, exception);
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
                            // Ignore because the file is closed anyway.
                            inputStream = null;
                        }
                    }
                }
            }
        }

        return ruleSetNames;
    }

    /**
     ********************************************************************************
     *
     * @param directoryPath
     *
     * @return
     */
    public static final void saveIncludedRuleSetNames(String directoryPath, String[] ruleSetNames)
    throws PMDException
    {
        if ((directoryPath != null) && (ruleSetNames != null))
        {
            File file;

            directoryPath = directoryPath.trim();
            file = new File(directoryPath + File.separator + RULE_SET_LIST_FILE_NAME);

            if (file.exists())
            {
                file.delete();
            }
            else
            {
                File directory = new File(directoryPath);

                directory.mkdirs();
            }

            PrintWriter outputStream = null;

            try
            {
                StringBuffer buffer;

                outputStream = new PrintWriter(new FileWriter(file));
                buffer = new StringBuffer(100);

                for (int n = 0; n < ruleSetNames.length; n++)
                {
                    buffer.append(ruleSetNames[n]);
                    buffer.append(',');
                }

                if (buffer.length() > 0)
                {
                    buffer.setLength(buffer.length() - 1);
                }

                outputStream.println(buffer.toString());
            }
            catch (FileNotFoundException exception)
            {
                String message = Resources.getString("RESOURCE_RuleSetListFileNotFound");
                PMDException pmdException = new PMDException(message, exception);
                pmdException.fillInStackTrace();
                throw pmdException;
            }
            catch (IOException exception)
            {
                String message;
                PMDException pmdException;

                message = Resources.getString("RESOURCE_RuleSetListFileIOError");
                pmdException = new PMDException(message, exception);
                pmdException.fillInStackTrace();
                throw pmdException;
            }
            finally
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
        }
    }
}