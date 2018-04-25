package com.banhui.console;

import com.alee.laf.WebLookAndFeel;
import com.banhui.console.rpc.HttpManager;
import com.banhui.console.ui.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.config.ConfigurationManager;
import org.xx.armory.config.XmlFileConfigurationProvider;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.ApplicationAdapter;
import org.xx.armory.swing.ApplicationEvent;

import java.awt.*;
import java.io.InputStream;
import java.net.URI;

import static org.xx.armory.commons.SysUtils.openInputStream;

/**
 * 应用程序的主入口。
 */
public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);
    private static final String FONT_FILE_NAME = "classpath://msyh.ttc";

    public static void main(
            String[] args
    ) {
        // 加载界面字体。
        final Font font1;
        try (final InputStream fontStream1 = openInputStream(FONT_FILE_NAME)) {
            font1 = Font.createFont(Font.TRUETYPE_FONT, fontStream1).deriveFont(Font.PLAIN, 12f);
        } catch (Exception ex) {
            LOGGER.error("cannot load font file", ex);
            return;
        }

        // 安装WebLookAndFeel
        WebLookAndFeel.globalControlFont = font1;
        WebLookAndFeel.globalTitleFont = font1;
        WebLookAndFeel.globalMenuFont = font1;
        WebLookAndFeel.globalAcceleratorFont = font1;
        WebLookAndFeel.globalTooltipFont = font1;
        WebLookAndFeel.globalTextFont = font1;
        WebLookAndFeel.install();

        final Application application = new Application(args, "banhuicon");

        application.setListener(new ApplicationAdapter() {
            @Override
            public void startUp(ApplicationEvent event) {
                super.startUp(event);
                ConfigurationManager.register(new XmlFileConfigurationProvider("classpath://armory.xml"));
                HttpManager.setBaseUri(URI.create("http://192.168.11.30"));
            }
        });

        application.run(new MainFrame());
    }
}
