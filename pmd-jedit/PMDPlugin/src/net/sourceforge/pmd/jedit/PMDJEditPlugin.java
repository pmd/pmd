/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
 */
package net.sourceforge.pmd.jedit;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.border.EtchedBorder;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPPLanguage;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.PHPLanguage;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.renderers.*;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleViolation;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;


public class PMDJEditPlugin extends EBPlugin
{

	public static final String NAME = "PMD";
	public static final String OPTION_RULES_PREFIX = "options.pmd.rules.";
	public static final String DEFAULT_TILE_MINSIZE_PROPERTY = "pmd.cpd.defMinTileSize";
	public static final String RUN_PMD_ON_SAVE = "pmd.runPMDOnSave";
	public static final String CUSTOM_RULES_PATH_KEY = "pmd.customRulesPath";
	public static final String SHOW_PROGRESS = "pmd.showprogress";
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
		if (ebmess instanceof BufferUpdate)
		{
			if(jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE))
			{
				BufferUpdate bu = (BufferUpdate)ebmess;

				if (bu.getWhat() == BufferUpdate.SAVED)
				{
					if(bu.getBuffer().getMode().getName().equals("java"))
					{
						check(bu.getBuffer(),bu.getView());
					}
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
		JFileChooser chooser = new JFileChooser(jEdit.getProperty("pmd.cpd.lastDirectory"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel pnlAccessory = new JPanel();
		JCheckBox chkRecursive = new JCheckBox("Recursive", false);
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
					JOptionPane.showMessageDialog(view,"Selection not a directory.","PMD",JOptionPane.ERROR_MESSAGE);
					return;
				}

				jEdit.setProperty("pmd.cpd.lastDirectory",selectedFile.getCanonicalPath());
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
			SelectedRules selectedRuleSets = new SelectedRules();
			RuleContext ctx = new RuleContext();
			ctx.setReport(new Report());
			ctx.setSourceCodeFilename(buffer.getPath());

			VFS vfs = buffer.getVFS();

			pmd.processFile(vfs._createInputStream(vfs.createVFSSession(buffer.getPath(),view),buffer.getPath(),false,view), selectedRuleSets.getSelectedRules(), ctx);

			if (ctx.getReport().isEmpty())
			{
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
				errorSource.clear();
			}

			else
			{
				String path = buffer.getPath();

				for (Iterator i = ctx.getReport().iterator(); i.hasNext();)
				{
					RuleViolation rv = (RuleViolation)i.next();
					errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getLine()-1,0,0,rv.getDescription()));
				}

				registerErrorSource();
			}
		}

		catch (RuleSetNotFoundException rsne)
		{
			rsne.printStackTrace();
		}

