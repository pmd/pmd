/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.pathfinder;


/**
 * Created on 09.08.2004
 * @author raik
 *         <p/>
 *         Will be executed if PathFinder finds a path.
 */
public interface Executable {

    void execute(CurrentPath path);
}
