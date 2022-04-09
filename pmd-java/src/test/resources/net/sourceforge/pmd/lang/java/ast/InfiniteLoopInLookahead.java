/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.*;

public class InfiniteLoopInLookahead {

    public void exam1(List resList) {
        resList.forEach(a -> {
            resList.forEach(b -> {
                resList.forEach(c -> {
                    resList.forEach(d -> {
                        resList.forEach(e -> {
                            resList.forEach(f -> {
                                resList.forEach(g -> {
                                    resList.forEach(h -> {
                                        resList // note: missing semicolon -> parse error here...
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    }
}
