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
		registry.registerMethodStub(value, methodHandlerStub);
		return methodHandlerStub;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControllerMethodValue other = (ControllerMethodValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
