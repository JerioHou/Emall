package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;

@Controller
public class PictureController {
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String fileupload(MultipartFile uploadFile) {
		Map result = new HashMap<>();

		//获取上传文件的后缀名
		String filename = uploadFile.getOriginalFilename();
		String extName = filename.substring(filename.lastIndexOf(".")+1);
		
		try {
			//上传文件
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
			String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			String url = IMAGE_SERVER_URL+path;
			result.put("error", 0);
			result.put("url", url);
			return JsonUtils.objectToJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", 1);
			result.put("message", "文件上传失败");
			return JsonUtils.objectToJson(result);
		}
	}
}
