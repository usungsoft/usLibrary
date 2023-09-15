package com.usungsoft.usLibrary.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.usungsoft.usLibrary.exception.RfidConvertException;

import org.apache.commons.lang3.StringUtils;

/**
 * Description: RFID En/Decoding
 */
public class RfidUtils {
    // <editor-fold desc="USSOFT">
    public static class Default {
        private static boolean mRunning = false;

        private Default() {
            System.loadLibrary("rfidLib");
        }

        private native String decodingRfid(String rfidCode);

        private native boolean checkSupportCompany(String cdCompany);

        public static Default getInstance() { return DefaultHolder.INSTANCE; }

        private static class DefaultHolder {
            private static final Default INSTANCE = new Default();
        }

        public String decodeRfid(@NonNull String cdCompany, @NonNull String cdDepart, @NonNull String rfidCode) throws RfidConvertException {
            String errMessage = "";

            if (StringUtils.isBlank(cdCompany))
                errMessage = "cdCompany";
            else if (StringUtils.isBlank(cdDepart))
                errMessage = "cdDepart";
            else if (StringUtils.isBlank(rfidCode) || rfidCode.length() != 32)
                errMessage = "rfidCode";

            if (!StringUtils.isBlank(errMessage))
                throw new RfidConvertException(String.format("%s 값을 확인해 주세요.", errMessage));
            else {
                if (!checkSupportCompany(cdCompany))
                    throw new RfidConvertException("현재 지원되지 않는 회사코드 입니다.");
            }

            try {
                if (!mRunning) mRunning = true;

                String result = decodingRfid(rfidCode);

                mRunning = false;

                Log.d(getClass().getSimpleName(), "decodingRfid result :: " + result);

                if (StringUtils.equalsAny("error[1]", "error[2]", "error[3]"))
                    return "";

                return result;
            } catch (IllegalArgumentException e) {
                Log.d(getClass().getSimpleName(), "decodingRfid Illegal error !\n" + e.getMessage());
                return "";
            }
        }
    }
    // </editor-fold>
}
