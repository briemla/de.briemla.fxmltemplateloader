package de.briemla.fxmltemplateloader;

import java.lang.reflect.Method;

interface IInstantiationTemplate extends IProperty {

	Method findGetter(String propertyName);

}