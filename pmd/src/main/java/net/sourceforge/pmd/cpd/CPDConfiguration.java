package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

import net.sourceforge.pmd.AbstractConfiguration;

/**
 * 
 * @author Brian Remedios
 */
public class CPDConfiguration extends AbstractConfiguration {

    private Language language;
    private int minimumTileSize;
    private boolean skipDuplicates;
    private Renderer renderer;
    
	private static final int MISSING_REQUIRED_ARGUMENT = 3;
	
    public CPDConfiguration(String[] args) {

    	 String languageString = findOptionalStringValue(args, "--language", "java");
    	 language = new LanguageFactory().createLanguage(languageString);
    	 
         String formatString = findOptionalStringValue(args, "--format", "text");
         renderer = getRendererFromString(formatString);
         
         final String systemDefaultEncoding = System.getProperty("file.encoding");
         setEncoding( findOptionalStringValue(args, "--encoding", systemDefaultEncoding) );

         minimumTileSize = Integer.parseInt(findRequiredStringValue(args, "--minimum-tokens"));
         
         skipDuplicates = findBooleanSwitch(args, "--skip-duplicate-files");
    }
    
	public CPDConfiguration(int theMinTileSize, Language theLanguage, String theEncoding) {
		minimumTileSize = theMinTileSize;
		language = theLanguage;
		setEncoding(theEncoding);
	}
    
    public void setEncoding(String theEncoding) {
        super.setSourceEncoding(theEncoding);
        
        if ( ! theEncoding.equals( System.getProperty("file.encoding") ) )
        	 System.setProperty("file.encoding", theEncoding);
    }
    
    public SourceCode sourceCodeFor(File file) {
    	return new SourceCode(
        	new SourceCode.FileCodeLoader(file, getSourceEncoding())
			);
    }
    
    public Language language() { return language; }
    
    public int minimumTileSize() { return minimumTileSize; }
    
    public boolean skipDuplicates() { return skipDuplicates; }

    public void skipDuplicates(boolean flag) { skipDuplicates = flag; }
    
    public FilenameFilter filenameFilter() { return language.getFileFilter(); }
    
    public Tokenizer tokenizer() { return language.getTokenizer(); }
    
    public Renderer renderer() { return renderer; }

    public static Renderer getRendererFromString(String name /*, String encoding*/) {
        if (name.equalsIgnoreCase("text") || name.equals("")) {
            return new SimpleRenderer();
        } else if ("xml".equals(name)) {
            return new XMLRenderer();
        }  else if ("csv".equals(name)) {
            return new CSVRenderer();
        }  else if ("vs".equals(name)) {
            return new VSRenderer();
        }
        try {
            return (Renderer) Class.forName(name).newInstance();
        } catch (Exception e) {
            System.out.println("Can't find class '" + name + "', defaulting to SimpleRenderer.");
        }
        return new SimpleRenderer();
    }

    public static boolean findBooleanSwitch(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String findOptionalStringValue(String[] args, String name, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    private static String findRequiredStringValue(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        System.out.println("No " + name + " value passed in");
        CPD.showUsage();
        System.exit(MISSING_REQUIRED_ARGUMENT);
        return "";
    }
}
