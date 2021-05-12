package io.github.ph1lou.space_conquest.utils;

public class BukkitUtils {

    public static String conversion(int timer) {

        String value;
        float sign = Math.signum(timer);
        timer = Math.abs(timer);

        if (timer % 60 > 9) {
            value = timer % 60 + "s";
        } else value = "0" + timer % 60 + "s";

        if(timer/3600>0) {

            if(timer%3600/60>9) {
                value = timer/3600+"h"+timer%3600/60+"m"+value;
            } else value = timer/3600+"h0"+timer%3600/60+"m"+value;
        } else if (timer / 60 > 0) {
            value = timer / 60 + "m" + value;
        }
        if (sign < 0) value = "-" + value;

        return value;
    }
}
