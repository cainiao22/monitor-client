package com.qding.bigdata.monitorjobClient.common;

public class Constant {

	private static volatile boolean isMaster;

	public static boolean isMaster() {
		return isMaster;
	}

	public static synchronized void setMaster(boolean isMaster) {
		Constant.isMaster = isMaster;
	}
    //参数： mobiles=15801029684|13641064288|15110234502|15011233180|18600295448|15313159809&title=监控报表&content=
 	public static final String ALARM_WECHAT="http://wukong.iqdnet.cn/wukongbg/admin/api/wx_msg_send_by_mobiles";

	public static final String POSTGRESQL = "postgresql";
	public static final String MYSQL = "mysql";
	public static final String HIVE = "hive";

}
