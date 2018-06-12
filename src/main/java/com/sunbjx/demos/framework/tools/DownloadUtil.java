package com.sunbjx.demos.framework.tools;


import com.sunbjx.demos.framework.tools.codec.Encodes;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author sunbjx
 * @since 2018/5/14 17:41
 */
public class DownloadUtil {

    private static final Logger logger = LoggerFactory.getLogger(DownloadUtil.class);

    public static void downLoadExcel(HSSFWorkbook excel, String fileName, HttpServletResponse response, HttpServletRequest request)
            throws Exception {
        if (excel == null || fileName == null || response == null) {
            return;
        }
        OutputStream ops = null;
        try {
            response.reset();
            // 设置文件类型
            //response.setContentType("application/msexcel");
            // 文件名
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.toUtf8String(request, fileName));

            //response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8"));
            ops = response.getOutputStream();
            excel.write(ops);

            ops.flush();
            response.flushBuffer();
        } catch (Exception e) {
            logger.error("Download Excel get error!", e);
        } finally {
            // 关闭流
            if (null != ops) {
                ops.close();
            }
            excel.close();
        }
    }
}
