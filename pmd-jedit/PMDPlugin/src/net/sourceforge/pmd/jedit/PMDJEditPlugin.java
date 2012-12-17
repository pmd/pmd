/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
*/
package net.sourceforge.pmd.jedit;

import java.awt.BorderLayout;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceFileSelector;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.cpd.AnyLanguage;
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
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import ise.java.awt.KappaLayout;

/** jEdit plugin for PMD
 * @version $Id$
 *
 * DONE: move strings to property file so they can be localized.
 * TODO: this really needs rewritten to move all the "instance" stuff to a new
 * class.
 **/
public class PMDJEditPlugin extends EBPlugin {

    public static final SourceType[] sourceTypes = new SourceType [] {SourceType.JAVA_14, SourceType.JAVA_15, SourceType.JAVA_16} ;
    public static final String NAME = "PMD";
    public static final String CHECK_DIR_RECURSIVE = "pmd.checkDirRecursive";
    public static final String CUSTOM_RULES_ONLY_KEY = "pmd.customRulesOnly";
    public static final String CUSTOM_RULES_PATH_KEY = "pmd.customRulesPath";
    public static final String DEFAULT_TILE_MINSIZE_PROPERTY = "pmd.cpd.defMinTileSize";
    public static final String EXCLUDE_CUSTOM_RULES_KEY = "pmd.excludeCustomRules";
    public static final String IGNORE_LITERALS = "pmd.ignoreliterals";
    public static final String JAVA_VERSION_PROPERTY = "pmd.java.version";
    public static final String LAST_DIRECTORY = "pmd.cpd.lastDirectory";
    public static final String LAST_EXCLUSION_REGEX = "pmd.cpd.lastExclusionRegex";
    public static final String LAST_INCLUSION_REGEX = "pmd.cpd.lastInclusionRegex";
    public static final String LAST_SELECTED_FILTER = "pmd.cpd.lastSelectedFilter";
    public static final String OPTION_RULES_PREFIX = "options.pmd.rules.";
    public static final String PRINT_RULE = "pmd.printRule";
    public static final String RUN_PMD_ON_SAVE = "pmd.runPMDOnSave";
    public static final String SHOW_PROGRESS = "pmd.showprogress";

    private static PMDJEditPlugin instance;
    private static ProgressBar progressBar;

    private Map<View, DefaultErrorSource> errorSources = new HashMap<View, DefaultErrorSource>();
    public static final String RENDERER = "pmd.renderer";

    private static int lastSelectedFilter = 0;
    private static String lastInclusion = "";
    private static String lastExclusion = "";

    public void start() {
        instance = this;
        // Log.log(Log.DEBUG,this,"Instance created.");
        lastSelectedFilter = jEdit.getIntegerProperty(LAST_SELECTED_FILTER, 0);
        lastInclusion = jEdit.getProperty(LAST_INCLUSION_REGEX, "");
        lastExclusion = jEdit.getProperty(LAST_EXCLUSION_REGEX, "");
    }

    public void stop() {
        instance = null;
        unRegisterErrorSources();
    }

    public static void checkDirectory(View view) {
        instance.instanceCheckDirectory(view);
    }

