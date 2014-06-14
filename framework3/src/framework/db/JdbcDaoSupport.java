/** 
 * @(#)JdbcDaoSupport.java
 */
package framework.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC�� �̿��� DAO�� �ۼ��Ҷ� ��ӹ޴� �θ� Ŭ�����̴�.
 */
public class JdbcDaoSupport {
	private static Log _logger = LogFactory.getLog(framework.db.JdbcDaoSupport.class);
	protected DB db = null;

	public JdbcDaoSupport(DB db) {
		this.db = db;
	}

	protected RecordSet select(String query) {
		return select(query, null, 0, 0);
	}

	protected RecordSet select(String query, Object[] where) {
		return select(query, where, 0, 0);
	}

	protected RecordSet select(String query, int currPage, int pageSize) {
		return select(query, null, currPage, pageSize);
	}

	protected RecordSet select(String query, Object[] where, int currPage, int pageSize) {
		if (where == null) {
			return _statmentSelect(query, currPage, pageSize);
		} else {
			return _prepardSelect(query, where, currPage, pageSize);
		}
	}

	protected int update(String query) {
		return update(query, null);
	}

	protected int update(String query, Object[] where) {
		if (where == null) {
			return _statmentUpdate(query);
		} else {
			return _prepardUpdate(query, where);
		}
	}

	protected void commit() {
		this.db.commit();
	}

	protected void rollback() {
		this.db.rollback();
	}

	protected Log getLogger() {
		return JdbcDaoSupport._logger;
	}

	private RecordSet _prepardSelect(String query, Object[] where, int currPage, int pageSize) {
		SQLPreparedStatement pstmt = this.db.createPrepareStatement(query);
		pstmt.set(where);
		RecordSet rs = pstmt.executeQuery(currPage, pageSize);
		pstmt.close();
		return rs;
	}

	private RecordSet _statmentSelect(String query, int currPage, int pageSize) {
		SQLStatement stmt = this.db.createStatement(query);
		RecordSet rs = stmt.executeQuery(currPage, pageSize);
		stmt.close();
		return rs;
	}

	private int _prepardUpdate(String query, Object[] where) {
		SQLPreparedStatement pstmt = this.db.createPrepareStatement(query);
		pstmt.set(where);
		int cnt = pstmt.executeUpdate();
		pstmt.close();
		return cnt;
	}

	private int _statmentUpdate(String query) {
		SQLStatement stmt = this.db.createStatement(query);
		int cnt = stmt.executeUpdate();
		stmt.close();
		return cnt;
	}
}