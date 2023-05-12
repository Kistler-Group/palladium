package com.AMS.injection.subtypefactory;

import java.lang.annotation.*;

/**
 * Basic class annotation that indicates a class that is  valid for use in the SubTypesFactory or any of the derivatives thereof
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubTypeConstructable {
}
