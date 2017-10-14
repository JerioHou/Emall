package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.search.SearchItemService;

public class ItemChangeListener implements MessageListener {

	@Autowired
	private SearchItemService searchItemService;
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage =null;
		long itemId;
		try{
			if (message instanceof TextMessage) {
				textMessage = (TextMessage) message;
				itemId = Long.parseLong(textMessage.getText());
				Thread.sleep(1000);
				//添加到索引库
				searchItemService.addItemDocument(itemId);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
