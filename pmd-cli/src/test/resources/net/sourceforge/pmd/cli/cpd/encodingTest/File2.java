import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File2 {
    public void dup() {
        static final Logger LOG = LoggerFactory.getLogger(File2.class);

        for (int j = 0; j < 10; j++) {
            if(LOG.isDebugEnabled()) {
                LOG.info(j + "ä");
            }
        }
    }
}