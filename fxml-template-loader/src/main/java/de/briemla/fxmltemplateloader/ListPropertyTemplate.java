package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ListPropertyTemplate extends Template implements IProperty {

	private final Method getter;
	private final List<IProperty> children;

	public ListPropertyTemplate(Template parent, Method getter) {
		super(parent);
		children = new ArrayList<>();
		this.getter = getter;
	}

	@Override
	protected Class<?> getInstanceClass() {
		throw new UnsupportedOperationException();
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

}
