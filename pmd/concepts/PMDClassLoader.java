package net.sourceforge.pmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;


/**
 * Provides special functionality for PMD class loading.
 *
 * @author Donald A. Leckie
 * @since September 19, 2002
 * @version $Revision$, $Date$
 */
public class PMDClassLoader extends ClassLoader
{

    /**
     ********************************************************************************
     */
    public PMDClassLoader()
    {
        super();
    }

    /**
     ********************************************************************************
     *
     * @param classFileName
     *
     * @return
     *
     * @throws PMDException
     */
    public Class loadRuleClass(String classFileName, String className)
    throws PMDException
    {
        classFileName = trim(classFileName);
        className = trim(className);

        if (classFileName == null)
        {
            String message = "Missing the class file name.";
            PMDException pmdException = new PMDException(message);

            pmdException.fillInStackTrace();

            throw pmdException;
        }

        if (className == null)
        {
            String message = "Missing the class name.";
            PMDException pmdException = new PMDException(message);

            pmdException.fillInStackTrace();

            throw pmdException;
        }

        FileInputStream inputStream = null;
        Class ruleClass = null;
        File file = new File(classFileName);

        if (file.exists() == false)
        {
            String template = "Could not open file \"{0}\" for class \"{1}\".";
            String[] parameters = {classFileName, className};
            String message = MessageFormat.format(template, parameters);
            PMDException pmdException = new PMDException(message);

            pmdException.fillInStackTrace();

            throw pmdException;
        }

        try
        {
            byte[] data;

            inputStream = new FileInputStream(classFileName);
            data = new byte[(int) file.length()];
            inputStream.read(data);
            ruleClass = defineClass(className, data, 0, data.length);
            resolveClass(ruleClass);
        }
        catch (IOException exception)
        {
            String template = "Error while reading file \"{0}\" for class \"{1}\".";
            String[] parameters = {classFileName, className};
            String message = MessageFormat.format(template, parameters);
            PMDException pmdException = new PMDException(message);

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
                    inputStream = null;
                }
            }
        }

        return ruleClass;
    }

    /**
     ********************************************************************************
     *
     * @param text
     *
     * @return
     */
    private String trim(String text)
    {
        if (text != null)
        {
            text = text.trim();

            if (text.length() == 0)
            {
                text = null;
            }
        }

        return text;
    }

    /**
     ********************************************************************************
     *
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            PMDClassLoader classLoader = new PMDClassLoader();
            String fileName = "c:\\cvs\\pmd\\classes\\net\\sourceforge\\pmd\\rules\\BracesRule.class";
            String className = "net.sourceforge.pmd.rules.BracesRule";
            Class theClass = classLoader.loadRuleClass(fileName, className);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}