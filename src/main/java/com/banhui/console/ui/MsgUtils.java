package com.banhui.console.ui;

import org.xx.armory.swing.UIControllers;

import static org.xx.armory.swing.DialogUtils.warn;

public class MsgUtils {
    private MsgUtils() {
    }

    public static void verifyError(
            Object event
    ) {
        warn(UIControllers.GLOBAL.getMessage("error-input-format"));
    }
}
