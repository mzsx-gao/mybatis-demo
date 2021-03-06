package com.enjoylearning.mybatis;

import com.enjoylearning.mybatis.entity.EmailSexBean;
import com.enjoylearning.mybatis.entity.TPosition;
import com.enjoylearning.mybatis.entity.TUser;
import com.enjoylearning.mybatis.mapper.TUserMapper;
import com.enjoylearning.mybatis.mapper.TUserTestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseTest {

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws IOException {
        //--------------------第一阶段---------------------------
        // 1.读取mybatis配置文件创SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        // 1.读取mybatis配置文件创SqlSessionFactory
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        inputStream.close();
    }

    @Test
    // 快速入门
    public void quickStart() {
        //--------------------第二阶段---------------------------
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
        //--------------------第三阶段---------------------------
        // 4.执行查询语句并返回单条数据
        TUser user = mapper.selectByPrimaryKey(2);
        System.out.println(user);
        System.out.println("----------------------------------");
        // 5.执行查询语句并返回多条数据
//		List<TUser> users = mapper.selectAll();
//		for (TUser tUser : users) {
//			System.out.println(tUser);
//		}
    }

    @Test
    // ibatis编程模型 本质分析
    public void originalOperation() throws IOException {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.执行查询语句并返回结果
        TUser user = sqlSession.selectOne("com.enjoylearning.mybatis.mapper.TUserMapper.selectByPrimaryKey", 2);
        System.out.println(user.toString());
    }


    @Test
    //知识点：resultType
    public void testAutoMapping() throws IOException {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserTestMapper mapper = sqlSession.getMapper(TUserTestMapper.class);
        // 4.执行查询语句并返回多条数据
        List<TUser> users = mapper.selectAll();
        for (TUser tUser : users) {
            System.out.println(tUser);
        }
    }

    @Test
    //知识点：resultMap
    public void testResultMap() throws IOException {
        //--------------------第二阶段---------------------------
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        //--------------------第三阶段---------------------------
        // 4.执行查询语句并返回单条数据
        List<TUser> users = mapper.selectTestResultMap();
        for (TUser tUser : users) {
            System.out.println(tUser.getUserName());
            System.out.println(tUser.getPosition().getPostName());
        }
    }


    // 多参数查询

    @Test
    public void testManyParamQuery() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        String email = "qq.com";
        Byte sex = 1;

        // 第一种方式使用map
//		Map<String, Object> params = new HashMap<>();
//		params.put("email", email);
//		params.put("sex", sex);
//		List<TUser> list1 = mapper.selectByEmailAndSex1(params);
//		System.out.println(list1.size());

        // 第二种方式直接使用参数
        List<TUser> list2 = mapper.selectByEmailAndSex2(email, sex);
        System.out.println(list2.size());

        // 第三种方式用对象
        EmailSexBean esb = new EmailSexBean();
        esb.setEmail(email);
        esb.setSex(sex);
        List<TUser> list3 = mapper.selectByEmailAndSex3(esb);
        System.out.println(list3.size());
    }
    @Test
    // 测试插入数据自动生成id
    public void testInsertGenerateId1() throws IOException {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
        // 4.执行查询语句并返回结果
        TUser user1 = new TUser();
        user1.setUserName("test1");
        user1.setRealName("realname1");
        user1.setEmail("myemail1");
        mapper.insert1(user1);
        sqlSession.commit();
        System.out.println(user1.getId());
    }

    @Test
    // 测试插入数据自动生成id
    public void testInsertGenerateId2() throws IOException {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);
        // 4.执行查询语句并返回结果
        TUser user2 = new TUser();
        user2.setUserName("test2");
        user2.setRealName("realname2");
        user2.setEmail("myemai2l");
        mapper.insert2(user2);
        sqlSession.commit();
        System.out.println(user2.getId());
    }

    @Test
    // 参数#和参数$区别测试(动态sql 入门)
    public void testSymbol() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        String inCol = "id, userName, realName, sex, mobile, email, note";
        String tableName = "t_user";
//		String userName = "lison";
        String userName = "'xxx' or 1=1";
        String orderStr = "sex,userName";

        List<TUser> list = mapper.selectBySymbol(tableName, inCol, orderStr, userName);
        System.out.println(list.size());

    }


    //--------------------------------动态sql---------------------------------------
    @Test
    // if用于select，并与where配合
    public void testSelectIfOper() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        String email = "qq.com";
        Byte sex = 1;
		List<TUser> list = mapper.selectIfOper(email, sex);
