package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.fxml.LoadException;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class BuilderTemplate extends InstantiationTemplate {

    private final BuilderFactory builderFactory;
    private final List<IProperty> builderProperties;
    private final Class<?> instanceType;

    /**
     * Create new elements using a given builder.
     */
    public BuilderTemplate(Template parent, List<IProperty> properties,
            BuilderFactory builderFactory, List<IProperty> builderProperties,
            Class<?> instanceType) {
        super(parent, properties);
        this.builderFactory = builderFactory;
        this.builderProperties = builderProperties;
        this.instanceType = instanceType;
    }

    @Override
    protected Object newInstance(TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        Builder<?> builder = builderFactory.getBuilder(instanceType);
        for (IProperty property : builderProperties) {
            property.apply(builder, registry);
        }
        return builder.build();
    }

    @Override
    protected Class<?> instanceType() {
        return instanceType;
    }

}
