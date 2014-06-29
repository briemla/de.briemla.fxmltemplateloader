package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.util.Builder;
import javafx.util.BuilderFactory;

class BuilderTemplate extends InstantiationTemplate {

	private final BuilderFactory builderFactory;
	private final List<IProperty> builderProperties;
	private final Class<?> instanceType;

	BuilderTemplate(Template parent, List<IProperty> properties, BuilderFactory builderFactory, List<IProperty> builderProperties, Class<?> instanceType) {
		super(parent, properties);
		this.builderFactory = builderFactory;
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
		Builder<?> builder = builderFactory.getBuilder(instanceType);
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
