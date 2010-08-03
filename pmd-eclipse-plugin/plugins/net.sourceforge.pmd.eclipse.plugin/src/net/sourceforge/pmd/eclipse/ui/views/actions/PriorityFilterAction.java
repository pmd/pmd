package net.sourceforge.pmd.eclipse.ui.views.actions;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PriorityDescriptor;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.views.PriorityFilter;
import net.sourceforge.pmd.eclipse.ui.views.ViolationOutline;
import net.sourceforge.pmd.eclipse.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;

/**
 * Filters Elements by their Marker's Priorities
 * 
 * @author SebastianRaffel ( 22.05.2005 )
 */
public class PriorityFilterAction extends Action {

    private ViolationOutline outlineView;
    private ViolationOverview overviewView;
    private PriorityFilter priorityFilter;
    private final RulePriority priority;

    private PriorityFilterAction(ViewerFilter[] filters, RulePriority thePriority) {
    	priority = thePriority;
    	
        setFilterFrom(filters);
        setupActionLook();
    }
    /**
     * Constructor, used for Violations Outline only
     * 
     * @param prio, the Priority to filter
     * @param view, the ViolationOutline
     */
    public PriorityFilterAction(RulePriority prio, ViolationOutline view) {
    	this(view.getFilters(), prio);
        outlineView = view;
    }

    /**
     * Constructor, used for Violations Overview only
     * 
     * @param prio, the Priority to filter
     * @param view, the violations Overview
     */
    public PriorityFilterAction(RulePriority prio, ViolationOverview view) {
    	this(view.getViewer().getFilters(), prio);
        overviewView = view;
    }

    private void setFilterFrom(ViewerFilter[] filters) {
    	
        for (Object filter : filters) {
            if (filter instanceof PriorityFilter)
                priorityFilter = (PriorityFilter) filter;
        }
    }
    
    /**
     * Setup the Actions Look by giving the right Image, Text and ToolTip-Text to it, depending on its Priority
     */
    private void setupActionLook() {
//        ImageDescriptor image = null;
//        String text = null;
//        String tooltipText = null;
//
//        // we set the Look - meaning Image, Text and ToolTip-Text -
//        // depending on the Action's Priority
//        switch (priority.intValue()) {
//        case 1:
//            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO1);
//            text = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_1);
//            tooltipText = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_1);
//            break;
//        case 2:
//            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO2);
//            text = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_2);
//            tooltipText = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_2);
//            break;
//        case 3:
//            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO3);
//            text = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_3);
//            tooltipText = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_3);
//            break;
//        case 4:
//            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO4);
//            text = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_4);
//            tooltipText = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_4);
//            break;
//        case 5:
//            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO5);
//            text = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_5);
//            tooltipText = AbstractPMDAction.getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_5);
//            break;
//        }

        PriorityDescriptor desc = UISettings.descriptorFor(priority);
        setImageDescriptor( ImageDescriptor.createFromImage( desc.getImage(Display.getCurrent())) );
        setText(desc.label);
        setToolTipText(desc.filterText);
    }

    /**
     * @return the Style, in which the Button is displayed
     */
    public int getStyle() {
        return AS_CHECK_BOX;
    }

    /**
     * Executes the Action
     */
    public void run() {
        // we add or remove an Integer with the Priority to a List
        // of Priorities, the Filter does the Rest
        if (isChecked()) {
            priorityFilter.addPriorityToList(priority.getPriority());
        } else {
            priorityFilter.removePriorityFromList(priority.getPriority());
        }

        if (outlineView != null)
            outlineView.refresh();
        else if (overviewView != null)
            overviewView.refresh();
    }

}
