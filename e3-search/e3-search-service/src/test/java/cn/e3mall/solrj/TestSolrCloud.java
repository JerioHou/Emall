package cn.e3mall.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {

//	@Test
//	public void testSolrCloud() throws Exception {
//		CloudSolrServer solrServer = new CloudSolrServer("192.168.1.106:2181,192.168.1.106:2182,192.168.1.106:2183");
//		solrServer.setDefaultCollection("collection2");
//		SolrInputDocument document = new SolrInputDocument();
//		document.setField("id", "solrCloud1");
//		document.setField("item_title", "隔壁是傻逼");
//		solrServer.add(document);
//		solrServer.commit();
//	}
}
