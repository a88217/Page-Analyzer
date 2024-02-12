
package hexlet.code.utils;

import java.util.Date;
import java.sql.Timestamp;

public final class Time {
    public static Timestamp getTime() {
        var date = new Date();
        var time = new Timestamp(date.getTime());
        return time;
    }
}

