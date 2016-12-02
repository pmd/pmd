package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;

public class AtomRendererTest extends AbstractRendererTst {

	@Override
	public Renderer getRenderer() {
		return new AtomRenderer();
	}

	@Override
	public String getExpected() {
		return "n/a:1:1:\tblah" + PMD.EOL;
	}

	@Override
	public String getExpectedEmpty() {
		return "";
	}

	@Override
	public String getExpectedMultiple() {
		return "n/a:1:1:\tblah" + PMD.EOL + "n/a:1:1:\tblah" + PMD.EOL;
	}

	@Override
	public String getExpectedError(ProcessingError error) {
		return "";
	}

	public static junit.framework.Test suite() {
		return new junit.framework.JUnit4TestAdapter(AtomRendererTest.class);
	}
}