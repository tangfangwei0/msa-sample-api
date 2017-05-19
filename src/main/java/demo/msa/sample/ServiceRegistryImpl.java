package demo.msa.sample;

import demo.msa.framework.registry.ServiceRegistry;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tangfw on 2017/5/14.
 */
//@Component
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {
    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private static final String REGISTRY_PATH = "/registry";
    private static final int SESSION_TIMEOUT = 5000;

    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;

    public ServiceRegistryImpl(){

    }

    public ServiceRegistryImpl(String zkServers){
        try {
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            latch.await();
            logger.debug("Connection to Zookeeper");
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Create zookeeper client failure", e);
        }

    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String registryPath = REGISTRY_PATH;
        try {
            //create root node
            if(zk.exists(registryPath, false) == null){
                zk.create(registryPath,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("Connection registry node:{ }", registryPath);
            }

            //create service node
            String servicePath = registryPath + "/" + serviceName;
            if(zk.exists(servicePath, false) == null){
                zk.create(servicePath,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("Connection service node:{ }", servicePath);
            }

            //create address node
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath,serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("Connection address node:{ } =>", addressNode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Create node failure", e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            latch.countDown();
        }
    }
}
