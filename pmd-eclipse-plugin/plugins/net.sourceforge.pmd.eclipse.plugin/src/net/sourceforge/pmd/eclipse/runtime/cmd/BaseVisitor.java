/*
 * <copyright>
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 *
 * </copyright>
 */
package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import name.herlin.command.Timer;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.ResourceWorkingSetFilter;

/**
 * Factor some useful features for visitors
 *
 * @author Philippe Herlin
 *
 */
public class BaseVisitor {
    private static final Logger log = Logger.getLogger(BaseVisitor.class);
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;
    private Map<IFile, Set<MarkerInfo>> accumulator;
    private PMDEngine pmdEngine;
    private RuleSet ruleSet;
    private int filesCount;
    private long pmdDuration;
    private IProjectProperties projectProperties;
    protected RuleSet hiddenRules;

    /**
     * The constructor is protected to avoid illegal instantiation
     *
     */
    protected BaseVisitor() {
        super();

        this.hiddenRules = new RuleSet();
    }

    /**
     * Returns the useTaskMarker.
     *
     * @return boolean
     */
    public boolean isUseTaskMarker() {
        return this.useTaskMarker;
    }

    /**
     * Sets the useTaskMarker.
     *
     * @param useTaskMarker
     *            The useTaskMarker to set
     */
    public void setUseTaskMarker(final boolean useTaskMarker) {
        this.useTaskMarker = useTaskMarker;
    }

    /**
     * Returns the accumulator.
     *
     * @return Map
     */
    public Map<IFile, Set<MarkerInfo>> getAccumulator() {
        return this.accumulator;
    }

    /**
     * Sets the accumulator.
     *
     * @param accumulator
     *            The accumulator to set
     */
    public void setAccumulator(final Map<IFile, Set<MarkerInfo>> accumulator) {
        this.accumulator = accumulator;
    }

