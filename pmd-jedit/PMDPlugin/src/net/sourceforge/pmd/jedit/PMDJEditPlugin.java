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
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceFileSelector;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

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
**/ 
public class PMDJEditPlugin extends EBPlugin
{

	public static final SourceType[] sourceTypes = 
		new SourceType[]{SourceType.JAVA_14, SourceType.JAVA_15, SourceType.JAVA_16};
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
	
	//private static RE re = new UncheckedRE("Starting at line ([0-9]*) of (\\S*)");

	private static PMDJEditPlugin instance;

	/* 	static {
			instance = new PMDJEditPlugin();
			instance.start();
		}
	 */
	private DefaultErrorSource errorSource;
	public static final String RENDERER = "pmd.renderer";

	// boilerplate JEdit code
	public void start()
	{
		instance = this;
		//Log.log(Log.DEBUG,this,"Instance created.");
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

	public static void checkDirectory(View view)
	{
		instance.instanceCheckDirectory(view);
	}

	public void handleMessage(EBMessage ebmess)
	{
		if (ebmess instanceof BufferUpdate && jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE))
		{
			BufferUpdate bu = (BufferUpdate)ebmess;

			if (bu.getWhat() == BufferUpdate.SAVED)
			{
				if(bu.getBuffer().getMode().getName().equals("java")) //NOPMD
				{
					check(bu.getBuffer(),bu.getView());
				}
			}
		}
	}

	// check all open buffers
	public static void checkAllOpenBuffers(View view)
	{
		instance.instanceCheckAllOpenBuffers(view);
	}

	public static void clearErrorList()
	{
		instance.instanceClearErrorList();
	}

	public void instanceClearErrorList()
	{
		errorSource.clear();
	}

