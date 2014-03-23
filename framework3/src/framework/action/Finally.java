/** 
 * @(#)Finally.java
 */
package framework.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �׼Ǹ޼ҵ尡 ȣ��� �Ŀ� ���ܿ��ο� ������� �ݵ�� ȣ���� �޼ҵ忡 �����Ѵ�.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Finally {
	/**
	 * �޼ҵ� �̸��� ��ġ �Ҷ� �����Ѵ�.
	 */
	String[] only() default {};

	/**
	 * �޼ҵ� �̸��� ��ġ���� ������ �����Ѵ�.
	 */
	String[] unless() default {};

	/** 
	 * �켱����
	 */
	int priority() default 0;
}