    public void handleMessage(EBMessage ebmess) {
        // maybe run PMD on buffer save
        if (ebmess instanceof BufferUpdate && jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE)) {
            try {
                BufferUpdate bu = (BufferUpdate) ebmess;
                String modename = bu.getBuffer().getMode().getName();
                if (bu.getWhat() == BufferUpdate.SAVED && ("java".equals(modename) || "jsp".equals(modename))) {
                    check(bu.getBuffer(), bu.getView());
                }
            } catch (Exception e) {                // NOPMD
            }
        }
    }

    // check all open buffers
    public static void checkAllOpenBuffers(View view) {
        instance.instanceCheckAllOpenBuffers(view);
    }

    public void instanceCheckDirectory(View view) {
        String[] paths = GUIUtilities.showVFSFileDialog(view, jEdit.getProperty(LAST_DIRECTORY), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        try {
            File selectedFile = null;

            if (paths != null && paths.length == 1) {
                boolean recursive = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(view, "Recursively check subdirectories?", "Recursive", JOptionPane.YES_NO_OPTION);
                selectedFile = new File(paths[0]);
                

                if (! selectedFile.isDirectory()) {
                    DefaultErrorSource errorSource = getErrorSource(view);
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, selectedFile.getAbsolutePath(), 0, 0, 0, jEdit.getProperty("net.sf.pmd.Selection_not_a_directory.", "Selection not a directory.")));                    // NOPMD
                    return ;
                }

                jEdit.setProperty(LAST_DIRECTORY, selectedFile.getCanonicalPath());
                jEdit.setBooleanProperty(CHECK_DIR_RECURSIVE, recursive);
                process(findFiles(selectedFile.getCanonicalPath(), recursive), view);
            }
        } catch (IOException e) {
            Log.log(Log.DEBUG, this, e);
        }
    }

    public void instanceCheckAllOpenBuffers(View view) {
        // I'm putting the files in a Set to work around some
        // odd behavior in jEdit - the buffer.getNext()
        // seems to iterate over the files twice.
        Buffer buffers[] = jEdit.getBuffers();

        if (buffers != null) {

            if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
                startProgressBarDisplay(view, 0, buffers.length);
            }

            for (Buffer buffer : buffers) {
                String modename = buffer.getMode().getName();
                if ("java".equals(modename) || "jsp".equals(modename)) {
                    Log.log(Log.DEBUG, this, "checking = " + buffer.getPath());
                    instanceCheck(buffer, view, false);
                }

                if (instance.progressBar != null) {
                    instance.progressBar.increment(1);
                }
            }

            endProgressBarDisplay();
        }
    }

    public void instanceCheck(Buffer buffer, View view, boolean clearErrorList) {
        DefaultErrorSource errorSource = getErrorSource(view);
        try {
            if (clearErrorList) {
                errorSource.clear();
            }

            String modename = buffer.getMode().getName();
            boolean isJsp = "jsp".equals(modename);

            PMD pmd = new PMD();
            int jvidx = jEdit.getIntegerProperty(JAVA_VERSION_PROPERTY, 1);
            pmd.setJavaVersion(sourceTypes[jvidx]);
            SelectedRules selectedRuleSets = new SelectedRules();
            RuleSets toCheck = isJsp ? selectedRuleSets.getJspSelectedRules() : selectedRuleSets.getSelectedRules();
            RuleContext ctx = new RuleContext();
            ctx.setReport(new Report());
            String path = buffer.getPath();
            ctx.setSourceCodeFilename(path);
            if (isJsp) {
                ctx.setSourceType(SourceType.JSP);
            }
            VFS vfs = buffer.getVFS();

            if (isJsp) {
                Reader reader = new StringReader(buffer.getText());
                pmd.processFile(reader, toCheck, ctx, SourceType.JSP);
            } else {
                pmd.processFile(vfs._createInputStream(vfs.createVFSSession(path, view), path, false, view), (String) buffer.getProperty("encoding"), toCheck, ctx);
            }

            if (ctx.getReport().isEmpty()) {
                // only show popup if run on save is NOT selected, otherwise it is annoying
                // even better, just show the message in the error list
                boolean run_on_save = jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE);
                if (! run_on_save) {
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, 0, 0, 0, jEdit.getProperty("net.sf.pmd.No_problems_found", "No problems found")));                    // NOPMD
                    // JOptionPane.showMessageDialog(view,  jEdit.getProperty("net.sf.pmd.No_problems_found",  "No problems found"),  NAME,  JOptionPane.INFORMATION_MESSAGE);
                }
                errorSource.clear();
            } else {
                String rulename = "";

                final boolean isPrintRule = jEdit.getBooleanProperty(PMDJEditPlugin.PRINT_RULE);

                for (Iterator<IRuleViolation> i = ctx.getReport().iterator(); i.hasNext();) {
                    IRuleViolation rv = i.next();
                    if (isPrintRule) {
                        rulename = rv.getRule().getName() + "->";
                    }

                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getBeginLine() - 1, 0, 0, rulename + rv.getDescription()));                    // NOPMD
                }
            }
        } catch (PMDException pmde) {
            String msg = jEdit.getProperty("net.sf.pmd.Error_while_processing_", "Error while processing ") + buffer.getPath() + ":\n" + pmde.getMessage();
            Throwable cause = pmde.getCause();
            String lexicalError = null;
            if (cause instanceof net.sourceforge.pmd.ast.TokenMgrError || cause instanceof net.sourceforge.pmd.jsp.ast.TokenMgrError) {
                lexicalError = cause.getMessage();
            }
            if (lexicalError != null) {
                int[] location = getLocation(lexicalError);
                errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, buffer.getPath(), location[0], location[1], location[1] + 1, lexicalError));
            } else {
                errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, buffer.getPath(), 0, 0, 0, msg));
            }
        } catch (Exception e) {
            String msg = jEdit.getProperty("net.sf.pmd.Error_while_processing_", "Error while processing ") + buffer.getPath() + ":\n" + e.getMessage() + "\n\n" + e.getCause();
            Log.log(Log.ERROR, this, "Exception processing file " + buffer.getPath(), e);
            errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, buffer.getPath(), 0, 0, 0, msg));
        }
    }

    public void process(final List<File> files, final View view) {
        // TODO: use SwingWorker?
        new Thread(new Runnable() {
            public void run() {
                processFiles(files, view);
            }
        }
       ).start();
    }

    // check current buffer
    public static void check(Buffer buffer, View view) {
        instance.instanceCheck(buffer, view, true);
    }

    void processFiles(List<File> files, View view) {
        DefaultErrorSource errorSource = getErrorSource(view);
        errorSource.clear();

        if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
            startProgressBarDisplay(view, 0, files.size());
        }

        PMD pmd = new PMD();
        int jvidx = jEdit.getIntegerProperty(JAVA_VERSION_PROPERTY, 1);
        pmd.setJavaVersion(sourceTypes[jvidx]);
        SelectedRules selectedRuleSets = null;

        try {
            selectedRuleSets = new SelectedRules();
        } catch (RuleSetNotFoundException rsne) {
            // should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
            Log.log(Log.ERROR, this, "PMD ERROR: Couldn't find a ruleset", rsne);
            JOptionPane.showMessageDialog(jEdit.getFirstView(), jEdit.getProperty("net.sf.pmd.Unable_to_find_rulesets,_halting_PMD", "Unable to find rulesets, halting PMD"), NAME, JOptionPane.ERROR_MESSAGE);
            return ;
        }

        RuleContext ctx = new RuleContext();

        List<Report> reports = new ArrayList <Report>();
        boolean foundProblems = false;

        for (File file : files) {
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(file.getAbsolutePath());

            try {
                RuleSets rules = null;
                Mode mode = ModeProvider.instance.getModeForFile(file.getName(), "");
                if ("java".equals(mode.getName())) {
                    rules = selectedRuleSets.getSelectedRules();
                } else if ("jsp".equals(mode.getName())) {
                    rules = selectedRuleSets.getJspSelectedRules();
                } else {
                    throw new PMDException("No rules for file " + file.getAbsolutePath());
                }

                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));                // PMD will close this
                pmd.processFile(stream, System.getProperty("file.encoding"), rules, ctx);
                for (Iterator<IRuleViolation> j = ctx.getReport().iterator(); j.hasNext();) {
                    foundProblems = true;
                    IRuleViolation rv = j.next();
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, file.getAbsolutePath(), rv.getBeginLine() - 1, 0, 0, rv.getDescription()));                    // NOPMD
                }
                if (! ctx.getReport().isEmpty()) {                    // That means Report contains some violations, so only cache such reports.
                    reports.add(ctx.getReport());
                }
            } catch (FileNotFoundException fnfe) {
                // should never happen, but if it does, carry on to the next file
                Log.log(Log.ERROR, this, "PMD ERROR: Unable to open file " + file.getAbsolutePath(), fnfe);
            } catch (PMDException pmde) {
                String msg = jEdit.getProperty("net.sf.pmd.Error_while_processing_", "Error while processing ") + file.getAbsolutePath() + ":\n" + pmde.getMessage();
                Throwable cause = pmde.getCause();
                String lexicalError = null;
                if (cause instanceof net.sourceforge.pmd.ast.TokenMgrError || cause instanceof net.sourceforge.pmd.jsp.ast.TokenMgrError) {
                    lexicalError = cause.getMessage();
                }
                if (lexicalError != null) {
                    int[] location = getLocation(lexicalError);
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, file.getAbsolutePath(), location[0], location[1], location[1] + 1, lexicalError));
                } else {
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, file.getAbsolutePath(), 0, 0, 0, msg));
                }
            }

            if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
                instance.progressBar.increment(1);
            }
        }

        if (! foundProblems) {
            errorSource.clear();
        } 

        endProgressBarDisplay();
    }

    // parses the output from a TokenMgrError to get the line and column of the error
    private int[] getLocation(String lexicalError) {
        int[] location = new int[2];
        try {
            Pattern p = Pattern.compile("(.*?)(\\d+)(.*?)(\\d+)(.*?)");
            Matcher m = p.matcher(lexicalError);
            if (m.matches()) {
                String ln = m.group(2);
                String cn = m.group(4);
                int line_number = -1;
                int column_number = 0;
                if (ln != null) {
                    line_number = Integer.parseInt(ln);
                }
                if (cn != null) {
                    column_number = Integer.parseInt(cn);
                }
                location[0] = Math.max(0, line_number - 1);
                location[1] = Math.max(0, column_number - 1);
            }
        } catch (Exception e) {            // NOPMD
            e.printStackTrace();
        }
        return location;
    }

    private List<File> findFiles(String dir, boolean recurse) {
        FileFinder f = new FileFinder();
        SourceFileSelector sfSelector = new SourceFileSelector();
        sfSelector.setSelectJspFiles(true);
        return f.findFilesFrom(dir, new net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter(sfSelector), recurse);
    }

    private void unRegisterErrorSources() {
        for (DefaultErrorSource errorSource : errorSources.values()) {
            ErrorSource.unregisterErrorSource(errorSource);
        }
    }
    
    private DefaultErrorSource getErrorSource(View view) {
        DefaultErrorSource errorSource = errorSources.get(view);
        if (errorSource == null) {
            errorSource = new DefaultErrorSource(NAME, view);
            errorSources.put(view, errorSource);
            ErrorSource.registerErrorSource(errorSource);
        }
        return errorSource;
    }

    public static void cpdCurrentFile(View view) throws IOException {
        String modeName = getFileType(view.getBuffer().getMode().getName());
        if (modeName == null) {
            JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Copy/Paste_detection_can_not_be_performed_on_this_file\nbecause_the_mode_can_not_be_determined.", "Copy/Paste detection can not be performed on this file\nbecause the mode can not be determined."), jEdit.getProperty("net.sf.pmd.Copy/Paste_Detector", "Copy/Paste Detector"), JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        instance.instanceCPDCurrentFile(view, view.getBuffer().getPath(), modeName);
    }

    public static void cpdCurrentFile(View view, VFSBrowser browser) throws IOException {
        VFSFile selectedFile[] = browser.getSelectedFiles();

        if (selectedFile == null || selectedFile.length == 0) {
            JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.One_file_must_be_selected", "One file must be selected"), NAME, JOptionPane.ERROR_MESSAGE);
            return ;
        }

        if (selectedFile[0].getType() == VFSFile.DIRECTORY) {
            JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Selected_file_cannot_be_a_Directory.", "Selected file cannot be a Directory."), NAME, JOptionPane.ERROR_MESSAGE);
            return ;
        }
        String path = selectedFile[0].getPath();
        instance.instanceCPDCurrentFile(view, path, getFileType(path));
    }

    // TODO: Replace this method with a smart file type/mode detector.
    private static String getFileType(String name) {
        if (name != null) {
            for (String lang : LanguageFactory.supportedLanguages) {
                if (name.endsWith(lang)) {
                    return lang;
                }

            }
            if (name.endsWith("h") || name.endsWith("cxx") || name.endsWith("c++")) {
                return "cpp";
            }
        }
        return name;
    }

    private void instanceCPDCurrentFile(View view, String filename, String fileType) throws IOException {
        CPD cpd = getCPD(fileType);
        // Log.log(Log.DEBUG, PMDJEditPlugin.class , "See mode " + view.getBuffer().getMode().getName());

        if (cpd != null) {
            cpd.add(new File(filename));
            cpd.go();
            instance.processDuplicates(cpd, view);
            view.getDockableWindowManager().showDockableWindow("cpd-viewer");
        } else {
            view.getStatus().setMessageAndClear(jEdit.getProperty("net.sf.pmd.CPD_does_not_yet_support_this_file_type>_", "CPD does not yet support this file type: ") + fileType);
            return ;
        }
    }

    public static void cpdDir(View view) {
        JFileChooser chooser = new JFileChooser(jEdit.getProperty(LAST_DIRECTORY));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JPanel pnlAccessory = new JPanel();
        pnlAccessory.setLayout(new KappaLayout());

        JLabel lblMinTileSize = new JLabel(jEdit.getProperty("net.sf.pmd.Minimum_Tile_size_>", "Minimum Tile size :"));
        JTextField txttilesize = new JTextField(3);
        txttilesize.setText(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY, 100) + "");

        JCheckBox chkRecursive = new JCheckBox(jEdit.getProperty("net.sf.pmd.Recursive", "Recursive"), jEdit.getBooleanProperty(CHECK_DIR_RECURSIVE));

        CPDFileFilter[] choices = getCPDFileFilters();
        JComboBox fileTypeSelector = new JComboBox(choices);
        fileTypeSelector.setSelectedIndex(lastSelectedFilter);
        fileTypeSelector.setEditable(false);

        JTextField inclusionsRegex = new JTextField();
        inclusionsRegex.setText(lastInclusion);
        JTextField exclusionsRegex = new JTextField();
        exclusionsRegex.setText(lastExclusion);

        pnlAccessory.add(lblMinTileSize, "0, 0, 1, 1, W,, 3");
        pnlAccessory.add(txttilesize, "1, 0, 1, 1, W,, 3");
        pnlAccessory.add(chkRecursive, "0, 1, 2, 1, W,, 3");
        pnlAccessory.add(fileTypeSelector, "0, 2, 2, 1, W,, 3");
        pnlAccessory.add(new JLabel(jEdit.getProperty("net.sf.pmd.Inclusions", "Inclusions")), "0, 3, 1, 1, W,, 3");
        pnlAccessory.add(inclusionsRegex, "1, 3, 1, 1, W, w, 3");
        pnlAccessory.add(new JLabel(jEdit.getProperty("net.sf.pmd.Exclusions", "Exclusions")), "0, 4, 1, 1, W,, 3");
        pnlAccessory.add(exclusionsRegex, "1, 4, 1, 1, W, w, 3");

        chooser.setAccessory(pnlAccessory);

        int returnVal = chooser.showOpenDialog(view);
        File selectedFile = null;
        String inclusions = null;
        String exclusions = null;
        CPDFileFilter mode = null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            inclusions = inclusionsRegex.getText();
            jEdit.setProperty(LAST_INCLUSION_REGEX, inclusions);
            lastInclusion = inclusions;
            exclusions = exclusionsRegex.getText();
            jEdit.setProperty(LAST_EXCLUSION_REGEX, exclusions);
            lastExclusion = exclusions;
            mode = (CPDFileFilter) fileTypeSelector.getSelectedItem();
            lastSelectedFilter = fileTypeSelector.getSelectedIndex();
            jEdit.setIntegerProperty(LAST_SELECTED_FILTER, lastSelectedFilter);
            mode.setInclusions(inclusions);
            mode.setExclusions(exclusions);

            if (! selectedFile.isDirectory()) {
                JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Selection_not_a_directory.", "Selection not a directory."), NAME, JOptionPane.ERROR_MESSAGE);
                return ;
            }

            int tilesize = 100;
            try {
                tilesize = Integer.parseInt(txttilesize.getText());
            } catch (NumberFormatException e) {
                // use the default.
                tilesize = jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY, 100);
            }

            try {
                jEdit.setBooleanProperty(CHECK_DIR_RECURSIVE, chkRecursive.isSelected());
                instance.instanceCPDDir(view, selectedFile.getCanonicalPath(), tilesize, chkRecursive.isSelected(), mode);
            } catch (IOException e) {
                Log.log(Log.ERROR, instance, "PMD ERROR: Unable to open file " + selectedFile, e);
            }
        }
    }

    /**
     * Run CPD on a directory selected from the File System Browser.
     * @param view The current view.
     * @param browser The file system browser supplying the user selecte directory to check.
     * @param recursive If true, check all files in the selected directory and all child
     * directories, otherwise, just check the files in the selected directory only.
     */
    public static void cpdDir(View view, VFSBrowser browser, boolean recursive) throws IOException {
        if (view != null && browser != null) {
            VFSFile selectedDir[] = browser.getSelectedFiles();
            if (selectedDir == null || selectedDir.length == 0) {
                JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.One_Directory_has_to_be_selected_in_which_to_detect_duplicate_code.", "One Directory has to be selected in which to detect duplicate code."), NAME, JOptionPane.ERROR_MESSAGE);
                return ;
            }

            if (selectedDir[0].getType() != VFSFile.DIRECTORY) {
                JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Selected_file_must_be_a_Directory.", "Selected file must be a Directory."), NAME, JOptionPane.ERROR_MESSAGE);
                return ;
            }

            // display chooser for type of files to check
            CPDFileFilter[] choices = getCPDFileFilters();
            CPDFileFilter choice = (CPDFileFilter) JOptionPane.showInputDialog(view, jEdit.getProperty("net.sf.pmd.Select_type_of_files_to_check>", "Select type of files to check:"), jEdit.getProperty("net.sf.pmd.CPD,_Select_File_Type", "CPD, Select File Type"), JOptionPane.OK_CANCEL_OPTION, null, choices, choices[0]);
            if (choice == null) {
                return ;
            }

            instance.instanceCPDDir(view, selectedDir[0].getPath(), jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY, 100), recursive, choice);
        }
    }

    private static CPDFileFilter[] getCPDFileFilters() {
        List<CPDFileFilter> filters = new ArrayList <CPDFileFilter>();
        Mode[] modes = jEdit.getModes();
        for (Mode mode : modes) {
            String filenameGlob = (String) mode.getProperty("filenameGlob");
            if (filenameGlob == null) {
                continue;
            }
            String modeName = mode.getName();
            filenameGlob = filenameGlob.replaceAll("[*.{}\\[\\]]", "");
            String[] extensions = filenameGlob.split("[,]");
            filters.add(new CPDFileFilter(modeName, modeName, extensions));
        }
        Collections.sort(filters);
        return filters.toArray(new CPDFileFilter[filters.size()]);
    }

    private void instanceCPDDir(final View view, final String dir, final int tileSize, final boolean recursive, final CPDFileFilter mode) {
        if (dir != null) {
            SwingWorker<CPD, Object> worker = new SwingWorker<CPD, Object>() {
                @Override
                public CPD doInBackground() {
                    jEdit.setProperty(LAST_DIRECTORY, dir);
                    DefaultErrorSource errorSource = getErrorSource(view);
                    errorSource.clear();
                    CPD cpd = getCPD(tileSize, mode);

                    try {
                        if (recursive) {
                            cpd.addRecursively(dir);
                        } else {
                            cpd.addAllInDirectory(dir);
                        }
                        cpd.go();
                        return cpd;
                    } catch (Exception e) {
                        Log.log(Log.ERROR, this, "Unable to run CPD: " + e.getMessage());
                        return null;
                    }
                }

                @Override
                public void done() {
                    try {
                        CPD cpd = get();
                        if (cpd == null) {
                            view.getStatus().setMessageAndClear(jEdit.getProperty("net.sf.pmd.Cannot_run_CPD_on_Invalid_directory/files.", "Cannot run CPD on Invalid directory/files."));
                            return;
                        }
                        instance.processDuplicates(cpd, view);
                    } catch (Exception e) {
                        Log.log(Log.ERROR, this, "CPD, nable to process duplicates: " + e.getMessage());
                    }
                }
            } ;
            worker.execute();
        }
    }

    private void processDuplicates(CPD cpd, View view) {
        CPDDuplicateCodeViewer dv = getCPDDuplicateCodeViewer(view);

        dv.clearDuplicates();
        Iterator<Match> matches = cpd.getMatches();

        if (matches.hasNext()) {
            while (matches.hasNext()) {
                Match match = matches.next();
                CPDDuplicateCodeViewer.Duplicates duplicates = dv. new Duplicates(match.getLineCount() + " duplicate lines", match.getSourceCodeSlice());                // NOPMD
                for (Iterator<TokenEntry> occurrences = match.iterator(); occurrences.hasNext();) {
                    TokenEntry mark = occurrences.next();
                    int lastLine = mark.getBeginLine() + match.getLineCount();
                    CPDDuplicateCodeViewer.Duplicate duplicate = dv. new Duplicate(mark.getTokenSrcID(), mark.getBeginLine(), lastLine);                    // NOPMD
                    duplicates.addDuplicate(duplicate);
                }
                dv.addDuplicates(duplicates);
            }
        } else {
            dv.getRoot().add(new DefaultMutableTreeNode(jEdit.getProperty("net.sf.pmd.No_duplicates_found.", "No duplicates found."), false));
        }
        dv.refreshTree();
        dv.expandAll();
    }

    public CPDDuplicateCodeViewer getCPDDuplicateCodeViewer(View view) {
        view.getDockableWindowManager().showDockableWindow("cpd-viewer");
        return (CPDDuplicateCodeViewer) view.getDockableWindowManager().getDockableWindow("cpd-viewer");

    }

    public static void checkFile(View view, VFSBrowser browser) {
        instance.checkFile(view, browser.getSelectedFiles());
    }

    public void checkFile(View view, VFSFile de[]) {
        if (view != null && de != null) {
            List<File> files = new ArrayList <File>();

            for (VFSFile file : de) {
                if (file.getType() == VFSFile.FILE) {
                    files.add(new File(file.getPath()));
                }
            }

            process(files, view);
        }
    }

    public static void checkDirectory(View view, VFSBrowser browser, boolean recursive) {
        VFSFile de[] = browser.getSelectedFiles();

        if (de == null || de.length == 0 || de[0].getType() != VFSFile.DIRECTORY) {
            JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Selection_must_be_a_directory", "Selection must be a directory"), NAME, JOptionPane.ERROR_MESSAGE);
            return ;
        }

        instance.process(instance.findFiles(de[0].getPath(), recursive), view);
    }

    public void startProgressBarDisplay(final View view, final int min, final int max) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                instance.progressBar = new ProgressBar(view, min, max);
                view.getStatus().add(instance.progressBar, BorderLayout.EAST);
                instance.progressBar.setVisible(true);
            }
        } );
    }

    public void endProgressBarDisplay() {
        if (instance.progressBar != null) {
            instance.progressBar.completeBar();
            instance.progressBar.setVisible(false);
        }
    }

    public void exportErrorAsReport(final View view, Report reports[]) {

        String format = jEdit.getProperty(PMDJEditPlugin.RENDERER);

        // "None", "Text", "Html", "XML", "CSV"
        if (format != null && ! format.equals("None")) {
            net.sourceforge.pmd.renderers.Renderer renderer = null;

            if ("XML".equals(format)) {
                renderer = new XMLRenderer();
            } else {
                if ("Html".equals(format)) {
                    renderer = new HTMLRenderer();
                } else {
                    if ("CSV".equals(format)) {
                        renderer = new CSVRenderer();
                    } else {
                        if ("Text".equals(format)) {
                            renderer = new TextRenderer();
                        } else {
                            JOptionPane.showMessageDialog(view, jEdit.getProperty("net.sf.pmd.Invalid_Renderer", "Invalid Renderer"), NAME, JOptionPane.ERROR_MESSAGE);
                            return ;
                        }
                    }
                }
            }

            if (reports != null) {
                final StringWriter sw = new StringWriter();
                final Writer writer = new BufferedWriter(sw);
                try {
                    renderer.setWriter(writer);
                    renderer.start();
                    for (Report report : reports) {
                        if (report != null) {
                            try {
                                renderer.renderFileReport(report);                                // TODO: pmd throws an NPE here
                            } catch (Exception e) {                                // NOPMD
                            }
                        }
                    }
                    renderer.end();
                } catch (IOException ioe) {
                    Log.log(Log.ERROR, this, "Renderer can't report.", ioe);
                } finally {
                    try {
                        writer.close();
                    } catch (Exception ignored) {                        // NOPMD
                    }
                }

                final Buffer buffer = jEdit.newFile(view);
                view.setBuffer(buffer);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        buffer.writeLock();
                        buffer.insert(0, sw.toString());
                        buffer.writeUnlock();
                    }
                } );
            }
        }
    }

    class ProgressBar extends JPanel {
        private JProgressBar pBar;
        private View view;

        public ProgressBar(View view, int min, int max) {
            super();
            this.view = view;
            setLayout(new BorderLayout());
            pBar = new JProgressBar(min, max);
            pBar.setBorder(new EtchedBorder(EtchedBorder.RAISED));
            pBar.setToolTipText(jEdit.getProperty("net.sf.pmd.PMD_Check_in_Progress", "PMD Check in Progress"));
            pBar.setForeground(jEdit.getColorProperty("pmd.progressbar.background"));
            pBar.setBackground(jEdit.getColorProperty("view.status.background"));

            pBar.setStringPainted(true);
            add(pBar, BorderLayout.CENTER);
        }

        public void increment(final int num) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pBar.setValue(pBar.getValue() + num);
                }
            } );
        }

        public void completeBar() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pBar.setValue(pBar.getMaximum());
                    view.getStatus().remove(pBar);
                }
            } );
        }
    }

    private CPD getCPD(int tileSize, final CPDFileFilter fileType) {
        Language lang = null;
        LanguageFactory lf = new LanguageFactory();
        List<String> supportedLanguages = Arrays.asList(LanguageFactory.supportedLanguages);

        Properties props = new Properties();
        if ("java".equals(fileType.getMode())) {
            props.setProperty(JavaTokenizer.IGNORE_LITERALS, String.valueOf(jEdit.getBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS)));
        }
        if (supportedLanguages.contains(fileType.getMode())) {
            lang = lf.createLanguage(fileType.getMode(), props);
        } else {
            lang = new AnyLanguage(fileType.getExtensions()) {
                public FilenameFilter getFileFilter() {
                    return fileType;
                }
            } ;
        }
        return lang == null ? null : new CPD(tileSize, lang);
    }

    private CPD getCPD(String fileType) {
        Mode mode = jEdit.getMode(fileType);
        if (mode == null) {
            return null;
        }
        String filenameGlob = (String) mode.getProperty("filenameGlob");
        if (filenameGlob == null) {
            return null;
        }
        filenameGlob = filenameGlob.replaceAll("[*.{}]", "");
        String[] extensions = filenameGlob.split("[,]");
        String modeName = mode.getName();
        CPDFileFilter filter = new CPDFileFilter(modeName, modeName, extensions);
        return getCPD(jEdit.getIntegerProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY, 100), filter);
    }

    /**
     * Run the PMD rule designer.
     */
    public static void runDesigner() {
        String[] args = new String [] {"-noexitonclose"} ;
        new Designer(args);
    }
}