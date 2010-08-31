package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.RulePriority;

import org.eclipse.swt.graphics.Image;

public class PriorityFieldAccessorAdapter<T extends Object> implements PriorityFieldAccessor<T> {

	public T valueFor(RulePriority priority) {	return null; }
	public Image imageFor(RulePriority priority) { return null; }
	public String labelFor(RulePriority priority) {	return null; }
}
