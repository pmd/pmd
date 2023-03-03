/**
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

@InternalApi
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

    @Deprecated
    @InternalApi
    public Renderer getRenderer() {
        return renderer;
    }

    @Deprecated
    @InternalApi
    public void start(String baseDir) {

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

    @Deprecated
    @InternalApi
    public void end(Report errorReport) {
        try {
            renderer.renderFileReport(errorReport);
            renderer.end();
            if (toConsole) {
                writer.flush();
            } else {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    @Deprecated
    @InternalApi
    public boolean isNoOutputSupplied() {
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

    // FIXME - hm, what about this consoleRenderer thing... need a test for this
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
        // in case of pipe or redirect, no interactive console.
        if (console != null) {
            try {
                Object res = FieldUtils.readDeclaredField(console, "cs", true);
                if (res instanceof Charset) {
                    return ((Charset) res).name();
                }
            } catch (IllegalArgumentException | ReflectiveOperationException ignored) {
                // fall-through
            }

            // Maybe this is Java17? Then there will be
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/Console.html#charset()
            // instead of the field "cs".
            try {
                Charset charset = (Charset) MethodUtils.invokeMethod(console, "charset");
                return charset.name();
            } catch (IllegalArgumentException | ReflectiveOperationException ignored) {
                // fall-through
            }
            return getNativeConsoleEncoding();
        }
        return null;
    }

    private static String getNativeConsoleEncoding() {
        try {
            Object res = MethodUtils.invokeStaticMethod(Console.class, "encoding");
            if (res instanceof String) {
                return (String) res;
            }
        } catch (IllegalArgumentException | ReflectiveOperationException ignored) {
            // fall-through
        }
        return null;
    }

    @Deprecated
    @InternalApi
    public GlobalAnalysisListener newListener(Project project) throws IOException {
        start(project.getBaseDir().toString());
        Renderer renderer = getRenderer();

        return new GlobalAnalysisListener() {
            final GlobalAnalysisListener listener = renderer.newListener();

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