		catch (PMDException pmde)
		{
			pmde.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "Error while processing " + buffer.getPath());
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}// check current buffer

	public void process(final List files, final View view)
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

	void processFiles(List files, View view)
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
			System.out.println("PMD ERROR: Couldn't find a ruleset");
			rsne.printStackTrace();
			JOptionPane.showMessageDialog(jEdit.getFirstView(), "Unable to find rulesets, halting PMD", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		RuleContext ctx = new RuleContext();


		boolean foundProblems = false;
		Report reports[] = new Report[files.size()];

		Iterator iter = files.iterator();
		for (int i=0;iter.hasNext();i++)
		{
			File file = (File)iter.next();
			//ctx.setReport(new Report());
			reports[i] = new Report(); //Store the reference to Reports generated since we will need it for Rendering output.
			ctx.setReport(reports[i]);
			ctx.setSourceCodeFilename(file.getAbsolutePath());

			try
			{
				pmd.processFile(new FileInputStream(file), selectedRuleSets.getSelectedRules(), ctx);
			}
			catch (FileNotFoundException fnfe)
			{
				// should never happen, but if it does, carry on to the next file
				System.out.println("PMD ERROR: Unable to open file " + file.getAbsolutePath());
			}
			catch (PMDException pmde)
			{
				pmde.printStackTrace();
				JOptionPane.showMessageDialog(jEdit.getFirstView(), "Error while processing " + file.getAbsolutePath());
			}

			if(jEdit.getBooleanProperty(SHOW_PROGRESS))
			{
				pbd.increment(1);
			}

			for (Iterator j = ctx.getReport().iterator(); j.hasNext();)
			{
				foundProblems = true;
				RuleViolation rv = (RuleViolation)j.next();
				errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.ERROR,  file.getAbsolutePath(), rv.getLine()-1,0,0,rv.getDescription()));
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
			exportErrorAsReport(view, reports);
		}

		endProgressBarDisplay(pbd);
		pbd = null;
	}

	private List findFiles(String dir, boolean recurse)
	{
		FileFinder f  = new FileFinder();
		return f.findFilesFrom(dir, new JavaLanguage.JavaFileOrDirectoryFilter(), recurse);
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
		VFS.DirectoryEntry selectedFile[] = browser.getSelectedFiles();

		if(selectedFile == null || selectedFile.length == 0)
		{
			JOptionPane.showMessageDialog(view, "One file must be selected", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(selectedFile[0].type == VFS.DirectoryEntry.DIRECTORY)
		{
			JOptionPane.showMessageDialog(view, "Selected file cannot be a Directory.", NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		instance.instanceCPDCurrentFile(view, selectedFile[0].path, getFileType(selectedFile[0].path));
	}

	//TODO: Replace this method with a smart file type/mode detector.
	private static String getFileType(String name)
	{
		if(name != null)
		{
			if(name.endsWith("java"))
			{
				return "java";
			}
			else if(name.endsWith("php"))
			{
				return "php";
			}
			else if(name.endsWith("c") || name.endsWith("cpp") || name.endsWith("c++"))
			{
				return "c";
			}
		}
		return null;
	}

	private void instanceCPDCurrentFile(View view, String filename, String fileType) throws IOException
	{
		CPD cpd = null;
		//Log.log(Log.DEBUG, PMDJEditPlugin.class , "See mode " + view.getBuffer().getMode().getName());

		if (fileType.equals("java"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing java");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new JavaLanguage());
		}
		else if (fileType.equals("php"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing PHP");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new PHPLanguage());
		}
		else if (fileType.equals("c"))
		{
			//Log.log(Log.DEBUG, PMDJEditPlugin.class, "Doing C/C++");
			cpd = new CPD(jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100),new CPPLanguage());
		}
		else
		{
			JOptionPane.showMessageDialog(view,"Copy/Paste detection can only be performed on Java,C/C++,PHP code.","Copy/Paste Detector",JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		cpd.add(new File(filename));
		cpd.go();
		instance.processDuplicates(cpd, view);
		view.getDockableWindowManager().showDockableWindow("cpd-viewer");
	}


	public static void cpdDir(View view)
	{
		JFileChooser chooser = new JFileChooser(jEdit.getProperty("pmd.cpd.lastDirectory"));
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
		JCheckBox chkRecursive = new JCheckBox("Recursive", false);
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
				JOptionPane.showMessageDialog(view,"Selection not a directory.","PMD",JOptionPane.ERROR_MESSAGE);
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
				instance.instanceCPDDir(view, selectedFile.getCanonicalPath(), tilesize, chkRecursive.isSelected());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void cpdDir(View view, VFSBrowser browser, boolean recursive) throws IOException
	{
		if(view != null && browser != null)
		{
			VFS.DirectoryEntry selectedDir[] = browser.getSelectedFiles();
			if(selectedDir == null || selectedDir.length == 0)
			{
				JOptionPane.showMessageDialog(view, "One Directory has to be selected in which to detect duplicate code.", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			if(selectedDir[0].type != VFS.DirectoryEntry.DIRECTORY)
			{
				JOptionPane.showMessageDialog(view, "Selected file must be a Directory.", NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			instance.instanceCPDDir(view, selectedDir[0].path, jEdit.getIntegerProperty(DEFAULT_TILE_MINSIZE_PROPERTY,100), recursive);
		}
	}

	private void instanceCPDDir(View view, String dir, int tileSize, boolean recursive) throws IOException
	{
		if(dir != null)
		{
			jEdit.setProperty("pmd.cpd.lastDirectory",dir);
			instance.errorSource.clear();

			CPD cpd = new CPD(tileSize, new JavaLanguage());

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

			CPDDuplicateCodeViewer.Duplicates duplicates = dv.new Duplicates(match.getLineCount() + " duplicate lines", match.getSourceCodeSlice());

			for (Iterator occurrences = match.iterator(); occurrences.hasNext();)
			{
				TokenEntry mark = (TokenEntry)occurrences.next();

				//System.out.println("Begin line " + mark.getBeginLine() +" of file "+ mark.getTokenSrcID() +" Line Count "+ match.getLineCount());
				int lastLine = mark.getBeginLine()+match.getLineCount();

				CPDDuplicateCodeViewer.Duplicate duplicate =  dv.new Duplicate(mark.getTokenSrcID(),mark.getBeginLine(),lastLine);

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

			process(files, view);
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

		instance.process(instance.findFiles(de[0].path, recursive),view);
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
			Renderer renderer = null;

			if (format.equals("XML"))
			{
				renderer = new XMLRenderer();
			}
			else if (format.equals("Html"))
			{
				renderer = new HTMLRenderer();
			}
			else if (format.equals("CSV"))
			{
				renderer = new CSVRenderer();
			}
			else if (format.equals("Text"))
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
				String output = null;
				for(int i=0;i<reports.length;i++)
				{
					output = renderer.render(reports[i]);
					strbuf.append(output);
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


}


