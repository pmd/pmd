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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Base class for a processable command. Such command doesn't execute on itself
 * but are explicitly executed by a processor which acts as an execution
 * environment.
 * @see name.herlin.command.CommandProcessor for more details on processor.
 */
public abstract class ProcessableCommand implements Command {

    /**
     * Impementation method of a processable command. Developers of concrete
     * commands are expected to implement the command logic there.
     * @throws CommandException
     */
    public abstract void execute() throws CommandException;
    
    /**
     * @return whether the command has result properties
     */
    public abstract boolean hasOutputProperties();

    /**
     * Implement the execution of the command through a processor. Developer
     * of concrete command cannot override this method. They are expected to
     * implement the execute method instead.
     */    
    public final void performExecute() throws CommandException {
        if (!isReadyToExecute()) {
            throw new UnsetInputPropertiesException();
        }

        CommandProcessorStrategy strategy = getCommandProcessorStrategy();
        CommandProcessor processor = strategy.getCommandProcessor(this);
        if (processor == null) {
            throw new UnregisteredCommandException("Processor cannot be found for that command");
        }
        
        processor.processCommand(this);
        
    }

    /**
     * @return the command preferred processor. The default is to return null
     * which means the command doesn't have a preferred processor. Developpers
     * of concrete commands may override this methods to return a concrete
     * command processor of their own.
     */
    public CommandProcessor getPreferredCommandProcessor() {
        return null;
    }
    
    /**
     * @return the current strategy to compute the best processor for that
     * command. The default is to find a concrete strategy in the strategy
     * bundle. If none found (or if the class cannot be loaded or instantiated)
     * the default strategy of the framework is returned. Developpers of
     * concrete commands may override this method to return a strategy of their
     * own without using the bundle. But in any case, the return of that method
     * MUST NOT be null.  
     */
    protected CommandProcessorStrategy getCommandProcessorStrategy() {
        CommandProcessorStrategy strategy = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(CommandProcessorStrategy.COMMAND_PROCESSOR_STRATEGY_BUNDLE);
            String strategyClassName = bundle.getString(CommandProcessorStrategy.KEY_COMMAND_PROCESSOR_STRATEGY_CLASS);
            Class strategyClass = Class.forName(strategyClassName);
            
            strategy = (CommandProcessorStrategy) strategyClass.newInstance();
            
        } catch (ClassNotFoundException e) {
            // ignored
        } catch (InstantiationException e) {
            // ignored
        } catch (IllegalAccessException e) {
            // ignored
        } catch (MissingResourceException e) {
            // ignored
        } finally {
            if (strategy == null) {
                strategy = new DefaultCommandProcessorStrategy();
            }
        }
        
        return strategy;

    }
}
