/*
 *  RuleEditor.java
 *
 *  Created on 18. november 2002, 21:50
 */
package pmd.config.ui;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.Rule;

/**
 * @author ole martin mørk
 * @created 18. november 2002
 */
public class RuleEditor extends PropertyEditorSupport {

	/**
	 * Gets the customEditor attribute of the RuleEditor object
	 *
	 * @return The customEditor value
	 */
	public Component getCustomEditor() {
		return new RuleEnabler( this );
	}


	/**
	 * Description of the Method
	 *
	 * @return Description of the Return Value
	 */
	public boolean supportsCustomEditor() {
		return true;
	}


	/**
	 * Gets the value attribute of the RuleEditor object
	 *
	 * @return The value value
	 */
	public Object getValue() {
		String string = getValueAsText( SelectedListModel.getSelectedListModelInstance().getData() );
		return string;
	}


	/**
	 * Gets the asText attribute of the RuleEditor object
	 *
	 * @return The asText value
	 */
	public String getAsText() {
		return getValue().toString();
	}


	/**
	 * Sets the value attribute of the RuleEditor object
	 *
	 * @param obj The new value value
	 */
	public void setValue( Object obj ) {
		if( obj != null ) {
			SelectedListModel.getSelectedListModelInstance().setData( ( String )obj );
			AvailableListModel.getInstance().refresh();
			AvailableListModel.getInstance().getData().removeAll( SelectedListModel.getSelectedListModelInstance().getData() );
		}
	}
	public void setAsText( String string ) throws IllegalArgumentException {}

	/**
	 * Gets the valueAsText attribute of the RuleEditor object
	 *
	 * @param value Description of the Parameter
	 * @return The valueAsText value
	 */
	private String getValueAsText( List value ) {
		StringBuffer buffer = new StringBuffer();
		if( value != null ) {
			Iterator iterator = value.iterator();
			while( iterator.hasNext() ) {
				Rule rule = ( Rule )iterator.next();
				buffer.append( rule.getName() ).append( ", " );
			}
		}
		return String.valueOf( buffer );
	}
}