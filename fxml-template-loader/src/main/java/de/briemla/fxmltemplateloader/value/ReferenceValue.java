package de.briemla.fxmltemplateloader.value;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import javafx.fxml.LoadException;

public class ReferenceValue implements IValue {

	private final String value;

	public ReferenceValue(String value) {
		super();
		this.value = value;
	}

	@Override
	public Object create(TemplateRegistry registry) throws LoadException {
		return registry.getFxElement(value);
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
		ReferenceValue other = (ReferenceValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
