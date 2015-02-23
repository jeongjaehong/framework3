package framework.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * ��� SQL ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 */
public abstract class DBStatement {
	protected static final Log logger = LogFactory.getLog(framework.db.DBStatement.class);

	/** 
	 * Statement�� close �� �����ϱ� ���� �߻� �޼ҵ�
	 */
	public abstract void close();
}