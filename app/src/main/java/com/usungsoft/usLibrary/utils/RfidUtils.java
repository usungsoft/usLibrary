package com.usungsoft.usLibrary.utils;

import androidx.annotation.NonNull;

import com.usungsoft.usLibrary.exception.RfidConvertException;

import org.apache.commons.lang3.StringUtils;

/**
 * Description: RFID En/Decoding
 */
public class RfidUtils {
    // <editor-fold desc="USSOFT">
    public static class Default {
        private Default() {
            System.loadLibrary("rfidLib");
        }

        private native String decodingRfid(String rfidCode);

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

            return decodingRfid(rfidCode);
        }
    }
    // </editor-fold>
}
