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

/**
 * Command Processor interface. A command processor implements a way to execute
 * a processable command. For instance the default command processor simply call
 * the execute method of the command. We could imagine a remote command
 * processor that could sent the command to a remote system, execute the command
 * and send back the result. The trick is that the command doesn't know where
 * and how it will be executed.
 */
import java.rmi.Remote;

public interface CommandProcessor extends Remote {
    
    /**
     * Execute a command
     * @param aCommand is the processable command to execute
     * @throws CommandException if any unexpected condition is met.
     */
    void processCommand(AbstractProcessableCommand aCommand) throws CommandException;
    
    /**
     * Wait for a command to finish
     * @param aCommand is the processable command to wait for
     * @throws CommandException if any unexpected condition is met.
     */
    void waitCommandToFinish(AbstractProcessableCommand aCommand) throws CommandException;
}
