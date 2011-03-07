package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.Comparator;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessorAdapter;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptorCache;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public interface MarkerColumnsUI {
	
	Comparator<RulePriority> compPriority = new Comparator<RulePriority>() { public int compare(RulePriority a, RulePriority b) 	{ return a.compareTo(b); } };
	
	ItemFieldAccessor<RulePriority, IMarker> priorityAcc = new ItemFieldAccessorAdapter<RulePriority, IMarker>(compPriority) {
		public RulePriority valueFor(IMarker marker) { 
			int prio = MarkerUtil.rulePriorityFor(marker, 1);
			return RulePriority.valueOf(prio);
			}
	};
	
	ItemFieldAccessor<Image, IMarker> priorityImgAcc = new ItemFieldAccessorAdapter<Image, IMarker>(null) {
		final Display display = Display.getCurrent();
		public Image imageFor(IMarker marker) { 
			RulePriority rp = priorityAcc.valueFor(marker);
			return PriorityDescriptorCache.instance.descriptorFor(rp).getImage(display);	
			}
	};
	
	ItemFieldAccessor<Integer, IMarker> lineNoAcc = new ItemFieldAccessorAdapter<Integer, IMarker>(Util.compInt) {
		public Integer valueFor(IMarker marker) { return (Integer)marker.getAttribute(IMarker.LINE_NUMBER, 0); }
	};
	
	ItemFieldAccessor<Long, IMarker> createdAcc = new ItemFieldAccessorAdapter<Long, IMarker>(Util.compLong) {
		public Long valueFor(IMarker marker) { return MarkerUtil.createdOn(marker, -1); }
	};
	
	ItemFieldAccessor<Boolean, IMarker> doneAcc = new ItemFieldAccessorAdapter<Boolean, IMarker>(Util.compBool) {
		public Boolean valueFor(IMarker marker) { return MarkerUtil.doneState(marker, false); }
	};
	
	ItemFieldAccessor<String, IMarker> ruleNameAcc = new ItemFieldAccessorAdapter<String, IMarker>(Util.compStr) {
		public String valueFor(IMarker marker) { return MarkerUtil.ruleNameFor(marker); }
	};
	
	ItemFieldAccessor<String, IMarker> messageAcc = new ItemFieldAccessorAdapter<String, IMarker>(Util.compStr) {
		public String valueFor(IMarker marker) { return MarkerUtil.messageFor(marker, "??"); }
	};
	
	ItemColumnDescriptor<Image, IMarker> priority		= new ItemColumnDescriptor<Image, IMarker>("tPriority", "Priority", SWT.CENTER, 20, false, priorityImgAcc);	
	ItemColumnDescriptor<Boolean, IMarker> done			= new ItemColumnDescriptor<Boolean, IMarker>("tDone", "done", SWT.LEFT, 50, false, doneAcc);
	ItemColumnDescriptor<Long, IMarker> created			= new ItemColumnDescriptor<Long, IMarker>("tCreated", "created", SWT.LEFT, 130, true, createdAcc, ValueFormatter.TimeFormatters);
	ItemColumnDescriptor<String, IMarker> ruleName		= new ItemColumnDescriptor<String, IMarker>("tRuleName", "Rule", SWT.LEFT, 190, true, ruleNameAcc);
	ItemColumnDescriptor<String, IMarker> message		= new ItemColumnDescriptor<String, IMarker>("tMsg",	 StringKeys.VIEW_OUTLINE_COLUMN_MESSAGE, SWT.LEFT, 260, true, messageAcc);
	ItemColumnDescriptor<Integer, IMarker> lineNumber	= new ItemColumnDescriptor<Integer, IMarker>("tLineNo", StringKeys.VIEW_OUTLINE_COLUMN_LINE, SWT.LEFT, 50, false, lineNoAcc);

}
