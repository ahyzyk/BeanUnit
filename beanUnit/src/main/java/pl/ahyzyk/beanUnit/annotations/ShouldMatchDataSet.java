package pl.ahyzyk.beanUnit.annotations;

import java.lang.annotation.*;

/**
 * Created by andrz on 17.03.2017.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ShouldMatchDataSet {
    /**
     * List of data set files used for comparison.
     */
    String[] value() default "";

    /**
     * List of columns to be excluded.
     */
    String[] excludeColumns() default "";
}
