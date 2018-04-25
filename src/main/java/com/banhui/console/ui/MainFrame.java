package com.banhui.console.ui;

import org.xx.armory.swing.Application;
import org.xx.armory.swing.UIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainFrame
        extends JFrame {
    private UIController uiController;
    private JDesktopPane desktopPane;

    public MainFrame() {
        initUi();

        // 设置标题。
        setTitle(getTitle() + " " + Application.version());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(
                    WindowEvent event
            ) {
                super.windowOpened(event);

                MainFrame.this.toFront();

                final int result = new SignInDlg().showModel(MainFrame.this);
            }
        });
    }

    /**
     * 初始化JFrame的界面元素。
     */
    private void initUi() {
        this.uiController = UIControllers.createJMDIFrameUIController(this);
        this.uiController.load();

        this.uiController.connect("exit", this::exit);
        this.uiController.connect("about", this::about);

        // 设置状态栏。
        initStatusBar();
    }

    private void initStatusBar() {
        //final J
        final JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 2));

        final JLabel toolTipLabel = new JLabel();
        toolTipLabel.setText("aaaaa");
        statusBar.add(toolTipLabel);

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setOrientation(JProgressBar.HORIZONTAL);
        statusBar.add(progressBar);

        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void about(
            ActionEvent event
    ) {
        AboutDialog dialog = new AboutDialog();
        dialog.setFixedSize(false);
        dialog.showModel(this);
    }

    private void exit(
            ActionEvent event
    ) {
        Application.current().shutdown();
    }
}
