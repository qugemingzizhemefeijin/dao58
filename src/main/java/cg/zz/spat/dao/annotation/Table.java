package cg.zz.spat.dao.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 注释在数据库映射实体上，映射类和表名称
 * 
 * @author chengang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Table {
	
	/**
	 * 表名称
	 * @return String
	 */
	String name() default "className";

}
