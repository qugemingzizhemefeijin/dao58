package cg.zz.spat.dao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {

	/**
	 * 是否更新的时候要求填写，updatable和insertable都为false的情况下，在insert语句插入成功后才能够得到自增ID
	 * @return boolean
	 */
	boolean updatable() default false;

	/**
	 * 是否插入的时候要求填写，updatable和insertable都为false的情况下，在insert语句插入成功后才能够得到自增ID
	 * @return boolean
	 */
	boolean insertable() default false;

}
