/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import pmd.config.ui.RuleEditor;
import pmd.config.ui.RuleSetChooserEditor;

/**
 * Bean descriptor for {@link PMDOptionsSettings}.
 */
public class PMDOptionsSettingsBeanInfo extends SimpleBeanInfo {

	/**
	 * Returns the property descriptors for the properties comprising the PMD settings.
	 *
	 * @return an array of property descriptors describing all PMD settings properties, not null.
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor descriptor[] = new PropertyDescriptor[5];
		try {
			
			PropertyDescriptor rules = new PropertyDescriptor( PMDOptionsSettings.PROP_RULES, PMDOptionsSettings.class, "getRules", "setRules" );
			rules.setDisplayName( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "PROP_rules" ) );
			rules.setShortDescription( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "HINT_rules" ) );
			rules.setPropertyEditorClass( RuleEditor.class );
			descriptor[0] = rules;

			PropertyDescriptor rulesets = new PropertyDescriptor( PMDOptionsSettings.PROP_RULESETS, PMDOptionsSettings.class, "getRulesets", "setRulesets" );
			rulesets.setDisplayName( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "PROP_rulesets" ) );
			rulesets.setShortDescription( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "HINT_rulesets" ) );
			rulesets.setPropertyEditorClass( RuleSetChooserEditor.class );
			rulesets.setExpert(true);
			descriptor[1] = rulesets;
			
			PropertyDescriptor enableScan = new PropertyDescriptor( PMDOptionsSettings.PROP_ENABLE_SCAN, PMDOptionsSettings.class, "isScanEnabled", "setScanEnabled" );
			enableScan.setDisplayName( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "PROP_enablescan" ) );
			enableScan.setShortDescription( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "HINT_enablescan" ) );
			descriptor[2] = enableScan;			
			
			PropertyDescriptor scanInterval = new PropertyDescriptor( PMDOptionsSettings.PROP_SCAN_INTERVAL, PMDOptionsSettings.class, "getScanInterval", "setScanInterval" );
			scanInterval.setDisplayName( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "PROP_scanInterval" ) );
			scanInterval.setShortDescription( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "HINT_scanInterval" ) );
			descriptor[3] = scanInterval;		
			
			PropertyDescriptor ruleProperties = new PropertyDescriptor( PMDOptionsSettings.PROP_RULE_PROPERTIES, PMDOptionsSettings.class, "getRuleProperties", "setRuleProperties" );
			ruleProperties.setHidden(true);
			descriptor[4] = ruleProperties;
			
		}
		catch( IntrospectionException ie ) {
			ErrorManager.getDefault().notify( ie );
		}
		return descriptor;
	}


	/**
	 * Returns an icon representing PMD settings.
	 *
	 * @param type the type of icon, one of {@link BeanInfo} constants
	 * @return the icon, not null.
	 */
	public Image getIcon( int type ) {
                return Utilities.loadImage( "pmd/resources/PMDOptionsSettingsIcon.gif" );
	}
}
