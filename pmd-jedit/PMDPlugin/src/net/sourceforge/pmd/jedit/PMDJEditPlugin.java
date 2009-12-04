/*
* User: tom
* Date: Jul 3, 2002
* Time: 2:33:24 PM
*/
package net.sourceforge.pmd.jedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.SourceFileSelector;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.util.designer.Designer;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

/** jEdit plugin for PMD
  @version $Id$
  
  TODO: move strings to property file so they can be localized.
**/
public class PMDJEditPlugin extends EBPlugin {

    public static final SourceType[] sourceTypes =
        new SourceType[] {SourceType.JAVA_14, SourceType.JAVA_15, SourceType.JAVA_16};
    public static final String NAME = "PMD";
    public static final String OPTION_RULES_PREFIX = "options.pmd.rules.";
    public static final String DEFAULT_TILE_MINSIZE_PROPERTY = "pmd.cpd.defMinTileSize";
    public static final String JAVA_VERSION_PROPERTY = "pmd.java.version";
    public static final String RUN_PMD_ON_SAVE = "pmd.runPMDOnSave";
    public static final String CUSTOM_RULES_PATH_KEY = "pmd.customRulesPath";
    public static final String SHOW_PROGRESS = "pmd.showprogress";
    public static final String IGNORE_LITERALS = "pmd.ignoreliterals";
    public static final String PRINT_RULE = "pmd.printRule";
    public static final String LAST_DIRECTORY = "pmd.cpd.lastDirectory";
    public static final String CHECK_DIR_RECURSIVE = "pmd.checkDirRecursive";

    private static PMDJEditPlugin instance;

    private DefaultErrorSource errorSource;
    public static final String RENDERER = "pmd.renderer";

    public void start() {
        instance = this;
        //Log.log(Log.DEBUG,this,"Instance created.");
        errorSource = new DefaultErrorSource( NAME );
    }

    public void stop() {
        instance = null;
        unRegisterErrorSource();
    }

    public static void checkDirectory( View view ) {
        instance.instanceCheckDirectory( view );
    }

