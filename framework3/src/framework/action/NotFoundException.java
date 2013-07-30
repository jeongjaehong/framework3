package framework.action;

/**
 * ȣ���� url�� ���� ��� �����ӿ�ũ ���ο��� �߻��ϴ� ����(404 not found)
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2427049883577660202L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Exception e) {
		super(e);
	}
}
