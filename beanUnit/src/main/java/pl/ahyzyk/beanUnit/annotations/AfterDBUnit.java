package pl.ahyzyk.beanUnit.annotations;

import java.lang.annotation.*;

/**
 * Created by andrz on 17.03.2017.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * Executed after dbUnit testing ex. @ShouldMatchDataSet
 *
 * Example of use:
 * @AfterDBUnit
 * function afterDBUnit(){
 *     ...
 * }
 */
public @interface AfterDBUnit {

}
