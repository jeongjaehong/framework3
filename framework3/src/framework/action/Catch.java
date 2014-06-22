package framework.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �׼Ǹ޼ҵ忡�� ���ܰ� ������ �Ŀ� ȣ���� �޼ҵ忡 �����Ѵ�.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Catch {
	/**
	 * ����Ŭ������ ��ġ�ϰų� ���� Ŭ������ �� �����Ѵ�. 
	 */
	Class<? extends Exception>[] value() default {};

	/** 
	 * �켱����
	 */
	int priority() default 0;
}