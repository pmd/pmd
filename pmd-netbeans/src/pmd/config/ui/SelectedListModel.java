/*
 * SelectedListModel.java
 *
 * Created on 16. november 2002, 13:16
 */

package pmd.config.ui;

import java.util.ArrayList;
import java.util.Iterator;
import net.sourceforge.pmd.Rule;

/**
 *
 * @author  ole martin mørk
 */
public class SelectedListModel extends AvailableListModel {
	
	private static SelectedListModel listmodel = new SelectedListModel();
	
	/** Creates a new instance of SelectedListModel */
	private SelectedListModel() {
		super();
	}
	
	public static AvailableListModel getInstance() {
		return listmodel;
	}
	
	public void refresh() {
		setList( new ArrayList() );
	}
	
	public void setData( String rules ) {
		Iterator iterator = AvailableListModel.getInstance().getData().iterator();
		while( iterator.hasNext() ) {
			Rule rule = (Rule)iterator.next();
			if( rules.indexOf( rule.getName() + ", " ) > -1 ) {
				add( rule );
			}
		}
	}
	
	public static SelectedListModel getSelectedListModelInstance() {
		return listmodel;
	}
}
