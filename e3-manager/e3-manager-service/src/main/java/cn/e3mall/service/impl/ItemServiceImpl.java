package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.EasyUIDateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemDescExample;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemParamItem;
import cn.e3mall.service.ItemService;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

	@Autowired
	TbItemMapper itemMapper;
	@Autowired
	TbItemDescMapper itemDescMapper;
	@Autowired
	TbItemParamItemMapper itemParamItemMapper;
	
	@Override
	public TbItem showItem(Long id) {
		if (id == null) {
			return null;	
		}
		return itemMapper.selectByPrimaryKey(id);
	}
	/**
	 * 获取商品列表
	 */
	@Override
	public EasyUIDateGridResult getItemList(int page, int rows) {
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//创建一个返回对象
		EasyUIDateGridResult result = new EasyUIDateGridResult();
		result.setRows(list);
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal()); 
		return result;
	}
	/**
	 * 新增商品
	 */
	@Override
	public E3Result addItem(TbItem item, String desc) {
		long itemId = IDUtils.genItemId();
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte)1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//商品表
		itemMapper.insert(item);
		//商品描述表
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDescMapper.insert(itemDesc);
		return E3Result.ok();
	}
	/**
	 * 批量删除商品
	 */
	@Override
	public E3Result deleteItem(String ids) {
		//string类型的ids转换为List<Long>
		List<Long> idList = stringToListLong(ids);
		
		TbItemExample example = new TbItemExample();
		example.createCriteria().andIdIn(idList);
		itemMapper.deleteByExample(example);
		
		TbItemDescExample descExample = new TbItemDescExample();
		descExample.createCriteria().andItemIdIn(idList);
		itemDescMapper.deleteByExample(descExample);
		return E3Result.ok();
	}

	private List<Long> stringToListLong(String ids){
		List<Long> idList = new ArrayList<Long>();
		String[] isStrings = ids.split(",");
		for (String id : isStrings) {
			idList.add(new Long(id));
		}
		return idList;
	}
	/**
	 * 商品下架
	 */
	@Override
	public E3Result instock(String ids) {
		//string类型的ids转换为List<Long>
		List<Long> idList = stringToListLong(ids);
		TbItem item = new TbItem();
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte)2);
		item.setUpdated(new Date());
		TbItemExample example = new TbItemExample();	
		example.createCriteria().andIdIn(idList);
		itemMapper.updateByExampleSelective(item, example);
		return E3Result.ok();
	}
	/**
	 * 商品上架
	 */
	@Override
	public E3Result reshelf(String ids) {
		//string类型的ids转换为List<Long>
		List<Long> idList = stringToListLong(ids);
		TbItem item = new TbItem();
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte)1);
		item.setUpdated(new Date());
		TbItemExample example = new TbItemExample();	
		example.createCriteria().andIdIn(idList);
		itemMapper.updateByExampleSelective(item, example);
		return E3Result.ok();
	}
	/**
	 * 
	 */
	@Override
	public E3Result getItemDesc(long itemId) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		return E3Result.ok(itemDesc);
	}
	/**
	 * 
	 */
	@Override
	public E3Result getItemParam(long itemId) {
		Map result = new HashMap<>();
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		result.put("paramData", item);
		result.put("id", item.getId());
		return E3Result.ok(result);
	}
	@Override
	public E3Result updateItem(TbItem item, String desc,String itemParams,long itemParamId) {
		item.setUpdated(new Date());
		int count =  itemMapper.updateByPrimaryKeySelective(item);
		if (count < 1) {
			return E3Result.build(500, "操作失败");	
		}
		
		TbItemParamItem paramItem = new TbItemParamItem();
		paramItem.setId(itemParamId);
		paramItem.setParamData(itemParams);
		paramItem.setUpdated(new Date());
		count = itemParamItemMapper.updateByPrimaryKeyWithBLOBsSelective(paramItem);
		if (count < 1) {
			return E3Result.build(500, "操作失败");	
		}
		return E3Result.ok();
	}
	
}
