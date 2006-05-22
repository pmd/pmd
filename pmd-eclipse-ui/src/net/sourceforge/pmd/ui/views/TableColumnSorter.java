package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Displays an Arrow-Image in a TableColumn, that shows in which Direction the Column is sorted
 * 
 * @author SebastianRaffel ( 22.05.2005 )
 */
public class TableColumnSorter extends ViewerSorter {

    /**
     * Constructor
     * 
     * @param column, the column to sort
     * @param order, the Direction to sort by, -1 (desc) or 1 (asc)
     */
    public TableColumnSorter(TableColumn column, int order) {
        Image image = null;

        if (order == 1) {
            image = PMDUiPlugin.getDefault().getImage("arrow_up", PMDUiConstants.ICON_LABEL_ARRUP);
        } else if (order == -1) {
            image = PMDUiPlugin.getDefault().getImage("arrow_dn", PMDUiConstants.ICON_LABEL_ARRDN);
        }

        // we delete all other Images
        // and set the current one
        TableColumn[] columns = column.getParent().getColumns();
        for (int i = 0; i < columns.length; i++) {
            columns[i].setImage(null);
        }

        column.setImage(image);
    }
}
