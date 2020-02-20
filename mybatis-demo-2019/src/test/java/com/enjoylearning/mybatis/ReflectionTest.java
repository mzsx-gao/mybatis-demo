package com.enjoylearning.mybatis;

import com.enjoylearning.mybatis.entity.TPosition;
import com.enjoylearning.mybatis.entity.TUser;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReflectionTest {

    //----------------源码分析之反射工具类的实例---------------------
    @Test
    public void reflectionTest(){

		//反射工具类初始化
		ObjectFactory objectFactory = new DefaultObjectFactory();
		TUser user = objectFactory.create(TUser.class);
		ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
		ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
		MetaObject metaObject = MetaObject.forObject(user, objectFactory, objectWrapperFactory, reflectorFactory);


		//使用Reflector读取类元信息
		Reflector findForClass = reflectorFactory.findForClass(TUser.class);
		Constructor<?> defaultConstructor = findForClass.getDefaultConstructor();
		String[] getablePropertyNames = findForClass.getGetablePropertyNames();
		String[] setablePropertyNames = findForClass.getSetablePropertyNames();
		System.out.println(defaultConstructor.getName());
		System.out.println(Arrays.toString(getablePropertyNames));
		System.out.println(Arrays.toString(setablePropertyNames));


	    //使用ObjectWrapper读取对象信息，并对对象属性进行赋值操作
		TUser userTemp = new TUser();
		ObjectWrapper wrapperForUser = new BeanWrapper(metaObject, userTemp);
		String[] getterNames = wrapperForUser.getGetterNames();
		String[] setterNames = wrapperForUser.getSetterNames();
		System.out.println(Arrays.toString(getterNames));
		System.out.println(Arrays.toString(setterNames));

		PropertyTokenizer prop = new PropertyTokenizer("userName");
		wrapperForUser.set(prop, "lison");
		System.out.println(userTemp);


    }

    @Test
    public void reflectionTest2(){
        //---------------------------------------------------------------
        //模拟数据库行数据转化成对象
        ObjectFactory objectFactory = new DefaultObjectFactory();
        TUser user = objectFactory.create(TUser.class);
        ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaObject metaObject = MetaObject.forObject(user, objectFactory, objectWrapperFactory, reflectorFactory);

        //1.模拟从数据库读取数据
        Map<String, Object> dbResult = new HashMap<>();
        dbResult.put("id", 1);
        dbResult.put("userName", "lison");
        dbResult.put("realName", "李晓宇");
        TPosition tp = new TPosition();
        tp.setId(1);
        dbResult.put("position_id", tp);
        //2.模拟映射关系
        Map<String, String> mapper = new HashMap<>();
        mapper.put("id", "id");
        mapper.put("userName", "userName");
        mapper.put("realName", "realName");
        mapper.put("position", "position_id");

        //3.使用反射工具类将行数据转换成pojo

        //获取BeanWrapper,既包括类元数据，同时还能对对象的属性赋值
        BeanWrapper objectWrapper = (BeanWrapper) metaObject.getObjectWrapper();

        Set<Map.Entry<String, String>> entrySet = mapper.entrySet();
        //遍历映射关系
        for (Map.Entry<String, String> colInfo : entrySet) {
            String propName = colInfo.getKey();//获得pojo的字段名称
            Object propValue = dbResult.get(colInfo.getValue());//模拟从数据库中加载数据对应列的值
            PropertyTokenizer proTokenizer = new PropertyTokenizer(propName);
            objectWrapper.set(proTokenizer, propValue);//将数据库的值赋值到pojo的字段中
        }
        System.out.println(metaObject.getOriginalObject());

    }
}