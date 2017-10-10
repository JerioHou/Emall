package cn.e3mall.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItemParamItem;

public interface ItemParamItemService {
	E3Result getItemParam(long itemId);
	int updateItemParam(String itemParams,long itemParamId);
}
