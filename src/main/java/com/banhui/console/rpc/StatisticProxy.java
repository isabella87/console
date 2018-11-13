package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StatisticProxy
        extends AbstractProxy {
    /**
     * 平台数据统计
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */
    //日统计数据
    public CompletableFuture<Result> dailyStatistic1(Map<String, Object> params) {
        return super.http().get("mgr/statistic/1-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic2(Map<String, Object> params) {
        return super.http().get("mgr/statistic/2-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic3(Map<String, Object> params) {
        return super.http().get("mgr/statistic/3-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic4(Map<String, Object> params) {
        return super.http().get("mgr/statistic/4-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic5(Map<String, Object> params) {
        return super.http().get("mgr/statistic/5-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic6(Map<String, Object> params) {
        return super.http().get("mgr/statistic/6-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic7(Map<String, Object> params) {
        return super.http().get("mgr/statistic/7-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic8(Map<String, Object> params) {
        return super.http().get("mgr/statistic/8-daily", params);
    }

    public CompletableFuture<Result> dailyStatistic9(Map<String, Object> params) {
        return super.http().get("mgr/statistic/9-daily", params);
    }

    //月统计数据
    public CompletableFuture<Result> monthStatistic1(Map<String, Object> params) {
        return super.http().get("mgr/statistic/1-month", params);
    }

    public CompletableFuture<Result> monthStatistic2(Map<String, Object> params) {
        return super.http().get("mgr/statistic/2-month", params);
    }

    public CompletableFuture<Result> monthStatistic3(Map<String, Object> params) {
        return super.http().get("mgr/statistic/3-month", params);
    }

    public CompletableFuture<Result> monthStatistic4(Map<String, Object> params) {
        return super.http().get("mgr/statistic/4-month", params);
    }

    public CompletableFuture<Result> monthStatistic5(Map<String, Object> params) {
        return super.http().get("mgr/statistic/5-month", params);
    }

    public CompletableFuture<Result> monthStatistic6(Map<String, Object> params) {
        return super.http().get("mgr/statistic/6-month", params);
    }

    public CompletableFuture<Result> monthStatistic7(Map<String, Object> params) {
        return super.http().get("mgr/statistic/7-month", params);
    }

    public CompletableFuture<Result> monthStatistic8(Map<String, Object> params) {
        return super.http().get("mgr/statistic/8-month", params);
    }

    public CompletableFuture<Result> monthStatistic9(Map<String, Object> params) {
        return super.http().get("mgr/statistic/9-month", params);
    }


    //第一次投资客户名单
    public CompletableFuture<Result> newInvestors(Map<String, Object> params) {
        return super.http().get("mgr/statistic/new-investors", params);
    }

    //vip名单
    public CompletableFuture<Result> dataOfVip(Map<String, Object> params) {
        return super.http().get("mgr/statistic/vip", params);
    }
}
