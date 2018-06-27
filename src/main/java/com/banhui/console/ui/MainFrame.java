package com.banhui.console.ui;

import org.xx.armory.swing.Application;
import org.xx.armory.swing.MDIFrameUIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.StatusBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.BorderFactory.createMatteBorder;
import static org.xx.armory.swing.ComponentUtils.combineBorders;
import static org.xx.armory.swing.ComponentUtils.showModel;

public final class MainFrame
        extends JFrame {
    private MDIFrameUIController uiController;

    public MainFrame() {
        initUi();

        // 设置标题。
        setTitle(getTitle() + " " + this.uiController.formatMessage("title-version", Application.version()));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(
                    WindowEvent event
            ) {
                super.windowOpened(event);

                MainFrame.this.toFront();

                final int result = showModel(MainFrame.this, new SignInDlg());
                if (result == DialogPane.OK) {
                    // 更新当前用户。
                }
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
        this.uiController.connect("browseCreditAssignments", this::browseCreditAssignments);
        this.uiController.connect("dailyStatistic", this::dailyStatistic);
        this.uiController.connect("monthStatistic", this::monthStatistic);
        this.uiController.connect("firstInvestor", this::firstInvestor);
        this.uiController.connect("vipInvestor", this::vipInvestor);

        this.uiController.connect("browseBaPrjEngineers", this::browseBaPrjEngineers);
        this.uiController.connect("browseBaPrjCtors", this::browseBaPrjCtors);
        this.uiController.connect("browseBaPrjOwners", this::browseBaPrjOwners);
        this.uiController.connect("browseBaPrjGuaranteePers", this::browseBaPrjGuaranteePers);
        this.uiController.connect("browseBaPrjGuaranteeOrgs", this::browseBaPrjGuaranteeOrgs);
        this.uiController.connect("browseBaPrjBorPers", this::browseBaPrjBorPers);
        this.uiController.connect("browseBaPrjBorOrgs", this::browseBaPrjBorOrgs);

        this.uiController.connect("browsePerson", this::browsePerson);
        this.uiController.connect("browseCorp", this::browseCorp);
        this.uiController.connect("browseCommerceTrade", this::browseCommerceTrade);
        this.uiController.connect("browseYiMeiMessage", this::browseYiMeiMessage);
        this.uiController.connect("browsePlatformMessage", this::browsePlatformMessage);

        this.uiController.connect("browseSysUsers", this::browseSysUsers);
        this.uiController.connect("changePassword", this::changePassword);

        this.uiController.connect("editSettings", this::editSettings);
        this.uiController.connect("exit", this::exit);
        this.uiController.connect("about", this::about);

        // 设置状态栏。
        initStatusBar();
    }

    private void browseCreditAssignments(ActionEvent actionEvent) {

    }

    private void dailyStatistic(ActionEvent actionEvent) {

    }

    private void monthStatistic(ActionEvent actionEvent) {

    }

    private void firstInvestor(ActionEvent actionEvent) {
    }

    private void vipInvestor(ActionEvent actionEvent) {

    }

    private void browsePerson(ActionEvent actionEvent) {
    }

    private void browseCorp(ActionEvent actionEvent) {
    }

    private void browseCommerceTrade(ActionEvent actionEvent) {
    }

    private void browsePlatformMessage(ActionEvent actionEvent) {
        this.uiController.openChild("browsePlatformMessage",BrowsePlatformMessageFrame::new);
    }

    private void browseYiMeiMessage(ActionEvent actionEvent) {
        this.uiController.openChild("browseYiMeiMessage",BrowseYiMeiMessageFrame::new);
    }

    private void initStatusBar() {
        final StatusBar statusBar = this.uiController.getStatusBar();
        statusBar.setBorder(combineBorders(createMatteBorder(2, 0, 0, 0, getBackground().brighter()), statusBar.getBorder()));

        final JLabel toolTipLabel = new JLabel();
        toolTipLabel.setText("aaaaa");

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setOrientation(JProgressBar.HORIZONTAL);

        final JButton showRpcHistory = new JButton();
        showRpcHistory.setText("History");

        statusBar.add(toolTipLabel);
        statusBar.add(progressBar);
        statusBar.add(showRpcHistory);
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

    private void changePassword(
            ActionEvent event
    ) {
        showModel(this, new ChangePasswordDlg());
    }

    private void editSettings(
            ActionEvent event
    ) {
        showModel(this, new SettingsDlg());
    }

    private void about(
            ActionEvent event
    ) {
        final AboutDialog dlg = new AboutDialog();
        dlg.setFixedSize(false);

        showModel(this, dlg);
    }

    private void exit(
            ActionEvent event
    ) {
        Application.current().shutdown();
    }
}
