package com.usungsoft.usLibrary.parsing;

import android.util.Log;

import com.usungsoft.usLibrary.BuildConfig;

import org.apache.commons.lang3.StringUtils;

public class Parse {
    private static String TAG = Parse.class.getSimpleName();

    /**
     * @param rfidCode is string of 24, 32 or Etc.. length
     *
     * @return {@code null} {@param rfidCode} parsed failed, if success 88 Barcode String.
     * */
    public static String convertRfidToBarcode(String rfidCode) {
        if (StringUtils.isBlank(rfidCode) || rfidCode.length() != 24) return null;

        String retBarcode = null;
        StringBuilder strBin = new StringBuilder();

        for (int i = 0; i < rfidCode.length(); i++) {
            // 1) 16진수 -> 10진수
            int convertedDec = Integer.parseInt(rfidCode.substring(i, i + 1), 16);

            // 2) 10진수 -> 2진수
            // 자릿수 맞추기위해 0으로 채움.
            strBin.append(StringUtils.leftPad(Integer.toBinaryString(convertedDec), 4, '0'));
        }

        String errMsg = null;

        if (StringUtils.isBlank(strBin.toString()) || strBin.length() < 58)
            errMsg = "[CODE_ERR1]=> SGTIN-Length 오류.";
        else if (!strBin.substring(0, 8).equals("00110000"))
            errMsg = "[CODE_ERR2]=> SGTIN-Header 오류.";
        else if (!strBin.substring(8, 11).equals("001"))
            errMsg = "[CODE_ERR3]=> SGTIN-Filter Value 오류.";
        else if (!strBin.substring(11, 14).equals("101"))
            errMsg = "[CODE_ERR4]=> SGTIN-Partition Value 오류.";

        if (!StringUtils.isBlank(errMsg)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, errMsg);

            return null;
        }

        // 자릿수 맞추기 위해 0으로 자릿수를 채운다.
        // COMPANY 구간 + ITEM 구간
        retBarcode = StringUtils.leftPad(Integer.parseInt(strBin.substring(14, 38), 2) + "", 6, '0')
                + StringUtils.leftPad(Integer.parseInt(strBin.substring(38, 58), 2) + "", 5, '0');

        if (StringUtils.isBlank(retBarcode)) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "[CODE_ERR5]=> SGTIN-RESULT nothing.");

            return null;
        }

        int odd = 0, even = 0, tot = 0;

        // Check Digit 구함
        for (int i = 1; i <= retBarcode.length(); i++) {
            int resultExpr = Integer.parseInt(retBarcode.substring(i - 1, (i - 1) + 1));

            if (i % 2 == 0) // even
                even += resultExpr;
            else // odd
                odd += resultExpr;
        }

        odd = odd * 3;
        tot = odd + even;

        return retBarcode + (10 - (tot % 10));
    }
}
