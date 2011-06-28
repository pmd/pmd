/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
*/
package net.sourceforge.pmd.jedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
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
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import ise.java.awt.KappaLayout;

/** jEdit plugin for PMD
 * @version $Id$
 *
 * DONE: move strings to property file so they can be localized.
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
    public static final String LAST_SELECTED_FILTER = "pmd.cpd.lastSelectedFilter";
    public static final String OPTION_RULES_PREFIX = "options.pmd.rules.";
    public static final String PRINT_RULE = "pmd.printRule";
    public static final String RUN_PMD_ON_SAVE = "pmd.runPMDOnSave";
    public static final String SHOW_PROGRESS = "pmd.showprogress";

    private static PMDJEditPlugin instance;

    private DefaultErrorSource errorSource;
    public static final String RENDERER = "pmd.renderer";

    private static int lastSelectedFilter = 0;
    private static String lastExclusion = "";

    public void start() {
        instance = this;
        // Log.log(Log.DEBUG,this,"Instance created.");
        errorSource = new DefaultErrorSource(NAME);
        lastSelectedFilter = jEdit.getIntegerProperty(LAST_SELECTED_FILTER, 0);
        lastExclusion = jEdit.getProperty(LAST_EXCLUSION_REGEX, "");
    }

    public void stop() {
        instance = null;
        unRegisterErrorSource();
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

    public static void clearErrorList() {
        instance.instanceClearErrorList();
    }

    public void instanceClearErrorList() {
        errorSource.clear();
    }

    public void instanceCheckDirectory(View view) {
        JFileChooser chooser = new JFileChooser(jEdit.getProperty(LAST_DIRECTORY));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        JPanel pnlAccessory = new JPanel();
        JCheckBox chkRecursive = new JCheckBox(jEdit.getProperty("net.sf.pmd.Recursive", "Recursive"), jEdit.getBooleanProperty(CHECK_DIR_RECURSIVE));
        pnlAccessory.add(chkRecursive);
        chooser.setAccessory(pnlAccessory);

        int returnVal = chooser.showOpenDialog(view);

        try {
            File selectedFile = null;

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();

                if (! selectedFile.isDirectory()) {
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, selectedFile.getAbsolutePath(), 0, 0, 0, jEdit.getProperty("net.sf.pmd.Selection_not_a_directory.", "Selection not a directory.")));                    // NOPMD
                    // JOptionPane.showMessageDialog(view,  jEdit.getProperty("net.sf.pmd.Selection_not_a_directory.",  "Selection not a directory."),  NAME,  JOptionPane.ERROR_MESSAGE);
                    return ;
                }

                jEdit.setProperty(LAST_DIRECTORY, selectedFile.getCanonicalPath());
                jEdit.setBooleanProperty(CHECK_DIR_RECURSIVE, chkRecursive.isSelected());
                process(findFiles(selectedFile.getCanonicalPath(), chkRecursive.isSelected()), view);
            } else {
                return ;                // In case the user presses cancel or escape.
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
            ProgressBar pbd = null;

            if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
                pbd = startProgressBarDisplay(view, 0, buffers.length);
            }

            for (Buffer buffer : buffers) {
                String modename = buffer.getMode().getName();
                if ("java".equals(modename) || "jsp".equals(modename)) {
                    Log.log(Log.DEBUG, this, "checking = " + buffer.getPath());
                    instanceCheck(buffer, view, false);
                }

                if (pbd != null) {
                    pbd.increment(1);
                }
            }

            endProgressBarDisplay(pbd);
        }
    }

    public void instanceCheck(Buffer buffer, View view, boolean clearErrorList) {
        try {
            unRegisterErrorSource();

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
            VFS vfs = buffer.getVFS();

            pmd.processFile(vfs._createInputStream(vfs.createVFSSession(path, view), path, false, view), (String) buffer.getProperty("encoding"), toCheck, ctx);

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

                registerErrorSource();
            }
        } catch (Exception e) {
            // DONE: is this useful to log? Yes it is.
            registerErrorSource();
            String msg = jEdit.getProperty("net.sf.pmd.Error_while_processing_", "Error while processing ") + buffer.getPath() + ":\n" + e.getMessage() + "\n\n" + e.getCause();
            Log.log(Log.ERROR, this, "+++++ Exception processing file " + buffer.getPath(), e);
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
        unRegisterErrorSource();
        errorSource.clear();

        ProgressBar pbd = null;

        if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
            pbd = startProgressBarDisplay(view, 0, files.size());
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
                pmd.processFile(new FileInputStream(file), System.getProperty("file.encoding"), selectedRuleSets.getSelectedRules(), ctx);                // NOPMD
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
                Log.log(Log.ERROR, this, msg, pmde);
                errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR, file.getAbsolutePath(), 0, 0, 0, msg));
                // JOptionPane.showMessageDialog(view,  msg,  "PMD",  JOptionPane.ERROR_MESSAGE);
            }

            if (jEdit.getBooleanProperty(SHOW_PROGRESS)) {
                pbd.increment(1);
            }
        }

        if (! foundProblems) {
            errorSource.clear();
        } else {
            registerErrorSource();
            exportErrorAsReport(view, reports.toArray(new Report[reports.size()]));
        }

        endProgressBarDisplay(pbd);
        pbd = null;
    }

    private List<File> findFiles(String dir, boolean recurse) {
        FileFinder f = new FileFinder();
        return f.findFilesFrom(dir, new net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter(new SourceFileSelector()), recurse);
    }

    private void registerErrorSource() {
        ErrorSource.registerErrorSource(errorSource);
    }

    private void unRegisterErrorSource() {
        ErrorSource.unregisterErrorSource(errorSource);
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

        JTextField exclusionsRegex = new JTextField();
        exclusionsRegex.setText(lastExclusion);

        pnlAccessory.add(lblMinTileSize, "0, 0, 1, 1, W,, 3");
        pnlAccessory.add(txttilesize, "1, 0, 1, 1, W,, 3");
        pnlAccessory.add(chkRecursive, "0, 1, 2, 1, W,, 3");
        pnlAccessory.add(fileTypeSelector, "0, 2, 2, 1, W,, 3");
        pnlAccessory.add(new JLabel(jEdit.getProperty("net.sf.pmd.Exclusions", "Exclusions")), "0, 3, 1, 1, W,, 3");
        pnlAccessory.add(exclusionsRegex, "1, 3, 1, 1, W, w, 3");

        chooser.setAccessory(pnlAccessory);

        int returnVal = chooser.showOpenDialog(view);
        File selectedFile = null;
        String exclusions = null;
        CPDFileFilter mode = null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            exclusions = exclusionsRegex.getText();
            jEdit.setProperty(LAST_EXCLUSION_REGEX, exclusions);
            lastExclusion = exclusions;
            mode = (CPDFileFilter) fileTypeSelector.getSelectedItem();
            lastSelectedFilter = fileTypeSelector.getSelectedIndex();
            jEdit.setIntegerProperty(LAST_SELECTED_FILTER, lastSelectedFilter);
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
                    instance.errorSource.clear();
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

    public ProgressBar startProgressBarDisplay(View view, int min, int max) {
        ProgressBar pbd = new ProgressBar(view, min, max);
        pbd.setVisible(true);
        return pbd;
    }

    public void endProgressBarDisplay(ProgressBar pbd) {
        if (pbd != null) {
            pbd.completeBar();
            pbd.setVisible(false);
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
                final StringBuffer strbuf = new StringBuffer();
                try {
                    renderer.start();
                    for (int i = 0; i < reports.length; i++) {
                        renderer.renderFileReport(reports[i]);
                    }

                    renderer.end();
                } catch (IOException ioe) {
                    Log.log(Log.ERROR, this, "Renderer can't report.", ioe);
                }
                final Buffer buffer = jEdit.newFile(view);
                view.setBuffer(buffer);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        buffer.writeLock();
                        buffer.insert(0, strbuf.toString());
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
            // TODO: why do these colors override the look and feel colors?
            // They probably should not.
            pBar.setUI(new BasicProgressBarUI() {
                public Color getSelectionBackground() {
                    return jEdit.getColorProperty("pmd.progressbar.foreground");
                }

                public Color getSelectionForeground() {
                    return jEdit.getColorProperty("pmd.progressbar.foreground");
                }
            }

           );
            pBar.setBorder(new EtchedBorder(EtchedBorder.RAISED));
            pBar.setToolTipText(jEdit.getProperty("net.sf.pmd.PMD_Check_in_Progress", "PMD Check in Progress"));
            pBar.setForeground(jEdit.getColorProperty("pmd.progressbar.background"));
            pBar.setBackground(jEdit.getColorProperty("view.status.background"));

            pBar.setStringPainted(true);
            add(pBar, BorderLayout.CENTER);
            view.getStatus().add(pBar, BorderLayout.EAST);
        }

        public void increment(int num) {
            pBar.setValue(pBar.getValue() + num);
        }

        public void completeBar() {
            pBar.setValue(pBar.getMaximum());
            view.getStatus().remove(pBar);
            view = null;
            pBar = null;
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