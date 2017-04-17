package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.common.util.StringUtil;
import com.crm.model.Dictionary;
import com.crm.model.dao.DictionaryDao;
import com.crm.service.DictionaryService;

/**
 * Service 实现类：数据字典
 * 
 * @author JingChenglong 2016-09-23 14:34
 *
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

	@Autowired
	private DictionaryDao dictionaryDao;// dictionary 数据字典持久化

	@Override
	@RedisCache(cacheKey = "getDictionaryListByType", expire=10800,name = "根据类型获取字典列表", type = Dictionary.class)
	public List<Dictionary> getDictionaryListByType(Integer companyId, String dicType) {

		if (companyId == null || companyId == 0 || StringUtil.isEmpty(dicType)) {
			return null;
		}
		return dictionaryDao.getDictionaryListByType(new Dictionary(companyId, dicType));
	}
}
