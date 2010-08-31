package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Brian Remedios
 *
 */
public class ChangeRecord<T extends Object> {

	final List<T> additions = new ArrayList<T>();
	final List<T> removals = new ArrayList<T>();
	final List<T> changes = new ArrayList<T>();
	
	public ChangeRecord() {	}
	
	public boolean hasAdditions() {
		return !additions.isEmpty();
	}
	
	public boolean hasRemovals() {
		return !removals.isEmpty();
	}
	
	public boolean hasChanges() {
		return !changes.isEmpty();
	}
	
	public void added(T record) {
		additions.add(record);
	}
	
	public void added(Collection<T> record) {
		additions.addAll(record);
	}
	
	public void removed(T record) {
		removals.add(record);
	}
	
	public void removed(Collection<T> record) {
		removals.addAll(record);
	}
	
	public void changed(T record) {
		changes.add(record);
	}
	
	public void changed(Collection<T> record) {
		changes.addAll(record);
	}
	
	public void mergeWith(ChangeRecord<T> otherRecord) {
		added(otherRecord.additions);
		removed(otherRecord.removals);
		changed(otherRecord.changes);
	}
	
}
