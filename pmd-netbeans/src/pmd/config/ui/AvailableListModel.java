/*
 *  ListModel.java
 *
 *  Created on 14. november 2002, 21:26
 */
package pmd.config.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractListModel;


import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 * @author ole martin mørk
 * @created 16. november 2002
 */
public class AvailableListModel extends AbstractListModel {
	private static AvailableListModel listmodel = new AvailableListModel();
	private List list = new ArrayList();


	/**
	 * Gets the instance attribute of the AvailableListModel class
	 *
	 * @return The instance value
	 */
	public static AvailableListModel getInstance() {
		return listmodel;
	}


	/** Creates a new instance of ListModel */
	protected AvailableListModel() {
		refresh();
	}


	/**
	 * Gets the elementAt attribute of the AvailableListModel object
	 *
	 * @param param Description of the Parameter
	 * @return The elementAt value
	 */
	public Object getElementAt( int param ) {
		return ( ( Rule )list.get( param ) );
	}


	/**
	 * Gets the size attribute of the AvailableListModel object
	 *
	 * @return The size value
	 */
	public int getSize() {
		return list.size();
	}


	/**
	 * Sets the list attribute of the AvailableListModel object
	 *
	 * @param list The new list value
	 */
	public void setList( List list ) {
		this.list = list;
	}


	/**
	 * Description of the Method
	 *
	 * @param o Description of the Parameter
	 */
	public void add( Object o ) {
		if( !list.contains( o ) ) {
			list.add( ( Rule )o );
			fireIntervalAdded( this, list.size(), list.size() );
		}
	}


	/**
	 * Description of the Method
	 *
	 * @param o Description of the Parameter
	 */
	public void remove( Object o ) {
		int i = list.indexOf( o );
		list.remove( o );
		fireIntervalRemoved( this, i, i );
	}


	/** Description of the Method */
	public void removeAll() {
		int i = list.size();
		list.clear();
		fireIntervalRemoved( this, 0, i );
	}


	/**
	 * Adds a feature to the All attribute of the AvailableListModel object
	 *
	 * @param coll The feature to be added to the All attribute
	 */
	public void addAll( Collection coll ) {
		int i = list.size();
		list.addAll( coll );
		fireIntervalAdded( this, i, list.size() );
	}


	/**
	 * Gets the data attribute of the AvailableListModel object
	 *
	 * @return The data value
	 */
	public List getData() {
		return list;
	}

	public void refresh() {
		try {
			RuleSetFactory ruleSetFactory = new RuleSetFactory();
			Iterator iterator = ruleSetFactory.getRegisteredRuleSets();
			while( iterator.hasNext() ) {
				RuleSet ruleset = ( RuleSet )iterator.next();
				Set rules = ruleset.getRules();
				addAll( rules );
			}
		}
		catch( RuleSetNotFoundException e ) {
			e.printStackTrace();
		}
	}
}
