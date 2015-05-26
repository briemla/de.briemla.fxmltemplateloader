package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.event.Event;
import javafx.event.EventHandler;

public class MethodHandlerStub<T extends Event> implements EventHandler<T> {

	private Method method;
	private Object controller;

	@Override
	public void handle(T event) {
		if (method == null) {
			throw new RuntimeException("Handler has not been bound correct.");
		}
		try {
			method.invoke(controller, event);
		} catch (InvocationTargetException exception) {
			throw new RuntimeException(exception);
		} catch (IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void bindTo(Object controller, Method method) {
		this.controller = controller;
		this.method = method;
	}

}
