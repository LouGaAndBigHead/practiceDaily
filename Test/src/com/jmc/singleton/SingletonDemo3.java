package com.jmc.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * 登记式单例：其本质也是饿汉式单例
 * @author 静静and茂茂
 * 登记式单例实际上是维护一组单例类的实例，将这些实例存放到一个Map（登记簿）中，
 * 对于已经登记过的实例，则从Map中直接返回，对于没有登记的，则先登记，再返回
 */
public class SingletonDemo3 {
	private static Map<String, SingletonDemo3> map = new HashMap<String,SingletonDemo3>();
	static{
		SingletonDemo3 single = new SingletonDemo3();
		map.put(single.getClass().getName(), single);
	}
	//保护的默认构造子
	protected SingletonDemo3(){}
	public static SingletonDemo3 getInstance(String name){
		if(name == null){
			name = SingletonDemo3.class.getName();
			System.out.println("name == null---->name="+name);
		}
		if(map.get(name) == null){
			try {
				map.put(name, (SingletonDemo3) Class.forName(name).newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return map.get(name);
	}
}
