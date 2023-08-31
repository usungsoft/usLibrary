package com.usungsoft.usLibrary;

import com.usungsoft.usLibrary.exception.RfidConvertException;
import com.usungsoft.usLibrary.utils.RfidUtils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void parseTest() {
        String rfidCode = "343375B69435664BBFF6AF40000002F8";

        RfidUtils.Default rfidConverter = RfidUtils.Default.getInstance();

        try {
            System.out.println(rfidConverter.decodeRfid("CCRTWORKS", "C", rfidCode));
        } catch (RfidConvertException e) {
            e.printStackTrace();
        }
    }
}