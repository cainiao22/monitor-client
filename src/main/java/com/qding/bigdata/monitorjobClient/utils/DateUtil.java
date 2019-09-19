package com.qding.bigdata.monitorjobClient.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String YYYY_MM = "yyyy-MM";
  public static final String YYYYMMDD = "yyyyMMdd";
  public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";

  /**
   * 获取相差天数
   *
   * @param big
   * @param small
   * @return
   */
  public static Integer getDayDiff(Date big, Date small) {
    if (big != null && small != null) {
      long sub = big.getTime() - small.getTime();
      long day = 1000L * 60 * 60 * 24;
      int presume = (int) (sub / day);

      Calendar bigCalendar = Calendar.getInstance();
      bigCalendar.setTime(big);
      Calendar cursor = Calendar.getInstance();
      cursor.setTime(small);

      cursor.add(Calendar.DAY_OF_YEAR, presume);

      if (cursor.get(Calendar.DAY_OF_YEAR) == bigCalendar.get(Calendar.DAY_OF_YEAR)) {
        return presume;
      } else {
        return presume + 1;
      }

    }
    return null;
  }

  /**
   * 日期字符串转�? 2012-12-21 -> 20121221
   *
   * @param date
   * @return
   */
  public static String standardDateToNumericDate(String date) {
    return date.replaceAll("-", "");
  }

  /**
   * 日期字符串转�? 20121221 -> 2012-12-21
   *
   * @param date
   * @return
   */
  public static String numericDateToStandardDate(String date) {
    StringBuilder builder = new StringBuilder();
    builder.append(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-")
        .append(date.substring(6));
    return builder.toString();
  }

  public static String formatDateTString(Date date) {
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
    return f.format(date);
  }

  public static String formatDateToFullString(Date date) {
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");
    return f.format(date);
  }

  public static String formatDateToDTString(Date date) {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    return f.format(date);
  }

  public static String formatDateToFullString2(Date date) {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return f.format(date);
  }

  /**
   * 增加分钟，可以为正负�?
   *
   * @param date Date
   * @param minute 分钟数可以为正负�?
   * @return Date
   */
  public static Date addMinutes(final Date date, final int minute) {
    if (null == date) {
      return null;
    }
    final Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MINUTE, minute);
    final Date end = c.getTime();
    return end;
  }

  /**
   * 增加分钟，可以为正负�?
   *
   * @param date Date
   * @param minuts 分钟数可以为正负�?
   * @return Date
   */
  public static List<String> getMinutes(final Date date, final int minute) {

    List<String> list = new ArrayList<String>();

    final Calendar c = Calendar.getInstance();
    int min = minute <= 0 ? minute : 0;
    int max = minute <= 0 ? -1 : minute;
    for (int i = min; i <= max; i++) {
      c.setTime(date);
      c.add(Calendar.MINUTE, i);
      list.add(formatDateToFullString(c.getTime()));
    }
    return list;
  }

  /**
   * 增加分钟，可以为正负�?
   *
   * @param date Date
   * @param minuts 分钟数可以为正负�?
   * @return Date
   */
  public static List<String> getAllMinutes(final Date date) {

    List<String> list = new ArrayList<String>();

    final Calendar c = Calendar.getInstance();
    for (int i = 0; i < 1440; i++) {
      c.setTime(date);
      c.add(Calendar.MINUTE, i);
      list.add(formatDateToFullString(c.getTime()));
    }
    return list;
  }

  /**
   * 增加分钟，可以为正负�?
   *
   * @param minuts 分钟数可以为正负�?
   * @return Date
   */
  public static List<String> getMinutesFromNow(final int minute) {
    return getMinutes(new Date(), minute);
  }

  /**
   * Description:获取今天到目前的�?有分钟字符串
   *
   * @return
   */
  public static List<String> getMinutesFromNowLimitToday() {
    final Calendar c = Calendar.getInstance();
    return getMinutesFromNow((c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)) * -1);
  }

  public static String getDayByN(int n, String format) {
    Calendar c = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat(format);
    c.setTime(new Date());
    c.add(Calendar.DATE, n);
    Date d2 = c.getTime();
    String s = df.format(d2);
    return s;
  }

  public static String getDayByN(Date dt, int n, String format) {
    Calendar c = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat(format);
    c.setTime(dt);
    c.add(Calendar.DATE, n);
    Date d2 = c.getTime();
    String s = df.format(d2);
    return s;
  }

  public static String getDayByN(int n) {
    return getDayByN(n, YYYY_MM_DD);
  }

  public static String getHourByN(Date dt, int n) {
    Calendar c = Calendar.getInstance();
    c.setTime(dt);
    c.add(Calendar.HOUR_OF_DAY, n);
    int hour = c.get(Calendar.HOUR_OF_DAY);

    return String.format("%02d", hour);
  }

  public static String getDayByN(Date dt, int n) {
    return getDayByN(dt, n, YYYY_MM_DD);
  }

  public static Date parseStr2Date(String day) {
 return parseStr2Date(day,YYYY_MM_DD);
  }
  public static Date parseStr2Date(String day,String format) {
	  DateFormat df = new SimpleDateFormat(format);
	  try {
		  return df.parse(day);
	  } catch (ParseException e) {
		  e.printStackTrace();
	  }
	  return null;
  }

  public static Date parseMinuteStr2Date(String minute) {
    DateFormat df = new SimpleDateFormat(YYYYMMDDHHMM);
    try {
      return df.parse(minute);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static long parseMinuteStr2Second(String minute) {
 return parseMinuteStr2Date(minute).getTime()/1000;
  }

  public static String formatDateByMilliseconds(long milliseconds) {
    return formatDateToFullString2(new Date(milliseconds));
  }

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    //List<String> allMinutes = getAllMinutes(DateUtil.parseStr2Date("20160508"));
    //System.out.println(allMinutes);
    long end = System.currentTimeMillis();
    System.out.println(getHourByN(new Date(), -13));
    System.out.println(end - start);
  }
  


	  public static String getMonthByN(Date dt, int n) {
	    return getMonthByN(dt, n, YYYY_MM);
	  }
	  public static String getMonthByN(Date dt, int n, String format) {
		    Calendar c = Calendar.getInstance();
		    DateFormat df = new SimpleDateFormat(format);
		    c.setTime(dt);
		    c.add(Calendar.MONTH, n);
		    Date d2 = c.getTime();
		    String s = df.format(d2);
		    return s;
		  }
}
