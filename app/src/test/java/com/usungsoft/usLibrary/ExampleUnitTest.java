package com.usungsoft.usLibrary;

import com.usungsoft.usLibrary.parsing.Parse;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void parseTest() {
        // 30340BDEF44ABB6000000000 => 194493765253
        // 30340BC9E8073B6000000000 => 193146074056
        System.out.println("1) ==============");
        System.out.println(Parse.convertRfidToBarcode("30340BDEF44ABB6000000000"));
        System.out.println(Parse.convertRfidToBarcode("30340BDEF44ABB6000000000").equals("194493765253"));

        System.out.println("2) ==============");
        System.out.println(Parse.convertRfidToBarcode("30340BC9E8073B6000000000"));
        System.out.println(Parse.convertRfidToBarcode("30340BC9E8073B6000000000").equals("193146074056"));

//        System.out.println("3) ==============");
//        System.out.println(Parse.RfidUidToBarcode("30340BDEF44ABB6000000000"));
//        System.out.println(Parse.RfidUidToBarcode("30340BDEF44ABB6000000000").equals("194493765253"));
//
//        System.out.println("4) ==============");
//        System.out.println(Parse.RfidUidToBarcode("30340BC9E8073B6000000000"));
//        System.out.println(Parse.RfidUidToBarcode("30340BC9E8073B6000000000").equals("193146074056"));
    }
}