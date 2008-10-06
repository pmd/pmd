/*
 * <copyright>
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 *
 * </copyright>
 */
package net.sourceforge.pmd.eclipse.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Implementation of a resolution generator to bring the quick fixes feature
 * of Eclipse to PMD
 *
 * @author Philippe Herlin
 *
 */
public class PMDResolutionGenerator implements IMarkerResolutionGenerator {

    /**
     * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
     */
    public IMarkerResolution[] getResolutions(IMarker marker) {
        final List markerResolutionList = new ArrayList();
        try {
            final String ruleName = (String) marker.getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME);
            if (ruleName != null) {
                final RuleSet ruleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
                final Rule rule = ruleSet.getRuleByName(ruleName);

                // The final implementation should ask the rule to give a list of fixes
                if (rule != null && rule.getName().equals("DuplicateImports")) {
                    markerResolutionList.add(new PMDResolution(new DeleteLineFix()));
                }
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (RuntimeException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION), e);
        }

        return (IMarkerResolution[]) markerResolutionList.toArray(new IMarkerResolution[markerResolutionList.size()]);
    }

}
