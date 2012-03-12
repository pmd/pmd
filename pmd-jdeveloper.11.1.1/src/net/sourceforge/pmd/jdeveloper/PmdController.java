/* $Header: SimpleController.java 16-aug-2007.22:06:23 bduff Exp $ */

/* Copyright (c) 2007, Oracle. All rights reserved.  */

/*
   MODIFIED    (MM/DD/YY)
    bduff       08/16/07 - Reformat to JCS.
    bduff       02/27/07 - Added copyright banner
 */
package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.ceditor.CodeEditor;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.extension.RegisteredByExtension;
import oracle.ide.model.Element;
import oracle.ide.model.Project;
import oracle.ide.model.RelativeDirectoryContextFolder;
import oracle.ide.view.View;

import oracle.jdeveloper.model.JavaSourceNode;


/**
 * The controller implementation is responsible for enabling and disabling
 * actions and performing any work required when they are invoked.
 */
@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public final class PmdController implements Controller {
    /**
     * Look up the numeric id of the action we defined in our extension
     * manifest. The string constant matches the value of the id attribute in
     * the action element of the extension manifest.<p>
     *
     * The numeric id can be used to retrieve an instance of IdeAction.
     */
    public static final int RUN_PMD_CMD_ID = Ide.findCmdID("net.sourceforge.pmd.jdeveloper.CheckPmd");

    public boolean handleEvent(final IdeAction action, final Context context) {
        // Command is handled by net.sourceforge.pmd.jdeveloper.PmdCommand
        return false;
    }

    public boolean update(final IdeAction action, final Context context) {
        final Element doc = context.getElement();
        final View view = context.getView();
        // RelativeDirectoryContextFolder -> a package
        if (doc instanceof Project || doc instanceof JavaSourceNode || doc instanceof RelativeDirectoryContextFolder ||
            view instanceof CodeEditor) {
            action.setEnabled(true);
            // TODO CPD
            // contextMenu.add(contextMenu.createMenuItem(cpdAction));
        } else {
            action.setEnabled(false);
        }
         return true;
    }
}
