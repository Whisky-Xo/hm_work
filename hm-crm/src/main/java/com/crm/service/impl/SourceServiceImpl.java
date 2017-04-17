package com.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.annotation.RedisCache;
import com.crm.common.util.StringUtil;
import com.crm.common.util.TimeUtils;
import com.crm.exception.EduException;
import com.crm.model.Source;
import com.crm.model.SourceStaffRela;
import com.crm.model.Staff;
import com.crm.model.dao.SourceDao;
import com.crm.model.dao.StaffDao;
import com.crm.service.SourceService;

/**
 * ServiceImpl：渠道
 * 
 * @author JingChenglong 2016-09-28 10:16
 *
 */
@Service
public class SourceServiceImpl implements SourceService {

	@Autowired
	private SourceDao sourceDao;// source 客资渠道信息持久化

	@Autowired
	private StaffDao staffDao;// Staff 职工信息持久化
	
	
	
	/*-- 新建渠道--*/
	public int createSource(Source source) throws EduException{
		source.setCreateTime(TimeUtils.getSysTime());
		source.setIsShow(true);// 新增加的渠道默认展示
		return sourceDao.createSource(source);
	}

	/*-- 根据ID更新渠道信息 --*/
	public int updateSource(Source source) throws EduException{
		return sourceDao.updateSource(source);
	}
	
	

	/*-- 电商：采集员获取其拥有的采集渠道集合 --*/
	@RedisCache(cacheKey = "getSrcListDsByStaffCj", name = "根据员工获取电商渠道列表",expire=10800,type = Source.class)
	public List<Source> getSrcListDsByStaffCj(Integer id) throws EduException {

		if (id == null || id == 0) {
			return null;
		}

		Staff staff = staffDao.getStaffInfoById(id);

		if (staff == null || staff.getId() == null || staff.getId() == 0) {
			return null;
		}

		/*-- 设定关联类型为:采集 --*/
		staff.setSrcRelaType(Source.SRC_RELA_TYPE_CJ);
		/*-- 渠道类型：电商渠道 --*/
		staff.setSrcType(Source.SRC_TYPE_DS);
		return sourceDao.getSrcListBySrcTypeStaffCj(staff);
	}

	/*-- 邀约员获取其拥有的邀约渠道集合 --*/
	public List<Source> getSrcListByStaffYy(Integer id) throws EduException {

		if (id == 0) {
			return null;
		}

		Staff staff = staffDao.getStaffInfoById(id);

		if (staff == null || staff.getId() == null || staff.getId() == 0) {
			return null;
		}

		/*-- 设定关联类型为:邀约 --*/
		staff.setSrcRelaType(Source.SRC_RELA_TYPE_YY);
		return sourceDao.getSourceListByStaff(staff);
	}

	/*-- 推广员：获取其拥有的转介绍推广渠道集合 --*/
	public List<Source> getSrcListByStaffTg(Integer id) throws EduException {

		if (id == 0) {
			return null;
		}

		Staff staff = staffDao.getStaffInfoById(id);

		if (staff == null || staff.getId() == null || staff.getId() == 0) {
			return null;
		}

		/*-- 设定关联类型为:推广 --*/
		staff.setSrcRelaType(Source.SRC_RELA_TYPE_TG);
		return sourceDao.getSourceListByStaff(staff);
	}

	/*-- 获取企业旗下所有渠道 --*/
	public List<Source> getSrcListOfComp(Integer companyId) throws EduException {

		if (companyId == null || companyId == 0) {
			return null;
		}

		return sourceDao.getSrcListOfComp(companyId);
	}

	/*-- 内部转介绍：采集员获取期拥有的指定渠道类型的渠道集合 --*/
	public List<Source> getSrcListInnerTypeByStaffCj(Integer id) throws EduException {

		if (id == 0) {
			return null;
		}

		Staff staff = staffDao.getStaffInfoById(id);

		if (staff == null || staff.getId() == null || staff.getId() == 0) {
			return null;
		}

		/*-- 设定关联类型为:采集 --*/
		staff.setSrcRelaType(Source.SRC_RELA_TYPE_CJ);
		/*-- 渠道类型：内部转介绍渠道 --*/
		staff.setSrcType(Source.SRC_TYPE_INNNER_INTRODUCE);
		return sourceDao.getSrcListBySrcTypeStaffCj(staff);
	}

	public List<Source> getSourceListByTypeId(Source source){
		return sourceDao.getSourceListByTypeId(source);
	}
	
	
	/*-- 获取指定渠道类型的所有渠道 --*/
	@RedisCache(cacheKey = "getSrcListByType", name = "根据类型获取渠道列表",expire=10800,type = Source.class)
	public List<Source> getSrcListByType(Integer companyId, String srcType) throws EduException {

		if (companyId == null || companyId == 0 || StringUtil.isEmpty(srcType)) {
			return null;
		}

		return sourceDao.getSrcListByType(new Source(companyId, srcType));
	}

	/*-- 获取指定名称的渠道信息 --*/
	public Source getSrcByName(String srcName, Integer companyId) throws EduException {

		Source src = new Source();
		src.setSrcName(srcName);
		src.setCompanyId(companyId);
		return sourceDao.getSrcByName(src);
	}
	
	
	
	/*-- 批量新增渠道与采集员关联 --*/
	public int addSrcStaffCjYyRela(List<SourceStaffRela> relas) throws EduException {
		return sourceDao.addSrcStaffCjYyRela(relas);
	}

	/*-- 删除渠道与采集员/邀约员关联 --*/
	public int deleteSrcStaffCjYyRela(SourceStaffRela rela) throws EduException {
		return sourceDao.deleteSrcStaffCjYyRela(rela);
	}

	/*-- 获取渠道关联的职工信息集合 --*/
	public List<Staff> getSrcRelaStaffs(SourceStaffRela rela) throws EduException {
		return sourceDao.getSrcRelaStaffs(rela);
	}
	
	
	
}