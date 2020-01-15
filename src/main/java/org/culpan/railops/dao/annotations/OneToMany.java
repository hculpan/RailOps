package org.culpan.railops.dao.annotations;

import org.culpan.railops.dao.BaseDao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    Class<? extends BaseDao> dao();
    String foreignKeyName();
    String keyFieldName() default "";
    boolean persist() default false;
}
