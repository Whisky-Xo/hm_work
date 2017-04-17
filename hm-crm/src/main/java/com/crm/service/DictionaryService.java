package com.crm.service;

import java.util.List;
import com.crm.exception.EduException;
import com.crm.model.Dictionary;

/**
 * 数据字典
 * 
 * @author JingChenglong 2016-12-23 10:35
 *
 */
public interface DictionaryService {

	public List<Dictionary> getDictionaryListByType(Integer companyId, String dicType) throws EduException;
}
