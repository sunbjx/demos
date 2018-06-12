package com.sunbjx.demos.framework.tools;

import com.sunbjx.demos.framework.tools.support.ImageProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author sunbjx
 * @since 2018/6/12 15:14
 */
public class ImageUtils {

    private final static Logger log = LoggerFactory.getLogger(ImageUtils.class);
    public static final String EXTENSION_STRING = "png, jpg, jpeg, gif, bmp";
    static String[] imageArray = new String[]{"png", "jpg", "jpeg", "gif", "bmp"};

    static Map<String, Integer> imageDirection = new HashMap<>();

    static {
        imageDirection.put("Top, left side (Horizontal / normal)", 0);
        imageDirection.put("Top, right side (Mirror horizontal)", 0);
        imageDirection.put("Bottom, right side (Rotate 180)", 180);
        imageDirection.put("Bottom, left side (Mirror vertical)", 0);
        imageDirection.put("Left side, top (Mirror horizontal and rotate 270 CW)", 90);
        imageDirection.put("Right side, top (Rotate 90 CW)", 90);
        imageDirection.put("Right side, bottom (Mirror horizontal and rotate 90 CW)", 270);
        imageDirection.put("Left side, bottom (Rotate 270 CW)", 270);
    }

    public static boolean isImage(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return false;
        }
        String format = filename.toLowerCase();
        for (String name : imageArray) {
            if (name.endsWith(format)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取图片的一些属性(宽高、图片类型)
     */
    @SuppressWarnings("rawtypes")
    public static ImageProperties getImageProperties(InputStream is) {
        ImageProperties ip = new ImageProperties();
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            iis.mark();
            Iterator iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                ip.setImageType(null);
            } else {
                ImageReader reader = (ImageReader) iter.next();
                ip.setImageType(reader.getFormatName());
            }
            iis.reset();
            BufferedImage bi = ImageIO.read(iis);
            ip.setWidth(bi.getWidth());
            ip.setHeight(bi.getHeight());
        } catch (Exception e) {
            ip.setWidth(0);
            ip.setHeight(0);
            ip.setImageType(null);
        }
        return ip;
    }

    /**
     * 获取图片的方向
     *
     * @author <a href="mailto:shiyanglong@knet.cn">史阳龙</a>
     */
    public static int getOrientation(InputStream is) {
        return 0;
    }

    /**
     * 图片的旋转
     *
     * @author <a href="mailto:shiyanglong@knet.cn">史阳龙</a>
     */
    public static BufferedImage rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // calculate the new image size
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 旋转
     *
     * @author <a href="mailto:shiyanglong@knet.cn">史阳龙</a>
     */
    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

    public static void main(String[] args) throws FileNotFoundException {
        ImageProperties is = ImageUtils
                .getImageProperties(new FileInputStream("C:\\Users\\Administrator\\Desktop\\QQ截图20140310131330.jpg"));

        System.out.println(is.toString());
    }
}
