package at.seywerth.smartics.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * date time formatter for inverter.
 * 
 * @author Raphael Seywerth
 *
 */
public class InverterDateTimeFormater {

    /**
     * "yyyy-mm-ddTHH:mm:ss.lll+zzzz" ?
     */
    private static final DateTimeFormatter dateTimeISOOffsetFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * archive date format
     */
    private static final DateFormat dateSimpleFormat = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * current time readable format like HH:mm:ss ?
     */
    private static final DateTimeFormatter timeReadableFormat = DateTimeFormatter.ISO_LOCAL_TIME;

    
    public static String getDDMMYYYYForTimestamp(Timestamp timestamp) {
    	//formatter.
    	return "";
    }

    public static Instant getInstantForSDF(String ddMMyyyy) throws ParseException {
    	return Instant.ofEpochMilli(dateSimpleFormat.parse(ddMMyyyy).getTime());    	
    }

    public static String getTimeReadableFormatted(final Instant instant) {
    	return timeReadableFormat.withLocale(Locale.GERMANY)
    							 .withZone(ZoneId.systemDefault())
    							 .format(instant);
    }

    public static Instant getInstantForISOOffsetDateTime(final String isoDateTime) {
    	return Instant.from(dateTimeISOOffsetFormat.parse(isoDateTime));
    }

}