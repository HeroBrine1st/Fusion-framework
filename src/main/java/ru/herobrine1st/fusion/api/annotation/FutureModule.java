package ru.herobrine1st.fusion.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureModule {
    String id();
    String name() default "";
    String description() default "";
    String version() default "1.0";
    String[] authors() default {};
    String url() default "";
}
