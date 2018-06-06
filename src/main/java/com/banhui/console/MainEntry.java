package com.banhui.console;

import com.alee.laf.WebFonts;
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

import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
            Map<TextAttribute, Object> fontAttributes = new HashMap<>();
            fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
            fontAttributes.put(TextAttribute.SIZE, 12.5f);

            font1 = Font.createFont(Font.TRUETYPE_FONT, fontStream1).deriveFont(fontAttributes);
        } catch (Exception ex) {
            LOGGER.error("cannot load font file", ex);
            return;
        }

        installWebLAF(font1);

        final Application application = new Application(args, "banhuicon");

        application.setListener(new ApplicationAdapter() {
            @Override
            public void startUp(ApplicationEvent event) {
                super.startUp(event);
                ConfigurationManager.register(new XmlFileConfigurationProvider("classpath://armory.xml"));
                HttpManager.setBaseUri(URI.create("http://192.168.11.30/p2psrv/"));
            }
        });

        application.run(MainFrame::new);
    }

    /**
     * 通过反射的方式修改WebFonts的内容。
     *
     * @param baseFont
     *         基准字体。admin
     */
    @SuppressWarnings("unchecked")
    private static void installWebLAF(
            Font baseFont
    ) {
        final Font titleFont = baseFont.deriveFont(Font.BOLD, 15.0F);
        final Font menuFont = baseFont.deriveFont(Font.BOLD, 12.5F);
        final FontUIResource f1 = new FontUIResource(baseFont);

        // 安装WebLookAndFeel
        WebLookAndFeel.globalControlFont = f1;
        WebLookAndFeel.globalTitleFont = f1;
        WebLookAndFeel.globalMenuFont = f1;
        WebLookAndFeel.globalAcceleratorFont = f1;
        WebLookAndFeel.globalTooltipFont = f1;
        WebLookAndFeel.globalTextFont = f1;

        try {
            final Class<?> webFontsClass = WebFonts.class;
            final Field fontsField = webFontsClass.getDeclaredField("fonts");
            fontsField.setAccessible(true);

            final Map<String, FontUIResource> fonts = (Map<String, FontUIResource>) fontsField.get(null);
            fonts.put("title", new FontUIResource(titleFont));
            fonts.put("menu", new FontUIResource(menuFont));
        } catch (ReflectiveOperationException ex) {
            LOGGER.warn("cannot set WebFonts.fonts", ex);
        }

        WebLookAndFeel.install();
    }
}
