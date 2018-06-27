package com.banhui.console.ui;

import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.VerificationEvent;
import org.xx.armory.swing.components.verifiers.InputComponentVerificationAdapter;

import javax.swing.*;
import java.awt.Window;

import static org.xx.armory.swing.DialogUtils.warn;

public class MsgUtils {
    private MsgUtils() {
    }

    public static void verifyError(
            Object event
    ) {
        final VerificationEvent evt = (VerificationEvent) event;
        final String message;
        switch (evt.getStatus()) {
            case REQUIRED_ERROR:
                message = UIControllers.GLOBAL.getMessage("error-input-required");
                break;
            case FORMAT_ERROR:
                message = UIControllers.GLOBAL.getMessage("error-input-format");
                break;
            case TOO_SMALL_ERROR:
                message = UIControllers.GLOBAL.getMessage("error-input-too-small");
                break;
            case TOO_LARGE_ERROR:
                message = UIControllers.GLOBAL.getMessage("error-input-too-large");
                break;
            default:
                return;
        }

        Window owner;
        if (evt.getSource() instanceof InputComponentVerificationAdapter) {
            final InputComponentVerificationAdapter adapter = (InputComponentVerificationAdapter) evt.getSource();
            owner = (Window) SwingUtilities.getAncestorOfClass(Window.class, adapter.getComponent());
        } else {
            owner = null;
        }
        warn(owner, message);
    }
}
