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
 * Root exception of the command framework
 */
public class CommandException extends Exception {

    /**
     * @param message A message for the exception
     * @param cause the root cause of that exception
     */
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * @param cause the root cause of that exception
     */
    public CommandException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Defaul constructor
     */
    public CommandException() {
        super();
    }
    
    /**
     * @param message A message for the exception
     */
    public CommandException(String message) {
        super(message);
    }
    
}
