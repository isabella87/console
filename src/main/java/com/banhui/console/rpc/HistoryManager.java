package com.banhui.console.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.xx.armory.commons.Validators.notNull;

/**
 * RPC请求和相应的历史管理器。
 */
public final class HistoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryManager.class);
    private static final int MAX_HISTORIES_COUNT = 100;
    private static final ReadWriteLock HISTORIES_LOCK = new ReentrantReadWriteLock();
    private static final Queue<History> HISTORIES = createHistoryQueue();

    private HistoryManager() {
    }

    private static Queue<History> createHistoryQueue() {
        return new LinkedList<>();
    }

    /**
     * 清除所有的历史。
     */
    public static void clear() {
        HISTORIES_LOCK.writeLock().lock();
        try {
            HISTORIES.clear();
        } finally {
            HISTORIES_LOCK.writeLock().unlock();
        }
    }

    /**
     * 添加一条新的历史。
     *
     * @param history
     *         新的历史。
     * @throws IllegalArgumentException
     *         如果参数{@code history}是{@code null}。
     */
    public static void addHistory(
            History history
    ) {
        notNull(history, "history");

        HISTORIES_LOCK.writeLock().lock();
        try {
            if (HISTORIES.size() > MAX_HISTORIES_COUNT) {
                final History oldestHistory = HISTORIES.poll();
                LOGGER.trace("removed oldest history: {}", oldestHistory);
            }

            HISTORIES.offer(history);
        } finally {
            HISTORIES_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取当前的所有历史。
     *
     * <p>所有的历史按照被添加的顺序排序，即按照时间排序。</p>
     *
     * @return 当前的所有历史。
     */
    public static List<History> histories() {
        HISTORIES_LOCK.readLock().lock();
        try {
            return new ArrayList<>(HISTORIES);
        } finally {
            HISTORIES_LOCK.readLock().unlock();
        }
    }
}
