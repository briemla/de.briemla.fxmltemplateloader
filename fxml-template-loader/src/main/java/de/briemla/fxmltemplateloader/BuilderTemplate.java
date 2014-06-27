package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.util.Builder;

class BuilderTemplate extends InstantiationTemplate {

	private final Builder<?> builder;
	private final List<IProperty> builderProperties;
	private final Class<?> instanceType;

	BuilderTemplate(Template parent, List<IProperty> properties, Builder<?> builder, List<IProperty> builderProperties, Class<?> instanceType) {
		super(parent, properties);
		this.builder = builder;
		this.builderProperties = builderProperties;
		this.instanceType = instanceType;
	}

	protected Object newInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
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
