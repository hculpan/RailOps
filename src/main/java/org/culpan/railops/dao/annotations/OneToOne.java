package org.culpan.railops.dao.annotations;

import org.culpan.railops.dao.BaseDao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
    String fieldName();
    Class<? extends BaseDao> dao();
    boolean persist() default false;
}
