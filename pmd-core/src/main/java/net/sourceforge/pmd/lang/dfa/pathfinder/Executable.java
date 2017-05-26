/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa.pathfinder;

/**
 * Will be executed if PathFinder finds a path.
 *
 * @author raik
 * @since Created on 09.08.2004
 */
public interface Executable {

    void execute(CurrentPath path);
}
