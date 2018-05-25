package com.banhui.console.ui;

import org.xx.armory.swing.Application;
import org.xx.armory.swing.MDIFrameUIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.xx.armory.swing.ComponentUtils.showModel;

public final class MainFrame
        extends JFrame {
    private MDIFrameUIController uiController;

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

                final int result = showModel(MainFrame.this, new SignInDlg());
            }
        });
    }

    /**
     * 初始化JFrame的界面元素。
     */
    private void initUi() {
        this.uiController = UIControllers.createMDIFrameUIController(this);
        this.uiController.load();

        this.uiController.connect("browseProjects", this::browseProjects);

        this.uiController.connect("browseBaPrjEngineers", this::browseBaPrjEngineers);
        this.uiController.connect("browseBaPrjCtors", this::browseBaPrjCtors);
        this.uiController.connect("browseBaPrjOwners", this::browseBaPrjOwners);
        this.uiController.connect("browseBaPrjGuaranteePers", this::browseBaPrjGuaranteePers);
        this.uiController.connect("browseBaPrjGuaranteeOrgs", this::browseBaPrjGuaranteeOrgs);
        this.uiController.connect("browseBaPrjBorPers", this::browseBaPrjBorPers);
        this.uiController.connect("browseBaPrjBorOrgs", this::browseBaPrjBorOrgs);

        this.uiController.connect("browseSysUsers", this::browseSysUsers);
        this.uiController.connect("exit", this::exit);
        this.uiController.connect("about", this::about);

        // 设置状态栏。
        initStatusBar();
    }

    private void initStatusBar() {
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

    private void browseProjects(
            ActionEvent event
    ) {
        this.uiController.openChild("browseProjects", BrowseProjectsFrame::new);
    }

    private void browseBaPrjEngineers(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjEngineers", BrowseBaPrjEngineersFrame::new);
    }

    private void browseBaPrjCtors(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjCtors", BrowseBaPrjCtorsFrame::new);
    }

    private void browseBaPrjOwners(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjOwners", BrowseBaPrjOwnersFrame::new);
    }

    private void browseBaPrjGuaranteePers(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjGuaranteePers", BrowseBaPrjGuaranteePersFrame::new);
    }

    private void browseBaPrjGuaranteeOrgs(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjGuaranteeOrgs", BrowseBaPrjGuaranteeOrgsFrame::new);
    }

    private void browseBaPrjBorPers(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjBorPers", BrowseBaPrjBorPersFrame::new);
    }

    private void browseBaPrjBorOrgs(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjBorOrgs", BrowseBaPrjBorOrgsFrame::new);
    }

    private void browseSysUsers(
            ActionEvent event
    ) {
        this.uiController.openChild("browseSysUsers", BrowseSysUsersFrame::new);
    }

    private void about(
            ActionEvent event
    ) {
        AboutDialog dlg = new AboutDialog();
        dlg.setFixedSize(false);

        showModel(this, dlg);
    }

    private void exit(
            ActionEvent event
    ) {
        Application.current().shutdown();
    }
}
