/*
 * Patterns Library - Implementation of various design patterns
 * Copyright (C) 2004 Philippe Herlin 
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 * Contact: philippe_herlin@yahoo.fr 
 * 
 */
package name.herlin.command;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Default command processor strategy implementation. The default strategy is
 * to search for the command among the registered commands (from the bundle). If
 * not found, query the command itself its preferred processor. If none, return
 * the framework default command processor.
 */
public class DefaultCommandProcessorStrategy implements CommandProcessorStrategy {
    private CommandProcessor defaultCommandProcessor = new DefaultCommandProcessor();
    private Map registeredCommandProcessors = new Hashtable();

    /**
     * Default constructor. Load registered command from bundle.
     */
    public DefaultCommandProcessorStrategy() {
        loadBundle();
    }

    /**
     * @param aCommand a command for which to finf a processor
     * @return a processor for the specified command according to the strategy.
     */
    public CommandProcessor getCommandProcessor(ProcessableCommand aCommand) {
        CommandProcessor aProcessor = getRegisteredCommandProcessor(aCommand); 

        if (aProcessor == null) {
            aProcessor = aCommand.getPreferredCommandProcessor();
        }

        if (aProcessor == null) {
            aProcessor = this.defaultCommandProcessor;
        }

        return aProcessor;

    }

    /**
     * @param aCommand a command to search in the registered command map.
     * @return a command processor from a registered command
     */
    protected CommandProcessor getRegisteredCommandProcessor(ProcessableCommand aCommand) {
        CommandProcessor aProcessor = null;

        try {
            String processorClassName = (String) registeredCommandProcessors.get(aCommand.getName());
            if (processorClassName != null) {
                Class clazz = Class.forName(processorClassName);
                aProcessor = (CommandProcessor) clazz.newInstance();
            }
        } catch (ClassNotFoundException e) {
            // ignore
        } catch (InstantiationException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }

        return aProcessor;

    }

    /**
     * Load the command processor strategy bundle. Automatically registered
     * found classes.
     */
    private void loadBundle() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(COMMAND_PROCESSOR_STRATEGY_BUNDLE);
            Enumeration e = bundle.getKeys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = bundle.getString(key);
                this.registeredCommandProcessors.put(key, value);
            }
        } catch (RuntimeException e) {
            // ignore bundle not found
        }
    }

}
