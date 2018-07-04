package com.jmc.singleton;

/**
 * @author 静静and茂茂
 * 懒汉式单例模式：为什么成为懒汉式，因为他在第一次调用的时候才实例化自己
 * Singleton通过构造方法私有化，避免了类在外部被实例化，
 * 在同一个虚拟机范围内，Singleton的唯一实例，只能通过getInstance方法访问
 */
//这是线程不安全的，并发环境下有可能出现多个Singleton实例，
public class SingletonDemo1 {
	//声明一个私有的构造方法，防止调用默认构造函数
	private SingletonDemo1(){}
	private static SingletonDemo1 singleton = null;
	//静态工厂方法
	public static SingletonDemo1 getInstance(){
		if(singleton == null){
			singleton = new SingletonDemo1();
		}
		return singleton;
	}
	
	/**
	 * 第一种线程安全改造方法：加上synchronized
	 * @return Singleton对象
	 */
	public static synchronized SingletonDemo1 getInstance2(){
		if(singleton == null){
			singleton = new SingletonDemo1();
		}
		return singleton;
	}
	
	/**
	 * 第二种线程安全改造方法：双重检查锁定
	 * @return
	 */
	public static SingletonDemo1 getInstance3(){
		if(singleton == null){
			synchronized(SingletonDemo1.class){
				if (singleton == null) {
					singleton = new SingletonDemo1();
				}
			}
		}
		return singleton;
	}
	
	/**
	 * 第三种改造方法：静态内部类，此种方法比以上两个改造方法都好，
	 * 既实现了线程安全，又避免了同步带来的性能影响
	 * @author 静静and茂茂
	 */
	private static class LazyHolder{
		private static final SingletonDemo1 INSTANCE = new SingletonDemo1();
	}
	public static final SingletonDemo1 getInstance4(){
		return LazyHolder.INSTANCE;
	}
}
