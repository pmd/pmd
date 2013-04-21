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
 * Exception thrown by the performExcecute method of a Command if not all
 * required input parameters of a command are not set. More formally, this
 * exception is thrown when Command.isReadyToExecute returns false.
 */
public class UnsetInputPropertiesException extends CommandException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message the exception message
     * @param cause a root cause
     */
    public UnsetInputPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause a root cause
     */
    public UnsetInputPropertiesException(Throwable cause) {
        super(cause);
    }

    /**
     * Default constructor
     *
     */
    public UnsetInputPropertiesException() {
        super();
    }

    /**
     * @param message the exception message
     */
    public UnsetInputPropertiesException(String message) {
        super(message);
    }

}
