package framework.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 액션메소드가 호출되기 전에 호출할 메소드에 적용한다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
	/**
	 * 메소드 이름이 일치 할때 동작한다.
	 * @return 필터를 적용하는 메소드 배열
	 */
	String[] only() default {};

	/**
	 * 메소드 이름이 일치하지 않을때 동작한다.
	 * @return 필터를 적용하지 않는 메소드 배열
	 */
	String[] unless() default {};

	/**
	 * 우선순위
	 * @return 우선순위
	 */
	int priority() default 0;
}