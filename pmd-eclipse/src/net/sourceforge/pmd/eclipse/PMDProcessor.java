package net.sourceforge.pmd.eclipse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.resources.MarkerInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * A class to process IFile resource against PMD
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.11  2003/09/29 22:38:49  phherlin
 * Adding and implementing "JDK13 compatibility" property.
 * Desactivated, waiting for PMD 1.2.2 to be released
 *
 * Revision 1.10  2003/08/14 16:10:41  phherlin
 * Implementing Review feature (RFE#787086)
 *
 * Revision 1.9  2003/07/07 19:27:52  phherlin
 * Making rules selectable from projects
 *
 * Revision 1.8  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.7  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 * Revision 1.6  2003/06/19 20:56:59  phherlin
 * Improve progress indicator accuracy
 *
 * Revision 1.5  2003/05/19 22:26:58  phherlin
 * Updating PMD engine to v1.05
 * Refactoring to improve performance
 *
 * Revision 1.4  2003/03/30 20:46:21  phherlin
 * Adding logging
 * Display error dialog in a thread safe way
 *
 * Revision 1.3  2003/03/27 22:11:09  phherlin
 * Fixing SWTException when PMD is processing a file with syntax error
 * (Thanks to Chris Grindstaff)
 *
 */
public class PMDProcessor {
    private static final PMDProcessor SELF = new PMDProcessor();
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDProcessor");
    private PMD pmdEngine;
    private PMD pmdEngineJdk13;

    /**
     * Default construtor
     */
    private PMDProcessor() {
        initialize();
    }

    /**
     * Return the processor instance
     */
    public static PMDProcessor getInstance() {
        return SELF;
    }

    /**
     * Process an IFile resource
     * @param file the IFile to process
     * @param fTask indicate if a task marker should be created
     * @param accumulator a map that contains impacted file and marker informations
     */
    public void run(IFile file, boolean fTask, Map accumulator) {
        log.info("Processing file " + file.getName());
        try {
            Reader input = new InputStreamReader(file.getContents());
            RuleContext context = new RuleContext();
            context.setSourceCodeFilename(file.getName());
            context.setReport(new Report());

            if (PMDPlugin.getDefault().isJdk13Enable(file.getProject())) {
                log.debug("Running in JDK13 compatibility mode");
                pmdEngineJdk13.processFile(input, PMDPlugin.getDefault().getRuleSetForResource(file, true), context);
            } else {
                pmdEngine.processFile(input, PMDPlugin.getDefault().getRuleSetForResource(file, true), context);
            }

            updateMarkers(file, context, fTask, accumulator);

        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (PMDException e) {
            log.warn(getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION));
            log.debug("", e);
        } finally {
            log.info("Processing done");
        }
    }

    /**
     * Update markers list for the specified file
     * @param file the file for which markes are to be updated
     * @param context a PMD context
     * @param fTask indicate if a task marker should be created
     * @param accumulator a map that contains impacted file and marker informations
     */
    private void updateMarkers(IFile file, RuleContext context, boolean fTask, Map accumulator) throws CoreException {
        Set markerSet = new HashSet();
        List reviewsList = findReviewedViolations(file);
        Review review = new Review();
        Iterator iter = context.getReport().iterator();
        while (iter.hasNext()) {
            RuleViolation violation = (RuleViolation) iter.next();
            review.ruleName = violation.getRule().getName();
            review.lineNumber = violation.getLine();

            if (!reviewsList.contains(review)) {
                markerSet.add(getMarkerInfo(violation, fTask ? PMDPlugin.PMD_TASKMARKER : PMDPlugin.PMD_MARKER));
                log.debug("Adding a violation " + violation);
            } else {
                log.debug(
                    "Ignoring violation of rule "
                        + violation.getRule().getName()
                        + " at line "
                        + violation.getLine()
                        + " because of a review.");
            }
        }

        if (accumulator != null) {
            log.debug("Adding markerSet to accumulator for file " + file);
            accumulator.put(file, markerSet);
        }
    }

    /**
     * Initialize the processor
     */
    private void initialize() {
        pmdEngine = new PMD();
        pmdEngineJdk13 = new PMD( /* new TargetJDK1_3() */ );
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }

    /**
     * Create a marker info object from a violation
     * @param violation a PMD violation
     * @param type a marker type
     * @return markerInfo a markerInfo object
     */
    private MarkerInfo getMarkerInfo(RuleViolation violation, String type) {
        MarkerInfo markerInfo = new MarkerInfo();

        markerInfo.setType(type);
        // markerInfo.setCreationTime(System.currentTimeMillis());

        List attributeNames = new ArrayList();
        List values = new ArrayList();

        attributeNames.add(IMarker.MESSAGE);
        values.add(violation.getDescription());

        attributeNames.add(IMarker.LINE_NUMBER);
        values.add(new Integer(violation.getLine()));

        attributeNames.add(PMDPlugin.KEY_MARKERATT_RULENAME);
        values.add(violation.getRule().getName());

        switch (violation.getRule().getPriority()) {
            case 1 :
                attributeNames.add(IMarker.PRIORITY);
                values.add(new Integer(IMarker.PRIORITY_HIGH));
            case 2 :
                attributeNames.add(IMarker.SEVERITY);
                values.add(new Integer(IMarker.SEVERITY_ERROR));
                break;

            case 5 :
                attributeNames.add(IMarker.SEVERITY);
                values.add(new Integer(IMarker.SEVERITY_INFO));
                break;

            case 3 :
                attributeNames.add(IMarker.PRIORITY);
                values.add(new Integer(IMarker.PRIORITY_HIGH));
            case 4 :
            default :
                attributeNames.add(IMarker.SEVERITY);
                values.add(new Integer(IMarker.SEVERITY_WARNING));
                break;
        }
        markerInfo.setAttributes((String[]) attributeNames.toArray(new String[attributeNames.size()]), values.toArray());

        return markerInfo;
    }

    /**
     * Search for reviewed violations in that file
     * @param file
     */
    private List findReviewedViolations(IFile file) {
        List reviewsList = new ArrayList();
        try {
            int lineNumber = 0;
            boolean findLine = false;
            boolean comment = false;
            Stack pendingReviews = new Stack();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
            while (reader.ready()) {
                String line = reader.readLine().trim();
                lineNumber++;
                if (line.startsWith("/*")) {
                    if (line.indexOf("*/") == -1) {
                        comment = true;
                    }
                } else if (comment && (line.indexOf("*/") != -1)) {
                    comment = false;
                } else if (!comment && line.startsWith(PMDPlugin.REVIEW_MARKER)) {
                    String tail = line.substring(17);
                    int index = tail.indexOf(':');
                    String ruleName = tail.substring(0, index);
                    pendingReviews.push(ruleName);
                    findLine = true;
                } else if (!comment && findLine) {
                    if (!line.equals("") && !line.startsWith("//")) {
                        findLine = false;
                        while (!pendingReviews.empty()) {
                            Review review = new Review();
                            review.ruleName = (String) pendingReviews.pop();
                            review.lineNumber = lineNumber;
                            reviewsList.add(review);
                        }
                    }
                }
            }

            if (log.isDebugEnabled()) {
                for (int i = 0; i < reviewsList.size(); i++) {
                    Review review = (Review) reviewsList.get(i);
                    log.debug("Review : rule " + review.ruleName + ", line " + review.lineNumber);
                }
            }

        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_IO_EXCEPTION, e);
        }

        return reviewsList;
    }

    /**
     * Private inner type to handle reviews
     */
    private class Review {
        public String ruleName;
        public int lineNumber;

        public boolean equals(Object obj) {
            boolean result = false;
            if (obj instanceof Review) {
                Review reviewObj = (Review) obj;
                result = (this.ruleName.equals(reviewObj.ruleName)) && (this.lineNumber == reviewObj.lineNumber);
            }
            return result;
        }

        public int hashCode() {
            return ruleName.hashCode() + lineNumber * lineNumber;
        }

    }

}
