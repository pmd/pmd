/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PmdThreadFactory implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "PmdThread " + counter.incrementAndGet());
    }

}
