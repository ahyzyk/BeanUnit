package pl.ahyzyk.beanUnit.annotations;

import java.lang.annotation.*;

/**
 * Created by andrz on 17.03.2017.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AfterDBTest {

}
