package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	TbItem getItemById(long itemId);
	TbItem showItem(Long id);
	EasyUIDateGridResult getItemList(int page, int rows);
	E3Result addItem(TbItem item,String desc);
	E3Result deleteItem(String ids);
	E3Result instock(String ids);
	E3Result reshelf(String ids);
	E3Result getItemDesc(long itemId);
	E3Result getItemParam(long itemId);
	E3Result updateItem(TbItem item,String desc,String itemParams,long itemParamId);
	TbItemDesc getItemDescById(long itemId);
}
