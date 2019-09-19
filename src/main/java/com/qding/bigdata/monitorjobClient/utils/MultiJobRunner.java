package com.qding.bigdata.monitorjobClient.utils;

import com.qding.bigdata.monitorjobClient.ZkService;
import com.qding.bigdata.monitorjobClient.common.Constant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanpf on 2017/7/24.
 */

public abstract class MultiJobRunner {

    private static final String SHARED_LOCKS_PARENT = "lock/shared";

    private static final String SHARED_LOCKS = SHARED_LOCKS_PARENT + "/sharedLocks";

    private static final Logger logger = LoggerFactory.getLogger(MultiJobRunner.class);

    private static volatile Integer totalNodeCount;

    private static volatile Integer currentNodeIndex;

    private static CountDownLatch countDownLatch = new CountDownLatch(2);

    private static String path;

    public static Integer getTotalNodeCount() {
        try {
            if (totalNodeCount == null)
                countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return totalNodeCount;
    }

    public static Integer getCurrentNodeIndex() {
        try {
            if (currentNodeIndex == null)
                countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentNodeIndex;
    }

    public static void initialize() throws Exception {
        path = ZkService.createZkNode(SHARED_LOCKS, "".getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
        path = path.substring(path.lastIndexOf("/") + 1);
        List<String> childrenNodes = ZkService.getChildren(ZkService.ZK_PATH_APP + SHARED_LOCKS_PARENT);
        totalNodeCount = childrenNodes.size();
        countDownLatch.countDown();
        currentNodeIndex = childrenNodes.indexOf(path);
        countDownLatch.countDown();
        //睡一会再注册，保证数据已经同步完成
        TimeUnit.MILLISECONDS.sleep(100);
        PathChildrenCache childrenCache = new PathChildrenCache(ZkService.getZkClient(), ZkService.ZK_PATH_APP + SHARED_LOCKS_PARENT, false);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED
                        || event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    List<String> childrenNodes = ZkService.getChildren(SHARED_LOCKS_PARENT);
                    totalNodeCount = childrenNodes.size();
                    currentNodeIndex = childrenNodes.indexOf(path);
                    logger.info("节点数量发生了变化，totalNodeCount：{}, currentNodeIndex:{}", totalNodeCount, currentNodeIndex);
                    if(totalNodeCount == 0 || currentNodeIndex == -1){
                        Map<String, String> param = new HashMap<>();
                        Map<String, String> params = new HashMap<>();
                        params.put("mobiles", "15801029684|13641064288|15110234502|15011233180|18600295448|15313159809");
                        params.put("title", "监控报表");
                        params.put("content", "监控节点异常，zookeeper连接失败，ip地址:" + getIpAddress());
                        HttpClientUtils.doPost(Constant.ALARM_WECHAT, params);
                    }
                }
            }
        });
        childrenCache.start();
    }

    protected abstract void execute();

    private static String getIpAddress(){
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                return ip.getHostAddress();
            }
        }

        return null;
    }
}
