package de.briemla.fxmltemplateloader;

import javafx.event.Event;

public class ControllerMethodValue implements IValue {

	private final String value;

	public ControllerMethodValue(String value) {
		this.value = value;
	}

	@Override
	public Object create(TemplateRegistry registry) {
		MethodHandlerStub<Event> methodHandlerStub = new MethodHandlerStub<>();
		registry.addMethodStub(value, methodHandlerStub);
		return methodHandlerStub;
	}

}
