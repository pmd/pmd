/*
 *  Copyright (c) 2002-2003, Ole-Martin Mørk
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
package pmd.config.ui;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/** The PropertyEditor of the Rule property
 * @author ole martin mørk
 * @created 18. november 2002
 */
public class RuleSetChooserEditor extends PropertyEditorSupport {
	RuleSetChooser chooser = new RuleSetChooser( this );

	/** 
	 * Returns the custom editor of the Rule property
	 * @return the editor
	 */
	public Component getCustomEditor() {
		return chooser;
	}


	/** 
	 * Returns true
	 * @return true
	 */
	public boolean supportsCustomEditor() {
		return true;
	}


	/** 
	 * Returns the selected rules
	 * @return the selected rules
	 */
	public Object getValue() {
		ListModel model = chooser.getListModel();
		StringBuffer buffer = new StringBuffer();
		for( int i = 0; i < model.getSize(); i++ ) {
			buffer.append( model.getElementAt( i ) ).append( ", " );
		}
		if( buffer.length() > 2 ) {
			buffer.delete( buffer.length() - 2, buffer.length() );
		}
		buffer.append( ", " ).append( chooser.includeStandardRules() );
		return buffer.toString();
	}


	/** 
	 * Returns a string representation of the property value
	 * @return the property as text
	 */
	public String getAsText() {
		return getValue().toString();
	}


	/** 
	 * Sets the value to be edited in the editor
	 * @param obj The new value
	 */
	public void setValue( Object obj ) {
		if( obj != null ) {
			DefaultListModel model = chooser.getListModel();
			StringTokenizer tokenizer = new StringTokenizer( obj.toString(), "," );
			int tokens = tokenizer.countTokens();
			for( int i = 0; i < tokens - 1 && tokenizer.hasMoreTokens(); i++ ) {
				model.addElement( tokenizer.nextToken().trim() );
			}
			if( tokenizer.hasMoreTokens() ) {
				chooser.setIncludeStandardRules(
					Boolean.valueOf( tokenizer.nextToken().trim() ).equals( Boolean.TRUE ) );
			}
			
		}
	}


	/** 
	 * Not implemented
	 * @param string the text
	 * @exception IllegalArgumentException never
	 */
	public void setAsText( String string ) throws IllegalArgumentException { }
}