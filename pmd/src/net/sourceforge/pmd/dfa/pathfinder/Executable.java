/*
 * Created on 09.08.2004
 */
package net.sourceforge.pmd.dfa.pathfinder;

import java.util.List;


/**
 * @author raik
 *         <p/>
 *         Will be executed if PathFinder finds a path.
 */
public interface Executable {

    void execute(List path);
}
