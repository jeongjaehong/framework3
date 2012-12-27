/* 
 * @(#)PagingUtil.java
 */
package framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * �׺���̼� ���� ����¡ ���� ���� ���̺귯��
 */
public class PagingUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private PagingUtil() {
	}

	/**
	 * ����¡�� ���� �ʿ��� ������ �����Ѵ�.
	 * @param totcnt ��ü ���ڵ� �Ǽ�
	 * @param pagenum ���� ������ ��ȣ 
	 * @param pagesize ���������� ������ ������
	 * @param displaysize �׺���̼� ����¡ ������
	 * @return totcnt(��ü ���ڵ� �Ǽ�), pagesize(���������� ������ ������), totalpage(��ü��������), pagenum(����������), startpage(����������), endpage(��������), beforepage(����������), afterpage(����������) ������ ��� �ִ� �� ��ü
	 */
	public static Map<String, Integer> getPagingMap(Integer totcnt, Integer pagenum, Integer pagesize, Integer displaysize) {
		int l_totcnt = totcnt.intValue();
		int l_pagenum = pagenum.intValue();
		int l_pagesize = pagesize.intValue();
		int l_displaysize = displaysize.intValue();
		int l_beforepage = 0;
		int l_afterpage = 0;
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		int l_totalpage = l_totcnt / l_pagesize;
		if (l_totcnt % l_pagesize != 0)
			l_totalpage += 1;
		int l_startpage = (((l_pagenum - 1) / l_displaysize) * l_displaysize) + 1;
		int l_endpage = (((l_pagenum - 1) + l_displaysize) / l_displaysize) * l_displaysize;
		if (l_totalpage <= l_endpage)
			l_endpage = l_totalpage;
		if ((l_startpage - l_displaysize) > 0)
			l_beforepage = ((((l_pagenum - l_displaysize) - 1) / l_displaysize) * l_displaysize) + 1;
		if ((l_startpage + l_displaysize) <= l_totalpage)
			l_afterpage = ((((l_pagenum + l_displaysize) - 1) / l_displaysize) * l_displaysize) + 1;
		resultMap.put("totcnt", Integer.valueOf(totcnt));
		resultMap.put("totalpage", Integer.valueOf(l_totalpage));
		resultMap.put("pagenum", Integer.valueOf(l_pagenum));
		resultMap.put("startpage", Integer.valueOf(l_startpage));
		resultMap.put("endpage", Integer.valueOf(l_endpage));
		resultMap.put("pagesize", Integer.valueOf(pagesize));
		resultMap.put("displaysize", Integer.valueOf(l_displaysize));
		resultMap.put("beforepage", Integer.valueOf(l_beforepage));
		resultMap.put("afterpage", Integer.valueOf(l_afterpage));
		return resultMap;
	}
}
