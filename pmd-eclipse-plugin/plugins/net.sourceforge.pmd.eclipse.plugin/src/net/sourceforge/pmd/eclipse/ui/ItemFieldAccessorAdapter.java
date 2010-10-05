package net.sourceforge.pmd.eclipse.ui;

import org.eclipse.swt.graphics.Image;

public class ItemFieldAccessorAdapter<T extends Object, V extends Object> implements ItemFieldAccessor<T, V> {

	public T valueFor(V item) {	return null; }
	public Image imageFor(V item) { return null; }
	public String labelFor(V item) { return null; }
}
