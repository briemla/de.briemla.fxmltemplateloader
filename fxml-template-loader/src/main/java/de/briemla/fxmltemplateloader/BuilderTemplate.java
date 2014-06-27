package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.util.Builder;

class BuilderTemplate extends InstantiationTemplate {

	private final Builder<?> builder;
	private final List<IProperty> builderProperties;
	private final Class<?> instanceType;

	BuilderTemplate(List<IProperty> properties, Builder<?> builder, List<IProperty> builderProperties, Class<?> instanceType) {
		super(properties);
		this.builder = builder;
		this.builderProperties = builderProperties;
		this.instanceType = instanceType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = newInstance();
		applyProperties(newInstance);
		return (T) newInstance;
	}

	private Object newInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		for (IProperty property : builderProperties) {
			property.apply(builder);
		}
		return builder.build();
	}

	@Override
	protected Class<?> instanceType() {
		return instanceType;
	}

}
