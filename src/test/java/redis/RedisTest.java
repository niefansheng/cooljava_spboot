package redis;

import com.CoolJavaApplication;
import com.controller.test.UserRedis;
import com.model.user.User;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@SpringBootConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoolJavaApplication.class)
@Slf4j
public class RedisTest {


    @Autowired
     UserRedis userRedis;

    @Before
    public void setup() {

        User user=new User();
        user.setName("测试用001号人");
        user.setPwd("测试用密码123");
        //使用前先删除
        userRedis.delete(this.getClass().getName() + "userByName:" + user.getName());
        //然后再添加
        userRedis.add(this.getClass().getName() + ":userByPkid:" + user.getPwd(), 10L, user);
    }

    @Test
    public void get() {
        User user = userRedis.get(this.getClass().getName() + ":userByPkid:3212312313223123");
        log.info("================================打开redis desk manager查看==================================");
        //System.out.println("redis测试 得到的人的名字是"+user.getName()+"密码是: "+user.getPwd());

    }

}