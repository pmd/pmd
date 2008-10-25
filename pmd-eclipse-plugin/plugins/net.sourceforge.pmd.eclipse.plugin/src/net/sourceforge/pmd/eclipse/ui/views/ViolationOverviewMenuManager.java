/*
 * Created on 11.11.2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.ProjectRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.actions.CalculateStatisticsAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.CollapseAllAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.PriorityFilterAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.ProjectFilterAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.ViolationPresentationTypeAction;

/**
 *
 *
 * @author Sven
 *
 */

public class ViolationOverviewMenuManager {
    private final ViolationOverview overview;
    private PriorityFilterAction[] priorityActions;

    public ViolationOverviewMenuManager(ViolationOverview overview) {
        this.overview = overview;
    }

    /**
     * Setup the Actions for the ActionBars
     */
    public void setupActions() {
        final Integer[] priorities = PMDPlugin.getDefault().getPriorityValues();
        this.priorityActions = new PriorityFilterAction[priorities.length];

        // create the Actions for the PriorityFilter
        for (int i = 0; i < priorities.length; i++) {
            this.priorityActions[i] = new PriorityFilterAction(priorities[i], overview); // NOPMD by Herlin on 09/10/06 15:02
            final boolean check = this.overview.getPriorityFilterList().contains(priorities[i]);
            this.priorityActions[i].setChecked(check);
        }
    }

    /**
     * Creates the ActionBars
     */
    public void createActionBars(IToolBarManager manager) {
        // Action for calculating the #violations/loc
        final Action calculateStats = new CalculateStatisticsAction(this.overview);
        manager.add(calculateStats);
        manager.add(new Separator());

        // the PriorityFilter-Actions
        for (PriorityFilterAction priorityAction : this.priorityActions) {
            manager.add(priorityAction);
        }
        manager.add(new Separator());

        // the CollapseAll-Action
        final Action collapseAllAction = new CollapseAllAction(this.overview);
        manager.add(collapseAllAction);
    }

    /**
     * Creates the DropDownMenu
     */
    public void createDropDownMenu(IMenuManager manager) {
        manager.removeAll();

        // both, Context- and DropDownMenu contain the same
        // SubMenu for filtering Projects
        createProjectFilterMenu(manager);
        createShowTypeSubmenu(manager);
    }



    /**
     * Creates the Context Menu
     */
    public void createContextMenu() {
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                MenuManager submenuManager;

                // one SubMenu for filtering Projects
                submenuManager = new MenuManager(getString(StringKeys.MSGKEY_VIEW_MENU_RESOURCE_FILTER));
                createProjectFilterMenu(submenuManager);
                manager.add(submenuManager);

                // ... another one for filtering Priorities
                submenuManager = new MenuManager(getString(StringKeys.MSGKEY_VIEW_MENU_PRIORITY_FILTER));
                for (PriorityFilterAction priorityAction : priorityActions) {
                    submenuManager.add(priorityAction);
                }
                manager.add(submenuManager);

                // ... another one for showing the presentation types
                submenuManager = new MenuManager(getString(StringKeys.MSGKEY_VIEW_MENU_PRESENTATION_TYPE));
                createShowTypeSubmenu(submenuManager);
                manager.add(submenuManager);

                // additions Action: Clear PMD Violations
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));

            }
        });

        final Tree tree = this.overview.getViewer().getTree();
        tree.setMenu(manager.createContextMenu(tree));

        this.overview.getSite().registerContextMenu(manager, this.overview.getViewer());
    }

    /**
     * Create the Menu for filtering Projects
     *
     * @param manager, the MenuManager
     */
    private void createProjectFilterMenu(IMenuManager manager) {
        final List<AbstractPMDRecord> projectFilterList = this.overview.getProjectFilterList();
        final List<ProjectRecord> projectList = new ArrayList<ProjectRecord>();

        // We get a List of all Projects
        final AbstractPMDRecord[] projects = this.overview.getAllProjects();
        for (int i = 0; i < projects.length; i++) {
            final ProjectRecord project = (ProjectRecord) projects[i];
            // if the Project contains Errors,
            // we add a FilterAction for it
            if (project.hasMarkers()) {
                final Action projectFilterAction = new ProjectFilterAction(project, this.overview); // NOPMD by Herlin on 09/10/06 15:03

                // if it is not already in the List,
                // we set it as "visible"
                if (!projectFilterList.contains(projects[i])) { // NOPMD by Herlin on 09/10/06 15:04
                    projectFilterAction.setChecked(true);
                }

                manager.add(projectFilterAction);
                projectList.add(project);
            }
        }
        manager.add(new Separator());
    }

    /**
     * Create menu for selecting the show type.
     * @param manager
     */
    private void createShowTypeSubmenu(IMenuManager manager) {
        final Action typeAction1 = new ViolationPresentationTypeAction(this.overview, ViolationOverview.SHOW_MARKERS_FILES);
        final Action typeAction2 = new ViolationPresentationTypeAction(this.overview, ViolationOverview.SHOW_FILES_MARKERS);
        final Action typeAction3 = new ViolationPresentationTypeAction(this.overview, ViolationOverview.SHOW_PACKAGES_FILES_MARKERS);
        manager.add(typeAction1);
        manager.add(typeAction2);
        manager.add(typeAction3);
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
