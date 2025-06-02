/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport;

/**
 * Minimized version of https://github.com/openjdk/jdk/blob/jdk-11%2B28/src/java.base/share/classes/java/util/concurrent/Flow.java
 */
public class ConcFlow {

    public interface Subscription { }

    public interface Subscriber<T> { }

    public interface Publisher<T> { }
}
