import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File1 {

    static final Logger LOG = LoggerFactory.getLogger(File1.class);

    public void dup() {
        for (int i = 0; i < 10; i++) {
            if(LOG.isInfoEnabled()){
                LOG.info(i + "ä");
            }
        }
    }
}