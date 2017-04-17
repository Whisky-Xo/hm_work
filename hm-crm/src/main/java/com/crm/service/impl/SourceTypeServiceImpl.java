package com.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.util.TimeUtils;
import com.crm.model.Source;
import com.crm.model.SourceType;
import com.crm.model.dao.SourceDao;
import com.crm.model.dao.SourceTypeDao;
import com.crm.service.SourceTypeService;


/**
 * ServiceImpl：渠道类型
 * 
 * @author JingChenglong 2016-09-27 10:48
 *
 */
@Service
public class SourceTypeServiceImpl implements SourceTypeService {

	@Autowired
	private SourceTypeDao sourceTypeDao;

	@Autowired
	private SourceDao sourceDao;

	/*-- 新增渠道类型 --*/
	public int createSourceType(SourceType sourceType) {

		// 校验是否存在同名渠道类型

		sourceType.setCreateTime(TimeUtils.getSysTime());
		sourceType.setUpdateTime(TimeUtils.getSysTime());
		sourceType.setUpdateIp(sourceType.getCreateIp());
		sourceType.setIsShow(true);// 新增渠道类型默认展示
		return sourceTypeDao.createSourceType(sourceType);
	}

	/*-- 根据ID更新渠道类型信息 --*/
	public int updateSourceType(SourceType sourceType) {

		sourceType.setUpdateTime(TimeUtils.getSysTime());
		return sourceTypeDao.updateSourceType(sourceType);
	}

	/*-- 根据ID获取渠道类型详细信息 --*/
	public SourceType getSourceTypeById(SourceType sourceType) {
		return sourceTypeDao.getSourceTypeById(sourceType);
	}

	/*-- 根据企业ID获取企业渠道类型及其类型下所有渠道信息 --*/
	public List<SourceType> getSrcListDetailByCompId(int compId) {

		List<SourceType> sourceType = sourceTypeDao.getSrcListDetailByCompId(compId);
		for (int i = 0; i < sourceType.size(); i++) {
			Source src = new Source();
			src.setTypeId(sourceType.get(i).getTypeId());
			src.setCompanyId(sourceType.get(i).getCompanyId());
			sourceType.get(i).setSrcList(sourceDao.getSourceListByTypeId(src));
		}

		return sourceType;
	}
}