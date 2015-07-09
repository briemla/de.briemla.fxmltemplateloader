package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.Method;

public abstract class Template {

    private final Template parent;

    Template(Template parent) {
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