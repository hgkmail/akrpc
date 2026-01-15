package com.shift.akrpc.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Curator 测试类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/15
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CuratorTest {

    private static CuratorFramework client;

    @BeforeAll
    static void beforeAll() {
        // 创建 CuratorFramework 实例
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .connectionTimeoutMs(15000)
                .sessionTimeoutMs(60000)
                .build();

        // 启动客户端
        client.start();

        assertEquals(CuratorFrameworkState.STARTED, client.getState());
    }

    @Test
    @Order(1)
    void test_create_node() throws Exception {
        String path = "/akrpc123";

        // 创建 CuratorCache 监听节点变化
        CuratorCache cache = CuratorCache.build(client, path);
        cache.start();
        cache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                log.info("=====Node event: {}, oldData: {}, data: {}", type, getDataDesc(oldData), getDataDesc(data));
            }
        });
        // 保持监听一段时间
        Thread.sleep(1000);

        // 返回节点的实际路径path（key）
        var createRes = client.create().withMode(CreateMode.EPHEMERAL).forPath(path, "hello".getBytes());

        // 返回 Stat 对象
        var updateRes = client.setData().forPath(path, "hello updated".getBytes());
        // 获取节点数据
        Stat stat = new Stat();
        var getRes = client.getData().storingStatIn(stat).forPath(path);
        // 返回 null
        client.delete().forPath(path);

        cache.close();

        log.info("=====Create node result: {}, update:{}, data: {}", createRes, updateRes, new String(getRes));
    }

    @NotNull
    private static String getDataDesc(ChildData childData) {
        if (childData == null) {
            return "null";
        }
        return "%s : %s".formatted(childData.getPath(), new String(childData.getData()));
    }

    @Test
    @Order(2)
    void test_lock() throws Exception {
        // 创建分布式锁
        InterProcessMutex lock = new InterProcessMutex(client, "/akrpc_lock");
        if (lock.acquire(5, TimeUnit.SECONDS)) {
            try {
                log.info("=====Lock acquired, doing work...");
                // 模拟工作
                Thread.sleep(1000);
            } finally {
                lock.release();
                log.info("=====Lock released.");
            }
        } else {
            fail("Could not acquire the lock within the specified time.");
        }

    }

    @Test
    @Order(3)
    void test_ls_root() {
        try {
            // 获取根节点的子节点列表，返回 path 列表（List<String>）
            var children = client.getChildren().forPath("/");
            // 输出子节点列表
            log.info("=====Root children: {}", children);
            assertNotNull(children);
        } catch (Exception e) {
            fail("Exception occurred while listing root children: " + e.getMessage());
        }
    }

    @AfterAll
    static void afterAll() {
        client.close();
        assertEquals(CuratorFrameworkState.STOPPED, client.getState());
    }

}
