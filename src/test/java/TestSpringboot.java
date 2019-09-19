import com.qding.bigdata.monitorjobClient.ClientApplication;
import com.qding.bigdata.monitorjobClient.component.MonitorJobExecuteLogHandler;
import com.qding.bigdata.monitorjobClient.dao.hive.HiveMonitorJobExecuteDao;
import com.qding.bigdata.monitorjobClient.utils.SqlUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author yanpf
 * @date 2018/8/23 15:03
 * @description
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class TestSpringboot {

    @Autowired
    MonitorJobExecuteLogHandler handler;

    @Autowired
    private HiveMonitorJobExecuteDao hiveMonitorJobExecuteDao;

    @Test
    public void test(){
        String msg = handler.getMobiles("闫鹏飞");
        System.out.println(msg);

    }

    @Test
    public void test2(){
        String parse = SqlUtil.parse("SELECT count(*) FROM databus.dwp_daily_shequ_data where dt=${当天-1}", new Date());

        System.out.println(parse);
    }

    @Test
    public void testExec(){

        System.out.println(hiveMonitorJobExecuteDao.execute("select count(*) from default.test5"));
    }
}
