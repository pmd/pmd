/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * <p>A console renderer with optional color support under *nix systems.</p>
 *
 * <pre>
 * * file: ./src/gilot/Test.java
 *     src:  Test.java:12
 *     rule: AtLeastOneConstructor
 *     msg:  Each class should declare at least one constructor
 *     code: public class Test
 *
 * * file: ./src/gilot/log/format/LogInterpreter.java
 *     src:  LogInterpreter.java:317
 *     rule: AvoidDuplicateLiterals
 *     msg:  The same String literal appears 4 times in this file; the first occurrence is on line 317
 *     code: logger.error( "missing attribute 'app_arg' in rule '" + ((Element)element.getParent()).getAttributeValue( "name" ) + "'" );
 *
 *     src:  LogInterpreter.java:317
 *     rule: AvoidDuplicateLiterals
 *     msg:  The same String literal appears 5 times in this file; the first occurrence is on line 317
 *     code: logger.error( "missing attribute 'app_arg' in rule '" + ((Element)element.getParent()).getAttributeValue( "name" ) + "'" );
 *
 * * warnings: 3
 *
 * </pre>
 *
 * <p>Colorization is turned on by supplying -D<b>pmd.color</b> - any value other than
 *    '0' or 'false', enables color - including an empty value (''). <b>Nota Bene:</b>
 *    colorization is atm only supported under *nix terminals accepting ansi escape
 *    sequences, such as xterm, rxvt et cetera.</p>
 */
public class PapariTextRenderer implements Renderer
{
    /**
     * Directory from where java was invoked.
     */
    private String pwd = null;

    private String yellowBold = "";
    private String whiteBold = "";
    private String redBold = "";
    private String cyan = "";
    private String green = "";

    private String colorReset = "";

    /**
     * Enables colors on *nix systems - not windows. Color support depends
     * on the pmd.color property, which should be set with the -D option
     * during execution - a set value other than 'false' or '0' enables color.
     *
     * btw, is it possible to do this on windows (ie; console colors)?
     */
    private void initColors()
    {
        if (System.getProperty("pmd.color") != null &&
                !(System.getProperty("pmd.color").equals("0") || System.getProperty("pmd.color").equals("false")))
        {
            this.yellowBold = "\u001B[1;33m";
            this.whiteBold = "\u001B[1;37m";
            this.redBold = "\u001B[1;31m";
            this.green = "\u001B[0;32m";
            this.cyan = "\u001B[0;36m";

            this.colorReset = "\u001B[0m";
        }
    }

    public String render(Report report)
    {
        StringBuffer buf = new StringBuffer(PMD.EOL);

        // init colors, if supported
        this.initColors();

        // last file
        String fileName = null;

        // keeps track of violations and errors
        int errors = 0;
        int warnings = 0;

        // iterating rule violations
        for (Iterator i = report.iterator(); i.hasNext();)
        {
            warnings++;

            RuleViolation rv = (RuleViolation) i.next();
            if (!rv.getFilename().equals(fileName))
            {
                fileName = rv.getFilename();
                buf.append( this.yellowBold  + "*" + this.colorReset  + " file: " + this.whiteBold + this.getRelativePath(fileName) + this.colorReset + PMD.EOL);
            }

            buf.append(this.green + "    src:  " + this.cyan + fileName.substring( fileName.lastIndexOf(File.separator)+1)+ this.colorReset + ":" + this.cyan + rv.getLine() + this.colorReset + PMD.EOL);
            buf.append(this.green + "    rule: " + this.colorReset + rv.getRule().getName() + PMD.EOL);
            buf.append(this.green + "    msg:  " + this.colorReset + rv.getDescription() + PMD.EOL);
            buf.append(this.green + "    code: " + this.colorReset + this.getLine( fileName, rv.getLine() ) + PMD.EOL + PMD.EOL);

        }

        // iterating errors
        for (Iterator i = report.errors(); i.hasNext();)
        {
            errors++;

            Report.ProcessingError error = (Report.ProcessingError)i.next();
            if (error.getFile().equals(fileName))
            {
                fileName = error.getFile();
                buf.append( this.redBold  + "*" + this.colorReset  + " file: " + this.whiteBold + this.getRelativePath(fileName) + this.colorReset + PMD.EOL);
            }
            buf.append(this.green + "    err:  " + this.cyan + error.getMsg() + this.colorReset + PMD.EOL + PMD.EOL);
        }

        // adding error message count, if any
        if ( errors > 0 )
        {
            buf.append(this.redBold  + "*" + this.colorReset + " errors:   "+ this.whiteBold + warnings + this.colorReset + PMD.EOL);
        }
        buf.append(this.yellowBold  + "*" + this.colorReset + " warnings: "+ this.whiteBold + warnings + this.colorReset + PMD.EOL);

        return buf.toString();
    }

    /**
     * Retrieves the requested line from the specified file.
     *
     * @param sourceFile the java or cpp source file
     * @param line line number to extract
     *
     * @return a trimmed line of source code
     */
    private String getLine( String sourceFile, int line )
    {
        String code = null;
        try
        {
            File file = new File( "." );
            BufferedReader br = new BufferedReader( new FileReader( new File( sourceFile ) ) );

            for ( int i = 0; line > i; i++ )
            {
                code = br.readLine().trim();
            }
            br.close();
        }
        catch ( IOException ioErr )
        {
            ioErr.printStackTrace();
        }
        return code;
    }

    /**
     * Attempts to determine the relative path to the file. If relative path cannot be found,
     * the original path is returnedi, ie - the current path for the supplied file.
     *
     * @param fileName well, the file with its original path.
     * @return the relative path to the file
     */
    private String getRelativePath( String fileName )
    {
        String relativePath = null;

        // check if working directory need to be assigned
        if (pwd == null)
        {
            try
            {
                this.pwd = new File(".").getCanonicalPath();
            }
            catch (IOException ioErr)
            {
                // to avoid further error
                this.pwd = "";
            }
        }

        // make sure that strings match before doing any substring-ing
        if (fileName.indexOf(this.pwd) == 0)
        {
            relativePath = "." + fileName.substring( this.pwd.length() );

            // remove current dir occuring twice - occurs if . was supplied as path
            if ( relativePath.startsWith( "." + File.separator + "." + File.separator ) )
            {
                relativePath = relativePath.substring(2);
            }
        }
		else
		{
			// this happens when pmd's supplied argument deviates from the pwd 'branch' (god knows this terminolgy - i hope i make some sense).
			// for instance, if supplied=/usr/lots/of/src and pwd=/usr/lots/of/shared/source
			// TODO: a fix to get relative path?
			relativePath = fileName;
		}

        return relativePath;
    }
}
