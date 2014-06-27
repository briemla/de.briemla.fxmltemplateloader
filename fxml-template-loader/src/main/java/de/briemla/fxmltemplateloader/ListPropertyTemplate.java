package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ListPropertyTemplate extends Template implements IProperty {

	private final Method getter;
	private final List<IProperty> children;

	public ListPropertyTemplate(Method getter) {
		super();
		children = new ArrayList<>();
		this.getter = getter;
	}

	@Override
	public void apply(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		List<?> list = (List<?>) getter.invoke(parent);
		list.addAll(create());
	}

	@Override
	public void addProperty(IProperty property) {
		children.add(property);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList<Object> arrayList = new ArrayList<>();
		for (IProperty child : children) {
			arrayList.add(child.create());
		}
		return (T) arrayList;
	}

	@Override
	Method findGetter(String propertyName) {
		return ReflectionUtils.findGetter(List.class, propertyName);
	}

	@Override
	Method findSetter(String propertyName) {
		throw new UnsupportedOperationException("Setter search not supported for: " + propertyName);
	}

}
