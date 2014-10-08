/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import net.sourceforge.pmd.util.database.DBURI;

public class CPDCommandLineInterface {
    private final static Logger LOGGER = Logger.getLogger(CPDCommandLineInterface.class.getName());

	private static final int DUPLICATE_CODE_FOUND = 4;

	public static final String NO_EXIT_AFTER_RUN = "net.sourceforge.pmd.cli.noExit";
	public static final String STATUS_CODE_PROPERTY = "net.sourceforge.pmd.cli.status";

	private static final String progName = "cpd";

	public static void setStatusCodeOrExit(int status) {
		if (isExitAfterRunSet())
			System.exit(status);
		else
			setStatusCode(status);
	}

	private static boolean isExitAfterRunSet() {
	    String noExit = System.getenv(NO_EXIT_AFTER_RUN);
	    if (noExit == null) {
	        noExit = System.getProperty(NO_EXIT_AFTER_RUN);
	    }
		return (noExit == null ? true : false);
	}

	private static void setStatusCode(int statusCode) {
		System.setProperty(STATUS_CODE_PROPERTY, Integer.toString(statusCode));
	}

	public static void main(String[] args) {
		CPDConfiguration arguments = new CPDConfiguration();
		JCommander jcommander = new JCommander(arguments);
		jcommander.setProgramName(progName);

		try {
			jcommander.parse(args);
			if (arguments.isHelp()) {
				jcommander.usage();
				System.out.println(buildUsageText());
				setStatusCodeOrExit(1);
				return;
			}
		} catch (ParameterException e) {
			jcommander.usage();
			System.out.println(buildUsageText());
			System.out.println(e.getMessage());
			setStatusCodeOrExit(1);
			return;
		}
		arguments.postContruct();
		// Pass extra parameters as System properties to allow language
		// implementation to retrieve their associate values...
		CPDConfiguration.setSystemProperties(arguments);
		CPD cpd = new CPD(arguments);

                //Add files 
                if ( null != arguments.getFiles() && ! arguments.getFiles().isEmpty() )
                {
                  addSourcesFilesToCPD(arguments.getFiles(),cpd, !arguments.isNonRecursive());
                }

                //Add Database URIS
                if ( null != arguments.getURI() && ! "".equals(arguments.getURI()) )
                {
                  addSourceURIToCPD(arguments.getURI(),cpd);
                }

		cpd.go();
		if (cpd.getMatches().hasNext()) {
			System.out.println(arguments.getRenderer().render(cpd.getMatches()));
			setStatusCodeOrExit(DUPLICATE_CODE_FOUND);
		}
	}

	private static void addSourcesFilesToCPD(List<String> files, CPD cpd, boolean recursive) {
		try {
			for (String file : files)
				if (recursive)
					cpd.addRecursively(file);
				else
					cpd.addAllInDirectory(file);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void addSourceURIToCPD(String uri, CPD cpd) {
          try {
                        LOGGER.fine(String.format("Attempting DBURI=%s" , uri));
                            DBURI dburi = new DBURI(uri);
                            LOGGER.fine(String.format("Initialised DBURI=%s"
                                                 , dburi
                                                 )
                                      );
                            LOGGER.fine(String.format("Adding DBURI=%s with DBType=%s"
                                                 , dburi.toString() 
                                                 , dburi.getDbType().toString()
                                                 )
                                      );
                            cpd.add(dburi);
              } catch (IOException e) {
                      throw new IllegalStateException( "uri="+uri, e);
              } catch (URISyntaxException ex) {
                      throw new IllegalStateException( "uri="+uri, ex);
              } catch (Exception ex) {
                throw new IllegalStateException( "uri="+uri, ex);
              }
	}

	private static final char EOL = '\n';
	public static String buildUsageText() {
		String helpText = "Usage:";

		helpText += " java net.sourceforge.pmd.cpd.CPD --minimum-tokens xxx --files xxx [--language xxx] [--encoding xxx] [--format (xml|text|csv|vs)] [--skip-duplicate-files] [--non-recursive]" + EOL;
		helpText += "i.e: " + EOL;

		helpText += " java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files c:\\jdk14\\src\\java " + EOL;
		helpText += "or: " + EOL;

		helpText += " java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/c/code --language c " + EOL;
		helpText += "or: " + EOL;

		helpText += " java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --encoding UTF-16LE --files /path/to/java/code --format xml" + EOL;
		return helpText;
	}

}