	public void instanceCheckDirectory(View view)
	{
		JFileChooser chooser = new JFileChooser(jEdit.getProperty(LAST_DIRECTORY));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel pnlAccessory = new JPanel();
		JCheckBox chkRecursive = new JCheckBox("Recursive", jEdit.getBooleanProperty(CHECK_DIR_RECURSIVE));
		pnlAccessory.add(chkRecursive);
		chooser.setAccessory(pnlAccessory);

		int returnVal = chooser.showOpenDialog(view);

		try
		{
			File selectedFile = null;

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				selectedFile = chooser.getSelectedFile();

				if(!selectedFile.isDirectory())
				{
					JOptionPane.showMessageDialog(view,"Selection not a directory.",NAME,JOptionPane.ERROR_MESSAGE);
					return;
				}

				jEdit.setProperty(LAST_DIRECTORY,selectedFile.getCanonicalPath());
				jEdit.setBooleanProperty(CHECK_DIR_RECURSIVE,chkRecursive.isSelected());
				process(findFiles(selectedFile.getCanonicalPath(), chkRecursive.isSelected()), view);
			}
			else
			{
				return; //Incase the user presses cancel or escape.
			}

		}
		catch(IOException e)
		{
			Log.log(Log.DEBUG,this,e);
		}
	}//End of instanceCheckDirectory

	public void instanceCheckAllOpenBuffers(View view)
	{
		// I'm putting the files in a Set to work around some
		// odd behavior in jEdit - the buffer.getNext()
		// seems to iterate over the files twice.
		Buffer buffers[] = jEdit.getBuffers();

		if(buffers != null)
		{
			ProgressBar pbd = null;

			if(jEdit.getBooleanProperty(SHOW_PROGRESS))
			{
				pbd = startProgressBarDisplay(view,0,buffers.length);
			}

			for (int i=0; i<buffers.length; i++ )
			{
				if (buffers[i].getName().endsWith(".java"))
				{
					//fileSet.add(buffer.getFile());
					Log.log(Log.DEBUG,this,"checking = " + buffers[i].getPath());
					instanceCheck(buffers[i],view, false);
				}

				if(pbd != null)
				{
					pbd.increment(1);
				}
			}

			endProgressBarDisplay(pbd);
		}

		//List files = new ArrayList();
		//files.addAll(fileSet);
		//process(files);
	}

	public void instanceCheck(Buffer buffer, View view, boolean clearErrorList)
	{
		try
		{
			unRegisterErrorSource();

			if(clearErrorList)
			{
				errorSource.clear();
			}

			PMD pmd = new PMD();
			int jvidx = jEdit.getIntegerProperty(JAVA_VERSION_PROPERTY, 1);
			pmd.setJavaVersion(sourceTypes[jvidx]);
			SelectedRules selectedRuleSets = new SelectedRules();
			RuleContext ctx = new RuleContext();
			ctx.setReport(new Report());
			String path = buffer.getPath();
			ctx.setSourceCodeFilename(path);
			VFS vfs = buffer.getVFS();

			pmd.processFile(vfs._createInputStream(vfs.createVFSSession(path, view), path, false, view),
					System.getProperty("file.encoding"),
					selectedRuleSets.getSelectedRules(), 
					ctx);

			if (ctx.getReport().isEmpty())
			{
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", NAME, JOptionPane.INFORMATION_MESSAGE);
				errorSource.clear();
			}

			else
			{
				String rulename="";
				
				final boolean isPrintRule = jEdit.getBooleanProperty(PMDJEditPlugin.PRINT_RULE);
				
				for (Iterator i = ctx.getReport().iterator(); i.hasNext();)
				{
					RuleViolation rv = (RuleViolation)i.next();
					if(isPrintRule)
					{
						rulename = rv.getRule().getName() +"->";
					}
					
					
					errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getBeginLine()-1,0,0,rulename + rv.getDescription())); //NOPMD
				}

				registerErrorSource();
			}
		}

		catch (RuleSetNotFoundException rsne)
		{
			Log.log(Log.ERROR, this, "RuleSet not found", rsne); 
		}

		catch (PMDException pmde)
		{
			String msg="Error while processing " + buffer.getPath();
			Log.log(Log.ERROR, this, msg, pmde);
			JOptionPane.showMessageDialog(jEdit.getFirstView(), msg);
		}

		catch(Exception e)
		{
			Log.log(Log.ERROR, this, "Exception processing file "+ buffer.getPath(), e);
		}
	}// check current buffer

	public void process(final List<File> files, final View view)
	{
		new Thread(new Runnable ()
				   {
					   public void run()
					   {
						   processFiles(files, view);
					   }
				   }

				  ).start();
	}

	// check current buffer
	public static void check(Buffer buffer, View view)
	{
		instance.instanceCheck(buffer, view, true);
	}

	void processFiles(List<File> files, View view)
	{
		unRegisterErrorSource();
		errorSource.clear();

		ProgressBar pbd = null;

		if(jEdit.getBooleanProperty(SHOW_PROGRESS))
		{
			pbd = startProgressBarDisplay(view,0,files.size());
		}

		PMD pmd = new PMD();
		SelectedRules selectedRuleSets = null;

		try
		{
			selectedRuleSets = new SelectedRules();
		}

		catch (RuleSetNotFoundException rsne)
		{
			// should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
			Log.log(Log.ERROR, this, "PMD ERROR: Couldn't find a ruleset", rsne);
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "Unable to find rulesets, halting PMD", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		RuleContext ctx = new RuleContext();

		List<Report> reports = new ArrayList<Report>();
		boolean foundProblems = false;

		Iterator<File> iter = files.iterator();
		while(iter.hasNext())
		{
			File file = iter.next();
			//ctx.setReport(new Report());
			ctx.setReport(new Report()); //NOPMD
			ctx.setSourceCodeFilename(file.getAbsolutePath());

			try
			{
				pmd.processFile(new FileInputStream(file), System.getProperty("file.encoding"), selectedRuleSets.getSelectedRules(), ctx); //NOPMD
				for (Iterator j = ctx.getReport().iterator(); j.hasNext();)
				{
					foundProblems = true;
					RuleViolation rv = (RuleViolation)j.next();
					errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR,  file.getAbsolutePath(), rv.getBeginLine()-1,0,0,rv.getDescription())); //NOPMD
				}
				if(!ctx.getReport().isEmpty())//That means Report contains some violations, so only cache such reports.
				{
					reports.add(ctx.getReport());
				}
			}
			catch (FileNotFoundException fnfe)
			{
				// should never happen, but if it does, carry on to the next file
				Log.log(Log.ERROR, this, "PMD ERROR: Unable to open file " + file.getAbsolutePath(), fnfe);
			}
			catch (PMDException pmde)
			{
				String msg = "Error while processing " + file.getAbsolutePath();
				Log.log(Log.ERROR, this, msg, pmde);
				JOptionPane.showMessageDialog(jEdit.getFirstView(), msg);
			}

			if(jEdit.getBooleanProperty(SHOW_PROGRESS))
			{
				pbd.increment(1);
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
			exportErrorAsReport(view, reports.toArray(new Report[reports.size()]));
		}

		endProgressBarDisplay(pbd);
		pbd = null;
	}

	private List<File> findFiles(String dir, boolean recurse)
	{
		FileFinder f  = new FileFinder();
		return f.findFilesFrom(dir, new net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter(new SourceFileSelector()), recurse);
	}

	private void registerErrorSource()
	{
		//Log.log(Log.DEBUG,this,"Registering ErrorSource of PMD");
		ErrorSource.registerErrorSource(errorSource);
	}

	private void unRegisterErrorSource()
	{
		//Log.log(Log.DEBUG,this,"Unregistering ErrorSource of PMD");
		ErrorSource.unregisterErrorSource(errorSource);
	}

	public static void cpdCurrentFile(View view) throws IOException
	{
		String modeName = getFileType(view.getBuffer().getMode().getName());
		instance.instanceCPDCurrentFile(view, view.getBuffer().getPath(), modeName);
	}

	public static void cpdCurrentFile(View view, VFSBrowser browser) throws IOException
	{
		VFSFile selectedFile[] = browser.getSelectedFiles();

		if(selectedFile == null || selectedFile.length == 0)
		{
			JOptionPane.showMessageDialog(view, "One file must be selected", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(selectedFile[0].getType() == VFSFile.DIRECTORY)
		{
			JOptionPane.showMessageDialog(view, "Selected file cannot be a Directory.", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		String path = selectedFile[0].getPath();
		instance.instanceCPDCurrentFile(view, path, getFileType(path));
	}

	//TODO: Replace this method with a smart file type/mode detector.
	private static String getFileType(String name)
	{
		if(name != null)
		{
			for (String lang : LanguageFactory.supportedLanguages) {
				if (name.endsWith(lang)) return lang;
			}
			if(name.endsWith("h") || name.endsWith("cxx") || name.endsWith("c++"))
			{
				return "cpp";
			}
		}
		return null;
	}

	private void instanceCPDCurrentFile(View view, String filename, String fileType) throws IOException
	{
		CPD cpd = getCPD(fileType, view);
		//Log.log(Log.DEBUG, PMDJEditPlugin.class , "See mode " + view.getBuffer().getMode().getName());

		if(cpd != null)
		{
			cpd.add(new File(filename));
			cpd.go();
			instance.processDuplicates(cpd, view);
			view.getDockableWindowManager().showDockableWindow("cpd-viewer");
		}
		else
		{
			view.getStatus().setMessageAndClear("Cannot run CPD on an Invalid file type");
			return;
		}
	}


	public static void cpdDir(View view)
	{
		JFileChooser chooser = new JFileChooser(jEdit.getProperty(LAST_DIRECTORY));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel pnlAccessory = new JPanel(new BorderLayout());

		JPanel pnlTile = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblMinTileSize = new JLabel("Minimum Tile size :");
		JTextField txttilesize = new JTextField(3);
		txttilesize.setText(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100)+"");
		pnlTile.add(lblMinTileSize);
		pnlTile.add(txttilesize);
		pnlAccessory.add(BorderLayout.NORTH, pnlTile);

		JPanel pnlRecursive = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JCheckBox chkRecursive = new JCheckBox("Recursive", jEdit.getBooleanProperty(CHECK_DIR_RECURSIVE));
		pnlRecursive.add(chkRecursive);
		pnlAccessory.add(BorderLayout.CENTER, pnlRecursive);

		chooser.setAccessory(pnlAccessory);

		int returnVal = chooser.showOpenDialog(view);
		File selectedFile = null;

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = chooser.getSelectedFile();

			if(!selectedFile.isDirectory())
			{
				JOptionPane.showMessageDialog(view,"Selection not a directory.",NAME,JOptionPane.ERROR_MESSAGE);
				return;
			}

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


			try
			{
				jEdit.setBooleanProperty(CHECK_DIR_RECURSIVE,chkRecursive.isSelected());
				instance.instanceCPDDir(view, selectedFile.getCanonicalPath(), tilesize, chkRecursive.isSelected());
			}
			catch(IOException e)
			{
				Log.log(Log.ERROR, instance, "PMD ERROR: Unable to open file " + selectedFile, e);
			}
		}
	}

	public static void cpdDir(View view, VFSBrowser browser, boolean recursive) throws IOException
	{
		if(view != null && browser != null)
		{
			VFSFile selectedDir[] = browser.getSelectedFiles();
			if(selectedDir == null || selectedDir.length == 0)
			{
				JOptionPane.showMessageDialog(view, "One Directory has to be selected in which to detect duplicate code.", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if(selectedDir[0].getType() != VFSFile.DIRECTORY)
			{
				JOptionPane.showMessageDialog(view, "Selected file must be a Directory.", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			instance.instanceCPDDir(view, selectedDir[0].getPath(), jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100), recursive);
		}
	}

	private void instanceCPDDir(View view, String dir, int tileSize, boolean recursive) throws IOException
	{
		if(dir != null)
		{
			jEdit.setProperty(LAST_DIRECTORY,dir);
			instance.errorSource.clear();
			CPD cpd = getCPD(tileSize, "java", view);

			if(cpd != null)
			{
				if(recursive)
				{
					cpd.addRecursively(dir);
				}
				else
				{
					cpd.addAllInDirectory(dir);
				}
				cpd.go();
				instance.processDuplicates(cpd, view);
			}
			else
			{
				view.getStatus().setMessageAndClear("Cannot run CPD on Invalid directory/files.");
				return;
			}
		}//End of if(dir != null)
	}

	private void processDuplicates(CPD cpd, View view)
	{
		//StringBuffer report = new StringBuffer();
		CPDDuplicateCodeViewer dv = getCPDDuplicateCodeViewer(view);

		dv.clearDuplicates();
		boolean foundDuplicates = false;

		for (Iterator i = cpd.getMatches(); i.hasNext();)
		{
			if(!foundDuplicates) //Set foundDuplicates to true and that too only once.
			{
				foundDuplicates = true;
			}

			Match match = (Match)i.next();

			CPDDuplicateCodeViewer.Duplicates duplicates = dv.new Duplicates(match.getLineCount() + " duplicate lines", match.getSourceCodeSlice()); //NOPMD

			for (Iterator occurrences = match.iterator(); occurrences.hasNext();)
			{
				TokenEntry mark = (TokenEntry)occurrences.next();

				int lastLine = mark.getBeginLine()+match.getLineCount();
				Log.log(Log.DEBUG, this, "Begin line " + mark.getBeginLine() +" of file "+ mark.getTokenSrcID() +" Line Count "+ match.getLineCount() +" last line "+ lastLine);

				CPDDuplicateCodeViewer.Duplicate duplicate =  dv.new Duplicate(mark.getTokenSrcID(),mark.getBeginLine(),lastLine); //NOPMD

				//System.out.println("Adding Duplicate " + duplicate +" to Duplicates "+ duplicates);
				duplicates.addDuplicate(duplicate);
			}//End of inner for

			dv.addDuplicates(duplicates);
		}//End of outer for

		if(!foundDuplicates)
		{
			dv.getRoot().add(new DefaultMutableTreeNode("No Duplicates found.",false));
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

	public void checkFile(View view,  VFSFile de[])
	{
		if(view != null && de != null)
		{
			List<File> files = new ArrayList<File>();

			for(int i=0;i<de.length;i++)
			{
				if(de[i].getType() == VFSFile.FILE)
				{
					files.add(new File(de[i].getPath())); //NOPMD
				}
			}

			process(files, view);
		}
	}

	public static void checkDirectory(View view, VFSBrowser browser, boolean recursive)
	{
		VFSFile de[] = browser.getSelectedFiles();

		if(de == null || de.length == 0 || de[0].getType() != VFSFile.DIRECTORY)
		{
			JOptionPane.showMessageDialog(view, "Selection must be a directory",NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		instance.process(instance.findFiles(de[0].getPath(), recursive),view);
	}

	public ProgressBar startProgressBarDisplay(View view, int min, int max)
	{
		ProgressBar pbd = new ProgressBar(view,min,max);
		pbd.setVisible(true);
		return pbd;
	}

	public void endProgressBarDisplay(ProgressBar pbd)
	{
		if(pbd != null)
		{
			pbd.completeBar();
			pbd.setVisible(false);
		}
	}

	public void exportErrorAsReport(final View view, Report reports[])
	{

		String format = jEdit.getProperty(PMDJEditPlugin.RENDERER);

		//"None", "Text", "Html", "XML", "CSV"
		if(format != null && !format.equals("None"))
		{
			net.sourceforge.pmd.renderers.Renderer renderer = null;

			if ("XML".equals(format))
			{
				renderer = new XMLRenderer();
			}
			else if ("Html".equals(format))
			{
				renderer = new HTMLRenderer();
			}
			else if ("CSV".equals(format))
			{
				renderer = new CSVRenderer();
			}
			else if ("Text".equals(format))
			{
				renderer = new TextRenderer();
			}
			else
			{
				JOptionPane.showMessageDialog(view, "Invalid Renderer", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if(reports != null)
			{
				final StringBuffer strbuf = new StringBuffer();
				// String output = null;
				try {
					renderer.start();
					for(int i=0;i<reports.length;i++)
						renderer.renderFileReport(reports[i]);
					renderer.end();
				}
				catch (IOException ioe) {
					Log.log(Log.ERROR, this, "Renderer can't report.", ioe );
				}
				view.setBuffer(jEdit.newFile(view));
				VFSManager.runInAWTThread(
					new Runnable()
					{
						public void run()
						{
							view.getTextArea().setText(strbuf.toString());
						}
					}
				);
			}//End of if
		}//End of exportErrorAsReport
	}


	class ProgressBar extends JPanel
	{
		private JProgressBar pBar;
		private View view;

		public ProgressBar(View view, int min,int max)
		{
			super();
			this.view = view;
			setLayout(new BorderLayout());
			pBar = new JProgressBar(min,max);
			pBar.setUI(new BasicProgressBarUI()
					   {
						   public Color getSelectionBackground()
						   {
							   return jEdit.getColorProperty("pmd.progressbar.foreground");
						   }

						   public Color getSelectionForeground()
						   {
							   return jEdit.getColorProperty("pmd.progressbar.foreground");
						   }
					   }

					  );
			//pBar.addNotify();
			pBar.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			pBar.setToolTipText("PMD Check in Progress");
			pBar.setForeground(jEdit.getColorProperty("pmd.progressbar.background")); //Color of the ProgressBar flow
			pBar.setBackground(jEdit.getColorProperty("view.status.background")); //Color of the ProgressBar Background

			pBar.setStringPainted(true);
			add(pBar, BorderLayout.CENTER);
			view.getStatus().add(pBar, BorderLayout.EAST);
		}

		public void increment(int num)
		{
			pBar.setValue(pBar.getValue()+num);
		}

		public void completeBar()
		{
			pBar.setValue(pBar.getMaximum());
			view.getStatus().remove(pBar);
			view = null;
			pBar = null;
		}
	}


	private CPD getCPD(int tileSize, String fileType, View view)
	{
		Language lang;
		LanguageFactory lf = new LanguageFactory();
		if (fileType.equals("java"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing java");
			Properties props = new Properties();
			props.setProperty(JavaTokenizer.IGNORE_LITERALS, String.valueOf(jEdit.getBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS)));
			lang = lf.createLanguage("java", props);
		}
		else if (fileType.equals("php"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing PHP");
			lang = lf.createLanguage("php");
		}
		else if (fileType.equals("cpp"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing C/C++");
			lang = lf.createLanguage("cpp");
		}
		else
		{
			JOptionPane.showMessageDialog(view,"Copy/Paste detection can only be performed on Java,C/C++,PHP code.","Copy/Paste Detector",JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		return new CPD(tileSize,lang);
	}

	private CPD getCPD(String fileType, View view)
	{
		return getCPD(jEdit.getIntegerProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY,100), fileType, view);
	}
}


