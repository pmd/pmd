package net.sourceforge.pmd.dfa;

import java.util.List;

public interface IProcessableStructure {
    List getBraceStack();

    List getContinueBreakReturnStack();
}
