package net.sourceforge.pmd.ui.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * PMDRecord for Files
 * 
 * @author SebastianRaffel ( 16.05.2005 )
 */
public class FileRecord extends PMDRecord {

    private IResource resource;
    private PackageRecord parent;

    /**
     * Constructor (not for use with the Model, no PackageRecord is provided here)
     * 
     * @param javaResource, the given File
     */
    public FileRecord(IResource javaResource) {
        resource = javaResource;
        parent = null;
    }

    /**
     * Constructor (for use with the Model)
     * 
     * @param javaResource
     * @param record
     */
    public FileRecord(IResource javaResource, PackageRecord record) {
        resource = javaResource;
        parent = record;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getParent() */
    public PMDRecord getParent() {
        return parent;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getChildren() */
    public PMDRecord[] getChildren() {
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getResource() */
    public IResource getResource() {
        return resource;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#createChildren() */
    protected PMDRecord[] createChildren() {
        // we don't have any children elements
        return null;
    }

    /**
     * Checks the File for PMD-Markers
     * 
     * @return true if the File has PMD-Markers, false otherwise
     */
    public boolean hasMarkers() {
        if (resource == null)
            return false;

        IMarker[] markers = findMarkers();
        if ((markers != null) && (markers.length > 0))
            return true;
        return false;
    }

    /**
     * Finds PMD-Markers in the File
     * 
     * @return an Array of Markers or null, if the File doesn't have any Markers
     */
    public IMarker[] findMarkers() {
        IMarker[] markers = null;
        try {
            // this is the overwritten Function from PMDRecord
            // we simply call the IResource-function to find Markers
            if (this.resource.isAccessible()) {
                markers = resource.findMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }

        return markers;
    }

    /**
     * Finds Markers, that have a given Attribute with a given Value
     * 
     * @param attributeName
     * @param value
     * @return an Array of Markers or null, if there aren't Markers matching these Attribute and Value
     */
    public IMarker[] findMarkersByAttribute(String attributeName, Object value) {
        IMarker[] markers = findMarkers();
        ArrayList attributeMarkers = new ArrayList();
        try {
            // we get all Markers and cath the ones that matches our criteria
            for (int i = 0; i < markers.length; i++) {
                Object val = markers[i].getAttribute(attributeName);

                // if the value is null, the Attribute doesn't exist
                if ((val != null) && (val.equals(value)))
                    attributeMarkers.add(markers[i]);
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }
        if (attributeMarkers.isEmpty())
            return null;

        // return an Array of the Markers
        IMarker[] markerArray = new IMarker[attributeMarkers.size()];
        attributeMarkers.toArray(markerArray);
        return markerArray;
    }

    /**
     * Calculates the Number of Code-Lines this File has The Function is adapted from the Eclipse Metrics-Plugin available at:
     * http://www.sourceforge.net/projects/metrics
     * 
     * @return the Lines of Code
     */
    public int getLinesOfCode() {
        if (!(resource instanceof IFile)) {
            return 0;
        }
        return 0; // deactivate this method for now

        // // the whole while has to be a String for this operation
        // // so we read the File line-wise into a String
        // int loc = 0;
        // String source = resourceToString(resource);
        // int firstCurly = source.indexOf('{');
        // if (firstCurly != -1) {
        // String body = source.substring(firstCurly+1, source.length()-1).trim();
        // StringTokenizer lines = new StringTokenizer(body, "\n");
        // while(lines.hasMoreTokens()) {
        // String trimmed = lines.nextToken().trim();
        // if (trimmed.length() == 0) continue;
        // if (trimmed.startsWith("/*")) {
        // while (trimmed.indexOf("*/") == -1) {
        // trimmed = lines.nextToken().trim();
        // }
        // if (lines.hasMoreTokens()) trimmed = lines.nextToken().trim();
        // }
        // if (!trimmed.startsWith("//")) loc++;
        //			
        // }
        // }
        // return loc;
    }

    /**
     * Reads a Resource's File and return the Code as String
     * 
     * @param resource
     * @return a String which is the Files Content
     */
    protected String resourceToString(IResource resource) {
        String fileContents = "";
        try {
            // we create a FileReader
            FileReader fileReader = new FileReader(resource.getRawLocation().toFile());
            BufferedReader bReader = new BufferedReader(fileReader);

            // ... and read the File line by line
            while (bReader.ready()) {
                fileContents += bReader.readLine() + "\n";
            }
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + resource.toString() + " in " + this.toString(), fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }

        return fileContents;
    }

    /**
     * Gets the Number of Methods, this Class contains
     * 
     * @return the Number of Methods
     */
    public int getNumberOfMethods() {
        return 0; // deactivate this method for now

        // // we need to change the Resource into a Java-File
        // IJavaElement element = JavaCore.create(resource);
        // ArrayList methods = new ArrayList();
        //		
        // if (element instanceof ICompilationUnit) {
        // try {
        // // ITypes can be Package Declarations or other Java Stuff too
        // IType[] types = ((ICompilationUnit) element).getTypes();
        // for (int i=0; i<types.length; i++) {
        // if (types[i] instanceof IType) {
        // // only if it is an IType itself, it's a Class
        // // from which we can get it's Methods
        // methods.addAll( Arrays.asList(
        // ((IType) types[i]).getMethods() ));
        // }
        // }
        // } catch (JavaModelException jme) {
        // PMDPlugin.getDefault().logError(
        // PMDConstants.MSGKEY_ERROR_JAVAMODEL_EXCEPTION +
        // this.toString(), jme);
        // }
        // }
        // if (methods.isEmpty())
        // return 0;
        //		
        // return methods.size();
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public PMDRecord addResource(IResource resource) {
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public PMDRecord removeResource(IResource resource) {
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getName() */
    public String getName() {
        return resource.getName();
    }

    /* @see net.sourceforge.pmd.ui.model.PMDRecord#getResourceType() */
    public int getResourceType() {
        return PMDRecord.TYPE_FILE;
    }
}
