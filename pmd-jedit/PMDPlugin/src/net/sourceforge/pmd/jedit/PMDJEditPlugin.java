/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
 */
package net.sourceforge.pmd.jedit;

import errorlist.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.cpd.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.io.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;


public class PMDJEditPlugin extends EBPlugin {

	public static final String NAME = "PMD";
	public static final String OPTION_RULES_PREFIX = "options.pmd.rules.";
	public static final String OPTION_UI_DIRECTORY_POPUP = "options.pmd.ui.directorypopup";
	public static final String DEFAULT_TILE_MINSIZE_PROPERTY = "pmd.cpd.defMinTileSize";
	public static final String RUN_PMD_ON_SAVE = "pmd.runPMDOnSave";
	public static final String CUSTOM_RULES_PATH_KEY = "pmd.customRulesPath";
	//private static RE re = new UncheckedRE("Starting at line ([0-9]*) of (\\S*)");

	private static PMDJEditPlugin instance;

/* 	static {
		instance = new PMDJEditPlugin();
		instance.start();
	}
 */
	private DefaultErrorSource errorSource;

	// boilerplate JEdit code
	public void start()
	{
		instance = this;
		errorSource = new DefaultErrorSource(NAME);
		//ErrorSource.registerErrorSource(errorSource);
	}

	public void stop()
	{
		instance = null;
		unRegisterErrorSource();
 	}


/* 	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("pmd-menu"));
	}

	public void createOptionPanes(OptionsDialog optionsDialog) {
		optionsDialog.addOptionPane(new PMDOptionPane());
	} */
	// boilerplate JEdit code

	public static void checkDirectory(View view) {
		instance.instanceCheckDirectory(view);
	}

