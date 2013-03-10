/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.pmd.util.database.DBURI;

import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.SourceObject;

public class CPD {
        private final static String CLASS_NAME = CPD.class.getCanonicalName();

        private final static Logger LOGGER = Logger.getLogger(CPD.class.getPackage().getName()); 

	private CPDConfiguration configuration;

	private Map<String, SourceCode> source = new TreeMap<String, SourceCode>();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private MatchAlgorithm matchAlgorithm;

    public CPD(CPDConfiguration theConfiguration) {
    	configuration = theConfiguration;
        // before we start any tokenizing (add(File...)), we need to reset the static TokenEntry status
        TokenEntry.clearImages();
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        matchAlgorithm = new MatchAlgorithm(source, tokens,configuration.getMinimumTileSize(),listener);
        matchAlgorithm.findMatches();
    }

    public Iterator<Match> getMatches() {
        return matchAlgorithm.matches();
    }

    public void add(File file) throws IOException {
        add(1, file);
    }

    public void addAllInDirectory(String dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }

    public void add(List<File> files) throws IOException {
        for (File f: files) {
            add(files.size(), f);
        }
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        if (!(new File(dir)).exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, configuration.filenameFilter(), recurse));
    }

    private Set<String> current = new HashSet<String>();

    private void add(int fileCount, File file) throws IOException {

        if (configuration.isSkipDuplicates()) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.err.println("Skipping " + file.getAbsolutePath() + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!file.getCanonicalPath().equals(new File(file.getAbsolutePath()).getCanonicalPath())) {
            System.err.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        listener.addedFile(fileCount, file);
        SourceCode sourceCode = configuration.sourceCodeFor(file);
        configuration.tokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

    public void add(DBURI dburi) throws IOException {

      try 
      {
        DBMSMetadata dbmsmetadata = new DBMSMetadata(dburi) ; 

        List<SourceObject> sourceObjectList = dbmsmetadata.getSourceObjectList ();
        LOGGER.log(Level.FINER, "Located {0} source objects", sourceObjectList.size());

        for (SourceObject sourceObject: sourceObjectList )
        {

          // Add DBURI as a faux-file 
          String falseFilePath =  String.format("/Database/%s/%s/%s"
                                                        ,sourceObject.getSchema() 
                                                        ,sourceObject.getType() 
                                                        ,sourceObject.getName() 
                                                      ) ;
          LOGGER.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

          listener.addedFile(1, new File(falseFilePath));
          SourceCode sourceCode = configuration.sourceCodeFor( dbmsmetadata.getSourceCode(sourceObject) 
                                                               ,falseFilePath
                                                             );
          configuration.tokenizer().tokenize(sourceCode, tokens);
          source.put(sourceCode.getFileName(), sourceCode);
        }
      }
      catch (Exception sqlException)
      {
        throw new RuntimeException("Problem with DBURI: "+dburi , sqlException ) ; 
      }
    }

	public static void main(String[] args) {
		CPDCommandLineInterface.main(args);
	}
}
