/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 *
 */
public interface GlobalAnalysisListener extends AutoCloseable {

    ThreadSafeAnalysisListener startFileAnalysis(DataSource file);


    static GlobalAnalysisListener tee(List<? extends GlobalAnalysisListener> list) {
        List<GlobalAnalysisListener> listeners = Collections.unmodifiableList(new ArrayList<>(list));
        return new GlobalAnalysisListener() {
            @Override
            public ThreadSafeAnalysisListener startFileAnalysis(DataSource file) {
                return ThreadSafeAnalysisListener.tee(CollectionUtil.map(listeners, it -> it.startFileAnalysis(file)));
            }

            @Override
            public void close() throws Exception {
                Exception composed = null;
                for (GlobalAnalysisListener it : list) {
                    try {
                        it.close();
                    } catch (Exception e) {
                        if (composed == null) {
                            composed = e;
                        } else {
                            composed.addSuppressed(e);
                        }
                    }
                }
                if (composed != null) {
                    throw composed;
                }
            }
        };
    }


}
