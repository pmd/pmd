package net.sourceforge.pmd.eclipse.ui;

/**
 * 
 * @author Brian Remedios
 */
public interface ColumnDescriptor {

	String id();
	
	String label();

	String tooltip();
	
	int defaultWidth();
}