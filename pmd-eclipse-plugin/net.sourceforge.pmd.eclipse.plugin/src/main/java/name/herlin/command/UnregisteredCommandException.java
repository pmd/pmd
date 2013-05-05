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
 * Exception thrown when a strategy cannot find a processor of a processable
 * command.
 */
public class UnregisteredCommandException extends CommandException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message the exception message
     * @param cause a root cause
     */
    public UnregisteredCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause a root cause
     */
    public UnregisteredCommandException(Throwable cause) {
        super(cause);
    }

    /**
     * Default constructor
     */
    public UnregisteredCommandException() {
        super();
    }

    /**
     * @param message the exception message
     */
    public UnregisteredCommandException(String message) {
        super(message);
    }

}
