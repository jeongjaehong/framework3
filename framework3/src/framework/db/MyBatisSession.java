package framework.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * MyBatisSession �� �̿��ϱ� ���� ��ü
 */
public class MyBatisSession extends DBStatement {
	private SqlSession _session = null;

	public static MyBatisSession create(DB db, SqlSessionFactory sqlSessionFactory) {
		return new MyBatisSession(db, sqlSessionFactory);
	}

	private MyBatisSession(DB db, SqlSessionFactory sqlSessionFactory) {
		_session = sqlSessionFactory.openSession(db.getConnection());
	}

	public SqlSession getSession() {
		return _session;
	}

	@Override
	public void close() {
		if (_session != null) {
			_session.close();
		}
	}
}
