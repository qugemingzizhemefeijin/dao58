package cg.zz.spat.dao.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 指定表需要可以通过存储过程执行增删该查等操作
 * 
 * @author chengang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface ProcedureName {

	/**
	 * 删除的存储过程名字
	 * @return String
	 */
	String delete();

	/**
	 * 添加的存储过程名字
	 * @return String
	 */
	String insert();

	/**
	 * 修改的存储过程名字
	 * @return String
	 */
	String update();

	/**
	 * 通过ID修改的存储过程名字
	 * @return String
	 */
	String updateByID();

	/**
	 * 查询数据的存储过程名字
	 * @return String
	 */
	String load();

	/**
	 * 分页查询数据的存储过程名字
	 * @return String
	 */
	String locaByPage();

}
