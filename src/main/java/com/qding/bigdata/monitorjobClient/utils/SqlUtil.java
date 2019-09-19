package com.qding.bigdata.monitorjobClient.utils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SqlUtil {

	public static String parse(String etlJobSql, Date scheduleTime) {

		List<List<String>> allMatchedSegmentList = RegexUtil.getAllContent("(\\$\\{(.+?)\\})", etlJobSql);
		for (List<String> matchedItem : allMatchedSegmentList) {
			String oriSegment = matchedItem.get(0);
			String expression = matchedItem.get(1).replaceAll("\\s+", "");
			List<String> matchedExpressionList = RegexUtil.getFirstCotent("^(当前小时|当天|当月|当年){1}(([+|\\-]){1}(\\d+)){0,1}$",
					expression);
			if (matchedExpressionList.size() != 4) {
				continue;
			}
			int offset = 0;
			if (StringUtils.isNotBlank(matchedExpressionList.get(1))) {
				offset = Integer.parseInt(matchedExpressionList.get(1));
			}
			if (matchedExpressionList.get(0).equals("当天")) {
				etlJobSql = etlJobSql.replace(oriSegment, "'" + DateUtil.getDayByN(scheduleTime, offset) + "'");
			}
			if (matchedExpressionList.get(0).equals("当月")) {
				etlJobSql = etlJobSql.replace(oriSegment, "'" + DateUtil.getMonthByN(scheduleTime, offset) + "'");
			}
			if (matchedExpressionList.get(0).equals("当年")) {
				etlJobSql = etlJobSql.replace(oriSegment, "'" + (scheduleTime.getYear() + 1900 + offset) + "'");
			}

			if (matchedExpressionList.get(0).equals("当前小时")) {
				etlJobSql = etlJobSql.replace(oriSegment, "'" + DateUtil.getHourByN(scheduleTime, offset) + "'");
			}
		}

		return etlJobSql.trim();
	}

}
