package com.sunbjx.demos.framework.tools.network;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * IP工具类
 *
 * @author sunbjx
 * @since 2018/6/12 16:37
 */
public class IPUtils {

    public static long ipToLong(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return 0;
        }
        try {
            long[] ipArray = new long[4];
            //先找到IP地址字符串中.的位置
            int position1 = ip.indexOf(".");
            int position2 = ip.indexOf(".", position1 + 1);
            int position3 = ip.indexOf(".", position2 + 1);
            //将每个.之间的字符串转换成整型
            ipArray[0] = Long.parseLong(ip.substring(0, position1));
            ipArray[1] = Long.parseLong(ip.substring(position1 + 1, position2));
            ipArray[2] = Long.parseLong(ip.substring(position2 + 1, position3));
            ipArray[3] = Long.parseLong(ip.substring(position3 + 1));
            return (ipArray[0] << 24) + (ipArray[1] << 16) + (ipArray[2] << 8) + ipArray[3];
        } catch (Exception e) {
            return 0;
        }
    }

    public static String longToIp(long longIp) {
        StringBuffer sb = new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        //将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    /**
     * Gets real ip address.
     *
     * @param request the request
     * @return the real ip address
     */
    public static String getRealIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        return ip;
    }

    /**
     * 验证 realIp 是不是 符合给定 的 ip or ipNetMask 规则。
     *
     * @param ipWithNetMask the ip with net mask
     * @param realIpAddress the real ip address
     * @return the boolean
     * @author fengyapeng
     * @since 2016 -07-26 13:57:26
     */
    public static boolean matchIpAddress(String ipWithNetMask, String realIpAddress) {
        return new IpAddressMatcher(ipWithNetMask).matches(realIpAddress);
    }

    /**
     * 验证 servletRequest 是不是 符合给定 的 ip or ipNetMask 规则。
     *
     * @param ipWithNetMask the ip with net mask
     * @param request       the request
     * @return the boolean
     * @author fengyapeng
     * @since 2016 -07-26 13:57:24
     */
    public static boolean matchIpAddress(String ipWithNetMask, HttpServletRequest request) {
        return new IpAddressMatcher(ipWithNetMask).matches(getRealIpAddress(request));
    }


    /**
     * Matches a request based on IP Address or subnet mask matching against the remote address.
     * <p>
     * Both IPv6 and IPv4 addresses are supported, but a matcher which is configured with an IPv4 address will
     * never match a request which returns an IPv6 address, and vice-versa.
     *
     * @author Luke Taylor
     * @since 3.0.2
     */
    public static final class IpAddressMatcher {

        private final int nMaskBits;
        private final InetAddress requiredAddress;

        /**
         * Takes a specific IP address or a range specified using the
         * IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
         *
         * @param ipAddress the address or range of addresses from which the request must come.
         */
        public IpAddressMatcher(String ipAddress) {

            if (ipAddress.indexOf('/') > 0) {
                String[] addressAndMask = StringUtils.split(ipAddress, "/");
                ipAddress = addressAndMask[0];
                nMaskBits = Integer.parseInt(addressAndMask[1]);
            } else {
                nMaskBits = -1;
            }
            requiredAddress = parseAddress(ipAddress);
        }

        public boolean matches(ServletRequest request) {
            return matches(request.getRemoteAddr());
        }

        public boolean matches(String address) {
            InetAddress remoteAddress = parseAddress(address);

            if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
                return false;
            }

            if (nMaskBits < 0) {
                return remoteAddress.equals(requiredAddress);
            }

            byte[] remAddr = remoteAddress.getAddress();
            byte[] reqAddr = requiredAddress.getAddress();

            int oddBits = nMaskBits % 8;
            int nMaskBytes = nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
            byte[] mask = new byte[nMaskBytes];

            Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

            if (oddBits != 0) {
                int finalByte = (1 << oddBits) - 1;
                finalByte <<= 8 - oddBits;
                mask[mask.length - 1] = (byte) finalByte;
            }

            //       System.out.println("Mask is " + new sun.misc.HexDumpEncoder().encode(mask));

            for (int i = 0; i < mask.length; i++) {
                if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i])) {
                    return false;
                }
            }

            return true;
        }

        private InetAddress parseAddress(String address) {
            try {
                return InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Failed to parse address" + address, e);
            }
        }
    }
}
