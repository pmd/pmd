package net.sourceforge.pmd.eclipse.ui.preferences.br;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * 
 * @author Brian Remedios
 */
public class TreeUtil {
	
	private TreeUtil() {}
	
	public static String getSelectionAsString(TreeViewer viewer, String columnSeparator, String lineSeparator) {
		
		String[][] data = getStringContents(viewer);
		StringBuilder sb = new StringBuilder();
		
		for (int r=0; r<data.length; r++) {
			sb.append(data[r][0]);
			for (int c=1; c<data[r].length; c++) {
				sb.append(columnSeparator).append(data[r][c]);
			}
			sb.append(lineSeparator);
		}
		return sb.toString();
	}
	
	public static String[][] getStringContents(TreeViewer viewer) {
		
		Object[] items = ((IStructuredSelection)viewer.getSelection()).toArray();
		ITableLabelProvider provider = (ITableLabelProvider)viewer.getLabelProvider();

		int columnCount = viewer.getTree().getColumnCount();
				
		String[][] output = new String[items.length][columnCount];
				
		for (int r=0; r<items.length; r++) {
			for (int c=0; c<columnCount; c++) {
				output[r][c] = provider.getColumnText(items[r], c);
			}
		}
		return output;
	}
	
	public static void copySelectionToClipboard(TreeViewer viewer) {
		
		Clipboard clipboard = new Clipboard(viewer.getTree().getDisplay());
		clipboard.setContents(
				new Object[] { getSelectionAsString(viewer, "\t", System.getProperty("line.separator"))},
				new Transfer[] { TextTransfer.getInstance() }
				);
	}
	
}
