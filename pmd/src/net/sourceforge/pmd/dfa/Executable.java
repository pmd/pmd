/*
 * Created on 09.08.2004
 */
package net.sourceforge.pmd.dfa;

import java.util.List;


/**
 * @author raik
 *         <p/>
 *         Will be executed if PathFinder finds a path.
 */
public interface Executable {

    public void execute(List path);
}
