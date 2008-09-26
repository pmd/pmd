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

package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;

/**
 * 
 * 
 * @author Sven
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 *
 */

public class ViolationPresentationTypeAction extends Action {
    private final ViolationOverview overview;
    private final int type;
    
    public ViolationPresentationTypeAction(ViolationOverview overview, int type) {
        super();
        this.overview = overview;
        this.type = type;

        setChecked(overview.getShowType() == type);
        switch (type) {
        case ViolationOverview.SHOW_FILES_MARKERS:
            // we set Image and Text for the Action
            setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_FILEMARKERS));
            setText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_MENU_FILEMARKERS));
            break;
        case ViolationOverview.SHOW_MARKERS_FILES:
            setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_MARKERFILES));
            setText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_MENU_MARKERFILES));
            break;
        case ViolationOverview.SHOW_PACKAGES_FILES_MARKERS:
            setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PACKFILES));
            setText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_MENU_PACKFILES));            
            break;
        default:
            // do nothing
        }
    }
    
    public int getStyle() {
        return AS_RADIO_BUTTON;
    }

    /**
     * Executes the Action
     */
    public void run() {
        this.overview.setShowType(this.type);
        this.overview.refresh();
    }
}
