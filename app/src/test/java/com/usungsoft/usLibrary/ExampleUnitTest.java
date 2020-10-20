package com.usungsoft.usLibrary;

import com.usungsoft.usLibrary.parsing.Parse;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        double rssi = -90.9;
        System.out.println(">>>> " + rssi);
        System.out.println(">>>> " + Math.round(rssi));
        System.out.println(">>>> " + Math.round(rssi) * -1);

        System.out.println("3) ==============");
        int index = 0;
        for (int dbm = -113; dbm <= -10; dbm++) {
            if (index == 101) break;
            System.out.println(">>>> Index: " + (index++) + ", value: " + dbm);
        }

        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);
        int sum = 0;
        for (Integer value : list)
            sum += value;
        System.out.println("3) ==============");
        System.out.println((float) sum / list.size() * 0.01f);
    }
}