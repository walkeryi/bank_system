package com.bank.util;

import java.text.SimpleDateFormat; import java.util.Date; import java.util.concurrent.atomic.AtomicInteger;

public class NoGenerator {
    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static String tail() {
        int n = SEQ.incrementAndGet() % 1000;
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + String.format("%03d", n);
    }
    public static String accountNo(){return "622202"+tail();}
    public static String tradeNo(){return "TR"+tail();}
    public static String applyNo(){return "LWA"+tail();}
    public static String loanNo(){return "LA"+tail();}
    public static String orderNo(){return "PO"+tail();}
    public static String repayNo(){return "RP"+tail();}
}
