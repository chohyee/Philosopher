package cn.wells.zhexuejiajiucanwenti;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 问题描述:有五个哲学家共用一张圆桌，分别坐在周围的五张椅子上，
 * 在圆桌上有五个碗和五支筷子，他们的生活方式是交替地进行思考和
 * 进餐。平时，一个哲学家进行思考, 饥饿时便试图取用其左右最靠近
 * 他的筷子，只有在他拿到两只筷子时才能进餐。进餐完毕，放下筷子继续思考。
 * =============================================================
 * 问题分析：为了实现对筷子的互斥，可以用一个信号量表示一只筷子，由这五个信号量构成信号量数组。
 * 当哲学家饥饿时总是先去拿左筷子，成功后再拿右筷子。当五位哲学家同时拿起左筷子，这是每位哲学家都没有右筷子可以拿，就会造成死锁。
 * =====================================================================================
 * 解决思路：设置值为4的记录型信号量，至多只允许四位哲学家同时去拿左筷子，只有拿到左筷子，
 * 才能继续拿右筷子。拿到两双筷子之后便可以用餐，用餐完毕，先放下左筷子，再放下右筷子。这样便可以避免死锁问题。
 * @author clover
 *
 */
public class ZheXueJiaJiuCanDemo {
	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(5);//新建一个线程池
		for(int i=0;i<5;++i){
			exec.submit(new Philosopher(i,new ChopStick(i),new ChopStick((i+1)%5)));//提交5个哲学家线程
		}
	}
	
}

class Philosopher implements Runnable{
	private int ID;//第i个哲学家左侧的筷子对应的筷子id号为i
	private ChopStick leftChop;
	private ChopStick rightChop;
	private Semaphore sem = new Semaphore(4);//五个哲学家只能有4个哲学家竞争筷子，这样才不会出现死锁
	public Philosopher(int ID,ChopStick leftChop,ChopStick rightChop) {
		this.ID = ID;
		this.leftChop = leftChop;
		this.rightChop = rightChop;
	}
	@Override
	public void run() {
		while(true){
			try {
				sem.acquire();//线程获取许可
				System.out.println("哲学家" + this.ID + "饿了，正拿筷子准备吃饭...");
				leftChop.getLock().lock();//获取左筷子锁
				System.out.println("哲学家" + this.ID + "拿起了左筷子(筷子号为"+leftChop.getID()+")...");
				rightChop.getLock().lock();//获取右筷子锁
				System.out.println("哲学家" + this.ID + "拿起了右筷子(筷子号为"+rightChop.getID()+")...");
				System.out.println("哲学家" + this.ID + "正在吃饭...");
				Double d = Math.random()*10000;
				Thread.sleep(d.intValue());//每个哲学家吃饭花费时间不一样
				System.out.println("哲学家" + this.ID + "吃完饭了正在思考...");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				sem.release();//释放许可
				leftChop.getLock().unlock();//释放左筷子锁
				rightChop.getLock().unlock();//释放右筷子锁
			}
		}
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
}

//筷子类
class ChopStick{
	private int ID;
	private ReentrantLock lock = new ReentrantLock();//为每一双筷子加一把重入锁
	//构造
	public ChopStick(int ID){
		this.ID = ID;
	}
	//获取锁
	public ReentrantLock getLock(){
		return lock;
	}
	//获取ID
	public int getID(){
		return ID;
	}
}
