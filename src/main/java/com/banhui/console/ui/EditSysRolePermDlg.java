package com.banhui.console.ui;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditSysRolePermDlg
        extends DialogPane {

    private volatile String roleName;

    public EditSysRolePermDlg(
            String roleName,
            String title
    ) {
        if (roleName != null && !roleName.isEmpty()) {
            setTitle(getTitle() + roleName);
        }
        this.roleName = roleName;
        controller().setText("title", title);
        JLabel titleLabel = controller().get(JLabel.class, "title");
        titleLabel.setForeground(Color.blue);

        controller().connect("pro-base", "change", this::proBase);
        controller().connect("pro-permissible", "change", this::proPermissible);
        controller().connect("pro-bor-per", "change", this::proBorPer);
        controller().connect("pro-bor-org", "change", this::proBorOrg);
        controller().connect("pro-gua-per", "change", this::proGuaPer);
        controller().connect("pro-gua-org", "change", this::proGuaOrg);
        controller().connect("pro-rating", "change", this::proRating);
        controller().connect("pro-audit", "change", this::proAudit);
        controller().connect("pro-operate", "change", this::proOperate);
        controller().connect("pro-repay", "change", this::proRepay);
        controller().connect("pro-loan", "change", this::proLoan);
        controller().connect("pro-bor-apply", "change", this::proBorApply);
        controller().connect("pro-protocol", "change", this::proProtocol);
        controller().connect("credit", "change", this::credit);
        controller().connect("statistics", "change", this::statistics);
        controller().connect("disclosure", "change", this::disclosure);
        controller().connect("prj-engineer", "change", this::prjEngineer);
        controller().connect("prj-ctors", "change", this::prjCtors);
        controller().connect("prj-owner", "change", this::prjOwner);
        controller().connect("prj-gua-per", "change", this::prjGuaPer);
        controller().connect("prj-gua-org", "change", this::prjGuaOrg);
        controller().connect("prj-bor-per", "change", this::prjBorPer);
        controller().connect("prj-bor-org", "change", this::prjBorOrg);
        controller().connect("acc-base", "change", this::accBase);
        controller().connect("acc-per", "change", this::accPer);
        controller().connect("acc-org", "change", this::accOrg);
        controller().connect("acc-transfer", "change", this::accTransfer);
        controller().connect("frozen-funds", "change", this::frozenFunds);
        controller().connect("plat-message", "change", this::platMessage);
        controller().connect("yimei-message", "change", this::yimeiMessage);
        controller().connect("reg-investors", "change", this::regInvestors);
        controller().connect("customer-manager", "change", this::customerManager);
        controller().connect("manager-statistic", "change", this::managerStatistic);
        controller().connect("sys-acc", "change", this::sysAcc);
        controller().connect("sys-role", "change", this::sysRole);

        new SysProxy().findPermsByRole(roleName)
                      .thenApplyAsync(Result::array)
                      .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void proBase(Object event) {
        final Boolean flag = controller().getBoolean("pro-base");
        int[] perms = {60012, 60010, 60011, 60014, 60013, 60001, 60002, 60003, 60015, 20483, 60016, 60017, 60018, 60019, 60020, 60021};
        doSelect(perms, flag);
    }

    private void proPermissible(Object event) {
        final Boolean flag = controller().getBoolean("pro-permissible");
        int[] perms = {80106, 80107, 80108};
        doSelect(perms, flag);
    }

    private void proBorPer(Object event) {
        final Boolean flag = controller().getBoolean("pro-bor-per");
        int[] perms = {80091, 80092, 80093};
        doSelect(perms, flag);
    }

    private void proBorOrg(Object event) {
        final Boolean flag = controller().getBoolean("pro-bor-org");
        int[] perms = {80096, 80097, 80098};
        doSelect(perms, flag);
    }

    private void proGuaPer(Object event) {
        final Boolean flag = controller().getBoolean("pro-gua-per");
        int[] perms = {80081, 80082, 80083};
        doSelect(perms, flag);
    }

    private void proGuaOrg(Object event) {
        final Boolean flag = controller().getBoolean("pro-gua-org");
        int[] perms = {80086, 80087, 80088};
        doSelect(perms, flag);
    }

    private void proRating(Object event) {
        final Boolean flag = controller().getBoolean("pro-rating");
        int[] perms = {80101, 80102};
        doSelect(perms, flag);
    }

    private void proAudit(Object event) {
        final Boolean flag = controller().getBoolean("pro-audit");
        int[] perms = {60060, 60061, 60062, 60063, 60064, 60065, 60066, 60067, 60069};
        doSelect(perms, flag);
    }

    private void proOperate(Object event) {
        final Boolean flag = controller().getBoolean("pro-operate");
        int[] perms = {60080, 60087, 60088, 60089, 60090, 60091, 60092, 60093, 60094, 60095, 60097, 60098, 60099, 60100, 60101, 60102, 60180, 60181};
        doSelect(perms, flag);
    }

    private void proRepay(Object event) {
        final Boolean flag = controller().getBoolean("pro-repay");
        int[] perms = {60130, 60131, 60133, 60134, 60135, 60171, 60136, 60137};
        doSelect(perms, flag);
    }

    private void proLoan(Object event) {
        final Boolean flag = controller().getBoolean("pro-loan");
        int[] perms = {60120, 60121};
        doSelect(perms, flag);
    }

    private void proBorApply(Object event) {
        final Boolean flag = controller().getBoolean("pro-bor-apply");
        int[] perms = {60150, 60151};
        doSelect(perms, flag);
    }

    private void proProtocol(Object event) {
        final Boolean flag = controller().getBoolean("pro-protocol");
        int[] perms = {60110, 60111, 60112, 60113, 60115};
        doSelect(perms, flag);
    }

    private void credit(Object event) {
        final Boolean flag = controller().getBoolean("credit");
        int[] perms = {60160, 60163};
        doSelect(perms, flag);
    }

    private void statistics(Object event) {
        final Boolean flag = controller().getBoolean("statistics");
        int[] perms = {80901, 80902, 80903, 80904};
        doSelect(perms, flag);
    }

    private void disclosure(Object event) {
        final Boolean flag = controller().getBoolean("disclosure");
        int[] perms = {81001, 81002, 81003, 81004, 81005, 81006, 81007, 81008};
        doSelect(perms, flag);
    }

    private void prjEngineer(Object event) {
        final Boolean flag = controller().getBoolean("prj-engineer");
        int[] perms = {80071, 80072, 80073, 80074, 80075};
        doSelect(perms, flag);
    }

    private void prjCtors(Object event) {
        final Boolean flag = controller().getBoolean("prj-ctors");
        int[] perms = {80021, 80022, 80023, 80024, 80025};
        doSelect(perms, flag);
    }

    private void prjOwner(Object event) {
        final Boolean flag = controller().getBoolean("prj-owner");
        int[] perms = {80031, 80032, 80033, 80034, 80035};
        doSelect(perms, flag);
    }

    private void prjGuaPer(Object event) {
        final Boolean flag = controller().getBoolean("prj-gua-per");
        int[] perms = {80061, 80062, 80063, 80064, 80065};
        doSelect(perms, flag);
    }

    private void prjGuaOrg(Object event) {
        final Boolean flag = controller().getBoolean("prj-gua-org");
        int[] perms = {80041, 80042, 80043, 80044, 80045};
        doSelect(perms, flag);
    }

    private void prjBorPer(Object event) {
        final Boolean flag = controller().getBoolean("prj-bor-per");
        int[] perms = {80011, 80012, 80013, 80014, 80015};
        doSelect(perms, flag);
    }

    private void prjBorOrg(Object event) {
        final Boolean flag = controller().getBoolean("prj-bor-org");
        int[] perms = {80051, 80052, 80053, 80054, 80055};
        doSelect(perms, flag);
    }

    private void accBase(Object event) {
        final Boolean flag = controller().getBoolean("acc-base");
        int[] perms = {20466, 20467, 20468, 20469, 20470, 20471, 20472, 20473, 20475, 20477, 20479, 20480, 20481, 20482, 20484};
        doSelect(perms, flag);
    }

    private void accPer(Object event) {
        final Boolean flag = controller().getBoolean("acc-per");
        int[] perms = {20460, 20461, 20462, 20463, 20465};
        doSelect(perms, flag);
    }

    private void accOrg(Object event) {
        final Boolean flag = controller().getBoolean("acc-org");
        int[] perms = {20490, 20491, 20492, 20493, 20494, 20463};
        doSelect(perms, flag);
    }

    private void accTransfer(Object event) {
        final Boolean flag = controller().getBoolean("acc-transfer");
        int[] perms = {20610, 20611, 20612, 20613, 20614, 20615};
        doSelect(perms, flag);
    }

    private void frozenFunds(Object event) {
        final Boolean flag = controller().getBoolean("frozen-funds");
        int[] perms = {20474, 20650, 20651, 20652};
        doSelect(perms, flag);
    }

    private void platMessage(Object event) {
        final Boolean flag = controller().getBoolean("plat-message");
        int[] perms = {20630, 20631, 20632, 20633};
        doSelect(perms, flag);
    }

    private void yimeiMessage(Object event) {
        final Boolean flag = controller().getBoolean("yimei-message");
        int[] perms = {20640};
        doSelect(perms, flag);
    }


    private void regInvestors(Object event) {
        final Boolean flag = controller().getBoolean("reg-investors");
        int[] perms = {20701, 20704, 20703, 20702, 20503};
        doSelect(perms, flag);
    }

    private void customerManager(Object event) {
        final Boolean flag = controller().getBoolean("customer-manager");
        int[] perms = {20931, 20430, 20432, 20433, 20434, 20435, 20436};
        doSelect(perms, flag);
    }

    private void managerStatistic(Object event) {
        final Boolean flag = controller().getBoolean("manager-statistic");
        int[] perms = {20440, 20441, 20442, 20443};
        doSelect(perms, flag);
    }

    private void sysAcc(Object event) {
        final Boolean flag = controller().getBoolean("sys-acc");
        int[] perms = {10000, 10001, 10002, 10003, 10004, 10005, 10006, 10104};
        doSelect(perms, flag);
    }

    private void sysRole(Object event) {
        final Boolean flag = controller().getBoolean("sys-role");
        int[] perms = {10007, 10008, 10009, 10010, 10011, 10012, 10013};
        doSelect(perms, flag);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            int[] perms = {10000, 10001, 10002, 10003, 10004, 10005, 10006, 10007, 10008, 10009, 10010, 10011, 10012, 10013, 10104, 20430, 20432,
                    20433, 20434, 20435, 20436, 20440, 20441, 20442, 20443, 20460, 20461, 20462, 20463, 20464, 20465, 20466, 20467, 20468, 20469,
                    20470, 20471, 20472, 20473, 20474, 20475, 20477, 20479, 20480, 20481, 20482, 20483, 20484, 20490, 20491, 20492, 20493, 20494, 20503,
                    20610, 20611, 20612, 20613, 20614, 20615, 20630, 20631, 20632, 20633, 20640, 20650, 20651, 20652, 20701, 20702, 20703, 20704,
                    20931, 60001, 60002, 60003, 60010, 60011, 60012, 60013, 60014, 60015, 60016, 60017, 60018, 60019, 60020, 60021, 60060, 60061, 60062, 60063, 60064,
                    60065, 60066, 60067, 60069, 60080, 60087, 60088, 60089, 60090, 60091, 60092, 60093, 60094, 60095, 60097, 60098, 60099, 60100, 60101, 60102, 60110,
                    60111, 60112, 60113, 60115, 60120, 60121, 60130, 60131, 60133, 60134, 60135, 60136, 60137, 60150, 60151, 60160, 60163, 60171, 80011, 80012, 80013,
                    80014, 80015, 80021, 80022, 80023, 80024, 80025, 80031, 80032, 80033, 80034, 80035, 80041, 80042, 80043, 80044, 80045, 80051, 80052, 80053, 80054,
                    80055, 80061, 80062, 80063, 80064, 80065, 80071, 80072, 80073, 80074, 80075, 80081, 80082, 80083, 80086, 80087, 80088, 80091, 80092, 80093, 80096,
                    80097, 80098, 80101, 80102, 80106, 80107, 80108, 80901, 80902, 80903, 80904, 81001, 81002, 81003, 81004, 81005, 81006, 81007, 81008, 60180, 60181};
            StringBuffer str = new StringBuffer();
            for (int perm1 : perms) {
                String perm = String.valueOf(perm1);
                Boolean flag = controller().getBoolean(perm);
                if (flag) {
                    str.append(perm1);
                    str.append(",");
                }
            }
            if (str.toString().isEmpty()) {
                warn(this.getOwner(), controller().getMessage("empty"));
                controller().enable("ok");
                return;
            }
            final Map<String, Object> params = new HashMap<>();
            params.put("role-name", roleName);
            params.put("perms", str.toString());
            new SysProxy().assignPerms(params)
                          .thenApplyAsync(Result::intValue)
                          .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Integer integer,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            super.done(OK);
        }
    }


    private void searchCallback(
            Object[] result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            for (Object perm : result) {
                if (controller().getBoolean(perm.toString()) != null) {
                    controller().setBoolean(perm.toString(), true);
                }
            }
        }
    }

    private void doSelect(
            int[] perms,
            boolean flag
    ) {
        if (flag) {
            for (int perm : perms) {
                controller().setBoolean(String.valueOf(perm), true);
            }
        } else {
            for (int perm : perms) {
                controller().setBoolean(String.valueOf(perm), false);
            }
        }
    }
}
