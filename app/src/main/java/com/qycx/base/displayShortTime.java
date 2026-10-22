package com.qycx.base;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class displayShortTime {
    public static class qyFuncs_Droid {

        public static String displayShortTime(long t) {
            String str = "";
            LocalDateTime dt0 = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
            LocalDateTime dt1 = LocalDateTime.ofInstant(Instant.ofEpochSecond(t), ZoneOffset.UTC);
            LocalDateTime dt2 = dt1.plusSeconds(ZoneOffset.systemDefault().getRules().getOffset(dt1).getTotalSeconds());
            LocalDateTime dtNow = LocalDateTime.now();
            LocalDateTime dtNow_local = dtNow.plusSeconds(ZoneOffset.systemDefault().getRules().getOffset(dtNow).getTotalSeconds());

            if (dt2.getYear() != dtNow_local.getYear()) {
                str += dt2.getYear();
            } else if (dt2.getMonth() != dtNow_local.getMonth() || dt2.getDayOfMonth() != dtNow_local.getDayOfMonth()) {
                str += dt2.getMonthValue() + "-" + dt2.getDayOfMonth();
            } else {
                str += String.format("%02d:%02d:%02d", dt2.getHour(), dt2.getMinute(), dt2.getSecond());
            }

            return str;
        }
    }
}
