package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiConstants;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * Creates the PMD Perspective
 * 
 * @author SebastianRaffel  ( 08.05.2005 )
 */
public class PMDPerspective implements IPerspectiveFactory {
	
	/* @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout) */
	public void createInitialLayout(IPageLayout layout) {
		String editorAreaId = layout.getEditorArea();
		String explorerAreaId = "org.eclipse.jdt.ui.PackageExplorer";
		String outlineAreaId = PMDUiConstants.ID_OUTLINE;
		String overviewAreaId = PMDUiConstants.ID_OVERVIEW;
		
		layout.addView(explorerAreaId, IPageLayout.LEFT, 0.25f, editorAreaId);
		layout.addView(outlineAreaId, IPageLayout.BOTTOM, 0.6f, explorerAreaId);
		layout.addView(overviewAreaId, IPageLayout.BOTTOM, 0.65f, editorAreaId);
	}
}


