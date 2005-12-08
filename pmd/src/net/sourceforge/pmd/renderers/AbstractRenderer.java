package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;

public abstract class AbstractRenderer implements Renderer {

    protected boolean showSuppressedViolations = true;

    public void showSuppressedViolations(boolean show) {
        this.showSuppressedViolations = show;
    }

    public abstract String render(Report report);
}
