package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemDescExample;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
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
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	
	@Override
	public TbItem showItem(Long id) {
		if (id == null) {
			return null;	
		}
		return itemMapper.selectByPrimaryKey(id);
	}
	@Override
	public TbItem getItemById(long itemId) {
		//查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
			if(StringUtils.isNotBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存中没有，查询数据库
		//根据主键查询
		//TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			//把结果添加到缓存
			try {
				jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
				//设置过期时间
				jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", ITEM_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);
		}
		return null;
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
	public E3Result addItem( TbItem item, String desc) {
		final long itemId = IDUtils.genItemId();
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
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId+"");
				return textMessage;
			}
		});
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
	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if(StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//把结果添加到缓存
		try {
			jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间
			jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", ITEM_CACHE_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
	
}
