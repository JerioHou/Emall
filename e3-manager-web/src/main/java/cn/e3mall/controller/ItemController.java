package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDateGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemParamItemService;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	ItemService ItemService;
	@Autowired
	ItemParamItemService ItemParamItemService;
	
	@RequestMapping("/showItem/{itemid}")
	@ResponseBody
	public TbItem showItem(@PathVariable("itemid") Long id){
		TbItem item = ItemService.showItem(id);
		return item;
	}
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDateGridResult getItemList(Integer page, Integer rows){
		return ItemService.getItemList(page, rows);
	}
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result saveItem(TbItem item,String desc){
		
		return ItemService.addItem(item, desc);
	}
	@RequestMapping(value="/rest/item/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result delteItem(String ids){
		return ItemService.deleteItem(ids);
	}
	@RequestMapping(value="/rest/item/instock",method=RequestMethod.POST)
	@ResponseBody
	public E3Result itemInstock(String ids){
		return ItemService.instock(ids);
	}
	@RequestMapping(value="/rest/item/reshelf",method=RequestMethod.POST)
	@ResponseBody
	public E3Result itemReshelf(String ids){
		return ItemService.reshelf(ids);
	}
	@RequestMapping(value="/rest/item/query/item/desc/{itemId}")
	@ResponseBody
	public E3Result getItemDesc(@PathVariable("itemId") Long itemId){
		return ItemService.getItemDesc(itemId);
	}
	@RequestMapping(value="/rest/item/param/item/query/{itemId}")
	@ResponseBody
	public E3Result getItemParam(@PathVariable("itemId") Long itemId){
		return ItemParamItemService.getItemParam(itemId);
	}
	///rest/item/update
	@RequestMapping(value="/rest/item/update")
	@ResponseBody
	public E3Result itemUpdate(TbItem item,String desc,String itemParams,long itemParamId){
		
		return ItemService.updateItem(item, desc, itemParams, itemParamId);
	}
}
