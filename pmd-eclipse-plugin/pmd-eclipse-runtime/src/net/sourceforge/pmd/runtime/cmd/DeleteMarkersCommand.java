/*
 * Created on 13.11.2006
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

package net.sourceforge.pmd.runtime.cmd;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import name.herlin.command.CommandException;

/**
 * Command to delete single markers.
 * This is useful if a large number of marker have to be deleted in order to run this in background.
 * For unknown reasons this took some time.
 * 
 * @author Sven
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/11/16 16:54:40  holobender
 * - changed command for the new cpd view
 * - possibility to set the number of maxviolations per file over the rule-properties
 *
 *
 */

public class DeleteMarkersCommand extends AbstractDefaultCommand {
    private IMarker[] marker;
    
    public DeleteMarkersCommand() {
        super();
        this.setDescription("Deletes a possible high number of markers");
        this.setName("DeleteMarkersCommand");
        this.setOutputProperties(true);
        this.setReadOnly(false);
        this.setTerminated(false);
        this.setMarkers(null);
        this.setUserInitiated(false);
    }
    
    public final void setMarkers(IMarker[] marker) { // NOPMD by Sven on 13.11.06 11:43
        this.marker = marker;
    }
    
    public boolean isReadyToExecute() {
        return marker != null;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.pmd.runtime.cmd.AbstractDefaultCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            beginTask("Deleting single markers", marker.length);
            for (int j = 0; j < marker.length && !isCanceled(); j++) {
                marker[j].delete();
                worked(1);
            }
        } catch (CoreException e) {
            throw new CommandException(e);        
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.runtime.cmd.AbstractDefaultCommand#reset()
     */
    public void reset() {
        setMarkers(null);
    }

}
