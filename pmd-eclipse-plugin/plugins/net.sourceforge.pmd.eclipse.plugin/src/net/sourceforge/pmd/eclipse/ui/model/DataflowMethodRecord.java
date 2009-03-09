/*
 * Created on 7 mai 2005
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.model;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;

/**
 * This class holds information for use with the dataflow view. It contains a
 * Java-Method and the corresponding PMD-Method (SimpleNode) and can return
 * Dataflow Anomalies for it.
 *
 * @author SebastianRaffel ( 07.06.2005 ), Philippe Herlin, Sven Jacob
 *
 */
public class DataflowMethodRecord {
    private final IMethod method;
    private final Node node;

    /**
     * Constructor
     *
     * @param javaMethod, the Method of the JavaModel
     * @param pmdMethod, the corresponding PMD-SimpleNode / ASTMethodDeclaration
     */
    public DataflowMethodRecord(IMethod javaMethod, Node pmdMethod) {
        if (javaMethod == null) {
            throw new IllegalArgumentException("javaMethod cannot be null");
        }

        if (pmdMethod == null) {
            throw new IllegalArgumentException("pmdMethod cannot be null");
        }

        this.method = javaMethod;
        this.node = pmdMethod;
    }

    /**
     * @return the PMD-Method
     */
    public Node getPMDMethod() {
        return this.node;
    }

    /**
     * @return the Java-Method
     */
    public IMethod getJavaMethod() {
        return this.method;
    }

    /**
     * @return the Resource (File) that contains this Method
     */
    public IResource getResource() {
        return this.method.getResource();
    }

    /**
     * Finds Dataflow-Anomalies for a Method
     *
     * @return a List of Anomalies
     */
    public IMarker[] getMarkers() {
        final List<IMarker> markers = new ArrayList<IMarker>();
        try {
            if (method.getResource().isAccessible()) {

                // we can only find Markers for a file
                // we use the DFA-Marker-ID set for Dataflow Anomalies
                final IMarker[] allMarkers = method.getResource().findMarkers(PMDRuntimeConstants.PMD_DFA_MARKER, true,
                        IResource.DEPTH_INFINITE);

                // we only want to get the Markers for this Method,
                // so we need to "extract" them from the whole List
                for (IMarker marker : allMarkers) {
                    // the Marker should have valid Information in it
                    // ... and we don't want it twice, so we check,
                    // if the Marker already exists
                    if (markerIsValid(marker) && !markerIsInList(marker, markers)) {
                        markers.add(marker);
                    }
                }
            }
        } catch (CoreException ce) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }

        // return the Arraylist-Markers as an IMarker-Array
        final IMarker[] markerArray = new IMarker[markers.size()];
        markers.toArray(markerArray);
        return markerArray;
    }

    /**
     * Returns a list of Attributes for a Dataflow Marker, (1.) the Error
     * Message, (2.) the beginning Line of the Error, (3.) the ending Line and
     * (4.) the Variable (Marker Attribute)
     *
     * @param marker
     * @return an Array of Attributes
     */
    private Object[] getMarkerAttributes(IMarker marker) {
        final List<Object> values = new ArrayList<Object>();

        // add Message, default ""
        values.add(marker.getAttribute(IMarker.MESSAGE, ""));

        // add the Lines, default 0
        // the default-Values help preventing an Exception
        int line1 = marker.getAttribute(IMarker.LINE_NUMBER, 0);
        int line2 = marker.getAttribute(PMDUiConstants.KEY_MARKERATT_LINE2, 0);

        // exchange the Lines if begin > end
        if (line2 < line1) {
            final int temp = line1;
            line1 = line2;
            line2 = temp;
        }

        values.add(Integer.valueOf(line1));
        values.add(Integer.valueOf(line2));

        // add the Variable
        values.add(marker.getAttribute(PMDUiConstants.KEY_MARKERATT_VARIABLE, ""));

        return values.toArray();
    }

    /**
     * Checks, if a Marker is valid, meaning that it (1.) is set for this Method
     * (between Begin and End-Line) and (2.) has a Variable and Message set
     *
     * @param marker
     * @return true if the Marker is valid, false otherwise
     */
    private boolean markerIsValid(IMarker marker) {
        boolean isValid = false;

        // get the Markers attributes
        final Object[] values = getMarkerAttributes(marker);
        final int line1 = ((Integer) values[1]).intValue();
        final int line2 = ((Integer) values[2]).intValue();

        // the Marker has to be in this Method
        if (line1 >= this.node.getBeginLine() && line2 <= this.node.getEndLine()) {
            isValid = true;
            for (int k = 0; k < values.length && isValid; k++) {

                // if it is a String, it has to be the Variable
                // or Message, which shouldn't be empty
                if (values[k] instanceof String && ((String) values[k]).equals("")) {
                    isValid = false;
                }

                // else it is one of the Lines (Line, Line2)
                // and they also should not be 0
                else if (values[k] instanceof Integer && ((Integer) values[k]).intValue() == 0) {
                    isValid = false;
                }
            }
        }

        // check the Values
        return isValid;
    }

    /**
     * Checks if a Marker is already in a List
     *
     * @param marker
     * @param list
     * @return true, is the marker exists in the list, false otherwise
     */
    private boolean markerIsInList(IMarker marker, List<IMarker> list) {
        boolean inList = false;

        if (list != null && !list.isEmpty()) {

            // here we can't simply compare Objects, because the Dataflow
            // Anomaly Calculation sets different Markers for the same Error

            // get the Markers Attributes and compare with all other Markers
            final Object[] markerAttr = getMarkerAttributes(marker);
            for (int i = 0; i < list.size() && !inList; i++) {
                // get the Marker from the List and its Attributes
                final Object[] listAttr = getMarkerAttributes(list.get(i));

                boolean markersAreEqual = true;
                for (int j = 0; j < listAttr.length; j++) {
                    // compare the String- and Integer-Values
                    if (markerAttr[j] instanceof String && !((String) markerAttr[j]).equalsIgnoreCase((String) listAttr[j])) {
                        markersAreEqual = false;
                    }

                    else if (markerAttr[j] instanceof Integer && !((Integer) markerAttr[j]).equals(listAttr[j])) {
                        markersAreEqual = false;
                    }
                }

                // markersAreEqual only stays true, when all Checks above fail
                // we need to do that to compare _all_ Attributes; if they all
                // are equal, the Marker exists, if not we check the next one
                if (markersAreEqual) {
                    inList = true;
                }
            }
        }

        return inList;
    }
}
