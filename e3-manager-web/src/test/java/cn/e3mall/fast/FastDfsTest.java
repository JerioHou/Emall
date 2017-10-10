package cn.e3mall.fast;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class FastDfsTest {

	@Test
	public void uploadTest() throws FileNotFoundException, IOException, MyException {
		//加载全局配置文件
		ClientGlobal.init("D:/Ecilpse/e3-manager-web/src/main/resources/conf/client.conf");
		//创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//获得 TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建一个StorageServer对象的引用
		StorageServer storageServer = null;
		//创建一个StorageClient对象 
		StorageClient storageClient = new  StorageClient(trackerServer, storageServer);
		
		String[] strings = storageClient.upload_file("C:/Users/Administrator/Desktop/IDEA快捷键.txt", "txt", null);
		for (String string : strings) {
			System.out.println(string);
		}
	}
}
