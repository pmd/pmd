/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import java.util.Objects;

import org.reactfx.EventSource;
import org.reactfx.EventStream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;
import net.sourceforge.pmd.util.fxdesigner.app.LogEntry.Category;


/**
 * Implements some kind of messenger pattern. Conceptually just a globally accessible
 * {@link EventSource} with some logging logic.
 *
 * <p>This patterns allows us to reduce coupling between controllers. The mediator pattern
 * implemented by {@link MainDesignerController} was starting to become very obnoxious,
 * every controller had to keep a reference to the main controller, and we had to implement
 * several levels of delegation for deeply nested controllers. Centralising message passing
 * into a few message channels also improves debug logging.
 *
 * <p>This abstraction is not sufficient to remove the mediator. The missing pieces are the
 * following:
 * <ul>
 * <li>Global state of the app: in particular the current language version of the editor, and
 * the current compilation unit, should be exposed globally.</li>
 * <li>Transformation requests: e.g. {@link MainDesignerController#wrapNode(Node)} allows to
 * associate a node with its rich-text representation by delegating to the code area. This would
 * be a "two-way" channel.</li>
 * </ul>
 *
 * @param <T> Type of the messages of this channel
 *
 * @author Clément Fournier
 * @since 6.12.0
 */
public class MessageChannel<T> {

    private final EventSource<Message<T>> channel = new EventSource<>();
    private final EventStream<Message<T>> distinct = channel.distinct();
    private final Category logCategory;


    MessageChannel(Category logCategory) {
        this.logCategory = logCategory;
    }


    /**
     * Returns a stream of messages to be processed by the given component.
     *
     * @param component Component listening to the channel
     *
     * @return A stream of messages
     */
    public EventStream<T> messageStream(ApplicationComponent component) {
        return distinct.hook(message -> component.logMessageTrace(message, () -> component.getDebugName() + " is handling message " + message))
                       .map(Message::getContent);
    }


    /**
     * Notifies the listeners of this channel with the given payload.
     * In developer mode, all messages are logged. The content may be
     * null.
     *
     * @param origin  Origin of the message
     * @param content Message to transmit
     */
    public void pushEvent(ApplicationComponent origin, T content) {
        channel.push(new Message<>(origin, logCategory, content));
    }


    /**
     * A message transmitted through a {@link MessageChannel}.
     * It's a pure data class.
     */
    public static final class Message<T> {

        private final T content;
        private final Category category;
        private final ApplicationComponent origin;


        Message(ApplicationComponent origin, Category category, T content) {
            this.content = content;
            this.category = category;
            this.origin = origin;
        }


        public Category getCategory() {
            return category;
        }


        /** Payload of the message. */
        public T getContent() {
            return content;
        }


        /** Component that pushed the message. */
        public ApplicationComponent getOrigin() {
            return origin;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Message that = (Message) o;
            return Objects.equals(content, that.content)
                && Objects.equals(origin, that.origin);
        }


        @Override
        public int hashCode() {
            return Objects.hash(content, origin);
        }


        @Override
        public String toString() {
            return getContent() + "(" + Objects.hashCode(getContent()) + ") from " + getOrigin().getClass().getSimpleName();
        }
    }
}