    public void handleMessage( EBMessage ebmess ) {
        // maybe run PMD on buffer save
        if ( ebmess instanceof BufferUpdate && jEdit.getBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE ) ) {
            BufferUpdate bu = ( BufferUpdate ) ebmess;
            if ( bu.getWhat() == BufferUpdate.SAVED && "java".equals( bu.getBuffer().getMode().getName() ) ) {
                check( bu.getBuffer(), bu.getView() );
            }
        }
    }

    // check all open buffers
    public static void checkAllOpenBuffers( View view ) {
        instance.instanceCheckAllOpenBuffers( view );
    }

    public static void clearErrorList() {
        instance.instanceClearErrorList();
    }

    public void instanceClearErrorList() {
        errorSource.clear();
    }

    public void instanceCheckDirectory( View view ) {
        JFileChooser chooser = new JFileChooser( jEdit.getProperty( LAST_DIRECTORY ) );
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        JPanel pnlAccessory = new JPanel();
        JCheckBox chkRecursive = new JCheckBox( "Recursive", jEdit.getBooleanProperty( CHECK_DIR_RECURSIVE ) );
        pnlAccessory.add( chkRecursive );
        chooser.setAccessory( pnlAccessory );

        int returnVal = chooser.showOpenDialog( view );

        try {
            File selectedFile = null;

            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                selectedFile = chooser.getSelectedFile();

                if ( !selectedFile.isDirectory() ) {
                    JOptionPane.showMessageDialog( view, "Selection not a directory.", NAME, JOptionPane.ERROR_MESSAGE );
                    return ;
                }

                jEdit.setProperty( LAST_DIRECTORY, selectedFile.getCanonicalPath() );
                jEdit.setBooleanProperty( CHECK_DIR_RECURSIVE, chkRecursive.isSelected() );
                process( findFiles( selectedFile.getCanonicalPath(), chkRecursive.isSelected() ), view );
            }
            else {
                return ; // In case the user presses cancel or escape.
            }
        }
        catch ( IOException e ) {
            Log.log( Log.DEBUG, this, e );
        }
    }

    public void instanceCheckAllOpenBuffers( View view ) {
        // I'm putting the files in a Set to work around some
        // odd behavior in jEdit - the buffer.getNext()
        // seems to iterate over the files twice.
        Buffer buffers[] = jEdit.getBuffers();

        if ( buffers != null ) {
            ProgressBar pbd = null;

            if ( jEdit.getBooleanProperty( SHOW_PROGRESS ) ) {
                pbd = startProgressBarDisplay( view, 0, buffers.length );
            }

            for ( Buffer buffer : buffers ) {
                if ( buffer.getName().endsWith( ".java" ) ) {  // TODO: check mode rather than filename
                    Log.log( Log.DEBUG, this, "checking = " + buffer.getPath() );
                    instanceCheck( buffer, view, false );
                }

                if ( pbd != null ) {
                    pbd.increment( 1 );
                }
            }

            endProgressBarDisplay( pbd );
        }
    }

    public void instanceCheck( Buffer buffer, View view, boolean clearErrorList ) {
        try {
            unRegisterErrorSource();

            if ( clearErrorList ) {
                errorSource.clear();
            }

            PMD pmd = new PMD();
            int jvidx = jEdit.getIntegerProperty( JAVA_VERSION_PROPERTY, 1 );
            pmd.setJavaVersion( sourceTypes[ jvidx ] );
            SelectedRules selectedRuleSets = new SelectedRules();
            RuleContext ctx = new RuleContext();
            ctx.setReport( new Report() );
            String path = buffer.getPath();
            ctx.setSourceCodeFilename( path );
            VFS vfs = buffer.getVFS();

            pmd.processFile( vfs._createInputStream( vfs.createVFSSession( path, view ), path, false, view ),
                    System.getProperty( "file.encoding" ),
                    selectedRuleSets.getSelectedRules(),
                    ctx );

            if ( ctx.getReport().isEmpty() ) {
                // only show popup if run on save is NOT selected, otherwise it is annoying
                boolean run_on_save = jEdit.getBooleanProperty( PMDJEditPlugin.RUN_PMD_ON_SAVE );
                if ( !run_on_save ) {
                    JOptionPane.showMessageDialog( view, "No problems found", NAME, JOptionPane.INFORMATION_MESSAGE );
                }
                errorSource.clear();
            }
            else {
                String rulename = "";

                final boolean isPrintRule = jEdit.getBooleanProperty( PMDJEditPlugin.PRINT_RULE );

                for ( Iterator<IRuleViolation> i = ctx.getReport().iterator(); i.hasNext(); ) {
                    IRuleViolation rv = i.next();
                    if ( isPrintRule ) {
                        rulename = rv.getRule().getName() + "->";
                    }

                    errorSource.addError( new DefaultErrorSource.DefaultError( errorSource, ErrorSource.WARNING, path, rv.getBeginLine() - 1, 0, 0, rulename + rv.getDescription() ) ); //NOPMD
                }

                registerErrorSource();
            }
        }
        catch ( RuleSetNotFoundException rsne ) {
            Log.log( Log.ERROR, this, "RuleSet not found", rsne );
        }
        catch ( PMDException pmde ) {
            String msg = "Error while processing " + buffer.getPath() + ":\n" + pmde.getMessage() + "\n\n" + pmde.getCause();
            Log.log( Log.ERROR, this, msg, pmde );
            JOptionPane.showMessageDialog( view, msg, "PMD", JOptionPane.ERROR_MESSAGE );
        }
        catch ( Exception e ) {
            // TODO: is this useful to log?
            Log.log( Log.ERROR, this, "Exception processing file " + buffer.getPath(), e );
        }
    }

    public void process( final List<File> files, final View view ) {
        // TODO: use SwingWorker?
        new Thread(
            new Runnable () {
                public void run() {
                    processFiles( files, view );
                }
            }
        ).start();
    }

    // check current buffer
    public static void check( Buffer buffer, View view ) {
        instance.instanceCheck( buffer, view, true );
    }

    void processFiles( List<File> files, View view ) {
        unRegisterErrorSource();
        errorSource.clear();

        ProgressBar pbd = null;

        if ( jEdit.getBooleanProperty( SHOW_PROGRESS ) ) {
            pbd = startProgressBarDisplay( view, 0, files.size() );
        }

        PMD pmd = new PMD();
        int jvidx = jEdit.getIntegerProperty( JAVA_VERSION_PROPERTY, 1 );
        pmd.setJavaVersion( sourceTypes[ jvidx ] );
        SelectedRules selectedRuleSets = null;

        try {
            selectedRuleSets = new SelectedRules();
        }
        catch ( RuleSetNotFoundException rsne ) {
            // should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
            Log.log( Log.ERROR, this, "PMD ERROR: Couldn't find a ruleset", rsne );
            JOptionPane.showMessageDialog( jEdit.getFirstView(), "Unable to find rulesets, halting PMD", NAME, JOptionPane.ERROR_MESSAGE );
            return ;
        }

        RuleContext ctx = new RuleContext();

        List<Report> reports = new ArrayList<Report>();
        boolean foundProblems = false;

        for ( File file : files ) {
            ctx.setReport( new Report() ); 
            ctx.setSourceCodeFilename( file.getAbsolutePath() );

            try {
                pmd.processFile( new FileInputStream( file ), System.getProperty( "file.encoding" ), selectedRuleSets.getSelectedRules(), ctx ); //NOPMD
                for ( Iterator<IRuleViolation> j = ctx.getReport().iterator(); j.hasNext(); ) {
                    foundProblems = true;
                    IRuleViolation rv = j.next();
                    errorSource.addError( new DefaultErrorSource.DefaultError( errorSource, ErrorSource.ERROR, file.getAbsolutePath(), rv.getBeginLine() - 1, 0, 0, rv.getDescription() ) ); //NOPMD
                }
                if ( !ctx.getReport().isEmpty() ) {   // That means Report contains some violations, so only cache such reports.
                    reports.add( ctx.getReport() );
                }
            }
            catch ( FileNotFoundException fnfe ) {
                // should never happen, but if it does, carry on to the next file
                Log.log( Log.ERROR, this, "PMD ERROR: Unable to open file " + file.getAbsolutePath(), fnfe );
            }
            catch ( PMDException pmde ) {
                String msg = "Error while processing " + file.getAbsolutePath() + ":\n" + pmde.getMessage();
                Log.log( Log.ERROR, this, msg, pmde );
                JOptionPane.showMessageDialog( view, msg, "PMD", JOptionPane.ERROR_MESSAGE );
            }

            if ( jEdit.getBooleanProperty( SHOW_PROGRESS ) ) {
                pbd.increment( 1 );
            }
        }

        if ( !foundProblems ) {
            JOptionPane.showMessageDialog( jEdit.getFirstView(), "No problems found", NAME, JOptionPane.INFORMATION_MESSAGE );
            errorSource.clear();
        }
        else {
            registerErrorSource();
            exportErrorAsReport( view, reports.toArray( new Report[ reports.size() ] ) );
        }

        endProgressBarDisplay( pbd );
        pbd = null;
    }

    private List<File> findFiles( String dir, boolean recurse ) {
        FileFinder f = new FileFinder();
        return f.findFilesFrom( dir, new net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter( new SourceFileSelector() ), recurse );
    }

    private void registerErrorSource() {
        ErrorSource.registerErrorSource( errorSource );
    }

    private void unRegisterErrorSource() {
        ErrorSource.unregisterErrorSource( errorSource );
    }

    public static void cpdCurrentFile( View view ) throws IOException {
        String modeName = getFileType( view.getBuffer().getMode().getName() );
        if ( modeName == null ) {
            JOptionPane.showMessageDialog( view, "Copy/Paste detection can not be performed on this file\nbecause the mode can not be determined.", "Copy/Paste Detector", JOptionPane.INFORMATION_MESSAGE );
            return ;
        }
        instance.instanceCPDCurrentFile( view, view.getBuffer().getPath(), modeName );
    }

    public static void cpdCurrentFile( View view, VFSBrowser browser ) throws IOException {
        VFSFile selectedFile[] = browser.getSelectedFiles();

        if ( selectedFile == null || selectedFile.length == 0 ) {
            JOptionPane.showMessageDialog( view, "One file must be selected", NAME, JOptionPane.ERROR_MESSAGE );
            return ;
        }

        if ( selectedFile[ 0 ].getType() == VFSFile.DIRECTORY ) {
            JOptionPane.showMessageDialog( view, "Selected file cannot be a Directory.", NAME, JOptionPane.ERROR_MESSAGE );
            return ;
        }
        String path = selectedFile[ 0 ].getPath();
        instance.instanceCPDCurrentFile( view, path, getFileType( path ) );
    }

    // TODO: Replace this method with a smart file type/mode detector.
    private static String getFileType( String name ) {
        if ( name != null ) {
            for ( String lang : LanguageFactory.supportedLanguages ) {
                if ( name.endsWith( lang ) )
                    return lang;
            }
            if ( name.endsWith( "h" ) || name.endsWith( "cxx" ) || name.endsWith( "c++" ) ) {
                return "cpp";
            }
        }
        return null;
    }

    private void instanceCPDCurrentFile( View view, String filename, String fileType ) throws IOException {
        CPD cpd = getCPD( fileType );
        //Log.log(Log.DEBUG, PMDJEditPlugin.class , "See mode " + view.getBuffer().getMode().getName());

        if ( cpd != null ) {
            cpd.add( new File( filename ) );
            cpd.go();
            instance.processDuplicates( cpd, view );
            view.getDockableWindowManager().showDockableWindow( "cpd-viewer" );
        }
        else {
            view.getStatus().setMessageAndClear( "CPD does not yet support this file type: " + fileType );
            return ;
        }
    }


    public static void cpdDir( View view ) {
        JFileChooser chooser = new JFileChooser( jEdit.getProperty( LAST_DIRECTORY ) );
        chooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );

        // TODO: need to figure out how to create these filters dynamically
        // TODO: check on the AnyLanguage and AnyTokenizer, can they be used for file types that are not specifically supported? 
        chooser.addChoosableFileFilter( new CPDFileFilter( "ruby", "Ruby files", "rb", "rbw" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "php", "PHP files", "php", "php3", "php4", "phtml", "inc" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "jsp", "JSP files", "jsp", "jsf", "jspf" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "fortran", "Fortran files", "for", "fort", "f77", "f90" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "cpp", "C/C++ files", "c", "cc", "cpp" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "javascript", "Javascript files", "js" ) );
        chooser.addChoosableFileFilter( new CPDFileFilter( "java", "Java files", "java" ) );

        JPanel pnlAccessory = new JPanel( new BorderLayout() );

        JPanel pnlTile = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JLabel lblMinTileSize = new JLabel( "Minimum Tile size :" );
        JTextField txttilesize = new JTextField( 3 );
        txttilesize.setText( jEdit.getIntegerProperty( DEFAULT_TILE_MINSIZE_PROPERTY, 100 ) + "" );
        pnlTile.add( lblMinTileSize );
        pnlTile.add( txttilesize );
        pnlAccessory.add( BorderLayout.NORTH, pnlTile );

        JPanel pnlRecursive = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JCheckBox chkRecursive = new JCheckBox( "Recursive", jEdit.getBooleanProperty( CHECK_DIR_RECURSIVE ) );
        pnlRecursive.add( chkRecursive );
        pnlAccessory.add( BorderLayout.CENTER, pnlRecursive );

        chooser.setAccessory( pnlAccessory );

        int returnVal = chooser.showOpenDialog( view );
        File selectedFile = null;
        String mode = null;

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            selectedFile = chooser.getSelectedFile();
            mode = ( ( CPDFileFilter ) chooser.getFileFilter() ).getMode();

            if ( !selectedFile.isDirectory() ) {
                JOptionPane.showMessageDialog( view, "Selection not a directory.", NAME, JOptionPane.ERROR_MESSAGE );
                return ;
            }

            int tilesize = 100;
            try {
                tilesize = Integer.parseInt( txttilesize.getText() );
            }
            catch ( NumberFormatException e ) {
                //use the default.
                tilesize = jEdit.getIntegerProperty( DEFAULT_TILE_MINSIZE_PROPERTY, 100 );
            }


            try {
                jEdit.setBooleanProperty( CHECK_DIR_RECURSIVE, chkRecursive.isSelected() );
                instance.instanceCPDDir( view, selectedFile.getCanonicalPath(), tilesize, chkRecursive.isSelected(), mode );
            }
            catch ( IOException e ) {
                Log.log( Log.ERROR, instance, "PMD ERROR: Unable to open file " + selectedFile, e );
            }
        }
    }

    public static void cpdDir( View view, VFSBrowser browser, boolean recursive ) throws IOException {
        if ( view != null && browser != null ) {
            VFSFile selectedDir[] = browser.getSelectedFiles();
            if ( selectedDir == null || selectedDir.length == 0 ) {
                JOptionPane.showMessageDialog( view, "One Directory has to be selected in which to detect duplicate code.", NAME, JOptionPane.ERROR_MESSAGE );
                return ;
            }

            if ( selectedDir[ 0 ].getType() != VFSFile.DIRECTORY ) {
                JOptionPane.showMessageDialog( view, "Selected file must be a Directory.", NAME, JOptionPane.ERROR_MESSAGE );
                return ;
            }
            instance.instanceCPDDir( view, selectedDir[ 0 ].getPath(), jEdit.getIntegerProperty( DEFAULT_TILE_MINSIZE_PROPERTY, 100 ), recursive, "java" );
        }
    }

    private void instanceCPDDir( View view, String dir, int tileSize, boolean recursive, String mode ) throws IOException {
        if ( dir != null ) {
            jEdit.setProperty( LAST_DIRECTORY, dir );
            instance.errorSource.clear();
            CPD cpd = getCPD( tileSize, mode );

            if ( cpd != null ) {
                if ( recursive ) {
                    cpd.addRecursively( dir );
                }
                else {
                    cpd.addAllInDirectory( dir );
                }
                cpd.go();
                instance.processDuplicates( cpd, view );
            }
            else {
                view.getStatus().setMessageAndClear( "Cannot run CPD on Invalid directory/files." );
                return ;
            }
        } 
    }

    private void processDuplicates( CPD cpd, View view ) {
        CPDDuplicateCodeViewer dv = getCPDDuplicateCodeViewer( view );

        dv.clearDuplicates();
        boolean foundDuplicates = false;

        for ( Iterator<Match> i = cpd.getMatches(); i.hasNext(); ) {
            if ( !foundDuplicates ) {    // Set foundDuplicates to true and that too only once.
                foundDuplicates = true;
            }

            Match match = i.next();

            CPDDuplicateCodeViewer.Duplicates duplicates = dv.new Duplicates( match.getLineCount() + " duplicate lines", match.getSourceCodeSlice() ); //NOPMD

            for ( Iterator<TokenEntry> occurrences = match.iterator(); occurrences.hasNext(); ) {
                TokenEntry mark = occurrences.next();

                int lastLine = mark.getBeginLine() + match.getLineCount();
                Log.log( Log.DEBUG, this, "Begin line " + mark.getBeginLine() + " of file " + mark.getTokenSrcID() + " Line Count " + match.getLineCount() + " last line " + lastLine );

                CPDDuplicateCodeViewer.Duplicate duplicate = dv.new Duplicate( mark.getTokenSrcID(), mark.getBeginLine(), lastLine ); //NOPMD

                duplicates.addDuplicate( duplicate );
            } 

            dv.addDuplicates( duplicates );
        }

        if ( !foundDuplicates ) {
            dv.getRoot().add( new DefaultMutableTreeNode( "No duplicates found.", false ) );
        }

        dv.refreshTree();
        dv.expandAll();
    } 


    public CPDDuplicateCodeViewer getCPDDuplicateCodeViewer( View view ) {
        view.getDockableWindowManager().showDockableWindow( "cpd-viewer" );
        return ( CPDDuplicateCodeViewer ) view.getDockableWindowManager().getDockableWindow( "cpd-viewer" );

    }

    public static void checkFile( View view, VFSBrowser browser ) {
        instance.checkFile( view, browser.getSelectedFiles() );
    }

    public void checkFile( View view, VFSFile de[] ) {
        if ( view != null && de != null ) {
            List<File> files = new ArrayList<File>();

            for (VFSFile file : de) {
                if ( file.getType() == VFSFile.FILE ) {
                    files.add( new File( file.getPath() ) ); 
                }
            }

            process( files, view );
        }
    }

    public static void checkDirectory( View view, VFSBrowser browser, boolean recursive ) {
        VFSFile de[] = browser.getSelectedFiles();

        if ( de == null || de.length == 0 || de[ 0 ].getType() != VFSFile.DIRECTORY ) {
            JOptionPane.showMessageDialog( view, "Selection must be a directory", NAME, JOptionPane.ERROR_MESSAGE );
            return ;
        }

        instance.process( instance.findFiles( de[ 0 ].getPath(), recursive ), view );
    }

    public ProgressBar startProgressBarDisplay( View view, int min, int max ) {
        ProgressBar pbd = new ProgressBar( view, min, max );
        pbd.setVisible( true );
        return pbd;
    }

    public void endProgressBarDisplay( ProgressBar pbd ) {
        if ( pbd != null ) {
            pbd.completeBar();
            pbd.setVisible( false );
        }
    }

    public void exportErrorAsReport( final View view, Report reports[] ) {

        String format = jEdit.getProperty( PMDJEditPlugin.RENDERER );

        // "None", "Text", "Html", "XML", "CSV"
        if ( format != null && !format.equals( "None" ) ) {
            net.sourceforge.pmd.renderers.Renderer renderer = null;

            if ( "XML".equals( format ) ) {
                renderer = new XMLRenderer();
            }
            else if ( "Html".equals( format ) ) {
                renderer = new HTMLRenderer();
            }
            else if ( "CSV".equals( format ) ) {
                renderer = new CSVRenderer();
            }
            else if ( "Text".equals( format ) ) {
                renderer = new TextRenderer();
            }
            else {
                JOptionPane.showMessageDialog( view, "Invalid Renderer", NAME, JOptionPane.ERROR_MESSAGE );
                return ;
            }

            if ( reports != null ) {
                final StringBuffer strbuf = new StringBuffer();
                try {
                    renderer.start();
                    for ( int i = 0;i < reports.length;i++ )
                        renderer.renderFileReport( reports[ i ] );
                    renderer.end();
                }
                catch ( IOException ioe ) {
                    Log.log( Log.ERROR, this, "Renderer can't report.", ioe );
                }
                view.setBuffer( jEdit.newFile( view ) );
                VFSManager.runInAWTThread(
                    new Runnable() {
                        public void run() {
                            view.getTextArea().setText( strbuf.toString() );
                        }
                    }
                );
            }
        }
    }


    class ProgressBar extends JPanel {
        private JProgressBar pBar;
        private View view;

        public ProgressBar( View view, int min, int max ) {
            super();
            this.view = view;
            setLayout( new BorderLayout() );
            pBar = new JProgressBar( min, max );
            pBar.setUI( new BasicProgressBarUI() {
                        public Color getSelectionBackground() {
                            return jEdit.getColorProperty( "pmd.progressbar.foreground" );
                        }

                        public Color getSelectionForeground() {
                            return jEdit.getColorProperty( "pmd.progressbar.foreground" );
                        }
                    }

                      );
            pBar.setBorder( new EtchedBorder( EtchedBorder.RAISED ) );
            pBar.setToolTipText( "PMD Check in Progress" );
            pBar.setForeground( jEdit.getColorProperty( "pmd.progressbar.background" ) );
            pBar.setBackground( jEdit.getColorProperty( "view.status.background" ) ); 

            pBar.setStringPainted( true );
            add( pBar, BorderLayout.CENTER );
            view.getStatus().add( pBar, BorderLayout.EAST );
        }

        public void increment( int num ) {
            pBar.setValue( pBar.getValue() + num );
        }

        public void completeBar() {
            pBar.setValue( pBar.getMaximum() );
            view.getStatus().remove( pBar );
            view = null;
            pBar = null;
        }
    }


    private CPD getCPD( int tileSize, String fileType ) {
        Language lang;
        LanguageFactory lf = new LanguageFactory();
        
        if ( "java".equals( fileType ) ) {
            Properties props = new Properties();
            props.setProperty( JavaTokenizer.IGNORE_LITERALS, String.valueOf( jEdit.getBooleanProperty( PMDJEditPlugin.IGNORE_LITERALS ) ) );
            lang = lf.createLanguage( "java", props );
        }
        else {
            lang = lf.createLanguage( fileType );
        }

        return lang == null ? null : new CPD( tileSize, lang );
    }

    private CPD getCPD( String fileType ) {
        return getCPD( jEdit.getIntegerProperty( PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY, 100 ), fileType );
    }

    /**
     * Run the PMD rule designer.
     */
    public static void runDesigner() {
        String[] args = new String[] {"-noexitonclose"};
        new Designer( args );
    }
}