/** 
 * @(#)PageNotFoundExeption.java
 */
package framework.action;

/**
 * ȣ���� url�� �ش��ϴ� ��Ʈ�ѷ��� �׼Ǹ޼ҵ尡 ���� ��� �����ӿ�ũ ���ο��� �߻��ϴ� ����
 */
public class PageNotFoundExeption extends RuntimeException {
	private static final long serialVersionUID = 2427049883577660202L;

	public PageNotFoundExeption() {
		super();
	}

	public PageNotFoundExeption(String message) {
		super(message);
	}

	public PageNotFoundExeption(Exception e) {
		super(e);
	}
}
