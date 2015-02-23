package framework.db;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * IBatisSession �� �̿��ϱ� ���� ��ü
 */
public class IBatisSession extends DBStatement {
	private SqlMapSession _session = null;

	public static IBatisSession create(DB db, SqlMapClient sqlMapClient) {
		return new IBatisSession(db, sqlMapClient);
	}

	private IBatisSession(DB db, SqlMapClient sqlMapClient) {
		_session = sqlMapClient.openSession(db.getConnection());
	}

	public SqlMapSession getSession() {
		return _session;
	}

	@Override
	public void close() {
		if (_session != null) {
			_session.close();
		}
	}
}