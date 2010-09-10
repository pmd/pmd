package net.sourceforge.pmd.eclipse.ui;

import java.util.Collections;
import java.util.List;

public class IndexedString implements Comparable<IndexedString>{

	public final String string;
	public final List<int[]> indexSpans;
	
	public IndexedString(String theString) {
		this(theString, Collections.EMPTY_LIST);
	}
	
	public IndexedString(String theString, List<int[]> theSpans) {
		string = theString;
		indexSpans = theSpans;
	}

	public int compareTo(IndexedString other) {
		
		int deltaLength = other.string.length() - string.length();
		
		return deltaLength == 0 ? 
				other.string.compareTo(string) : 
				deltaLength;
	}
	
	public static final IndexedString Empty = new IndexedString("");
}
