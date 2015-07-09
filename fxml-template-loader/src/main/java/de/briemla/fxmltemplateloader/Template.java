package de.briemla.fxmltemplateloader;

import java.lang.reflect.Method;

public abstract class Template {

    private final Template parent;

    public Template(Template parent) {
        super();
        this.parent = parent;
    }

    public Template getParent() {
        return parent;
    }

    public abstract void prepare(IProperty property);

    public abstract Method findGetter(String propertyName);

    public abstract Method findSetter(String propertyName);

}