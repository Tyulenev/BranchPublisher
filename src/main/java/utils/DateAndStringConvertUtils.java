package utils;

import lombok.SneakyThrows;

import javax.ws.rs.InternalServerErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndStringConvertUtils {

    private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    @SneakyThrows
    public Date convertStringToDate(String stringDate) {
        if(stringToDateIsParseAble(stringDate)) {
            return simpleDateFormat.parse(stringDate);
        }
        throw new InternalServerErrorException("Can not parse Date = " + stringDate);
    }

    public String convertDateToString(Date date) {
        return simpleDateFormat.format(date);
    }

    public boolean stringToDateIsParseAble(String stringDate) {
        try {
            simpleDateFormat.parse(stringDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public Long getDeltaTimeLong(String inputDateString) {
        final String datePatternFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat formatter = new SimpleDateFormat(datePatternFormat);
        Long currTime = new Date().getTime();
        try {
            Date inDate = formatter.parse(inputDateString);
            Long inDateLong = inDate.getTime();
            return (currTime - inDateLong)/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getDeltaTimeLong(String publishDateString, String deployDateString) {
        final String datePatternFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat formatter = new SimpleDateFormat(datePatternFormat);
        try {
            Long publishDateLong = formatter.parse(publishDateString).getTime();
            Long deployDateLong = formatter.parse(deployDateString).getTime();
            return (deployDateLong - publishDateLong)/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
