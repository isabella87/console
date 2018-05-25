package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProjectProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的贷款项目。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的贷款项目列表。
     */
    public CompletableFuture<Result> allProjects(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/prj/loan-projs", params);
    }
}
