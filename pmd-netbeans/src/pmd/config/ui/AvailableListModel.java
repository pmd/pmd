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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

import net.sourceforge.pmd.Rule;
import pmd.config.ConfigUtils;

/**
 * The datamodel for the available rules list
 *
 * @author ole martin mørk
 * @created 16. november 2002
 */
public class AvailableListModel extends AbstractListModel {
	/** The instance */
	private static AvailableListModel listmodel = new AvailableListModel();
	/** The data in the list */
	private List list = new ArrayList();


	/**
	 * Gets the instance of the AvailableListModel class
	 *
	 * @return The instance
	 */
	public static AvailableListModel getInstance() {
		return listmodel;
	}


	/** Creates a new instance of ListModel */
	protected AvailableListModel() {
		refresh();
	}


	/**
	 * Gets the element at the specified index
	 *
	 * @param index index of the list
	 * @return The list element
	 */
	public Object getElementAt( int index ) {
		return ( ( Rule )list.get( index ) );
	}


	/**
	 * Gets the size of the list
	 *
	 * @return The size value
	 */
	public int getSize() {
		return list.size();
	}


	/**
	 * Sets the list.
	 *
	 * @param list The new list value
	 */
	public void setList( List list ) {
		this.list = list;
	}


	/**
	 * Adds object <CODE>o</CODE> to the list
	 *
	 * @param o The parameter to add to the list
	 */
	public void add( Object o ) {
		if( !list.contains( o ) ) {
			list.add( ( Rule )o );
			Collections.sort( list, new RuleComparator() );
			fireIntervalAdded( this, list.size(), list.size() );
		}
	}


	/**
	 * Removes <code>o</code> from the list
	 *
	 * @param o the object to remove
	 */
	public void remove( Object o ) {
		int i = list.indexOf( o );
		list.remove( o );
		Collections.sort( list, new RuleComparator() );
		fireIntervalRemoved( this, i, i );
	}


	/** Removes all elements in the list */
	public void removeAll() {
		int i = list.size();
		list.clear();
		fireIntervalRemoved( this, 0, i );
	}


	/**
	 * Adds all data in <CODE>coll</CODE> to the list
	 *
	 * @param coll The collection containing Rule elements
	 */
	public void addAll( Collection coll ) {
		int i = list.size();
		list.addAll( coll );
		Collections.sort( list, new RuleComparator() );
		fireIntervalAdded( this, i, list.size() );
	}


	/**
	 * Returns the data for the list
	 *
	 * @return The data
	 */
	public List getList() {
		return list;
	}


	/** Resets the list */
	public void refresh() {
		list = ConfigUtils.getAllAvailableRules();
		Collections.sort( list, new RuleComparator() );
	}
}
