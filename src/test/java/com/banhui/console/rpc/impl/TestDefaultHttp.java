package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.History;
import com.banhui.console.rpc.HistoryManager;
import com.banhui.console.rpc.Http;
import com.banhui.console.rpc.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.config.ConfigurationManager;
import org.xx.armory.config.XmlFileConfigurationProvider;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestDefaultHttp {
    private final Logger logger = LoggerFactory.getLogger(TestDefaultHttp.class);

    private Http http;

    @BeforeClass
    public static void setUpOnce() {
        ConfigurationManager.register(new XmlFileConfigurationProvider("classpath://armory.xml"));
    }

    @Before
    public void setUp() {
        final DefaultHttp http = new DefaultHttp();
        http.setBaseUri(URI.create("http://192.168.11.30"));
        this.http = http;
    }

    @After
    public void cleanUp() {
        final List<History> histories = HistoryManager.histories();
        Collections.reverse(histories);
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("---- BEGIN HISTORIES ----\n");
        histories.forEach(history -> logBuilder.append("# ").append(history).append('\n'));
        logBuilder.append("---- END HISTORIES ------");
        logger.debug("\n{}", logBuilder);
        HistoryManager.clear();
    }

    @Test
    public void testGetSimplePage()
            throws Exception {
        final String url1 = "/portal/index.html";
        http.get(url1, null).get();
    }

    @Test
    public void testGetSimpleRaw()
            throws Exception {
        final String url1 = "/p2psrv/security/captcha-image";
        http.getRaw(url1, null).get();
    }

    @Test
    public void testSignIn()
            throws Exception {
        final String url1 = "/accsrv/account/sign-in";
        final Map<String, Object> params = new LinkedHashMap<>();
        params.put("login-name-or-mobile", "test10");
        params.put("pwd", "810829lihong#");
        http.post(url1, params)
            .thenApply(Result::map)
            .thenAccept(map -> {
                assertEquals(Boolean.FALSE, map.get("extraInput"));
                assertEquals(Boolean.TRUE, map.get("valid"));
            })
            .get();
    }
}
