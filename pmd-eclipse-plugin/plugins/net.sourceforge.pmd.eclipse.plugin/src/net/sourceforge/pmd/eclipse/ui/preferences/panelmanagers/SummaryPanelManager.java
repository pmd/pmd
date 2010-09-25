package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;


import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.PageBuilder;
import net.sourceforge.pmd.eclipse.ui.StringArranger;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.util.ClassUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author Brian Remedios
 */
public class SummaryPanelManager extends AbstractRulePanelManager {

	private StyledText		viewField;
	private PageBuilder		pb = new PageBuilder(3, SWT.COLOR_BLUE);
	private StringArranger 	arranger = new StringArranger("   ");
	
	public SummaryPanelManager(String theId, String theTitle, EditorUsageMode theMode, ValueChangeListener theListener) {
		super(theId, theTitle, theMode, theListener);
	}

	public static String asLabel(Class<?> type) {
		
		return type.isArray() ?
			ClassUtil.asShortestName(type.getComponentType()) + "[]" :
			ClassUtil.asShortestName(type);
	}
	
	@Override
	protected void adapt() {
		   Rule rule = soleRule();
		   
		   pb.clear();
		   
		   pb.addHeading("Name");
		   pb.addText( rule.getName() );
		   
		   pb.addHeading("Description");
		   pb.addRawText( arranger.format(rule.getDescription()).toString() );
		   
		   Map<PropertyDescriptor<?>, Object> valuesByDescriptor = Configuration.filteredPropertiesOf(rule);
		   if (!valuesByDescriptor.isEmpty()) {
			   pb.addHeading("Parameters");
			   for (Map.Entry<PropertyDescriptor<?>, Object> entry : valuesByDescriptor.entrySet()) {
				   PropertyDescriptor desc = entry.getKey();
				   pb.addText(desc.name() + '\t' + asLabel(desc.type()));
			   }
		   }
			
		   for (String example : rule.getExamples()) {
			   pb.addHeading("Example");
			   pb.addText(example.trim() );
		   }
		   
		   pb.showOn(viewField);
	}

	@Override
	protected boolean canManageMultipleRules() { return false; }

	protected boolean canWorkWith(Rule rule) { return true; }

	@Override
	protected void clearControls() {
		// TODO Auto-generated method stub
	}

	@Override
	public Control setupOn(Composite parent) {

		Composite panel = new Composite(parent, 0);
		panel.setLayout(new FillLayout());

		viewField = new StyledText(panel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewField.setTabs(20);
		
		return panel;
	}

	@Override
	public void showControls(boolean flag) {
		viewField.setVisible(flag);
	}

}