	public void instanceCheckDirectory(View view) {
		if (jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP))
		{
			String dir = JOptionPane.showInputDialog(jEdit.getFirstView(), "Please type in a directory to scan", NAME, JOptionPane.QUESTION_MESSAGE);
			if (dir != null)
			{
				if (!(new File(dir)).exists() || !(new File(dir)).isDirectory() )
				{
					JOptionPane.showMessageDialog(jEdit.getFirstView(), dir + " is not a valid directory name", NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				process(findFiles(dir, false));
			}
		}
		else
		{
			VFSBrowser browser = (VFSBrowser)view.getDockableWindowManager().getDockable("vfs.browser");
			if(browser == null) {
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "Can't run PMD on a directory unless the file browser is open", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			process(findFiles(browser.getDirectory(), false));
		}
	}

	public void handleMessage(EBMessage ebmess)
	{
		if (ebmess instanceof BufferUpdate)
		{
			if(jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE))
			{
				BufferUpdate bu = (BufferUpdate)ebmess;
				if (bu.getWhat() == BufferUpdate.SAVED)
				{
					instance.check(bu.getBuffer(),bu.getView());
				}
			}
		}
	}

	// check all open buffers
	public static void checkAllOpenBuffers(View view) {
		instance.instanceCheckAllOpenBuffers(view);
	}

	public void instanceCheckAllOpenBuffers(View view) {
		// I'm putting the files in a Set to work around some
		// odd behavior in jEdit - the buffer.getNext()
		// seems to iterate over the files twice.
		Buffer buffers[] = jEdit.getBuffers();

		if(buffers != null)
		{
			for (int i=0; i<buffers.length; i++ )
			{
					if (buffers[i].getName().endsWith(".java"))
					{
						//fileSet.add(buffer.getFile());
						System.out.println("checking = " + buffers[i].getPath());
						instanceCheck(buffers[i],view, false);
					}
			}
		}

		//List files = new ArrayList();
		//files.addAll(fileSet);
		//process(files);
	}
	// check all open buffers


	// check directory recursively
	public static void checkDirectoryRecursively(View view) {
		instance.instanceCheckDirectoryRecursively(view);
	}

	public void instanceCheckDirectoryRecursively(View view) {
		if (jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP))
		{
			String dir = JOptionPane.showInputDialog(jEdit.getFirstView(), "Please type in a directory to scan recursively", NAME, JOptionPane.QUESTION_MESSAGE);
			if (dir != null && dir.trim() != null)
			{
				if (!(new File(dir)).exists() || !(new File(dir)).isDirectory() ) {
					JOptionPane.showMessageDialog(jEdit.getFirstView(), dir + " is not a valid directory name", NAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
				process(findFiles(dir, true));

			}
		}
		else
		{
			VFSBrowser browser = (VFSBrowser)view.getDockableWindowManager().getDockable("vfs.browser");
			if(browser == null) {
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "Can't run PMD on a directory unless the file browser is open", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			process(findFiles(browser.getDirectory(), true));
		}
	}
	// check directory recursively

	// clear error list
	public static void clearErrorList() {
		instance.instanceClearErrorList();
	}

	public void instanceClearErrorList() {
		errorSource.clear();
	}
	// clear error list

	private void process(final List files) {
		new Thread(new Runnable () {
			           public void run() {
				           processFiles(files);
			           }
		           }).start();
	}

	// check current buffer
	public static void check(Buffer buffer, View view) {
		instance.instanceCheck(buffer, view, true);
	}

	public void instanceCheck(Buffer buffer, View view, boolean clearErrorList) {
		try {
			unRegisterErrorSource();
			if(clearErrorList)
			{
				errorSource.clear();
			}

			PMD pmd = new PMD();
			SelectedRules selectedRuleSets = new SelectedRules();
			RuleContext ctx = new RuleContext();
			ctx.setReport(new Report());
			ctx.setSourceCodeFilename(buffer.getPath());

			VFS vfs = buffer.getVFS();

			pmd.processFile(vfs._createInputStream(vfs.createVFSSession(buffer.getPath(),view),buffer.getPath(),false,view), selectedRuleSets.getSelectedRules(), ctx);
			if (ctx.getReport().isEmpty()) {
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
				errorSource.clear();
			} else {
				String path = buffer.getPath();
				for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
					RuleViolation rv = (RuleViolation)i.next();
					errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getLine()-1,0,0,rv.getDescription()));
				}
				registerErrorSource();
			}
		} catch (RuleSetNotFoundException rsne) {
			rsne.printStackTrace();
		} catch (PMDException pmde) {
			pmde.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "Error while processing " + buffer.getPath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}// check current buffer

	private void processFiles(List files) {
		unRegisterErrorSource();
		errorSource.clear();
		PMD pmd = new PMD();
		SelectedRules selectedRuleSets = null;
		try {
			selectedRuleSets = new SelectedRules();
		} catch (RuleSetNotFoundException rsne) {
			// should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
			System.out.println("PMD ERROR: Couldn't find a ruleset");
			rsne.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "Unable to find rulesets, halting PMD", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		RuleContext ctx = new RuleContext();
		ctx.setReport(new Report());

		boolean foundProblems = false;
		for (Iterator i = files.iterator(); i.hasNext();) {
			File file = (File)i.next();
			ctx.setReport(new Report());
			ctx.setSourceCodeFilename(file.getAbsolutePath());
			try {
				pmd.processFile(new FileInputStream(file), selectedRuleSets.getSelectedRules(), ctx);
			} catch (FileNotFoundException fnfe) {
				// should never happen, but if it does, carry on to the next file
				System.out.println("PMD ERROR: Unable to open file " + file.getAbsolutePath());
			} catch (PMDException pmde) {
				pmde.printStackTrace();
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "Error while processing " + file.getAbsolutePath());
			}
			for (Iterator j = ctx.getReport().iterator(); j.hasNext();) {
				foundProblems = true;
				RuleViolation rv = (RuleViolation)j.next();
				errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING,  file.getAbsolutePath(), rv.getLine()-1,0,0,rv.getDescription()));
			}
		}//End of for
		if (!foundProblems)
		{
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", NAME, JOptionPane.INFORMATION_MESSAGE);
			errorSource.clear();
		}
		else
		{
			registerErrorSource();
		}
	}

	private List findFiles(String dir, boolean recurse) {
		FileFinder f  = new FileFinder();
		return f.findFilesFrom(dir, new JavaLanguage.JavaFileOrDirectoryFilter(), recurse);
	}

	private void registerErrorSource()
	{
		Log.log(Log.DEBUG,this,"Registering ErrorSource of PMD");
		ErrorSource.registerErrorSource(errorSource);
	}

	private void unRegisterErrorSource()
	{
		Log.log(Log.DEBUG,this,"Unregistering ErrorSource of PMD");
		ErrorSource.unregisterErrorSource(errorSource);
	}

	public static void cpdCurrentFile(View view) throws IOException
	{
		/* if(!view.getBuffer().getMode().getName().equals("java"))
	{
			JOptionPane.showMessageDialog(view,"Copy/Paste detection can only be performed on Java code.","Copy/Paste Detector",JOptionPane.INFORMATION_MESSAGE);
			return;
	} */
		CPD cpd = null;
		//Log.log(Log.DEBUG, PMDJEditPlugin.class , "See mode " + view.getBuffer().getMode().getName());

		String modeName = view.getBuffer().getMode().getName();
		if (modeName.equals("java"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing java");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new JavaLanguage());
		}
		else if (modeName.equals("php"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing PHP");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new PHPLanguage());
		}
		else if (modeName.equals("c") || modeName.equals("c++"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing C/C++");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new CPPLanguage());
		}
		else
		{
			JOptionPane.showMessageDialog(view,"Copy/Paste detection can only be performed on Java,C/C++,PHP code.","Copy/Paste Detector",JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		cpd.add(new File(view.getBuffer().getPath()));
		cpd.go();
		instance.processDuplicates(cpd, view);
	}

	public static void cpdDir(View view, boolean recursive) throws IOException
	{
		JFileChooser chooser = new JFileChooser(jEdit.getProperty("pmd.cpd.lastDirectory"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel pnlAccessory = new JPanel();
		pnlAccessory.add(new JLabel("Minimum Tile size :"));
		JTextField txttilesize = new JTextField("100");
		pnlAccessory.add(txttilesize);
		chooser.setAccessory(pnlAccessory);

		int returnVal = chooser.showOpenDialog(view);
		File selectedFile = null;
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = chooser.getSelectedFile();
			if(!selectedFile.isDirectory())
			{
				JOptionPane.showMessageDialog(view,"Selection not a directory.","PMD",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else
		{
			return; //Incase the user presses cancel or escape.
		}

		jEdit.setProperty("pmd.cpd.lastDirectory",selectedFile.getCanonicalPath());
		instance.errorSource.clear();

		int tilesize = 100;
		try
		{
			tilesize = Integer.parseInt(txttilesize.getText());
		}
		catch(NumberFormatException e)
		{
			//use the default.
			tilesize = jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100);
		}

		CPD cpd = new CPD(tilesize, new JavaLanguage());
		if(recursive)
		{
			cpd.addRecursively(selectedFile.getCanonicalPath());
		}
		else
		{
			cpd.addAllInDirectory(selectedFile.getCanonicalPath());
		}
		cpd.go();
		instance.processDuplicates(cpd, view);
	}


	private void processDuplicates(CPD cpd, View view)
	{
		//StringBuffer report = new StringBuffer();
		CPDDuplicateCodeViewer dv = getCPDDuplicateCodeViewer(view);

		dv.clearDuplicates();
		for (Iterator i = cpd.getMatches(); i.hasNext();)
		{
			Match match = (Match)i.next();

			CPDDuplicateCodeViewer.Duplicates duplicates = dv.new Duplicates(match.getLineCount() + " duplicate lines", match.getSourceCodeSlice());

			for (Iterator occurrences = match.iterator(); occurrences.hasNext();)
			{
				Mark mark = (Mark)occurrences.next();

				//System.out.println("Begin line " + mark.getBeginLine() +" of file "+ mark.getTokenSrcID() +" Line Count "+ match.getLineCount());
				int lastLine = mark.getBeginLine()+match.getLineCount();

				CPDDuplicateCodeViewer.Duplicate duplicate =  dv.new Duplicate(mark.getTokenSrcID(),mark.getBeginLine(),lastLine);

				//System.out.println("Adding Duplicate " + duplicate +" to Duplicates "+ duplicates);
				duplicates.addDuplicate(duplicate);
			}
			dv.addDuplicates(duplicates);
		}
		dv.refreshTree();
		dv.expandAll();
	}//End of processDuplicates


	public CPDDuplicateCodeViewer getCPDDuplicateCodeViewer(View view)
	{
		view.getDockableWindowManager().showDockableWindow("cpd-viewer");
		return (CPDDuplicateCodeViewer)view.getDockableWindowManager().getDockableWindow("cpd-viewer");

	}

	public static void checkFile(View view, VFSBrowser browser)
	{
		instance.checkFile(view, browser.getSelectedFiles());
	}

	public void checkFile(View view,  VFS.DirectoryEntry de[])
	{
		if(view != null && de != null)
		{
			List files = new ArrayList();
			for(int i=0;i<de.length;i++)
			{
				if(de[i].type == VFS.DirectoryEntry.FILE)
				{
					files.add(new File(de[i].path));
				}
			}
		}
	}

	public static void checkDirectory(View view, VFSBrowser browser, boolean recursive)
	{
		VFS.DirectoryEntry de[] = browser.getSelectedFiles();
		if(de == null || de.length == 0 || de[0].type != VFS.DirectoryEntry.DIRECTORY)
		{
			JOptionPane.showMessageDialog(view, "Selection must be a directory",NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		instance.process(instance.findFiles(de[0].path, recursive));
	}


	class ProgressBarDialog extends JDialog
	{
		private JProgressBar pBar;

		public ProgressBarDialog(View view)
		{
			super(view,NAME);
			pBar = new JProgressBar();
		}

		public void show()
		{


		}
	}

}


