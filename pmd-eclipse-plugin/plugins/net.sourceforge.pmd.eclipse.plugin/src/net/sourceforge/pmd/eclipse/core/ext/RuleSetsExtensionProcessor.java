/*
 * Created on 2 juil. 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.eclipse.core.ext;

import net.sourceforge.pmd.eclipse.core.IRuleSetManager;
import net.sourceforge.pmd.eclipse.core.IRuleSetsExtension;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

/**
 * This class processes the AdditionalRuleSets extension point
 *
 * @author Herlin
 *
 */

public class RuleSetsExtensionProcessor {
    private static final String EXTENTION_POINT = "net.sourceforge.pmd.eclipse.plugin.rulesets";
    private static final String CLASS_ATTRIBUTE = "class";
    private final IRuleSetManager ruleSetManager;

    /**
     * Constructor
     * @param ruleSetManager the plugin RuleSetManager
     */
    public RuleSetsExtensionProcessor(IRuleSetManager ruleSetManager) {
        this.ruleSetManager = ruleSetManager;
    }

    /**
     * Process the extension point
     */
    public void process() throws CoreException {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENTION_POINT);
        for (IConfigurationElement element : extensionPoint.getConfigurationElements()) {
            processExecutableExtension(element);
        }
    }

    /**
     * Process an extension
     * @param element the extension to process
     */
    private void processExecutableExtension(IConfigurationElement element) throws CoreException {
        final Object object = element.createExecutableExtension(CLASS_ATTRIBUTE);
        if (object instanceof IRuleSetsExtension) {
            final IRuleSetsExtension extension = (IRuleSetsExtension) object;

            extension.registerRuleSets(ruleSetManager.getRegisteredRuleSets());
            extension.registerDefaultRuleSets(ruleSetManager.getDefaultRuleSets());

        } else {
            PMDPlugin.getDefault().log(IStatus.ERROR, "Extension " + element.getName() + " is not an instance of IRuleSetsExtension", null);
        }
    }

}
