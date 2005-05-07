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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default command processor implementation. This processor simply call the
 * execute method of the command.
 */

public class DefaultCommandProcessor implements CommandProcessor {
    private static final Log log = LogFactory.getLog("name.herlin.command.DefaultCommandProcessor");

    /**
     * Execute the command.
     * 
     * @param aCommand
     *            the command to execute
     * @throws CommandException
     *             if an unexpected condidition occurred.
     */
    public void processCommand(final AbstractProcessableCommand aCommand) throws CommandException {
        log.debug("Beginning command " + aCommand.getName());
        if (aCommand.isReadyToExecute()) {
            aCommand.execute();
        } else {
            throw new UnsetInputPropertiesException();
        }

        log.debug("Ending command " + aCommand.getName());
    }

    /**
     * @see name.herlin.command.CommandProcessor#waitCommandToFinish(name.herlin.command.AbstractProcessableCommand)
     */
    public void waitCommandToFinish(final AbstractProcessableCommand aCommand) throws CommandException {
        // Do nothing because a default command executes synchronously
        // So when this method is executed the command has already terminated

    }
}