package de.briemla.fxmltemplateloader.template;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.event.Event;

import de.briemla.fxmltemplateloader.value.MethodHandlerStub;

public class TemplateRegistry {

    private final HashMap<Object, Object> elements;
    private final HashMap<Object, MethodHandlerStub<Event>> methods;

    public TemplateRegistry() {
        super();
        elements = new HashMap<>();
        methods = new HashMap<>();
    }

    void register(Object id, Object object) {
        if (elements.containsKey(id)) {
            throw new RuntimeException("ID already registered. fx:id must be unique: " + id);
        }
        elements.put(id, object);
    }

    void link(ControllerAccessor controller) {
        for (Entry<Object, Object> entry : elements.entrySet()) {
            controller.linkField(entry.getKey(), entry.getValue());
        }
        for (Entry<Object, MethodHandlerStub<Event>> entry : methods.entrySet()) {
            Object key = entry.getKey();
            MethodHandlerStub<Event> handler = entry.getValue();
            controller.linkMethodHandler(key, handler);
        }
    }

    public void registerMethodStub(String value, MethodHandlerStub<Event> methodHandlerStub) {
        if (methods.containsKey(value)) {
            throw new RuntimeException("Method already registered. Name must be unique: " + value);
        }
        methods.put(value, methodHandlerStub);
    }

    public Object getFxElement(Object id) {
        return elements.get(id);
    }
}