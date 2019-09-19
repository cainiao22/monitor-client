package com.qding.bigdata.monitorjobClient.utils;

import com.qding.bigdata.monitorjobClient.ZkService;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanpf on 2017/7/19.
 * 基于zookeeper的乐观锁任务执行工具
 */
public class SynchronizedJobRunner {

    private static final String ZK_PATH_LOCK = "lock/synchronized/";

    private String lock;
    private SynchronizedJob synchronizedJob;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SynchronizedJobRunner(String lock, SynchronizedJob synchronizedJob) {
        this.lock = ZK_PATH_LOCK + lock;
        this.synchronizedJob = synchronizedJob;
    }

    /**
     * 基于zookeeper的锁同步机制
     */
    public void execute() {
        String nodePath = ZkService.createZkNode(this.lock, "".getBytes(), CreateMode.EPHEMERAL);
        if(nodePath == null){
            registerWatcher();
            return;
        }
        //走到这里代表节点创建成功，也就意味着拿到了那把锁，可以执行了
        try {
            synchronizedJob.run();
        } catch (Exception e) {
            logger.error("任务执行出现错误，即将重试,{}", e);
            registerWatcher();
        } finally {
            try {
                ZkService.deleteZkNode(this.lock);
            } catch (Exception e) {
                logger.error("释放锁失败，{}", e);
            }
        }
    }

    /**
     * 重新监听节点的变化事件
     */
    private void registerWatcher() {
        logger.info("创建监听器,path:{}", lock);
        ZkService.registerWatcher(lock, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                    execute();
                }
            }
        });
    }

    public interface SynchronizedJob {
        void run();
    }
}
