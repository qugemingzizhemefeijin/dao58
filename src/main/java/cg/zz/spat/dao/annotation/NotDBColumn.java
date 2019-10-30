package cg.zz.spat.dao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 表明数据库实体某个字段不是DB的字段
 * 
 * @author chengang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface NotDBColumn {

}
