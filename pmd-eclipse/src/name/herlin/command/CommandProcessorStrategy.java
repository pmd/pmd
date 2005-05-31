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
 * Interface for a command strategy. Command strategy is an object that computes
 * what is the best command processor for a processable command. Commands may
 * be registered along with their processor in a bundle on the form:<br>
 * <pre>
 * command_name = processor_class
 * </pre>
 * The bundle should contain a line to determine what is the concrete strategy
 * class:<br>
 * <pre>
 * strategy.class = class for the concrete strategy
 * </pre> 
 */
public interface CommandProcessorStrategy {
    /**
     * Name of the bundle. This is "properties.CommandProcessorStrategy" by
     * default.
     */
    String COMMAND_PROCESSOR_STRATEGY_BUNDLE = "properties.CommandProcessorStrategy"; // NOPMD
    
    /**
     * Key of the concrete strategy class. The value is "strategy.class". It
     * cannot be changed.
     */
    String STRATEGY_CLASS_KEY = "strategy.class";
    
    /**
     * @param aCommand a processable command instance.
     * @return a command processor for this command according to the strategy.
     */
    CommandProcessor getCommandProcessor(AbstractProcessableCommand aCommand);
    
}
