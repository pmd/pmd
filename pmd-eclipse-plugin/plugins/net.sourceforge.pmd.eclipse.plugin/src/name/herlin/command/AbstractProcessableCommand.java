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
 * Base class for processable commands. Such command doesn't execute on itself
 * but are explicitly executed by a processor which acts as an execution
 * environment.
 * @see name.herlin.command.CommandProcessor for more details on processor.
 */
public abstract class AbstractProcessableCommand implements Command {

    private static final long serialVersionUID = 1L;

    // @PMD:REVIEWED:SingularField: by Herlin on 10/05/05 23:24
    private CommandProcessor commandProcessor;
    private boolean terminated;

    /**
     * Implementation method of a processable command. Developers of concrete
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

        getCommandProcessor().processCommand(this);

    }

    /**
     * @see Command#join()
     */
    public final void join() throws CommandException {
        getCommandProcessor().waitCommandToFinish(this);
    }

    /**
     * @return the command preferred processor. The default is to return null
     * which means the command doesn't have a preferred processor. Developers
     * of concrete commands may override this methods to return a concrete
     * command processor of their own.
     */
    public CommandProcessor getPreferredCommandProcessor() {
        return null;
    }

    /**
     * @see name.herlin.command.Command#getDescription()
     */
    public abstract String getDescription();

    /**
     * @see name.herlin.command.Command#getName()
     */
    public abstract String getName();

    /**
     * @see name.herlin.command.Command#isReadOnly()
     */
    public abstract boolean isReadOnly();

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public abstract boolean isReadyToExecute();

    /**
     * @return Returns the terminated.
     */
    public final boolean isTerminated() {
        return terminated;
    }

    /**
     * @param terminated The terminated to set.
     */
    public final void setTerminated(final boolean terminated) {
        this.terminated = terminated;
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public abstract void reset();

    /**
     * @return the current strategy to compute the best processor for that
     * command. The default is to find a concrete strategy in the strategy
     * bundle. If none found (or if the class cannot be loaded or instantiated)
     * the default strategy of the framework is returned. Developers of
     * concrete commands may override this method to return a strategy of their
     * own without using the bundle. But in any case, the return of that method
     * MUST NOT be null.
     */
    protected CommandProcessorStrategy getCommandProcessorStrategy() {
        CommandProcessorStrategy strategy = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(CommandProcessorStrategy.COMMAND_PROCESSOR_STRATEGY_BUNDLE);
            String strategyClassName = bundle.getString(CommandProcessorStrategy.STRATEGY_CLASS_KEY);
            Class<?> strategyClass = Class.forName(strategyClassName);

            strategy = (CommandProcessorStrategy) strategyClass.newInstance();

        } catch (ClassNotFoundException e) {
            // @PMD:REVIEWED:EmptyCatchBlock: by Herlin on 01/05/05 19:00
            // ignored
        } catch (InstantiationException e) {
            // @PMD:REVIEWED:EmptyCatchBlock: by Herlin on 01/05/05 19:00
            // ignored
        } catch (IllegalAccessException e) {
            // @PMD:REVIEWED:EmptyCatchBlock: by Herlin on 01/05/05 19:00
            // ignored
        } catch (MissingResourceException e) {
            // @PMD:REVIEWED:EmptyCatchBlock: by Herlin on 01/05/05 19:00
            // ignored
        } finally {
            if (strategy == null) {
                strategy = new DefaultCommandProcessorStrategy();
            }
        }

        return strategy;

    }

    /**
     * @return the command processor for that command
     */
    protected CommandProcessor getCommandProcessor() throws CommandException {
        if (commandProcessor == null) {
            final CommandProcessorStrategy strategy = getCommandProcessorStrategy();
            commandProcessor = strategy.getCommandProcessor(this);
            if (commandProcessor == null) {
                throw new UnregisteredCommandException("Processor cannot be found for that command");
            }
        }

        return commandProcessor;
    }
}
