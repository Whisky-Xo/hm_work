package com.crm.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 实体类基类
 * 
 * @author JingChenglong 2016-09-08 19:52
 *
 */
public class BaseObject implements Serializable {

	private static final long serialVersionUID = 277961454681883902L;

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE).toString();
	}
}