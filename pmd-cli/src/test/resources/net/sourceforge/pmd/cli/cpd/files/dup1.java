import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class dup1 {
    static final Logger LOG = LoggerFactory.getLogger(dup1.class);

    public static void main(String[] args) {
        if(LOG.isInfoEnabled()) {
            LOG.info("Test1");
            LOG.info("Test2");
            LOG.info("Test3");
            LOG.info("Test4");
            LOG.info("Test5");
            LOG.info("Test6");
            LOG.info("Test7");
            LOG.info("Test8");
            LOG.info("Test9");
        }
    }
}