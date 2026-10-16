package com.bank.util;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {
    public static String md5(String t) { return DigestUtils.md5Hex(t == null ? "" : t); }
}
