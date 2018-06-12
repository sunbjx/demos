package com.sunbjx.demos.framework.tools;

import java.nio.charset.Charset;

/**
 * @author sunbjx
 * @since 2018/6/12 15:05
 */
public interface CharsetConst {

    String ISO_8859_1_STR = "ISO-8859-1";
    String UTF_8_STR      = "UTF-8";
    String GBK_STR        = "GBK";
    String GB2312_STR     = "GB2312";
    String GB18030_STR    = "GB18030";
    String BIG5_STR       = "Big5";


    Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    Charset UTF_8      = Charset.forName("UTF-8");
    Charset GBK        = Charset.forName("GBK");
    Charset GB2312     = Charset.forName("GB2312");
    Charset GB18030    = Charset.forName("GB18030");
    Charset BIG5       = Charset.forName("Big5");
}
