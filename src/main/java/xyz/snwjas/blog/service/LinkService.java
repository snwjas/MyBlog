package xyz.snwjas.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import xyz.snwjas.blog.model.entity.LinkEntity;
import xyz.snwjas.blog.model.params.LinkSearchParam;
import xyz.snwjas.blog.model.vo.LinkVO;
import xyz.snwjas.blog.model.PageResult;

import java.util.List;

/**
 * Link Service
 *
 * @author Myles Yang
 */
public interface LinkService {

	/**
	 * 条件查询友链
	 */
	IPage<LinkEntity> pageBy(LinkSearchParam param);

	/**
	 * 添加友链
	 */
	int add(LinkVO vo);

	/**
	 * 根据id删除友链
	 */
	int deleteById(int linkId);

	/**
	 * 更新友链
	 */
	int update(LinkVO vo);

	/**
	 * 获取友链数目
	 */
	int getCount();

	/**
	 * 根据id获取友链
	 */
	LinkVO getById(int linkId);

	/**
	 * 更新 URL Logo 解析器
	 * @param parser
	 * @return
	 */
	int updateLogoParser(String parser);

	LinkVO covertToVO(LinkEntity linkEntity);

	List<LinkVO> covertToListVO(List<LinkEntity> linkEntityList);

	PageResult<LinkVO> covertToPageResult(IPage<LinkEntity> page);

}
