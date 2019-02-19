package com.banhui.console.ui;

import com.banhui.console.rpc.BaseVersionService;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.MDIFrameUIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.StatusBar;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.swing.BorderFactory.createMatteBorder;
import static org.xx.armory.swing.ComponentUtils.combineBorders;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.DialogUtils.confirm;


public final class MainFrame
        extends JFrame {
    private MDIFrameUIController uiController;

    public MainFrame() {
        initUi();

        // 设置标题。
        setTitle(getTitle() + " " + this.uiController.formatMessage("title-version", BaseVersionService.getBhtVersion()));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(
                    WindowEvent event
            ) {
                super.windowOpened(event);
                MainFrame.this.toFront();

                int compare = compareVersion();
                if (compare == -1) {
                    if (confirm(null, uiController.getMessage("find-update"))) {
                        uiController.call("update");
                    } else {
                        SignIn();
                    }
                } else {
                    SignIn();
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
        this.uiController.connect("infoDisclosure", this::infoDisclosure);

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

        this.uiController.connect("exportExcel", this::exportExcel);
        this.uiController.connect("editSettings", this::editSettings);
        this.uiController.connect("exit", this::exit);
        this.uiController.connect("resign", this::resign);
        this.uiController.connect("about", this::about);
        this.uiController.connect("manual", this::downloadManual);
        this.uiController.connect("update", this::checkAndupdate);

    }

    private void resign(ActionEvent actionEvent) {
        SignIn();
    }

    private void checkAndupdate(
            ActionEvent actionEvent
    ) {

        if (compareVersion() == -1) {
            this.uiController.disable("update");
            FileUtil fileUtil = new FileUtil(null);
            String dirPath = fileUtil.choiceDirToSave("setup.exe");
            if (dirPath != null && !dirPath.isEmpty()) {
                try {
                    URL url = new URL("http://192.168.11.30/update/console/setup.exe");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(3000);
                    urlCon.setReadTimeout(3000);
                    int code = urlCon.getResponseCode();
                    if (code != HttpURLConnection.HTTP_OK) {
                        throw new Exception("文件读取失败");
                    }
                    InputStream is = new DataInputStream(urlCon.getInputStream());
                    OutputStream os = new DataOutputStream(new FileOutputStream(dirPath));
                    byte[] buffer = new byte[1024];
                    final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Long>() {

                        @Override
                        public String getTitle() {
                            return "下载最新文件";
                        }

                        @Override
                        protected Collection<Long> load() {
                            List<Long> retList = new ArrayList<>();
                            try {
                                int rc;
                                while ((rc = is.read(buffer, 0, buffer.length)) > 0) {
                                    retList.add((long) rc);
                                    os.write(buffer, 0, rc);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    is.close();
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return retList;
                        }

                        @Override
                        protected String getCurrent(
                                int i,
                                Long rc
                        ) {
                            return "下载文件中:";
                        }

                        @Override
                        protected void execute(
                                int i,
                                Long rc
                        ) {
                        }
                    });
                    showModel(null, dlg);
                    if (confirm(null, this.uiController.getMessage("update-confirm"))) {
                        Runtime.getRuntime().exec(dirPath); // 打开exe文件
                        this.uiController.call("exit");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.uiController.enable("update");
        } else if (compareVersion() == 99) {
            prompt(null, "访问版本服务暂停，请稍后再试！");
        } else {
            prompt(null, "当前已是最新版本");
        }
    }

    private void downloadManual(
            ActionEvent actionEvent
    ) {
        FileUtil fileUtil = new FileUtil(null);
        String dirPath = fileUtil.choiceDirToSave("操作手册.docx");
        fileUtil.writeFile(fileUtil.readFile("/操作手册.docx"), dirPath);
    }

    private void exportExcel(
            ActionEvent actionEvent
    ) {
        InternalFramePane internalFramePane = this.uiController.getSelectedChild();
        if (internalFramePane instanceof BaseFramePane) {
            BaseFramePane baseFramePane = (BaseFramePane) internalFramePane;

            String titile = baseFramePane.getExport_title();
            TypedTableModel typedTableModel = baseFramePane.getTypedTableModel();
            if (typedTableModel != null && typedTableModel.getRowCount() != 0 && titile != null) {
                this.uiController.disable("exportExcel");

                baseFramePane.export();

                this.uiController.enable("exportExcel");
            } else {
                warn(null, "导出内容为空！");
            }
        } else {
            warn(null, "导出内容为空！");
        }


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

    private void infoDisclosure(ActionEvent actionEvent) {
        this.uiController.openChild("infoDisclosure", BrowseInfoDisclosureFrame::new);
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

    private void initStatusBar(String curUserName) {
        final StatusBar statusBar = this.uiController.getStatusBar();
        statusBar.setBorder(combineBorders(createMatteBorder(2, 0, 0, 0, getBackground().brighter()), statusBar.getBorder()));

        final JLabel toolTipLabel = new JLabel();
        toolTipLabel.setText("就绪");

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setBorderPainted(false);
        progressBar.setOrientation(JProgressBar.HORIZONTAL);
        progressBar.setBackground(new Color(180,160,120));
//        progressBar.setForeground(Color.red);

        final JButton showRpcHistory = new JButton();
        showRpcHistory.setText("当前用户为："+curUserName);

        final JLabel curUserLabel = new JLabel();
        curUserLabel.setText("当前用户为："+curUserName+"    ");

        JPanel hPanel = new JPanel();
        int width = statusBar.getWidth();
        hPanel.setSize(width-20,20);

//        statusBar.add(toolTipLabel);
        statusBar.add(hPanel);
        statusBar.add(curUserLabel);
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

    private void SignIn() {
        SignInDlg dlg = new SignInDlg();
        dlg.setFixedSize(false);
        if (showModel(MainFrame.this, dlg)== DialogPane.OK) {
            // 更新当前用户。
            String curUserName = dlg.getCurUserName();
            if(curUserName != null && !curUserName.isEmpty()){
                initStatusBar(curUserName);
            }
        }
    }

    /**
     * 返回99不能访问最新版本地址，或者获取版本信息出错
     *
     * @return
     */
    private int compareVersion() {
        String lastedVersion = BaseVersionService.getLatestVersion("http://192.168.11.30/update/console/lasted");
        String localeVersion = BaseVersionService.getBhtVersion();
        if (lastedVersion != null && !lastedVersion.isEmpty()) {
            return BaseVersionService.compareVersion(localeVersion.trim(), lastedVersion.trim());
        } else {
            return 99;
        }
    }

}
