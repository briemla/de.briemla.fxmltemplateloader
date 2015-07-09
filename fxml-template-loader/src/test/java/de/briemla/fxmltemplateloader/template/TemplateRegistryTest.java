package de.briemla.fxmltemplateloader.template;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import javafx.event.Event;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import de.briemla.fxmltemplateloader.value.MethodHandlerStub;

public class TemplateRegistryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void register() throws Exception {
        TemplateRegistry registry = new TemplateRegistry();
        String id = "someId";
        Object previouslyAddedElement = new Object();
        registry.register(id, previouslyAddedElement);

        Object returnedElementFromRegistry = registry.getFxElement(id);

        assertThat(returnedElementFromRegistry, is(equalTo(previouslyAddedElement)));
    }

    @Test
    public void registerSameIdTwice() throws Exception {
        TemplateRegistry registry = new TemplateRegistry();
        Object id = "someId";
        Object firstObject = new Object();
        Object secondObject = new Object();
        registry.register(id, firstObject);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(startsWith("ID already registered."));

        registry.register(id, secondObject);
    }

    @Test
    public void registerSameMethodTwice() throws Exception {
        TemplateRegistry registry = new TemplateRegistry();
        String id = "someId";
        MethodHandlerStub<Event> firstMethod = new MethodHandlerStub<>();
        MethodHandlerStub<Event> secondMethod = new MethodHandlerStub<>();
        registry.registerMethodStub(id, firstMethod);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(startsWith("Method already registered."));

        registry.registerMethodStub(id, secondMethod);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void link() throws Exception {
        TemplateRegistry registry = new TemplateRegistry();
        String name = "someMethod";
        MethodHandlerStub<Event> method = mock(MethodHandlerStub.class);
        ControllerAccessor controller = mock(ControllerAccessor.class);
        registry.registerMethodStub(name, method);

        registry.link(controller);

        verify(controller).linkMethodHandler("someMethod", method);
        verifyZeroInteractions(method);
    }
}
