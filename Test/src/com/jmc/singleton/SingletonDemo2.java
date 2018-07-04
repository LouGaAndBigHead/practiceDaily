package com.jmc.singleton;

/**
 * 饿汉式单例：在类创建的时候就已经创建好一个静态的对象供系统使用，
 * 以后不再改变，所以天生线程安全
 * @author 静静and茂茂
 *
 */
public class SingletonDemo2 {
	//同样也声明一个私有构造方法
	private SingletonDemo2(){}
	private static final SingletonDemo2 singleton = new SingletonDemo2();
	//静态工厂方法
	public static SingletonDemo2 getInstance(){
		return singleton;
	}
	/**
	 * 因为相比较于上一个直接锁住方法的例子，这种缩小锁影响范围的方式会大幅提高程序的执行效率。
	 * 因为如果把锁直接加在方法上，那么无论是否已经创建过该类的实例，所有线程都只能一个一个的
	 * 依次执行整个方法体，会造成大量的阻塞时间。另外，我们之所以要处理线程安全问题，只是因为
	 * 在getInstance（）方法前几次被并发执行时，可能会有多个线程得到“single == null”
	 * 为“true”的结果，从而有可能出现创建多个对象的情况。而一旦有线程完成了创建实例的操作，那
	 * 么在不考虑其他修改方法的情况下，对于getInstance（）这种只读操作，其方法内部就不再存在
	 * 线程安全问题。所以，如果对象已经创建，我们完全可以让其他所有线程都并行执行getInstance（）
	 * 方法，于是便有了了这种“为创建对象时同步执行，已创建对象后异步执行”的优化方式。
	 */
}
