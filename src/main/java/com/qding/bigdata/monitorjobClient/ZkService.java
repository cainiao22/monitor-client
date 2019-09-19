package com.qding.bigdata.monitorjobClient;

import com.qding.bigdata.monitorjobClient.common.Constant;
import com.qding.bigdata.monitorjobClient.utils.UUIDUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZkService {
	private static final String ZK_ADDRESS = "m7-qding-bd-244:12181,m7-qding-bd-242:12181,BJ-HOST-113:12181,m7-qding-bd-245:12181,BJ-HOST-138:12181";
	public static final String ZK_PATH_APP = "/bigdata/monitor_client/";
	private static final String ZK_PATH_MASTER = "/bigdata/monitor_client/master";
	private static final String ZK_PATH_SERVER = "/bigdata/monitor_client/server";

	private static Logger logger = LoggerFactory.getLogger(ZkService.class);

	private static CuratorFramework zkClient = null;

	public static void start() throws Exception {
		String clientId = UUIDUtil.createId();
		//半分钟连接一次。连接500次，两个小时
		zkClient = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(5000, 5000*6));
		zkClient.start();
		System.out.println(clientId + ",zk client start successfully!");
		leaderLatch(zkClient, clientId);
	}

	private static   void leaderLatch(CuratorFramework client, String clientId) throws Exception {
		final LeaderLatch leaderLatch = new LeaderLatch(client, ZK_PATH_MASTER, clientId);
		leaderLatch.addListener(new LeaderLatchListener() {
			public void isLeader() {
				Constant.setMaster(true);
			}

			public void notLeader() {
				Constant.setMaster(false);
			}
		});

		leaderLatch.start();
//		leaderLatch.await();
	}

	public static String createZkNode(String path, byte[] data, CreateMode createMode){
		try{
			String thisPath = getFormattedPath(path);
			logger.info("创建节点,path:{}" + thisPath);
			String nodePath = zkClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(thisPath, data);
			return nodePath;
		}catch (Exception e){
			logger.error("创建节点失败{},path:{}, createMode:{}", e, path, createMode);
			return null;
		}
	}

	private static String getFormattedPath(String path) {
		String thisPath = path;
		if(!path.startsWith("/")){
            thisPath = ZK_PATH_APP + path;
        }
		return thisPath;
	}

	public static boolean deleteZkNode(String path){
		try{
			String thisPath = getFormattedPath(path);
			logger.info("删除节点信息,path:{}" + thisPath);
			zkClient.delete().forPath(thisPath);
		}catch (Exception e){
			logger.error("删除节点失败,path:{}", path, e);
			return false;
		}
		return true;
	}

	public static void registerWatcher(String path, Watcher watcher){
		try {
			String thisPath = getFormattedPath(path);
			logger.info("创建监听器,path:{}" + thisPath);
			zkClient.getData().usingWatcher(watcher).forPath(thisPath);
		} catch (Exception e) {
			logger.error("创建监听器失败，系统出现异常,{}", e);
		}
	}

	public static CuratorFramework getZkClient(){
		return zkClient;
	}

	public static List<String> getChildren(String path) throws Exception {
		String thisPath = getFormattedPath(path);
		logger.info("获取子节点信息,path:{}" + thisPath);
		List<String> childrenNodes = zkClient.getChildren().forPath(thisPath);
		return childrenNodes;
	}
}
