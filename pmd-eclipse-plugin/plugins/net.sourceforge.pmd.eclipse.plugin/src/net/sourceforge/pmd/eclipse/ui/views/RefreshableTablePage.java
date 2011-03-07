package net.sourceforge.pmd.eclipse.ui.views;

import org.eclipse.jface.viewers.TableViewer;

public interface RefreshableTablePage {

	TableViewer tableViewer();
	
	void refresh();
}
