/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PmdBanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmdBanner.class);

    private static final String BANNER_RESOURCE = "/net/sourceforge/pmd/cli/internal/banner.txt";

    private PmdBanner() {}

    public static List<String> loadBanner() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader bannerReader = new BufferedReader(
                new InputStreamReader(PmdBanner.class.getResourceAsStream(BANNER_RESOURCE), StandardCharsets.UTF_8))) {
            String line = bannerReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bannerReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.debug("Couldn't load banner", e);
        }
        return lines;
    }
}
