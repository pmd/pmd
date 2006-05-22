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
 * A command interface
 */
public interface Command extends java.io.Serializable {
    
    /**
     * A command can execute. If a technical error or unexpected condition
     * occurred, a command may throw a CommandException. The execution method
     * returns nothing. It is expected that command result is returned via
     * output properties.
     * @throws CommandException on unexpected condition
     */
    void performExecute() throws CommandException;
    
    /**
     * @return true if the command is a read only command, that is it doesn't
     * apply any modification to the underlying model.
     */
    boolean isReadOnly();
    
    /**
     * @return true if the command is ready to execute. It is expected that
     * concrete commands are not ready by default and switched to ready only
     * when all input properties are set. If a perform execute is called when
     * ready to execute is false, then a UnsetPropertiesException is thrown.
     */
    boolean isReadyToExecute();
    
    /**
     * @return whether this command has terminated its job
     */
    boolean isTerminated();
    
    /**
     * Concrete command should implement this method to reset input properties
     * in order to make the instance of the command to be reused for further
     * usage.
     */
    void reset();
    
    /**
     * @return the name of the Command
     */
    String getName();
    
    /**
     * @return the description of the Command
     */
    String getDescription();
    
    /**
     * Wait until that command is finished
     *
     */
    void join() throws CommandException;
    
}
