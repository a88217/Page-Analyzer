
package hexlet.code.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

public final class Time {
    public static Timestamp getTime() {
        var date = new Date();
        var time = new Timestamp(date.getTime());
        return time;
    }

    public static String timeString(Timestamp timestamp) {
        var time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return time.format(timestamp);
    }

}

