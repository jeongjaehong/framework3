package framework.action;

/**
 * ȣ���� url�� ���� ��� �����ӿ�ũ ���ο��� �߻��ϴ� ����(404 not found)
 */
public class _404Exception extends RuntimeException {
	private static final long serialVersionUID = 2427049883577660202L;

	public _404Exception() {
		super();
	}

	public _404Exception(String message) {
		super(message);
	}

	public _404Exception(Exception e) {
		super(e);
	}
}
