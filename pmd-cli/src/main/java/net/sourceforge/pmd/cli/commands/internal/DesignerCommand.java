/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.cli.internal.CliExitCode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

@Command(name = "designer", showDefaultValues = true,
    versionProvider = DesignerVersionProvider.class,
    description = "The PMD visual rule designer")
public class DesignerCommand extends AbstractPmdSubcommand {

    @SuppressWarnings("unused")
    @Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionRequested;

    @Override
    protected CliExitCode execute() {
        final String[] rawArgs = spec.commandLine().getParseResult().expandedArgs().toArray(new String[0]);

        Path pmdDistDir = Paths.get(System.getenv("PMD_DIST_DIR"));
        Path pmdLibDir = pmdDistDir.resolve("lib");
        try (Stream<Path> files = Files.list(pmdLibDir)) {
            List<Path> uiJars = files.filter(p -> p.getFileName().toString().startsWith("pmd-ui")).collect(Collectors.toList());
            System.out.println("uiJars = " + uiJars);
            if (uiJars.isEmpty()) {
                downloadLatestPmdDesigner(pmdLibDir);
            }
            if (uiJars.size() > 1) {
                throw new RuntimeException("Multiple pmd-ui jars detected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Class<?> designerStarter = DesignerCommand.class.getClassLoader().loadClass("net.sourceforge.pmd.util.fxdesigner.DesignerStarter");
            Method launchGui = designerStarter.getDeclaredMethod("launchGui", String[].class);
            Object result = launchGui.invoke(null, (Object) rawArgs);
            Method getCode = result.getClass().getMethod("getCode");
            int exitCode = (int) getCode.invoke(result);
            return exitCode == 0 ? CliExitCode.OK : CliExitCode.ERROR;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadLatestPmdDesigner(Path pmdLibDir) throws IOException {
        URI latestPmdDesignerRelease = URI.create("https://api.github.com/repos/pmd/pmd-designer/releases/latest");
        URLConnection urlConnection = latestPmdDesignerRelease.toURL().openConnection();
        String downloadUrl = null;
        String jarFileName = null;
        try (InputStream inputStream = urlConnection.getInputStream()) {
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String tagName = jsonElement.getAsJsonObject().get("tag_name").getAsString();
            System.out.println("tagName = " + tagName);
            JsonArray assets = jsonElement.getAsJsonObject().get("assets").getAsJsonArray();
            Map<String, String> assetUrls = new HashMap<>();
            for (int i = 0; i < assets.size(); i++) {
                String name = assets.get(i).getAsJsonObject().get("name").getAsString();
                String url = assets.get(i).getAsJsonObject().get("browser_download_url").getAsString();
                assetUrls.put(name, url);
            }
            System.out.println("assetUrls = " + assetUrls);

            jarFileName = "pmd-ui-" + tagName + ".jar";
            downloadUrl = assetUrls.get(jarFileName);
        }

        URLConnection urlConnection1 = URI.create(downloadUrl).toURL().openConnection();
        Path target = pmdLibDir.resolve(jarFileName);
        try (BufferedInputStream in = new BufferedInputStream(urlConnection1.getInputStream());
             OutputStream out = Files.newOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                out.write(buffer, 0, read);
            }
        }
        System.out.println("Downloaded to " + target);
    }
}

class DesignerVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        Class<?> designerVersion = DesignerCommand.class.getClassLoader().loadClass("net.sourceforge.pmd.util.fxdesigner.DesignerVersion");
        String currentVersion = (String) designerVersion.getDeclaredMethod("getCurrentVersion").invoke(null);
        return new String[] { "PMD Rule Designer " + currentVersion };
    }
    
}
