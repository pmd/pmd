/*
 * RuleSetListModel.java
 *
 * Created on 21. februar 2003, 21:36
 */

package pmd.config.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author  ole martin mørk
 */
public class RuleSetListModel extends AbstractListModel{
	
	private List list = new ArrayList();
	
	public Object getElementAt(int param) {
		return list.get( param );
	}	

	public int getSize() {
		return list.size();
	}	
	
	public List getList() {
		return list;
	}
	
	public boolean contains( Object o ) {
		return list.contains( o );
	}
	
	public void removeElementAt( int i ) {
		list.remove( i );
		fireIntervalRemoved( this, i, i+1 );
	}
	
	public void addElement( Object o ) {
		list.add( o );
		fireIntervalAdded(o, getSize()-1, getSize() );
	}
	
	public void setList( List list ) {
		this.list = list;
		fireIntervalAdded( list, 0, list.size() );
	}
	
}
