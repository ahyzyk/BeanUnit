package pl.ahyzyk.beanUnit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrz on 17.03.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
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
