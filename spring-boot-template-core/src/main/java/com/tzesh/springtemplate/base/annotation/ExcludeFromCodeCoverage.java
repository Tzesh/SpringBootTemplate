package com.tzesh.springtemplate.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes that should be excluded from code coverage tools.
 * Usage: Add @ExcludeFromCodeCoverage to abstract/base classes you want ignored in coverage reports.
 * @see RetentionPolicy
 * @see ElementType
 * @author tzesh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcludeFromCodeCoverage {
}

