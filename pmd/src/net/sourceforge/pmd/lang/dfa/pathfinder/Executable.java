/*
 * Created on 09.08.2004
 */
package net.sourceforge.pmd.lang.dfa.pathfinder;


/**
 * @author raik
 *         <p/>
 *         Will be executed if PathFinder finds a path.
 */
public interface Executable {

    void execute(CurrentPath path);
}
