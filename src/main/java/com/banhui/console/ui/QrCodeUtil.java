package com.banhui.console.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.xx.armory.swing.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.commons.Validators.notNull;
import static org.xx.armory.swing.DialogUtils.prompt;

public class QrCodeUtil {

    public BufferedImage getQrCode(
            String url,
            String logo
    )
            throws WriterException, IOException {
        final int width = 300;
        final int height = 300;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// 创建图片
        final BufferedImage bg = toBufferedImage(bitMatrix);// 二维码图片

        if (logo != null && !logo.isEmpty()) {
            final BufferedImage iconBi = ImageIO.read(new File(logo));// 读logo
            if (iconBi != null) {
                final Graphics2D g = (Graphics2D) img.getGraphics();// 开启画图
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                final int iconWidth = iconBi.getWidth();
                final int iconHeight = iconBi.getHeight();
                if (iconWidth > 100 || iconHeight > 100) {
                    prompt(null, "无法使用此LOGO！");
                    return bg;
                }
                g.drawImage(bg.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
                g.drawImage(iconBi.getScaledInstance(iconWidth, iconHeight, Image.SCALE_DEFAULT),
                            (width - iconWidth) / 2,
                            (height - iconHeight) / 2, Color.WHITE, null);
                final Font font = new Font("Microsoft Yahei", Font.PLAIN, 18);
                g.setColor(Color.BLACK);
                g.setFont(font);
                return img;

            }
        }
        return bg;
    }

    public void outputImage(
            String filePath,
            BufferedImage img
    ) {
        String format = "png";
        try {
            ImageIO.write(img, format, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String choiceDirToSave(
    ) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("另存为");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileFilter pngFilter = new FileNameExtensionFilter("PNG图片 file(*.png)", "png");
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.setFileFilter(pngFilter);

        if (fileChooser.showSaveDialog(Application.mainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath() + ".png";
        } else {
            return null;
        }
    }

    public static BufferedImage toBufferedImage(
            BitMatrix matrix
    ) {
        notNull(matrix, "matrix");
        final int BLACK = 0x000000;
        final int WHITE = 0xFFFFFF;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
}
