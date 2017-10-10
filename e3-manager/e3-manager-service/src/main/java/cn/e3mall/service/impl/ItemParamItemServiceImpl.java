package cn.e3mall.service.impl;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbItemParamItemMapper;
import cn.e3mall.pojo.TbItemParamItem;
import cn.e3mall.pojo.TbItemParamItemExample;
import cn.e3mall.service.ItemParamItemService;

@Service
public class ItemParamItemServiceImpl implements ItemParamItemService{

	@Autowired
	TbItemParamItemMapper itemParamItemMapper;
	@Override
	public E3Result getItemParam(long itemId) {
		TbItemParamItemExample example = new TbItemParamItemExample();
//		example.createCriteria().andItemIdEqualTo(itemId);
//		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
//		return E3Result.ok(list.get(0));
		TbItemParamItem paramItem = itemParamItemMapper.selectByItemId(itemId);
		return E3Result.ok(paramItem);
	}
	@Override
	public int updateItemParam(String itemParams,long itemParamId) {
		TbItemParamItem paramItem = new TbItemParamItem();
		paramItem.setId(itemParamId);
		paramItem.setParamData(itemParams);
		paramItem.setUpdated(new Date());
		int updatecount = itemParamItemMapper.updateByPrimaryKeyWithBLOBsSelective(paramItem);
		return updatecount;
	}

}
