/*
 * Copyright (c) 2002, Ole-Martin Mørk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package pmd.config;

import java.awt.Image;
import java.beans.*;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *  Description of {@link PMDOptionsSettings}.
 *
 * @author  Ole-Martin Mørk
 * @created  24. oktober 2002
 */
public class PMDOptionsSettingsBeanInfo extends SimpleBeanInfo
{

    /**
     * Returns the description of the PMD properties
     * @return the description of the rulesets property
     */
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try {
			PropertyDescriptor rulesets = new PropertyDescriptor( "rulesets", PMDOptionsSettings.class );
			rulesets.setDisplayName( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "PROP_rulesets" ) );
			rulesets.setShortDescription( NbBundle.getMessage( PMDOptionsSettingsBeanInfo.class, "HINT_rulesets" ) );
			return new PropertyDescriptor[]{rulesets};
		}
		catch( IntrospectionException ie ) {
			ErrorManager.getDefault().notify( ie );
			return null;
		}
	}

    /**
     * Returns the icon for the property.
     * @return the icon
     * @param type the type of icon
     */
	public Image getIcon( int type )
	{
		if( type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16 ) {
			return Utilities.loadImage( "pmd/resources/PMDOptionsSettingsIcon.gif" );
		}
		else {
			return Utilities.loadImage( "pmd/resources/PMDOptionsSettingsIcon32.gif" );
		}
	}
}
