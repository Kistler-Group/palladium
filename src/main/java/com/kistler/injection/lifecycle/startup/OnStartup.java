package com.kistler.injection.lifecycle.startup;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnStartup {
	Class<?>[] after() default {};

	Class<?>[] before() default {};
}
