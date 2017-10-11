package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbContent;


public interface ContentCategoryService {
	List<EasyUITreeNode> getContentCategoryList(long parentId);
	E3Result addContentCategory(long parentId, String name);
	E3Result updateContentCategory(long id, String text);
	E3Result deleteContentCategory(long id);

}
