package test.net.sourceforge.pmd.dfa;

import junit.framework.TestCase;
import net.sourceforge.pmd.dfa.Linker;
import net.sourceforge.pmd.dfa.IProcessableStructure;
import net.sourceforge.pmd.dfa.LinkerException;
import net.sourceforge.pmd.dfa.SequenceException;

import java.util.List;

public class LinkerTest extends TestCase {

    private static class NullProcessableStructure implements IProcessableStructure {
        public List getBraceStack() {return null;}
        public List getCBRStack() {return null;}
    }

    public void testComputePaths() {
        try {
            Linker l = new Linker(new NullProcessableStructure());
            l.computePaths();
        } catch (LinkerException le) {
            // cool
        } catch (SequenceException se) {
            fail("Unexpected SequenceException");
        }
    }
}
