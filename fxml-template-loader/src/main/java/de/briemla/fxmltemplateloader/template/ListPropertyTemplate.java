package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.briemla.fxmltemplateloader.IProperty;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import javafx.fxml.LoadException;

public class ListPropertyTemplate extends Template implements IProperty {

    private final Method getter;
    private final List<IProperty> children;

    public ListPropertyTemplate(Template parent, Method getter) {
        super(parent);
        children = new ArrayList<>();
        this.getter = getter;
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, LoadException {
        List<?> list = (List<?>) getter.invoke(parent);
        list.addAll(create(registry));
    }

    @Override
    public void prepare(IProperty property) {
        children.add(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (IProperty child : children) {
            arrayList.add(child.create(registry));
        }
        return (T) arrayList;
    }

    @Override
    public Method findGetter(String propertyName) {
        return ReflectionUtils.findGetter(List.class, propertyName);
    }

    @Override
    public Method findSetter(String propertyName) {
        throw new UnsupportedOperationException("Setter search in List class not supported for: " + propertyName);
    }

}
