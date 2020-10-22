/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public abstract class VFTestContstants {
    private static final Path ROOT_PATH = Paths.get("src", "test", "resources", "net", "sourceforge",
            "pmd", "lang", "vf").toAbsolutePath();

    public static final Path SFDX_PATH = ROOT_PATH.resolve("metadata-sfdx");

    public static final Path MDAPI_PATH = ROOT_PATH.resolve("metadata-mdapi");
    public static final List<String> ABSOLUTE_MDAPI_OBJECTS_DIRECTORIES =
            Collections.singletonList(MDAPI_PATH.resolve("objects").toAbsolutePath().toString());

    public static final List<String> RELATIVE_APEX_DIRECTORIES = Collections.singletonList(".." + File.separator + "classes");
    public static final List<String> RELATIVE_OBJECTS_DIRECTORIES = Collections.singletonList(".." + File.separator + "objects");
}
