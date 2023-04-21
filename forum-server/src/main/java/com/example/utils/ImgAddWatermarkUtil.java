package com.example.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImgAddWatermarkUtil {

    // MutipartFile 转换为File
    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;

    }

    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWatermark(File srcImgFile,String toPath) throws IOException {
        //将文件对象转化为图片对象
        Image srcImg = ImageIO.read(srcImgFile);
        //获取图片的宽
        int srcImgWidth = srcImg.getWidth(null);
        //获取图片的高
        int srcImgHeight = srcImg.getHeight(null);

        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        // 加水印
        //创建画笔
        Graphics2D g = bufImg.createGraphics();
        //绘制原始图片
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        //-------------------------文字水印 start----------------------------
        //根据图片的背景设置水印颜色
        g.setColor(new Color(255,255,255,128));
        //设置字体  画笔字体样式为微软雅黑，加粗，文字大小为60pt
        int imgSize = srcImgHeight / 15;
        g.setFont(new Font("微软雅黑", Font.ITALIC, imgSize));
        //水印内容
        String waterMarkContent="origin:codebase";
        //设置水印的坐标(为原图片中间位置)
        int x = srcImgWidth - getWatermarkLength(waterMarkContent, g) - 10;
        int y = srcImgHeight - imgSize;
        //画出水印 第一个参数是水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
        g.drawString(waterMarkContent, x, y);
        g.dispose();
        //-------------------------文字水印 end----------------------------
        String extension = toPath.substring(toPath.lastIndexOf(".")+1);
        // 输出图片
        FileOutputStream outImgStream = new FileOutputStream(toPath);
        ImageIO.write(bufImg, extension, outImgStream);
        outImgStream.flush();
        outImgStream.close();
    }

    /**
     * 获取水印文字的长度
     * @param waterMarkContent
     * @param g
     * @return
     */
    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
}
