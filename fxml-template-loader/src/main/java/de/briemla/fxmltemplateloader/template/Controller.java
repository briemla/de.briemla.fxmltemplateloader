package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;

public interface Controller {

    Object instance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
