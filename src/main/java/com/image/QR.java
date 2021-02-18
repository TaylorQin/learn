package com.image;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.xmlgraphics.util.UnitConv;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QR {

    /**
     * 生成二维码
     *
     * @param contents
     * @param width
     * @param height
     * @return BufferedImage
     * @throws WriterException
     */
    public static BufferedImage enQRCode(String contents, int width, int height) throws WriterException {
        final Map<EncodeHintType, Object> hints = new HashMap(8) {
            {
                put(EncodeHintType.CHARACTER_SET, "UTF-8");
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                put(EncodeHintType.MARGIN, 0);
            }
        };
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 绘图， 将图片画到背景图上
     *
     * @param backgroundPath 背景图
     * @param zxingImage     待画的图
     * @param x
     * @param y
     * @return BufferedImage
     * @throws IOException
     */
    public static BufferedImage drawImage(String backgroundPath, BufferedImage zxingImage, int x, int y) throws IOException {
        BufferedImage backgroundImage;
        try (InputStream imagein = new FileInputStream(backgroundPath)) {
            backgroundImage = ImageIO.read(imagein);
        }
        Objects.requireNonNull(backgroundImage, ">>>>>image should not be bull");
        Objects.requireNonNull(zxingImage, ">>>>>QR should not be null");
        if ((zxingImage.getWidth() + x) > backgroundImage.getWidth() || (zxingImage.getHeight() + y) > backgroundImage.getHeight()) {
            throw new IOException(">>>>>QR should be a little small than image");
        }

        Graphics2D g = backgroundImage.createGraphics();
        g.drawImage(zxingImage, x, y, zxingImage.getWidth(), zxingImage.getHeight(), null);
        return backgroundImage;
    }

    /**
     * 将文本画到背景图中， 超宽自动换行
     *
     * @param backgroundImage
     * @param text 文本内容
     * @param x
     * @param y
     * @param font
     * @param color
     * @param maxWidth 文本最大宽度，超宽换行
     * @param lineHeight      行高
     * @return 额外换行数量， 文本不超长返回0， 超长则返回此文本换行数量
     */
    public static int drawText(BufferedImage backgroundImage, String text, int x, int y, Font font, Color color, int maxWidth, int lineHeight) {
        Graphics2D g = backgroundImage.createGraphics();
        g.setColor(color);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setFont(font);
        return drawText(g, font, text, x, y, maxWidth, lineHeight);
    }

    /**
     * 文字超出限定长度自动换行
     *
     * @param g           画布
     * @param font        字体样式
     * @param text        文字
     * @param widthLength 最大长度  （多少长度后需要换行）
     * @param x           文字位置坐标  x
     * @param y           文字位置坐标 Y
     * @param yn          每次换行偏移多少pt
     */
    private static int drawText(Graphics2D g, Font font, String text, int x, int y, int widthLength, int yn) {
        int result = 0;
        FontMetrics fg = g.getFontMetrics(font);
        java.util.List<String> ls = new ArrayList<>(2);
        getListText(fg, text, widthLength, ls);
        for (int i = 0; i < ls.size(); i++) {
            if (i == 0) {
                g.drawString(ls.get(i), (int) UnitConv.mm2pt(x), (int) UnitConv.mm2pt(y));
            } else {
                g.drawString(ls.get(i), (int) UnitConv.mm2pt(x), (int) UnitConv.mm2pt(y + i * yn));
                result = i;
            }
        }
        return result;
    }


    /**
     * 递归 切割字符串
     *
     * @param fg
     * @param text
     * @param widthLength
     * @param ls
     */
    private static void getListText(FontMetrics fg, String text, int widthLength, java.util.List<String> ls) {
        String ba = text;
        boolean b = true;
        int i = 1;
        while (b) {
            if (fg.stringWidth(text) > widthLength) {
                text = text.substring(0, text.length() - 1);
                i++;
            } else {
                b = false;
            }
        }
        if (i != 1) {
            ls.add(ba.substring(0, ba.length() - i));
            getListText(fg, ba.substring(ba.length() - i), widthLength, ls);
        } else {
            ls.add(text);
        }
    }


    public static InputStream bufferedImageToInputStream(BufferedImage backgroundImage) throws IOException {
        return bufferedImageToInputStream(backgroundImage, "png");
    }

    public static InputStream bufferedImageToInputStream(BufferedImage backgroundImage, String format) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try (ImageOutputStream imOut = ImageIO.createImageOutputStream(bs)) {
            ImageIO.write(backgroundImage, format, imOut);
            InputStream is = new ByteArrayInputStream(bs.toByteArray());
            return is;
        }
    }

    public static void saveFile(InputStream is, String fileName) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(is);
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName))) {
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
        }
    }

    public static void main(String[] args) {
        int width = 450;
        int height = 450;
        String content = "https://job.ctrip.com/m/index.html#/job-detail/760039923";
        BufferedImage zxingImage = null;
        try {
            zxingImage = enQRCode(content, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        String backgroundPath = "D:\\Users\\xtqin\\Pictures\\0zm0k120008f589ph3015.jpg";
        String title = "工作职责:\n";
        String text = "职责描述：\n" +
                "1、负责业务/营销页面的开发、第三方合作站点和移动端Web APP开发\n" +
                "2、负责HTML5开发团队技术工作，包括针对开发需求的技术方案设计、开发指导和团队技能提升等\n" +
                "3、设计和规划HTML5的整体架构和技术规范，并在日常开发实践中贯彻执行\n" +
                "4、优化代码实现，提高产品性能\n" +
                "5、配合开发主管，参与HTML5应用架构的设计和规范化工作";

        InputStream inputStream = null;
        try {
            BufferedImage image = drawImage(backgroundPath, zxingImage, 525, 1640);
            //Font font = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Windows\\Fonts\\msyhbd.ttc"));
            Font font = new Font("微软雅黑", Font.BOLD, 20);
            font = font.deriveFont(70F).deriveFont(Font.BOLD);

            int x = 55, y = 250, maxWidth = 1250, lineHeight = 30;

            Color color = new Color(255, 255, 255);
            drawText(image, title, x, y - 50, font, color, maxWidth, lineHeight);

            font = font.deriveFont(50F).deriveFont(Font.BOLD);
            String[] split = text.split("\\n");
            for (String s : split) {
                y += (drawText(image, s, x, y, font, color, maxWidth, lineHeight) + 1) * lineHeight;
                inputStream = bufferedImageToInputStream(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String originalFileName = "D:\\Users\\xtqin\\Desktop\\result.png";
        try {
            saveFile(inputStream, originalFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}