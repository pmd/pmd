/*
 * Created on 20 nov. 2004
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse.cmd;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

/**
 * This is a base implementation for a command.
 * This could be used as a root implementation for the simplest a command
 * which is a command that executes whitout any Command Processor.
 * Or this could be used as a base implementation for more complex form such as
 * Job Command.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public abstract class DefaultCommand implements Command, PMDConstants, PMDPluginConstants {
    private boolean readOnly;
    private String description;
    private String name;
    private boolean outputData;

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /**
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return Returns the outputData.
     */
    public boolean hasOutputData() {
        return outputData;
    }
    
    /**
     * @param outputData The outputData to set.
     */
    public void setOutputData(boolean hasOutputData) {
        this.outputData = hasOutputData;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * By default a command is executed without a Command Processor
     * @throws CommandException if a internal technical error occurred
     */
    public void performExecute() throws CommandException {
        execute();
    }
    
    /**
     * The implementation of the command logic
     * @throws CommandException if a internal technical error occurred
     */
    protected abstract void execute() throws CommandException;

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
}
