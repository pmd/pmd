/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

abstract class BaseCommandTest<T> {

    protected abstract T createCommand();
    
    protected abstract void addStandardParams(List<String> argList);
    
    protected void assertError(final String... params) {
        final T cmd = createCommand();
        final ParseResult parseResult = parseCommand(cmd, params);
        assertThat(parseResult.errors(), Matchers.not(Matchers.empty()));
    }

    protected T setupAndParse(final String... params) {
        final T cmd = createCommand();
        final ParseResult parseResult = parseCommand(cmd, params);

        assertThat(parseResult.errors(), Matchers.empty());

        return cmd;
    }
    
    private ParseResult parseCommand(final Object cmd, final String... params) {
        final List<String> argList = new ArrayList<>();
        argList.addAll(Arrays.asList(params));

        addStandardParams(argList);

        final CommandLine commandLine = new CommandLine(cmd)
            .setCaseInsensitiveEnumValuesAllowed(true);
        
        // Collect errors instead of simply throwing during parsing
        commandLine.getCommandSpec().parser().collectErrors(true);
        
        return commandLine.parseArgs(argList.toArray(new String[0]));
    }

}
