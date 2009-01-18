package net.sourceforge.pmd.eclipse.ui.preferences.br;

/**
 * A callback f'n that informs a parent panel to adjust its layout or reset its minimum
 * size and thus its scrollable limits.
 * 
 * @author Brian Remedios
 */
public interface SizeChangeListener {
    void addedRows(int newRowCount);
}
