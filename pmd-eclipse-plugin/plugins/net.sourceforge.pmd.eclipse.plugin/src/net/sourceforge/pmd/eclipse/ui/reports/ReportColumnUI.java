package net.sourceforge.pmd.eclipse.ui.reports;

import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessorAdapter;
import net.sourceforge.pmd.renderers.Renderer;

import org.eclipse.swt.SWT;

/**
 * 
 * @author Brian Remedios
 */
public interface ReportColumnUI {
	
	ItemFieldAccessor<String, Renderer> nameAcc = new ItemFieldAccessorAdapter<String, Renderer>(null) {
		public String valueFor(Renderer renderer) {	return renderer.getName(); }
	};

	ItemFieldAccessor<String, Renderer> descriptionAcc = new ItemFieldAccessorAdapter<String, Renderer>(null) {
		public String valueFor(Renderer renderer) {	return renderer.getDescription(); }
	};
	
	ItemFieldAccessor<Boolean, Renderer> showSuppressedAcc = new ItemFieldAccessorAdapter<Boolean, Renderer>(null) {
		public Boolean valueFor(Renderer renderer) { return renderer.isShowSuppressedViolations(); }
	};
	
	ItemFieldAccessor<String, Renderer> propertiesAcc = new ItemFieldAccessorAdapter<String, Renderer>(null) {
		public String valueFor(Renderer renderer) { return ReportManager.asString(renderer.getPropertyDefinitions()); }
	};
	
	ItemColumnDescriptor<String, Renderer> name   		= new ItemColumnDescriptor<String, Renderer>("", "Name", SWT.LEFT, 55, true, nameAcc);
	ItemColumnDescriptor<String, Renderer> description 	= new ItemColumnDescriptor<String, Renderer>("", "Format", SWT.LEFT, 99, true, descriptionAcc);
	ItemColumnDescriptor<Boolean, Renderer> suppressed 	= new ItemColumnDescriptor<Boolean, Renderer>("", "Show suppressed", SWT.LEFT, 40, true, showSuppressedAcc);
	ItemColumnDescriptor<String, Renderer> properties 	= new ItemColumnDescriptor<String, Renderer>("", "Properties", SWT.LEFT, 99, true, propertiesAcc);

	ItemColumnDescriptor[] VisibleColumns = new ItemColumnDescriptor[] { name, suppressed, properties }; 
}
