package cg.zz.spat.dao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 标记在数据库实体的字段上
 * 
 * @author chengang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {

	/**
	 * 数据库字段名
	 * @return String
	 */
	String name() default "fieldName";

	/**
	 * set方法名
	 * @return String
	 */
	String setFuncName() default "setField";

	/**
	 * get方法名
	 * @return String
	 */
	String getFuncName() default "getField";

	/**
	 * 数据库是否有默认名
	 * @return boolean
	 */
	boolean defaultDBValue() default false;

}
