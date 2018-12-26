package com.banhui.console.ui;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.MDIFrameUIController;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.AboutDialog;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.StatusBar;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static javax.swing.BorderFactory.createMatteBorder;
import static org.xx.armory.swing.ComponentUtils.combineBorders;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.DialogUtils.confirm;


public final class MainFrame
        extends JFrame {
    private MDIFrameUIController uiController;
    private static String tableTitle;
    private static TypedTableModel tableModel;

    public static void setTableTitleAndTableModel(
            String thisExportTableTile,
            TypedTableModel thisExportTableModel
    ) {
        tableTitle = thisExportTableTile;
        tableModel = thisExportTableModel;
    }

    public MainFrame() {
        initUi();

        // 设置标题。
        setTitle(getTitle() + " " + this.uiController.formatMessage("title-version", getBhtVersion()));
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

        this.uiController.connect("exportExcel", this::exportExcel);
        this.uiController.connect("editSettings", this::editSettings);
        this.uiController.connect("exit", this::exit);
        this.uiController.connect("about", this::about);
        this.uiController.connect("manual", this::downloadManual);
        this.uiController.connect("update", this::update);

        this.uiController.disable("exportExcel");
        String lastedVersion = getLatestVersion("http://192.168.11.30/update/console/lasted");
        String localeVersion = getBhtVersion();
        int compare = compareVersion(localeVersion, lastedVersion);
        if (compare == -1) {
            this.uiController.enable("update");
        } else {
            this.uiController.disable("update");
        }
//        JMenuBar bar = this.uiController.get(JMenuBar.class,"update");
//        bar.removeAll();
        // 设置状态栏。
//        initStatusBar();
    }

    private void update(
            ActionEvent actionEvent
    ) {
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
                        return "下载协议中:";
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
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (tableModel != null && tableModel.getRowCount() != 0 && tableTitle != null) {
            this.uiController.disable("exportExcel");
            new ExcelExportUtil(tableTitle, tableModel).choiceDirToSave();
            this.uiController.enable("exportExcel");
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
        showRpcHistory.setText(Application.settings().getProperty("last-signed-user"));

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

    public static int compareVersion(
            String v1,
            String v2
    ) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static String getLatestVersion(String urlStr) {
        URL url;
        BufferedReader in = null;
        String str = null;
        try {
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            str = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String getBhtVersion() {
        String version = null;
        Properties properties = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream("/default-settings.properties");
        try {
            properties.load(in);
            version = properties.getProperty("bht-version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}
