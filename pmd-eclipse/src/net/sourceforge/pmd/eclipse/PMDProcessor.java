package net.sourceforge.pmd.eclipse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            pmdEngine.processFile(input, PMDPlugin.getDefault().getRuleSetForResource(file, true), context);

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

        Iterator iter = context.getReport().iterator();
        while (iter.hasNext()) {
            RuleViolation violation = (RuleViolation) iter.next();
            markerSet.add(getMarkerInfo(violation, fTask ? PMDPlugin.PMD_TASKMARKER : PMDPlugin.PMD_MARKER));
            log.debug("Adding a violation " + violation);
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
}