//        List<TUser> list = mapper.selectIfandWhereOper(email, null);
//		List<TUser> list = mapper.selectChooseOper(email , sex);

        System.out.println(list.size());

    }

    @Test
    // if用于update，并与set配合
    public void testUpdateIfOper() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        TUser user = new TUser();
        user.setId(3);
        user.setUserName("cindy");
        user.setRealName("王美丽");
        user.setEmail("xxoo@163.com");
        user.setMobile("18695988747");
//		user.setNote("cindy's note");
        user.setSex((byte) 2);
//		System.out.println(mapper.updateIfOper(user));
        System.out.println(mapper.updateIfAndSetOper(user));
        sqlSession.commit();

    }


    @Test
    // if用于insert，并与trim配合
    public void testInsertIfOper() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        TUser user = new TUser();
        user.setUserName("mark");
        user.setRealName("毛毛");
        user.setEmail("xxoo@163.com");
        user.setMobile("18695988747");
        user.setNote("mark's note");
        user.setSex((byte) 1);
        System.out.println(mapper.insertIfOper(user));
//		System.out.println(mapper.insertSelective(user));
    }


    @Test
    // Foreach用于in查询
    public void testForeach4In() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        String[] names = new String[]{"lison", "james"};
        List<TUser> users = mapper.selectForeach4In(names);

        for (TUser tUser : users) {
            System.out.println(tUser.getUserName());
        }

        System.out.println(users.size());
    }


    @Test
    // Foreach用于批量插入
    public void testForeach4Insert() {
        // 2.获取sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        TUser user1 = new TUser();
        user1.setUserName("king");
        user1.setRealName("李小京");
        user1.setEmail("li@qq.com");
        user1.setMobile("18754548787");
        user1.setNote("king's note");
        user1.setSex((byte) 1);
        TUser user2 = new TUser();
        user2.setUserName("deer");
        user2.setRealName("陈大林");
        user2.setEmail("chen@qq.com");
        user2.setMobile("18723138787");
        user2.setNote("deer's note");
        user2.setSex((byte) 1);


        int i = mapper.insertForeach4Batch(Arrays.asList(user1, user2));
        System.out.println("------批量更新获取主键的方式与单条insert完全相同--------");
        System.out.println(user1.getId());
        System.out.println(user2.getId());

    }

    @Test
    // 批量更新
    public void testBatchExcutor() {
        // 2.获取sqlSession
//		SqlSession sqlSession = sqlSessionFactory.openSession(true);
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, true);
        // 3.获取对应mapper
        TUserMapper mapper = sqlSession.getMapper(TUserMapper.class);

        TUser user = new TUser();
        user.setUserName("mark");
        user.setRealName("毛毛");
        user.setEmail("xxoo@163.com");
        user.setMobile("18695988747");
        user.setNote("mark's note");
        user.setSex((byte) 1);
        TPosition positon1 = new TPosition();
        positon1.setId(1);
        user.setPosition(positon1);
        System.out.println(mapper.insertSelective(user));

        TUser user1 = new TUser();
        user1.setId(3);
        user1.setUserName("cindy");
        user1.setRealName("王美丽");
        user1.setEmail("xxoo@163.com");
        user1.setMobile("18695988747");
        user1.setNote("cindy's note");
        user1.setSex((byte) 2);
        user.setPosition(positon1);
        System.out.println(mapper.updateIfAndSetOper(user1));

        sqlSession.commit();
        System.out.println("----------------");
        System.out.println(user.getId());
        System.out.println(user1.getId());

    }

    //------------------------------动态sql结束----------------------------------------

    @Test
    public void mybatisGeneratorTest() throws FileNotFoundException {
        List<String> warnings = new ArrayList<String>(); //警告信息list
        boolean overwrite = true;
        String genCfg = "generatorConfig.xml";
        File configFile = new File(getClass().getClassLoader().getResource(genCfg).getFile());
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = null;
        try {
            config = cp.parseConfiguration(configFile);//读取配置文件
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = null;
        try {
            myBatisGenerator = new MyBatisGenerator(config, callback, warnings);//实例化生成器
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            myBatisGenerator.generate(null); //生成实体bean，生成mapper接口 和xml
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



	
/*	@Test
	// 注解测试
	public void testAnno() {
		// 2.获取sqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		// 3.获取对应mapper
		TJobHistoryAnnoMapper mapper = sqlSession.getMapper(TJobHistoryAnnoMapper.class);
		
		List<TJobHistory> list = mapper.selectByUserId(1);
		System.out.println(list.size());
		
		List<TJobHistory> listAll = mapper.selectAll();
		System.out.println(listAll.size());
		
		TJobHistory job = new TJobHistory();
		job.setTitle("产品经理");
		job.setUserId(1);
		job.setCompName("美团");
		job.setYears(3);
		
		mapper.insert(job);
		System.out.println(job.getId());
	}
*/


}
