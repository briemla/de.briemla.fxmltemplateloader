package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.Method;

import de.briemla.fxmltemplateloader.IProperty;

interface IInstantiationTemplate extends IProperty {

	Method findGetter(String propertyName);

}