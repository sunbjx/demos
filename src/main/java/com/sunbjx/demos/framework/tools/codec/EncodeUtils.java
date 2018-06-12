package com.sunbjx.demos.framework.tools.codec;

import com.sunbjx.demos.framework.tools.CharsetConst;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author sunbjx
 * @since 2018/6/12 15:25
 */
public class EncodeUtils {

    private static final String DEFAULT_URL_ENCODING = CharsetConst.UTF_8_STR;
    private static final char[] BASE62               = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();


    public static String base64Encode(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    public static String base64UrlSafeEncode(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    public static byte[] base64Decode(String input) {
        return Base64.decodeBase64(input);
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }


    public static String urlEncode(String str, String encoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, encoding);
    }

    public static String urlDecode(String str, String encoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, encoding);
    }
}
