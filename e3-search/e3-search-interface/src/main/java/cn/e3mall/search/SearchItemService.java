package cn.e3mall.search;

import java.io.IOException;

import cn.e3mall.common.utils.E3Result;

public interface SearchItemService {
	E3Result importItmes();
	E3Result addItemDocument(long itemId)throws Exception;
}
