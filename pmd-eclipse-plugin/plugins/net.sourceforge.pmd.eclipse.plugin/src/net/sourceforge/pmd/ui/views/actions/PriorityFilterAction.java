package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.PriorityFilter;
import net.sourceforge.pmd.ui.views.ViolationOutline;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters Elements by their Marker's Priorities
 * 
 * @author SebastianRaffel ( 22.05.2005 )
 */
public class PriorityFilterAction extends Action {

    private ViolationOutline outlineView;
    private ViolationOverview overviewView;
    private PriorityFilter priorityFilter;
    private Integer priority;

    /**
     * Constructor, used for Violations Outline only
     * 
     * @param prio, the Priority to filter
     * @param view, the ViolationOutline
     */
    public PriorityFilterAction(Integer prio, ViolationOutline view) {
        outlineView = view;
        priority = prio;

        ViewerFilter[] filters = view.getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof PriorityFilter)
                priorityFilter = (PriorityFilter) filters[i];
        }

        setupActionLook();
    }

    /**
     * Constructor, used for Violations Overview only
     * 
     * @param prio, the Priority to filter
     * @param view, the violations Overview
     */
    public PriorityFilterAction(Integer prio, ViolationOverview view) {
        overviewView = view;
        priority = prio;

        ViewerFilter[] filters = view.getViewer().getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof PriorityFilter)
                priorityFilter = (PriorityFilter) filters[i];
        }

        setupActionLook();
    }

    /**
     * Setup the Actions Look by giving the right Image, Text and ToolTip-Text to it, depending on its Priority
     */
    private void setupActionLook() {
        ImageDescriptor image = null;
        String text = null;
        String tooltipText = null;

        // we set the Look - meaning Image, Text and ToolTip-Text -
        // depending on the Action's Priority
        switch (priority.intValue()) {
        case 1:
            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO1);
            text = getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_1);
            tooltipText = getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_1);
            break;
        case 2:
            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO2);
            text = getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_2);
            tooltipText = getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_2);
            break;
        case 3:
            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO3);
            text = getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_3);
            tooltipText = getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_3);
            break;
        case 4:
            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO4);
            text = getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_4);
            tooltipText = getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_4);
            break;
        case 5:
            image = PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PRIO5);
            text = getString(StringKeys.MSGKEY_VIEW_FILTER_PRIORITY_5);
            tooltipText = getString(StringKeys.MSGKEY_VIEW_TOOLTIP_FILTER_PRIORITY_5);
            break;
        }

        setImageDescriptor(image);
        setText(text);
        setToolTipText(tooltipText);
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
            priorityFilter.addPriorityToList(priority);
        } else {
            priorityFilter.removePriorityFromList(priority);
        }

        if (outlineView != null)
            outlineView.refresh();
        else if (overviewView != null)
            overviewView.refresh();
    }

    /**
     * Helper mehod to retreive an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
