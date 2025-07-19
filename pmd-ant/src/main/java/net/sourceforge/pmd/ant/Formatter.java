/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.FileNameRenderer;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.ListenerInitializer;

/**
 * Part of PMD Ant task configuration. Setters of this class are interpreted by Ant as properties
 * settable in the XML. This is therefore published API.
 *
 * <p>This class is used to configure a specific {@link Renderer} for outputting the violations. This is called
 * a formatter in PMD Ant task configuration and might look like this:
 *
 * <pre>{@code
 * <pmd>
 *   <formatter type="html" toFile="${build}/pmd_report.html"/>
 * </pmd>
 * }</pre>
 *
 * @see PMDTask#addFormatter(Formatter)
 */
public class Formatter {

    private File toFile;
    private String type;
    private boolean toConsole;
    private boolean showSuppressed;
    private final List<Parameter> parameters = new ArrayList<>();
    private Writer writer;
    private Renderer renderer;

    public void setShowSuppressed(boolean value) {
        this.showSuppressed = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public void setToConsole(boolean toConsole) {
        this.toConsole = toConsole;
    }

    public void addConfiguredParam(Parameter parameter) {
        this.parameters.add(parameter);
    }

    private void start(String baseDir) {

        Properties properties = createProperties();

        Charset charset;
        {
            String s = (String) properties.get("encoding");
            if (null == s) {

                if (toConsole) {
                    s = getConsoleEncoding();
                    if (null == s) {
                        s = System.getProperty("file.encoding");
                    }
                }

                if (null == s) {
                    charset = StandardCharsets.UTF_8;
                } else {
                    charset = Charset.forName(s);
                }

                // Configures the encoding for the renderer.
                final Parameter parameter = new Parameter();
                parameter.setName("encoding");
                parameter.setValue(charset.name());
                parameters.add(parameter);
            } else {
                charset = Charset.forName(s);
            }
        }

        try {
            if (toConsole) {
                writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
            }
            if (toFile != null) {
                writer = getToFileWriter(baseDir, toFile, charset);
            }
            renderer = createRenderer();
            renderer.setWriter(writer);
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    boolean isNoOutputSupplied() {
        return toFile == null && !toConsole;
    }

    @Override
    public String toString() {
        return "file = " + toFile + "; renderer = " + type;
    }

    private static String[] validRendererCodes() {
        return RendererFactory.supportedRenderers().toArray(new String[0]);
    }

    private static String unknownRendererMessage(String userSpecifiedType) {
        String[] typeCodes = validRendererCodes();
        StringBuilder sb = new StringBuilder(100);
        sb.append("Formatter type must be one of: '").append(typeCodes[0]);
        for (int i = 1; i < typeCodes.length; i++) {
            sb.append("', '").append(typeCodes[i]);
        }
        sb.append("', or a class name; you specified: ").append(userSpecifiedType);
        return sb.toString();
    }

    Renderer createRenderer() {
        if (StringUtils.isBlank(type)) {
            throw new BuildException(unknownRendererMessage("<unspecified>"));
        }

        Properties properties = createProperties();
        Renderer renderer = RendererFactory.createRenderer(type, properties);
        renderer.setShowSuppressedViolations(showSuppressed);
        return renderer;
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        for (Parameter parameter : parameters) {
            properties.put(parameter.getName(), parameter.getValue());
        }
        return properties;
    }

    private static Writer getToFileWriter(String baseDir, File toFile, Charset charset) throws IOException {
        final File file;
        if (toFile.isAbsolute()) {
            file = toFile;
        } else {
            file = new File(baseDir + System.getProperty("file.separator") + toFile.getPath());
        }

        OutputStream output = null;
        Writer writer = null;
        boolean isOnError = true;
        try {
            output = Files.newOutputStream(file.toPath());
            writer = new BufferedWriter(new OutputStreamWriter(output, charset));
            isOnError = false;
        } finally {
            if (isOnError) {
                IOUtil.closeQuietly(output);
                IOUtil.closeQuietly(writer);
            }
        }
        return writer;
    }

    private static String getConsoleEncoding() {
        Console console = System.console();
        // in case of pipe or redirect, no interactive console, we get null
        if (console != null) {
            // Since Java 22, this returns a console even for redirected streams.
            // In that case, we need to check Console.isTerminal()
            // https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/io/Console.html#isTerminal()
            // See: JLine As The Default Console Provider (JDK-8308591)
            try {
                Method method = Console.class.getMethod("isTerminal");
                Object isTerminal = method.invoke(console);
                if (isTerminal instanceof Boolean && !(Boolean) isTerminal) {
                    // stop here, we don't have an interactive console.
                    return null;
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {
                // fall-through - we use a Java Runtime < 22.
            }

            // Maybe this is Java17+? Then there will be a public method charset()
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/Console.html#charset()
            try {
                Method method = Console.class.getMethod("charset");
                Object charset = method.invoke(console);
                if (charset instanceof Charset) {
                    return ((Charset) charset).name();
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {
                // fall-through
            }

            {
                // try to use the system property "sun.jnu.encoding", which is the platform encoding.
                // this property is not specified and might not always be available, but it is for
                // openjdk 11: https://github.com/openjdk/jdk11u/blob/cee8535a9d3de8558b4b5028d68e397e508bef71/src/java.base/share/native/libjava/System.c#L384
                // if it exists, we use it - this avoids illegal reflective access below.
                String jnuEncoding = System.getProperty("sun.jnu.encoding");
                if (jnuEncoding != null) {
                    return jnuEncoding;
                }
            }

            // the following parts are accessing private/protected fields via reflection
            // this should work with Java 8 and 11. With Java 11, you'll see warnings abouts
            // illegal reflective access, see #1860. However, the access still works.

            // Fall-Back 1: private field "cs" in java.io.Console
            try {
                Field field = Console.class.getDeclaredField("cs");
                field.setAccessible(true);
                Object csField = field.get(console);
                if (csField instanceof Charset) {
                    return ((Charset) csField).name();
                }
            } catch (IllegalArgumentException | ReflectiveOperationException ignored) {
                // fall-through
            }

            // Fall-Back 2: private native method "encoding()" in java.io.Console
            try {
                Method method = Console.class.getDeclaredMethod("encoding");
                method.setAccessible(true);
                Object encoding = method.invoke(console);
                if (encoding instanceof String) {
                    return (String) encoding;
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {
                // fall-through
            }
        }
        // we couldn't determine the correct platform console encoding
        return null;
    }

    GlobalAnalysisListener newListener(Project project) throws IOException {
        start(project.getBaseDir().toString());
        return new GlobalAnalysisListener() {
            final GlobalAnalysisListener listener = renderer.newListener();

            @Override
            public ListenerInitializer initializer() {
                return new ListenerInitializer() {
                    @Override
                    public void setFileNameRenderer(FileNameRenderer fileNameRenderer) {
                        renderer.setFileNameRenderer(fileNameRenderer);
                    }
                };
            }

            @Override
            public FileAnalysisListener startFileAnalysis(TextFile file) {
                return listener.startFileAnalysis(file);
            }

            @Override
            public void close() throws Exception {
                listener.close();
                if (!toConsole) {
                    writer.close();
                }
            }
        };
    }
}
