package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

/**
 *
 * @author Brian Remedios
 */
public class ListManager {

	private final List 	 list;
	private final Button upButton;
	private final Button downButton;
	private final Button deleteButton;

	public ListManager(List theList, Button theUpButton, Button theDownButton, Button theDeleteButton) {

		list = theList;
		upButton = theUpButton;
		downButton = theDownButton;
		deleteButton = theDeleteButton;

		registerListeners();
		updateButtonStates();
	}

	private void registerListeners() {
		list.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event arg0) {
				updateButtonStates();
			}

		});
		upButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) { shiftUp(); }
		});
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) { shiftDown(); }
		});
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) { delete(); }
		});
	}

	private void shiftUp() {
		// TODO
		updateButtonStates();
	}

	private void shiftDown() {
		// TODO
		updateButtonStates();
	}

	private void delete() {
		int[] indices = list.getSelectionIndices();
		list.remove(indices);

		updateButtonStates();
	}

	private void updateButtonStates() {

		if (!hasSelection()) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return;
		}

		upButton.setEnabled(hasSelectionIndex(0));
		downButton.setEnabled(hasSelectionIndex(list.getItemCount()-1));
		deleteButton.setEnabled(true);
	}

	private boolean hasSelection() {
		return list.getSelectionCount() > 0;
	}


	private boolean hasSelectionIndex(int index) {

		for (int i : list.getSelectionIndices()) {
			if (i == index) return false;
		}
		return true;
	}
}
