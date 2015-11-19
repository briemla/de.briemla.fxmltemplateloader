package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.briemla.fxmltemplateloader.PropertyCollection;

public class ConstructorTemplate extends InstantiationTemplate {

    private final Constructor<?> constructor;

    public ConstructorTemplate(Template parent, Constructor<?> constructor,
            PropertyCollection properties) {
        super(parent, properties);
        this.constructor = constructor;
    }

    @Override
    protected Object newInstance(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance();
    }

    @Override
    protected Class<?> instanceType() {
        return constructor.getDeclaringClass();
    }

}
