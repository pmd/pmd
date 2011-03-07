package net.sourceforge.pmd.eclipse.ui.views.ast;

import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessorAdapter;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.ast.Node;

import org.eclipse.swt.SWT;

/**
 * 
 * @author Brian Remedios
 */
public interface NodeColumnUI {

	ItemFieldAccessor<String, Node> typeNameAcc = new ItemFieldAccessorAdapter<String, Node>(Util.compStr) {
		public String valueFor(Node node) {	return node.toString(); }
	};
	
	ItemFieldAccessor<String, Node> imageAcc = new ItemFieldAccessorAdapter<String, Node>(Util.compStr) {
		public String valueFor(Node node) {	return node.getImage(); }
	};

	ItemFieldAccessor<Integer, Node> beginLineNumAcc = new ItemFieldAccessorAdapter<Integer, Node>(Util.compInt) {
		public Integer valueFor(Node node) { return node.getBeginLine(); }
	};
	
	ItemFieldAccessor<Integer, Node> endLineNumAcc = new ItemFieldAccessorAdapter<Integer, Node>(Util.compInt) {
		public Integer valueFor(Node node) { return node.getEndLine(); }
	};
	
	ItemFieldAccessor<Integer, Node> beginColumnAcc = new ItemFieldAccessorAdapter<Integer, Node>(Util.compInt) {
		public Integer valueFor(Node node) { return node.getBeginColumn(); }
	};
	
	ItemFieldAccessor<Integer, Node> endColumnAcc = new ItemFieldAccessorAdapter<Integer, Node>(Util.compInt) {
		public Integer valueFor(Node node) { return node.getEndColumn(); }
	};
	
	ItemFieldAccessor<String, Node> derivedAcc = new ItemFieldAccessorAdapter<String, Node>(Util.compStr) {
		public String valueFor(Node node) { return NodeImageDeriver.derivedTextFor(node); }
	};	
	
	ItemFieldAccessor<String, Node> imageOrDerivedAcc = new ItemFieldAccessorAdapter<String, Node>(Util.compStr) {
		public String valueFor(Node node) { return node.getImage() == null ? NodeImageDeriver.derivedTextFor(node) : node.getImage(); }
	};
	
	ItemColumnDescriptor typeName 	= new ItemColumnDescriptor("", StringKeys.NODE_COLUMN_NAME, 	SWT.LEFT, 85, true, typeNameAcc);
	ItemColumnDescriptor imageData	= new ItemColumnDescriptor("", StringKeys.NODE_IMAGE_DATA, SWT.LEFT, 25, true,  imageAcc);
	ItemColumnDescriptor lineNum	= new ItemColumnDescriptor("", StringKeys.NODE_LINE_NUM, 	SWT.RIGHT, 35, true, beginLineNumAcc);
	ItemColumnDescriptor derived	= new ItemColumnDescriptor("", StringKeys.NODE_DERIVED, 	SWT.LEFT, 25, true, derivedAcc);
	ItemColumnDescriptor imageOrDerived	= new ItemColumnDescriptor("", StringKeys.NODE_IMG_OR_DERIVED, 	SWT.LEFT, 25, true, imageOrDerivedAcc);

	ItemColumnDescriptor[] VisibleColumns = new ItemColumnDescriptor[] { lineNum, typeName, imageOrDerived };

}
