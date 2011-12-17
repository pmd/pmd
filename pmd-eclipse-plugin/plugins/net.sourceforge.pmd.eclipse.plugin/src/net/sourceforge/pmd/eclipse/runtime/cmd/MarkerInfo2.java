package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Brian Remedios
 */
public class MarkerInfo2 {

	private final String type;
	private List<String> names;
	private List<Object> values;

	public MarkerInfo2(String theType, int expectedSize) {
		type = theType;
		names = new ArrayList<String>(expectedSize);
		values = new ArrayList<Object>(expectedSize);
	}

	public void add(String name, Object value) {
		names.add(name);
		values.add(value);
	}

	public void add(String name, int value) {
		add(name, Integer.valueOf(value));
	}
	
	public void addAsMarkerTo(IFile file) throws CoreException {
		
		 IMarker marker = file.createMarker(type);
         marker.setAttributes(names.toArray(new String[names.size()]), values.toArray());
	}
}
