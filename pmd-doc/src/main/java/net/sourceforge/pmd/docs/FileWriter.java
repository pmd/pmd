/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileWriter {

    void write(Path path, List<String> lines) throws IOException;
}
