/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;

import java.util.Arrays;

/**
 * Calculates from an absolute offset in the source file the line/column coordinate.
 * This is needed as Rhino only offers absolute positions for each node.
 * Some other languages like XML and Apex use this, too.
 * 
 * Idea from: http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/SourceFile.java
 */
public class SourceCodePositioner {

    private int[] lineOffsets;
    private int sourceCodeLength;
    
    public SourceCodePositioner(String sourceCode) {
	analyzeLineOffsets(sourceCode);
    }

    private void analyzeLineOffsets(String sourceCode) {
	String[] lines = sourceCode.split("\n");
	sourceCodeLength = sourceCode.length();
	
	int startOffset = 0;
	int lineNumber = 0;
	
	lineOffsets = new int[lines.length];
	
	for (String line : lines) {
	    lineOffsets[lineNumber] = startOffset;
	    lineNumber++;
	    startOffset += line.length() + 1; // +1 for the "\n" character
	}
    }

    public int lineNumberFromOffset(int offset) {
	int search = Arrays.binarySearch(lineOffsets, offset);
	int lineNumber;
	if (search >= 0) {
	    lineNumber = search;
	} else {
	    int insertionPoint = search;
	    insertionPoint += 1;
	    insertionPoint *= -1;
	    lineNumber = insertionPoint - 1; // take the insertion point one before
	}
	return lineNumber + 1; // 1-based line numbers
    }

    public int columnFromOffset(int lineNumber, int offset) {
	int columnOffset = offset - lineOffsets[lineNumber - 1];
	return columnOffset + 1; // 1-based column offsets
    }

    public int getLastLine() {
        return lineOffsets.length;
    }

    public int getLastLineColumn() {
        return columnFromOffset(getLastLine(), sourceCodeLength - 1);
    }
}
