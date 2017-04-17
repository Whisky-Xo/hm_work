package com.crm.common.freemarker;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class MyView implements View {

	private View view;

	public MyView(View view) {
		this.view = view;
	}

	@Override
	public String getContentType() {
		return view.getContentType();
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long start = System.currentTimeMillis();
		view.render(model, request, response);
		long end = System.currentTimeMillis();
		System.out.println("render view spent " + (end - start) + " ms");
	}
}
