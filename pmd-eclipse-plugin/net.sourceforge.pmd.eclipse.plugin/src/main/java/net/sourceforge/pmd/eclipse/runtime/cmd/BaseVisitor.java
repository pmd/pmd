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
import java.io.File;
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
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;
import net.sourceforge.pmd.eclipse.util.IOUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.NumericConstants;
import net.sourceforge.pmd.util.StringUtil;

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
    private Map<IFile, Set<MarkerInfo2>> accumulator;
//    private PMDEngine pmdEngine;
    private RuleSet ruleSet;
    private int fileCount;
    private long pmdDuration;
    private IProjectProperties projectProperties;
    protected RuleSet hiddenRules;

    private PMDConfiguration configuration;
    
    
    protected PMDConfiguration configuration() {
    	if (configuration == null) configuration = new PMDConfiguration();
    	return configuration;
    }
    /**
     * The constructor is protected to avoid illegal instantiation
     *
     */
    protected BaseVisitor() {
        super();

        hiddenRules = new RuleSet();
    }

    /**
     * Returns the useTaskMarker.
     *
     * @return boolean
     */
    public boolean isUseTaskMarker() {
        return useTaskMarker;
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
    public Map<IFile, Set<MarkerInfo2>> getAccumulator() {
        return accumulator;
    }

    /**
     * Sets the accumulator.
     *
     * @param accumulator
     *            The accumulator to set
     */
    public void setAccumulator(final Map<IFile, Set<MarkerInfo2>> accumulator) {
        this.accumulator = accumulator;
    }

    /**
     * @return
     */
    public IProgressMonitor getMonitor() {
        return monitor;
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

//    /**
//     * @return Returns the pmdEngine.
//     */
//    public PMDEngine getPmdEngine() {
//        return pmdEngine;
//    }
//
//    /**
//     * @param pmdEngine
//     *            The pmdEngine to set.
//     */
//    public void setPmdEngine(final PMDEngine pmdEngine) {
//        this.pmdEngine = pmdEngine;
//    }

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
        return fileCount;
    }

    /**
     * @return actual PMD duration
     */
    public long getActualPmdDuration() {
        return pmdDuration;
    }

    /**
     * Set the project properties (note that visitor is expected to be called one project at a time
     */
    public void setProjectProperties(IProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    private boolean isIncluded(IFile file) throws PropertiesException {
    	return projectProperties.isIncludeDerivedFiles() || !projectProperties.isIncludeDerivedFiles() && !file.isDerived();
    }
    
    /**
     * Run PMD against a resource
     *
     * @param resource
     *            the resource to process
     */
    protected final void reviewResource(IResource resource) {

    	IFile file = (IFile) resource.getAdapter(IFile.class);
    	if (file == null || file.getFileExtension() == null) return;

    	Reader input = null;
    	try {
    		boolean included = isIncluded(file);
    		log.debug("Derived files included: " + projectProperties.isIncludeDerivedFiles());
    		log.debug("file " + file.getName() + " is derived: " + file.isDerived());
    		log.debug("file checked: " + included);

    		final File sourceCodeFile = file.getRawLocation().toFile();
    		if (included && getRuleSet().applies(sourceCodeFile) && isFileInWorkingSet(file)) {
    			subTask("PMD checking: " + file.getName());

    			setLanguageVersion(file);

    			Timer timer = new Timer();

    			RuleContext context = PMD.newRuleContext(file.getName(), sourceCodeFile);

    			input = new InputStreamReader(file.getContents(), file.getCharset());
    			//                    getPmdEngine().processFile(input, getRuleSet(), context);
    			//                    getPmdEngine().processFile(sourceCodeFile, getRuleSet(), context);

    			RuleSets rSets = new RuleSets(getRuleSet());
    			new SourceCodeProcessor(configuration()).processSourceCode(input, rSets, context);

    			timer.stop();
    			pmdDuration += timer.getDuration();

    			updateMarkers(file, context, isUseTaskMarker(), getAccumulator());

    			worked(1);
    			fileCount++;
    		} else {
    			log.debug("The file " + file.getName() + " is not in the working set");
    		}

    	} catch (CoreException e) {
    		log.error("Core exception visiting " + file.getName(), e); // TODO:		// complete message
    	} catch (PMDException e) {
    		log.error("PMD exception visiting " + file.getName(), e); // TODO: 		// complete message
    	} catch (IOException e) {
    		log.error("IO exception visiting " + file.getName(), e); // TODO: 		// complete message
    	} catch (PropertiesException e) {
    		log.error("Properties exception visiting " + file.getName(), e); // TODO:	// complete message
    	} finally {
    		IOUtil.closeQuietly(input);
    	}

    }

	private void setLanguageVersion(IFile file) {
		LanguageVersion version = PMDPlugin.javaVersionFor(file.getProject());
		if (version != null) configuration().setDefaultLanguageVersion(version);
	}

    /**
     * Test if a file is in the PMD working set
     *
     * @param file
     * @return true if the file should be checked
     */
    private boolean isFileInWorkingSet(final IFile file) throws PropertiesException {
        boolean fileInWorkingSet = true;
        IWorkingSet workingSet = projectProperties.getProjectWorkingSet();
        if (workingSet != null) {
            ResourceWorkingSetFilter filter = new ResourceWorkingSetFilter();
            filter.setWorkingSet(workingSet);
            fileInWorkingSet = filter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

    /**
     * Update markers list for the specified file
     *
     * @param file
     *            the file for which markers are to be updated
     * @param context
     *            a PMD context
     * @param fTask
     *            indicate if a task marker should be created
     * @param accumulator
     *            a map that contains impacted file and marker informations
     */
    
    private int maxAllowableViolationsFor(Rule rule) {
    	 
         return rule.hasDescriptor(PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR) ?
             rule.getProperty(PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR) :
             PMDRuntimeConstants.MAX_VIOLATIONS_DESCRIPTOR.defaultValue();
    }
    
    public static String markerTypeFor(RuleViolation violation) {

    	int priorityId = violation.getRule().getPriority().getPriority();

    	switch (priorityId) {
	    	case 1: return PMDRuntimeConstants.PMD_MARKER_1;
	    	case 2: return PMDRuntimeConstants.PMD_MARKER_2;
	    	case 3: return PMDRuntimeConstants.PMD_MARKER_3;
	    	case 4: return PMDRuntimeConstants.PMD_MARKER_4;
	    	case 5: return PMDRuntimeConstants.PMD_MARKER_5;
	    	default: return PMDRuntimeConstants.PMD_MARKER;
    	}
    }
    
    private void updateMarkers(IFile file, RuleContext context, boolean fTask, Map<IFile, Set<MarkerInfo2>> accumulator)
            throws CoreException, PropertiesException {
    	
        Set<MarkerInfo2> markerSet = new HashSet<MarkerInfo2>();
        List<Review> reviewsList = findReviewedViolations(file);
        Review review = new Review();
        Iterator<RuleViolation> iter = context.getReport().iterator();
//        final IPreferences preferences = PMDPlugin.getDefault().loadPreferences();
//        final int maxViolationsPerFilePerRule = preferences.getMaxViolationsPerFilePerRule();
        Map<Rule, Integer> violationsByRule = new HashMap<Rule, Integer>();

        Rule rule = null;
        while (iter.hasNext()) {
            RuleViolation violation = iter.next();
            rule = violation.getRule();
            review.ruleName = rule.getName();
            review.lineNumber = violation.getBeginLine();

            if (reviewsList.contains(review)) {
                log.debug("Ignoring violation of rule " + rule.getName() + " at line " + violation.getBeginLine() + " because of a review.");
                continue;
            	}

            Integer count = violationsByRule.get(rule);
            if (count == null) {
                count = NumericConstants.ZERO;
                violationsByRule.put(rule, count);
                }

            int maxViolations = maxAllowableViolationsFor(rule);

            if (count.intValue() < maxViolations) {
             	// Ryan Gustafson 02/16/2008 - Always use PMD_MARKER, as people get confused as to why PMD problems don't always show up on Problems view like they do when you do build.
                // markerSet.add(getMarkerInfo(violation, fTask ? PMDRuntimeConstants.PMD_TASKMARKER : PMDRuntimeConstants.PMD_MARKER));
                markerSet.add(
                		getMarkerInfo(violation, markerTypeFor(violation))
                		);
                   /*
                   if (isDfaEnabled && violation.getRule().usesDFA()) {
                       markerSet.add(getMarkerInfo(violation, PMDRuntimeConstants.PMD_DFA_MARKER));
                   } else {
                        markerSet.add(getMarkerInfo(violation, fTask ? PMDRuntimeConstants.PMD_TASKMARKER : PMDRuntimeConstants.PMD_MARKER));
                   }
                    */
               violationsByRule.put(rule, Integer.valueOf(count.intValue() + 1));

               log.debug("Adding a violation for rule " + rule.getName() + " at line " + violation.getBeginLine());
                } else {
                    log.debug("Ignoring violation of rule " + rule.getName() + " at line " + violation.getBeginLine()
                            + " because maximum violations has been reached for file " + file.getName());
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
        final List<Review> reviews = new ArrayList<Review>();
        BufferedReader reader = null;
        try {
            int lineNumber = 0;
            boolean findLine = false;
            boolean comment = false;
            final Stack<String> pendingReviews = new Stack<String>();
            reader = new BufferedReader(new InputStreamReader(file.getContents()));
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
                        final String tail = line.substring(PMDRuntimeConstants.PLUGIN_STYLE_REVIEW_COMMENT.length());
                        final String ruleName = tail.substring(0, tail.indexOf(':'));
                        pendingReviews.push(ruleName);
                        findLine = true;
                    } else if (!comment && findLine && StringUtil.isNotEmpty(line) && !line.startsWith("//")) {
                        findLine = false;
                        while (!pendingReviews.empty()) {
                            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops:
                            // by Herlin on 01/05/05 18:36
                            final Review review = new Review();
                            review.ruleName = pendingReviews.pop();
                            review.lineNumber = lineNumber;
                            reviews.add(review);
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
        } finally {
        	IOUtil.closeQuietly(reader);
        }

        return reviews;
    }

    private MarkerInfo2 getMarkerInfo(RuleViolation violation, String type) throws PropertiesException {
        
    	Rule rule = violation.getRule();
    	
    	MarkerInfo2 info = new MarkerInfo2(type, 7);

    	info.add(IMarker.MESSAGE, violation.getDescription());
    	info.add(IMarker.LINE_NUMBER, violation.getBeginLine());
    	info.add(PMDRuntimeConstants.KEY_MARKERATT_LINE2, violation.getEndLine());
    	info.add(PMDRuntimeConstants.KEY_MARKERATT_RULENAME, rule.getName());
    	info.add(PMDRuntimeConstants.KEY_MARKERATT_PRIORITY ,rule.getPriority().getPriority());

        switch (rule.getPriority().getPriority()) {
        case 1:
        	info.add(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        	info.add(IMarker.SEVERITY, projectProperties.violationsAsErrors() ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
            break;
        case 2:           
            if (projectProperties.violationsAsErrors()) {
            	info.add(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            } else {
            	info.add(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);                
            	info.add(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            }
            break;

        case 5:
        	info.add(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
            break;

        case 3:
        	info.add(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        case 4:
        default:
        	info.add(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            break;
        }
        
        return info;
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
                Review reviewObj = (Review) obj;
                result = ruleName.equals(reviewObj.ruleName) && lineNumber == reviewObj.lineNumber;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return ruleName.hashCode() + lineNumber * lineNumber;
        }

    }
}
