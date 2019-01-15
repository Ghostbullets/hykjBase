package com.hykj.base.utils.text;

import java.text.DecimalFormat;

/**
 * https://www.jianshu.com/p/5286a44ed9b1参考
 */
public class DecimalUtils {

    public static String signPriceSec(double price) {

        return new DecimalFormat("￥0.00").format(price);
    }

    public static String priceSec(double price) {
        //123.123   结果123.12
        // 123.0   结果123.00
        // 0.123  结果0.12
        return new DecimalFormat("0.00").format(price);
    }

    public static String signPriceSingle(double price) {
        return new DecimalFormat("￥0.0").format(price);
    }

    public static String priceSingle(double price) {
        //123.123   结果123.1
        // 123.0   结果123
        // 0.123  结果0.1
        return new DecimalFormat("0.0").format(price);
    }


    public static String signIntPrice(double price) {
        return new DecimalFormat("￥#############.##").format(price);
    }

    //（#.）截取整数部分和小数点后两位（小数部分为0时，不显示小数）
    public static String intPrice(double price) {
        //123.123   结果123.12
        // 123.0   结果123
        // 0.123  结果0.12
        return new DecimalFormat("#############.##").format(price);
    }

    /**
     * 返回带符号的除以100的价格
     * @param price
     * @return
     */
    public static String getSignPrice(int price){
        return signIntPrice(price/(float)100);
    }

    /**
     * 返回不带符号除以100的价格
     * @param price
     * @return
     */
    public static String getPrice(int price){
        return intPrice(price/(float)100);
    }
}
