/*
 * @(#)PMDPlugin.java $Revision$ ($Date$)
 * Copyright (c) 2004
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.gel;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.ProgressMonitor;

import com.gexperts.gel.Gel;
import com.gexperts.gel.GelAction;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.stat.Metric;


/**
 * PMD plugin for Gel.
 *
 * @author Andrey Lumyanski
 * @version $Revision$ ($Date$)
 */
public class PMDPlugin implements GelAction, Runnable, ReportListener {
	private Gel gel;
	private Report report;
	private ArrayList listOfFiles;
	private ProgressMonitor progressMonitor;
	private RuleSet arrayRuleSet[];

	/**
	 * Creates a <code>PMDPlugin</code> object.
	 */
	public PMDPlugin() {
		super();
		gel = null;
		report = null;
		listOfFiles = null;
		progressMonitor = null;
		arrayRuleSet = null;
	}

	/**
	 * Returns <code>true</code> if the plugin is active.
	 *
	 * @param gel a <code>Gel</code> instance.
	 *
	 * @return <code>true</code> if the plugin is active.
	 */
	public boolean isActive(Gel gel) {
		return gel.getEditor() != null;
	}

	/**
	 * Performs action.
	 *
	 * @param gel Gel a <code>Gel</code> instance.
	 */
	public void perform(Gel gel) {
		this.gel = gel;
		report = new Report();
		report.addListener(this);
		gel.clearMessages();
		if (gel.getProject() == null && gel.getEditor() == null) {
			gel.showMessage("There are no java files to analyse!");
			return;
		}
		gel.addMessage("PMD started");

		try {
			RuleSetFactory rsf = new RuleSetFactory();
			Properties props = new Properties();
			props.load(rsf.getClass().getResourceAsStream("/rulesets/rulesets.properties"));
			String rulesetFilenames = props.getProperty("rulesets.filenames");
			ArrayList listOfRuleSet = new ArrayList();
			StringTokenizer st = new StringTokenizer(rulesetFilenames, ",");
			RuleSet ruleSet = null;
			String rsFileName = null;
			while (st.hasMoreTokens()) {
				rsFileName = st.nextToken();
				ruleSet = rsf.createRuleSet(rsFileName);
				ruleSet.setFileName(rsFileName);
				listOfRuleSet.add(ruleSet);
			}
			arrayRuleSet = new RuleSet[listOfRuleSet.size()];
			for (int i = 0; i < arrayRuleSet.length; ++i) {
				arrayRuleSet[i] = (RuleSet) listOfRuleSet.get(i);
			}

			RuleSetDialog dlgRuleSet = new RuleSetDialog(arrayRuleSet);
			dlgRuleSet.show();
			if (dlgRuleSet.isCanceled()) {
				gel.addMessage("PMD canceled.");
				return;
			}

			if (gel.getProject() != null) {
				listOfFiles = new ArrayList();

				Iterator it = gel.getProject().getSourcePaths().iterator();
				FileFinder ff = new FileFinder();
				FilenameFilter filter =
					new JavaLanguage.JavaFileOrDirectoryFilter();

				while (it.hasNext()) {
					String srcDir = (String) it.next();
					List files = ff.findFilesFrom(srcDir, filter, true);

					if (files != null) {
						listOfFiles.addAll(files);
					}
				}
				if (listOfFiles.isEmpty()) {
					if (gel.getEditor() != null) {
						gel.addMessage("WARNING: There are no java files in project source paths. Try to analyse current data in editor.");
					} else {
						gel.addMessage("WARNING: There are no java files to analyse.");
						gel.addMessage("PMD finished");
						return;
					}
				}
			} else {
				String name = gel.getEditor().getFileName();

				if (name == null) {
					name = "Untitled";
				}

				listOfFiles.add(new File(name));
			}

			if (listOfFiles != null) {
				progressMonitor =
					new ProgressMonitor(null, "PMD", "", 0, listOfFiles.size());
			}
		} catch (Exception e) {
			StringBuffer msg = new StringBuffer();
			msg.append("PMD error:");
			msg.append(e.getClass().getName());
			msg.append(":");
			msg.append(e.getLocalizedMessage());
			gel.showMessage(msg.toString());
		}

		Thread threadPMD = new Thread(this);
		threadPMD.start();
	}

	/**
	 * Returns a plugin's name.
	 *
	 * @return a plugin's name.
	 */
	public String getName() {
		return "PMD";
	}

	/**
	 * A <code>Runnable</code> interface implementation.
	 */
	public void run() {
		try {
			PMD pmd = new PMD();
			RuleContext ctx = new RuleContext();
			RuleSet ruleSet = new RuleSet();
			Iterator it;
			Rule rule;
			for (int i = 0; i < arrayRuleSet.length; ++i) {
				if (arrayRuleSet[i].include()) {
					it = arrayRuleSet[i].getRules().iterator();
					while (it.hasNext()) {
						rule = (Rule) it.next();
						if (rule.include()) {
							ruleSet.addRule(rule);
						}
					}
				}
			}
			if (ruleSet.size() > 0) {
				ctx.setReport(report);

				if (gel.getProject() == null || listOfFiles.isEmpty()) {
					String code = gel.getEditor().getContents();
					String name = gel.getEditor().getFileName();

					if (name == null) {
						name = "Untitled";
					}

					ctx.setSourceCodeFilename(name);
					progressMonitor.setNote(name);
					Reader reader = new StringReader(code);
					pmd.processFile(reader, ruleSet, ctx);
					progressMonitor.setProgress(1);
				} else {
					it = listOfFiles.iterator();
					int index = 0;

					while (it.hasNext()) {
						File file = (File) it.next();
						ctx.setSourceCodeFilename(file.getAbsolutePath());
						++index;
						progressMonitor.setNote(file.getAbsolutePath());
						Reader reader = new FileReader(file);
						pmd.processFile(reader, ruleSet, ctx);
						progressMonitor.setProgress(index);
						if (progressMonitor.isCanceled()) {
							gel.addMessage("PMD canceled.");
							return;
						}
					}
				}

				if (report.isEmpty()) {
					gel.addMessage("No problems found");
				} else {
					StringBuffer msg = new StringBuffer();
					msg.append(report.size());
					msg.append(" problems found");
					gel.addMessage(msg.toString());
				}
			} else {
				gel.addMessage("It is not chosen any rule!");
			}
		} catch (Exception e) {
			StringBuffer msg = new StringBuffer();
			msg.append("PMD error:");
			msg.append(e.getClass().getName());
			msg.append(":");
			msg.append(e.getLocalizedMessage());
			gel.showMessage(msg.toString());
		}

		gel.addMessage("PMD finished");
	}

	/**
	 * Processes a <em>RuleViolation</em> adding.
	 *
	 * @param rv a <em>RuleViolation</em>
	 */
	public void ruleViolationAdded(RuleViolation rv) {
		StringBuffer msg = new StringBuffer();
		msg.append("[");
		msg.append(rv.getRule().getName());
		msg.append("]: ");
		msg.append(rv.getFilename());
		msg.append(":");
		msg.append(rv.getLine());
		msg.append(": ");
		msg.append(rv.getDescription());
		gel.addMessage(msg.toString());
	}

	/**
	 * Processes a <em>Metric</em> adding.
	 *
	 * @param metric a<em>Metric</em>
	 */
	public void metricAdded(Metric metric) {
	}
}