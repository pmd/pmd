/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;

import org.apache.commons.io.FilenameUtils;

public class CPD {
    private final static Logger LOGGER = Logger.getLogger(CPD.class.getName()); 

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

    public void addAllInDirectory(String dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }

    public void add(List<File> files) throws IOException {
        for (File f: files) {
            add(f);
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

    public void add(File file) throws IOException {

        if (configuration.isSkipDuplicates()) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.err.println("Skipping " + file.getAbsolutePath() + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!FilenameUtils.equalsNormalizedOnSystem(file.getAbsoluteFile().getCanonicalPath(), file.getAbsolutePath())) {
            System.err.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        if (!file.exists()) {
            System.err.println("Skipping " + file + " since it doesn't exist (broken symlink?)");
            return;
        }

        SourceCode sourceCode = configuration.sourceCodeFor(file);
        add(sourceCode);
    }

    public void add(DBURI dburi) throws IOException {

      try 
      {
        DBMSMetadata dbmsmetadata = new DBMSMetadata(dburi) ; 

        List<SourceObject> sourceObjectList = dbmsmetadata.getSourceObjectList ();
        LOGGER.log(Level.FINER, "Located {0} database source objects", sourceObjectList.size());

        for (SourceObject sourceObject: sourceObjectList )
        {
          // Add DBURI as a faux-file 
          String falseFilePath =  sourceObject.getPseudoFileName();
          LOGGER.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

          SourceCode sourceCode = configuration.sourceCodeFor( dbmsmetadata.getSourceCode(sourceObject) 
                                                               ,falseFilePath
                                                             );
          add(sourceCode);
        }
      }
      catch (Exception sqlException)
      {
        LOGGER.log(Level.SEVERE, "Problem with Input URI", sqlException);
        throw new RuntimeException("Problem with DBURI: "+dburi , sqlException ) ; 
      }
    }

    private void add(SourceCode sourceCode) throws IOException {
        if (configuration.isSkipLexicalErrors()) {
            addAndSkipLexicalErrors(sourceCode);
        } else {
            addAndThrowLexicalError(sourceCode);
        }
    }

    private void addAndThrowLexicalError(SourceCode sourceCode) throws IOException {
        configuration.tokenizer().tokenize(sourceCode, tokens);
        listener.addedFile(1,  new File(sourceCode.getFileName()));
        source.put(sourceCode.getFileName(), sourceCode);
    }

    private void addAndSkipLexicalErrors(SourceCode sourceCode) throws IOException {
        TokenEntry.State savedTokenEntry = new TokenEntry.State(tokens.getTokens());
        try {
            addAndThrowLexicalError(sourceCode);
        } catch (TokenMgrError e) {
            System.err.println("Skipping " + e.getMessage());
            tokens.getTokens().clear();
            tokens.getTokens().addAll(savedTokenEntry.restore());
        }
    }

    /**
     * List names/paths of each source to be processed.
     * 
     * @return names of sources to be processed 
     */
    public List<String> getSourcePaths() {
        return new ArrayList<String>(source.keySet());  
    }

    /**
     * Get each Source to be processed.
     * 
     * @return all Sources to be processed 
     */
    public List<SourceCode> getSources() {
        return new ArrayList<SourceCode>(source.values());  
    }
    
    
	public static void main(String[] args) {
		CPDCommandLineInterface.main(args);
	}
}
