package com.crm.controller.crm.adapter;

import java.util.List;

import com.crm.api.constant.PageConfConst;
import com.crm.model.ClientInfo;

/**
 * 数据过滤器
 * 
 * @author JingChenglong 2017-02-17 17:11
 *
 */
public class ClientInfoAdapter {

	public static String doFilter(String type, List<ClientInfo> clientList, String template) {
		String str = "";
		if (PageConfConst.TAB_ALL.equals(type)) {
			str = ClientInfoAllFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_NEW.equals(type)) {
			str = ClientInfoNewFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_TRACE.equals(type)) {
			str = ClientInfoTraceFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_ORDER.equals(type)) {
			str = ClientInfoOrderFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_COME.equals(type)) {
			str = ClientInfoComeFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_SUCCESS.equals(type)) {
			str = ClientInfoSuccessFilter.doFilter(clientList, template);
		} else if (PageConfConst.TAB_INVALID.equals(type)) {
			str = ClientInfoInvalidFilter.doFilter(clientList, template);
		}
		return str;
	}

}