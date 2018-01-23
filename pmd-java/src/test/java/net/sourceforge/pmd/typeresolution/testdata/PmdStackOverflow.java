/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class PmdStackOverflow {

    public void shouldThrowStackOverfloError() {
        MessageBuilder messageBuilder = new MessageBuilderA();

        // Works
        PartBuilder partBuilder = messageBuilder.newComponent();
        messageBuilder.addComponent(partBuilder.withSomeValue("ABC"));

        // Does not work
        messageBuilder.addComponent(messageBuilder.newComponent().withSomeValue("ABC"));
    }

}

abstract class MessageBuilder<T extends MessageBuilder, U extends PartBuilder<U>> {

    public abstract U newComponent();

    public T addComponent(U ignore) {
        return (T) this;
    }
}

class MessageBuilderA extends MessageBuilder<MessageBuilderA, PartBuilderA> {

    @Override
    public PartBuilderA newComponent() {
        return new PartBuilderA();
    }
}

class PartBuilder<T extends PartBuilder> {

    public T withSomeValue(String ignore) {
        return (T) this;
    }
}

class PartBuilderA extends PartBuilder<PartBuilderA> {
}
