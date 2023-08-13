package com.assigndevelopers.library_api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Ref: Utils
 * https://mkyong.com/java/java-convert-ip-address-to-decimal-number/
 * https://mkyong.com/java/how-to-get-ip-address-in-java/
 * https://mkyong.com/java/how-to-get-client-ip-address-in-java/
 * */
public class Utils {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$";
        return StringUtils.hasText(email) && Pattern.matches(emailRegex, email);
    }

    public static boolean isValidPhone(String number) {
        return !number.contains(".") && number.length() == 10;
    }

    public static boolean isValidIP(String ip) {
        String regex = "^(\\d{1,3}|[0-9]{1,3}\\.){3}\\d{1,3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    // Get localhost / host Server IP
    public static String getLocalHostIp() throws UnknownHostException {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            // System.out.println("Current IP address : " + ip.getHostAddress());

        } catch (UnknownHostException e) {
            throw new UnknownHostException();
            // e.printStackTrace();
        }
        assert ip != null;
        return ip.getHostAddress();
    }

    // Get hostname
    public static String getHostName() throws UnknownHostException {
        InetAddress i = null;
        try {
            i = InetAddress.getLocalHost();
            // System.out.println("Current IP address : " + ip.getHostAddress());

        } catch (UnknownHostException e) {
            throw new UnknownHostException();
            // e.printStackTrace();
        }
        assert i != null;
        return i.getHostName();
    }

    /*
     * Proxy Server or Cloudflare
     * For web application which is behind a proxy server,
     * load balancer or the popular Cloudflare solution,
     * you should get the client IP address via the
     * HTTP request header X-Forwarded-For (XFF).*/
    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return isValidIP("173.201.182.15") ? remoteAddr : "";
    }

    /*
     * Alternative:
     * Review the client’s HTTP request header, and
     * try to identify where the IP address is stored.*/
    public static Map<String, String> getRequestHeadersInMap(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            result.put(key, value);
        }

        return result;
    }
}
