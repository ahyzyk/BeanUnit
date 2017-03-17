package pl.ahyzyk.beanUnit.dbUnit;

/**
 * Created by andrz on 17.03.2017.
 */
public interface BiExceptionConsumer<T1, T> {
    void accept(T1 t1, T t) throws Exception;
}
