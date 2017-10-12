package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;

@Service
public class ContentServiceImpl implements ContentService{

	@Autowired
	JedisClient jedisClient;
	@Autowired
	private TbContentMapper contentMapper;
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public E3Result addContent(TbContent content) {
		//删除redis中的缓存数据
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId()+"");
		//补全属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入数据
		contentMapper.insert(content);
		return E3Result.ok();
	}
	@Override
	public List<TbContent> getContentListByCid(long cid) {

		//先从redis中取数据，取不到时再查数据库
		try {
			String json = jedisClient.hget(CONTENT_LIST, cid+"");
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TbContentExample example = new TbContentExample();
		example.createCriteria().andCategoryIdEqualTo(cid);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		//将数据添加到redis中
		try {
			String jsonList = JsonUtils.objectToJson(list);
			jedisClient.hset(CONTENT_LIST, cid+"", jsonList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
