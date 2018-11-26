package com.banhui.console.ui;

import org.xx.armory.swing.Application;
import org.xx.armory.swing.DialogUtils;
import org.xx.armory.swing.MDIFrameUIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.StatusBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

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
//
                Boolean flag = false;
                Boolean timeout = false;
                String fudStr = Application.settings().getProperty("first-used-date");
                if (fudStr == null || fudStr.isEmpty()) {
                    Application.settings().setProperty("first-used-date", String.valueOf(new Date().getTime()));
                } else {
                    long fudLong = Long.valueOf(fudStr);
                    Date now = new Date();
                    if ((now.getTime() - fudLong) / 1000 > 10) {
                        timeout = true;
                    }
                }


                /*if (!flag && timeout) {
                    //TODO 试用期已过，弹出注册框
                    DialogUtils.inputText(null,"30天试用期已过，请填写注册码：",null);

                }else{*/
                    final int result = showModel(MainFrame.this, new SignInDlg());
                    if (result == DialogPane.OK) {
                        // 更新当前用户。
                    //}
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
        this.uiController.connect("browseBaPrjMortgage", this::browseBaPrjMortgage);

        this.uiController.connect("browsePerson", this::browsePerson);
        this.uiController.connect("browseCorp", this::browseCorp);
        this.uiController.connect("browseCommerceTrade", this::browseCommerceTrade);
        this.uiController.connect("browseYiMeiMessage", this::browseYiMeiMessage);
        this.uiController.connect("browsePlatformMessage", this::browsePlatformMessage);

        this.uiController.connect("browseAssignRegClient", this::browseAssignRegClient);
        this.uiController.connect("browseMyClient", this::browseMyClient);
        this.uiController.connect("browseClientMgrManagement", this::browseClientMgrManagement);
        this.uiController.connect("browseQueryNewRegClient", this::browseQueryNewRegClient);
        this.uiController.connect("dailyPerfStatistics", this::dailyPerfStatistics);
        this.uiController.connect("monthPerfStatistics", this::monthPerfStatistics);
        this.uiController.connect("createQrCode", this::createQrCode);

        this.uiController.connect("browseSysUsers", this::browseSysUsers);
        this.uiController.connect("browseSysRoles", this::browseSysRoles);
        this.uiController.connect("changePassword", this::changePassword);

        this.uiController.connect("editSettings", this::editSettings);
        this.uiController.connect("exit", this::exit);
        this.uiController.connect("about", this::about);

        // 设置状态栏。
//        initStatusBar();
    }

    private void createQrCode(ActionEvent actionEvent) {
        CreateQrCodeDlg dlg = new CreateQrCodeDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void monthPerfStatistics(ActionEvent actionEvent) {
        this.uiController.openChild("monthPerfStatistics", BrowseMonthPerfStatisticsFrame::new);
    }

    private void dailyPerfStatistics(ActionEvent actionEvent) {
        this.uiController.openChild("dailyPerfStatistics", BrowseDailyPerfStatisticsFrame::new);
    }

    private void browseQueryNewRegClient(ActionEvent actionEvent) {
        this.uiController.openChild("browseQueryNewRegClient", BrowseQueryNewRegClientFrame::new);
    }

    private void browseClientMgrManagement(ActionEvent actionEvent) {
        this.uiController.openChild("browseClientMgrManagement", BrowseClientMgrManagementFrame::new);
    }

    private void browseMyClient(ActionEvent actionEvent) {
        this.uiController.openChild("browseMyClient", BrowseMyClientFrame::new);
    }

    private void browseSysRoles(ActionEvent actionEvent) {
        this.uiController.openChild("browseSysRoles", BrowseSysRolesFrame::new);
    }

    private void dailyStatistic(
            ActionEvent actionEvent
    ) {
        StatisticDailyDlg dlg = new StatisticDailyDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void monthStatistic(ActionEvent actionEvent) {
        StatisticMonthDlg dlg = new StatisticMonthDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void firstInvestor(ActionEvent actionEvent) {
        StatisticNewInvestorsDlg dlg = new StatisticNewInvestorsDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void vipInvestor(ActionEvent actionEvent) {
        StatisticVipDlg dlg = new StatisticVipDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);

    }

    private void browsePerson(ActionEvent actionEvent) {
        this.uiController.openChild("browsePerson", BrowsePerAccountFrame::new);
    }

    private void browseCorp(
            ActionEvent actionEvent
    ) {
        this.uiController.openChild("browseCorp", BrowseOrgAccountFrame::new);
    }

    private void browseAssignRegClient(ActionEvent actionEvent) {
        this.uiController.openChild("browseAssignRegClient", BrowseAssignRegClientFrame::new);
    }

    private void browseCommerceTrade(ActionEvent actionEvent) {
        this.uiController.openChild("browseCommerceTrade", BrowseB2cDetailFrame::new);
    }

    private void browsePlatformMessage(ActionEvent actionEvent) {
        this.uiController.openChild("browsePlatformMessage", BrowsePlatformMessageFrame::new);
    }

    private void browseYiMeiMessage(ActionEvent actionEvent) {
        this.uiController.openChild("browseYiMeiMessage", BrowseYiMeiMessageFrame::new);
    }

    private void initStatusBar() {
        final StatusBar statusBar = this.uiController.getStatusBar();
        statusBar.setBorder(combineBorders(createMatteBorder(2, 0, 0, 0, getBackground().brighter()), statusBar.getBorder()));

        final JLabel toolTipLabel = new JLabel();
        toolTipLabel.setText("就绪");

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setOrientation(JProgressBar.HORIZONTAL);

        final JButton showRpcHistory = new JButton();
        showRpcHistory.setText(  Application.settings().getProperty("last-signed-user"));

        statusBar.add(toolTipLabel);
        statusBar.add(progressBar);
        statusBar.add(showRpcHistory);
    }

    private void browseProjects(
            ActionEvent event
    ) {
        this.uiController.openChild("browseProjects", BrowseProjectsFrame::new);
    }

    private void browseCreditAssignments(
            ActionEvent actionEvent
    ) {
        this.uiController.openChild("browseCreditAssignments", BrowseCreditAssignmentsFrame::new);
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

    private void browseBaPrjMortgage(
            ActionEvent event
    ) {
        this.uiController.openChild("browseBaPrjMortgage", BrowseBaPrjMortgageFrame::new);
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
