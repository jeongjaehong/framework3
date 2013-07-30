package framework.db;

/**
 * DB������ �߻��� ��� �����ӿ�ũ ���ο��� �߻��ϴ� ����
 */
public class DBException extends RuntimeException {
	private static final long serialVersionUID = 1042099258893291176L;

	public DBException() {
		super();
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Exception e) {
		super(e);
	}
}