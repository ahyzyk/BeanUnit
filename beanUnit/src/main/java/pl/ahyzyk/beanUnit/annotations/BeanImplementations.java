package pl.ahyzyk.beanUnit.annotations;

import java.lang.annotation.*;

/**
 * Created by andrz on 17.03.2017.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited

/**
 * Allow add class implementations before been constructs.
 * Added class will be injected in place of first object type
 *
 * Example of use:
 * @BeanImplementations
 * public void function(BeanManager beanManager){
 *     beanManager.addImplementation(IEasyEjb3.class, EasyEjb3.class)
 * }
 */
public @interface BeanImplementations {

}
