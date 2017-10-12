package cn.e3mall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class JedisTest {
	@Test
	public void jedisClusterTest() {
		//创建一个JedisCluster对象。有一个参数nodes是一个set类型。set中包含若干个HostAndPort对象。
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.1.110", 7001));
		nodes.add(new HostAndPort("192.168.1.110", 7002));
		nodes.add(new HostAndPort("192.168.1.110", 7003));
		nodes.add(new HostAndPort("192.168.1.110", 7004));
		nodes.add(new HostAndPort("192.168.1.110", 7005));
		nodes.add(new HostAndPort("192.168.1.110", 7006));
		JedisCluster jedisCluster  = new JedisCluster(nodes);
		//直接使用JedisCluster对象操作redis。
		jedisCluster.set("key1", "value1");
		String string = jedisCluster.get("key1");
		System.out.println(string);
		//关闭JedisCluster对象
		jedisCluster.close();
	}
}
