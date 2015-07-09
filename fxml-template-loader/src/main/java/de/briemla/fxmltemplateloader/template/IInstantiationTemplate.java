package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.Method;

interface IInstantiationTemplate extends IProperty {

    Method findGetter(String propertyName);

}