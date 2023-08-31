package com.usungsoft.usLibrary.utils;

import androidx.annotation.NonNull;

import com.usungsoft.usLibrary.BuildConfig;
import com.usungsoft.usLibrary.exception.RfidConvertException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: RFID En/Decoding
 */
public class RfidUtils {
    // <editor-fold desc="USSOFT">
    public static class Default {
        List<String> idxTable1 = new ArrayList<>();
        List<String> idxTable2 = new ArrayList<>();
        List<String> idxTable3 = new ArrayList<>();

        private Default() {
            // idx1 initialize
            String[] idx1 = BuildConfig.TABLE_INDEX_ONE.split(",");
            idxTable1.addAll(Arrays.asList(idx1));

            // idx2 initialize
            String[] idx2 = BuildConfig.TABLE_INDEX_TWO.split(",");

            idxTable2.addAll(Arrays.asList(idx2));

            // idx3 initialize
            String[] idx3 = BuildConfig.TABLE_INDEX_THREE.split(",");

            idxTable3.addAll(Arrays.asList(idx3));
        }

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

            int lengthHexIndex;
            int idxSerial = Integer.parseInt(Common.hexToDecimal(rfidCode, 31, 32));
            String strRFID = rfidCode.substring(0, 31 - (idxSerial));
            String strSerial = rfidCode.substring(31 - idxSerial);

            if (strRFID.length() <= 18)
                lengthHexIndex = 6;
            else if (strRFID.length() <= 24)
                lengthHexIndex = 8;
            else
                lengthHexIndex = 10;

            String strHexIDX = strRFID.substring((strRFID.length() - 1) - (lengthHexIndex - 1));
            String strHexSCS = strRFID.substring(0, (strRFID.length() - 1) - strHexIDX.length() + 1);

            int intData;
            StringBuilder idxTblNo = new StringBuilder();

            for (int i = 0; i < strHexIDX.length(); i += 2) {
                intData = Integer.parseInt(Common.hexToDecimal(strHexIDX, i, 2 + i));

                idxTblNo.append(((intData & 192) >> 6));
                idxTblNo.append((intData & 48) >> 4);
                idxTblNo.append((intData & 12) >> 2);
                idxTblNo.append(intData & 3);
            }

            int idx1;
            StringBuilder strSCS = new StringBuilder();

            for (int i = 0; i < strHexSCS.length(); i++) {
                intData = Integer.parseInt(Common.hexToDecimal(strHexSCS, i, 1 + i));
                idx1 = (intData & 15) + 1;

                if (StringUtils.equals(idxTblNo.substring(i, i + 1), "1"))
                    strSCS.append(idxTable1.get(idx1 - 1));
                else if (StringUtils.equals(idxTblNo.substring(i, i + 1), "2"))
                    strSCS.append(idxTable2.get(idx1 - 1));
                else if (StringUtils.equals(idxTblNo.substring(i, i + 1), "3"))
                    strSCS.append(idxTable3.get(idx1 - 1));
            }

            return strSCS + "-" + strSerial;
        }
    }
    // </editor-fold>
}