    /**
     * @return
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }

    /**
     * @param monitor
     */
    public void setMonitor(final IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Tell whether the user has required to cancel the operation
     *
     * @return
     */
    public boolean isCanceled() {
        return getMonitor() == null ? false : getMonitor().isCanceled();
    }

    /**
     * Begin a subtask
     *
     * @param name
     *            the task name
     */
    public void subTask(final String name) {
        if (getMonitor() != null) {
            getMonitor().subTask(name);
        }
    }

    /**
     * Inform of the work progress
     *
     * @param work
     */
    public void worked(final int work) {
        if (getMonitor() != null) {
            getMonitor().worked(work);
        }
    }

    /**
     * @return Returns the pmdEngine.
     */
    public PMDEngine getPmdEngine() {
        return this.pmdEngine;
    }

    /**
     * @param pmdEngine
     *            The pmdEngine to set.
     */
    public void setPmdEngine(final PMDEngine pmdEngine) {
        this.pmdEngine = pmdEngine;
    }

    /**
     * @return Returns the ruleSet.
     */
    public RuleSet getRuleSet() {
        return this.ruleSet;
    }

    /**
     * @param ruleSet
     *            The ruleSet to set.
     */
    public void setRuleSet(final RuleSet ruleSet) {
        ruleSet.addRuleSet(hiddenRules);
        this.ruleSet = ruleSet;
    }

    /**
     * @return the number of files that has been processed
     */
    public int getProcessedFilesCount() {
        return this.filesCount;
    }

    /**
     * @return actual PMD duration
     */
    public long getActualPmdDuration() {
        return this.pmdDuration;
    }

    /**
     * Set the project properties (note that visitor is expected to be called one project at a time
     */
    public void setProjectProperties(IProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    /**
     * Run PMD against a resource
     *
     * @param resource
     *            the resource to process
     */
    protected final void reviewResource(final IResource resource) {
        final IFile file = (IFile) resource.getAdapter(IFile.class);
        if (file != null && file.getFileExtension() != null && file.getFileExtension().equals("java")) {

            try {
                boolean included = this.projectProperties.isIncludeDerivedFiles() || !this.projectProperties.isIncludeDerivedFiles() && !file.isDerived();
                log.debug("Derived files included: " + this.projectProperties.isIncludeDerivedFiles());
                log.debug("file " + file.getName() + " is derived: " + file.isDerived());
                log.debug("file checked: " + included);

                if (isFileInWorkingSet(file) && (this.projectProperties.isIncludeDerivedFiles() || !this.projectProperties.isIncludeDerivedFiles() && !file.isDerived())) {
                    subTask("PMD Checking file " + file.getName());

                    Timer timer = new Timer();

                    final RuleContext context = new RuleContext();
                    context.setSourceCodeFile(file.getRawLocation().toFile());
                    context.setSourceCodeFilename(file.getName());
                    context.setReport(new Report());

                    final Reader input = new InputStreamReader(file.getContents(), file.getCharset());
                    getPmdEngine().processFile(input, getRuleSet(), context);
                    input.close();

                    timer.stop();
                    this.pmdDuration += timer.getDuration();

                    updateMarkers(file, context, isUseTaskMarker(), getAccumulator());

                    worked(1);
                    this.filesCount++;
                } else {
                    log.debug("The file " + file.getName() + " is not in the working set");
                }

            } catch (CoreException e) {
                log.error("Core exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (PMDException e) {
                log.error("PMD exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (IOException e) {
                log.error("IO exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (PropertiesException e) {
                log.error("Properties exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            }

        }
    }

    /**
     * Test if a file is in the PMD working set
     *
     * @param file
     * @return true if the file should be checked
     */
    private boolean isFileInWorkingSet(final IFile file) throws PropertiesException {
        boolean fileInWorkingSet = true;
        final IWorkingSet workingSet = this.projectProperties.getProjectWorkingSet();
        if (workingSet != null) {
            final ResourceWorkingSetFilter filter = new ResourceWorkingSetFilter();
            filter.setWorkingSet(workingSet);
            fileInWorkingSet = filter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

    /**
     * Update markers list for the specified file
     *
     * @param file
     *            the file for which markes are to be updated
     * @param context
     *            a PMD context
     * @param fTask
     *            indicate if a task marker should be created
     * @param accumulator
     *            a map that contains impacted file and marker informations
     */
    private void updateMarkers(final IFile file, final RuleContext context, final boolean fTask, final Map<IFile, Set<MarkerInfo>> accumulator)
            throws CoreException {
        final Set<MarkerInfo> markerSet = new HashSet<MarkerInfo>();
        final List<Review> reviewsList = findReviewedViolations(file);
        final Review review = new Review();
        final Iterator<RuleViolation> iter = context.getReport().iterator();
        final IPreferences preferences = PMDPlugin.getDefault().loadPreferences();
//        final int maxViolationsPerFilePerRule = preferences.getMaxViolationsPerFilePerRule();
        final Map<Rule, Integer> violationsCounter = new HashMap<Rule, Integer>();

        while (iter.hasNext()) {
            final RuleViolation violation = iter.next();
            review.ruleName = violation.getRule().getName();
            review.lineNumber = violation.getBeginLine();

            if (reviewsList.contains(review)) {
                log.debug("Ignoring violation of rule " + violation.getRule().getName() + " at line " + violation.getBeginLine()
                        + " because of a review.");
            } else {
                Integer counter = violationsCounter.get(violation.getRule());
                if (counter == null) {
                    counter = new Integer(0);
                    violationsCounter.put(violation.getRule(), counter);
                }

                int maxViolations = violation.getRule().getProperty(PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR);

                if (counter.intValue() < maxViolations) {
                	// Ryan Gustafson 02/16/2008 - Always use PMD_MARKER, as people get confused as to why PMD problems don't always show up on Problems view like they do when you do build.
                    // markerSet.add(getMarkerInfo(violation, fTask ? PMDRuntimeConstants.PMD_TASKMARKER : PMDRuntimeConstants.PMD_MARKER));
                    markerSet.add(getMarkerInfo(violation, PMDRuntimeConstants.PMD_MARKER));
                    /*
                    if (isDfaEnabled && violation.getRule().usesDFA()) {
                        markerSet.add(getMarkerInfo(violation, PMDRuntimeConstants.PMD_DFA_MARKER));
                    } else {
                        markerSet.add(getMarkerInfo(violation, fTask ? PMDRuntimeConstants.PMD_TASKMARKER : PMDRuntimeConstants.PMD_MARKER));
                    }
                    */
                    violationsCounter.put(violation.getRule(), new Integer(counter.intValue() + 1));

                    log.debug("Adding a violation for rule " + violation.getRule().getName() + " at line " + violation.getBeginLine());
                } else {
                    log.debug("Ignoring violation of rule " + violation.getRule().getName() + " at line " + violation.getBeginLine()
                            + " because maximum violations has been reached for file " + file.getName());
                }

            }
        }

        if (accumulator != null) {
            log.debug("Adding markerSet to accumulator for file " + file.getName());
            accumulator.put(file, markerSet);
        }
    }

    /**
     * Search for reviewed violations in that file
     *
     * @param file
     */
    private List<Review> findReviewedViolations(final IFile file) {
        final List<Review> reviewsList = new ArrayList<Review>();
        try {
            int lineNumber = 0;
            boolean findLine = false;
            boolean comment = false;
            final Stack<String> pendingReviews = new Stack<String>();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line != null) {
                    line = line.trim();
                    lineNumber++;
                    if (line.startsWith("/*")) {
                        comment = line.indexOf("*/") == -1;
                    } else if (comment && line.indexOf("*/") != -1) {
                        comment = false;
                    } else if (!comment && line.startsWith(PMDRuntimeConstants.PLUGIN_STYLE_REVIEW_COMMENT)) {
                        final String tail = line.substring(17);
                        final String ruleName = tail.substring(0, tail.indexOf(':'));
                        pendingReviews.push(ruleName);
                        findLine = true;
                    } else if (!comment && findLine && !line.equals("") && !line.startsWith("//")) {
                        findLine = false;
                        while (!pendingReviews.empty()) {
                            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops:
                            // by Herlin on 01/05/05 18:36
                            final Review review = new Review();
                            review.ruleName = pendingReviews.pop();
                            review.lineNumber = lineNumber;
                            reviewsList.add(review);
                        }
                    }
                }
            }

            // if (log.isDebugEnabled()) {
            // for (int i = 0; i < reviewsList.size(); i++) {
            // final Review review = (Review) reviewsList.get(i);
            // log.debug("Review : rule " + review.ruleName + ", line " +
            // review.lineNumber);
            // }
            // }

        } catch (CoreException e) {
            PMDPlugin.getDefault().logError("Core Exception when searching reviewed violations", e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError("IO Exception when searching reviewed violations", e);
        }

        return reviewsList;
    }

    /**
     * Create a marker info object from a violation
     *
     * @param violation
     *            a PMD violation
     * @param type
     *            a marker type
     * @return markerInfo a markerInfo object
     */
    private MarkerInfo getMarkerInfo(final RuleViolation violation, final String type) {
        final MarkerInfo markerInfo = new MarkerInfo();

        markerInfo.setType(type);

        final List<String> attributeNames = new ArrayList<String>();
        final List<Object> values = new ArrayList<Object>();

        attributeNames.add(IMarker.MESSAGE);
        values.add(violation.getDescription());

        attributeNames.add(IMarker.LINE_NUMBER);
        values.add(new Integer(violation.getBeginLine()));

        attributeNames.add(PMDRuntimeConstants.KEY_MARKERATT_LINE2);
        values.add(new Integer(violation.getEndLine()));

        attributeNames.add(PMDRuntimeConstants.KEY_MARKERATT_RULENAME);
        values.add(violation.getRule().getName());

        attributeNames.add(PMDRuntimeConstants.KEY_MARKERATT_PRIORITY);
        values.add(new Integer(violation.getRule().getPriority().getPriority()));

        switch (violation.getRule().getPriority().getPriority()) {
        case 1:
            attributeNames.add(IMarker.PRIORITY);
            values.add(new Integer(IMarker.PRIORITY_HIGH));
        case 2:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_ERROR));
            break;

        case 5:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_INFO));
            break;

        case 3:
            attributeNames.add(IMarker.PRIORITY);
            values.add(new Integer(IMarker.PRIORITY_HIGH));
        case 4:
        default:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_WARNING));
            break;
        }

        markerInfo.setAttributeNames(attributeNames.toArray(new String[attributeNames.size()]));
        markerInfo.setAttributeValues(values.toArray());

        return markerInfo;
    }

    /**
     * Private inner type to handle reviews
     */
    private class Review {
        public String ruleName;
        public int lineNumber;

        @Override
        public boolean equals(final Object obj) {
            boolean result = false;
            if (obj instanceof Review) {
                final Review reviewObj = (Review) obj;
                result = this.ruleName.equals(reviewObj.ruleName) && this.lineNumber == reviewObj.lineNumber;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return ruleName.hashCode() + lineNumber * lineNumber;
        }

    }
}
