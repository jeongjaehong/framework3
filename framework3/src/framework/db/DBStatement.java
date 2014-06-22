package framework.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * ��� SQL ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 */
public abstract class DBStatement {
	private static Log _logger = LogFactory.getLog(framework.db.DBStatement.class);

	/** 
	 * Statement�� close �� �����ϱ� ���� �߻� �޼ҵ�
	 */
	public abstract void close();

	/** 
	 * DBStatement �ΰŰ�ü�� �����Ѵ�.
	 * ��� �α״� �ش� �ΰŸ� �̿��ؼ� ����Ͽ��� �Ѵ�.
	 * <br>
	 * ex1) ���� ������ ����� ��� : getLogger().error("...�����޽�������")
	 * <br>
	 * ex2) ����� ������ ����� ��� : getLogger().debug("...����׸޽�������")
	 * @return DBStatement�� �ΰŰ�ü
	 */
	protected Log getLogger() {
		return DBStatement._logger;
	}
}