package cn.wells.zhexuejiajiucanwenti;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ��������:�������ѧ�ҹ���һ��Բ�����ֱ�������Χ�����������ϣ�
 * ��Բ��������������֧���ӣ����ǵ����ʽ�ǽ���ؽ���˼����
 * ���͡�ƽʱ��һ����ѧ�ҽ���˼��, ����ʱ����ͼȡ�����������
 * ���Ŀ��ӣ�ֻ�������õ���ֻ����ʱ���ܽ��͡�������ϣ����¿��Ӽ���˼����
 * =============================================================
 * ���������Ϊ��ʵ�ֶԿ��ӵĻ��⣬������һ���ź�����ʾһֻ���ӣ���������ź��������ź������顣
 * ����ѧ�Ҽ���ʱ������ȥ������ӣ��ɹ��������ҿ��ӡ�����λ��ѧ��ͬʱ��������ӣ�����ÿλ��ѧ�Ҷ�û���ҿ��ӿ����ã��ͻ����������
 * =====================================================================================
 * ���˼·������ֵΪ4�ļ�¼���ź���������ֻ������λ��ѧ��ͬʱȥ������ӣ�ֻ���õ�����ӣ�
 * ���ܼ������ҿ��ӡ��õ���˫����֮�������òͣ��ò���ϣ��ȷ�������ӣ��ٷ����ҿ��ӡ���������Ա����������⡣
 * @author clover
 *
 */
public class ZheXueJiaJiuCanDemo {
	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(5);//�½�һ���̳߳�
		for(int i=0;i<5;++i){
			exec.submit(new Philosopher(i,new ChopStick(i),new ChopStick((i+1)%5)));//�ύ5����ѧ���߳�
		}
	}
	
}

class Philosopher implements Runnable{
	private int ID;//��i����ѧ�����Ŀ��Ӷ�Ӧ�Ŀ���id��Ϊi
	private ChopStick leftChop;
	private ChopStick rightChop;
	private Semaphore sem = new Semaphore(4);//�����ѧ��ֻ����4����ѧ�Ҿ������ӣ������Ų����������
	public Philosopher(int ID,ChopStick leftChop,ChopStick rightChop) {
		this.ID = ID;
		this.leftChop = leftChop;
		this.rightChop = rightChop;
	}
	@Override
	public void run() {
		while(true){
			try {
				sem.acquire();//�̻߳�ȡ���
				System.out.println("��ѧ��" + this.ID + "���ˣ����ÿ���׼���Է�...");
				leftChop.getLock().lock();//��ȡ�������
				System.out.println("��ѧ��" + this.ID + "�����������(���Ӻ�Ϊ"+leftChop.getID()+")...");
				rightChop.getLock().lock();//��ȡ�ҿ�����
				System.out.println("��ѧ��" + this.ID + "�������ҿ���(���Ӻ�Ϊ"+rightChop.getID()+")...");
				System.out.println("��ѧ��" + this.ID + "���ڳԷ�...");
				Double d = Math.random()*10000;
				Thread.sleep(d.intValue());//ÿ����ѧ�ҳԷ�����ʱ�䲻һ��
				System.out.println("��ѧ��" + this.ID + "���극������˼��...");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				sem.release();//�ͷ����
				leftChop.getLock().unlock();//�ͷ��������
				rightChop.getLock().unlock();//�ͷ��ҿ�����
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

//������
class ChopStick{
	private int ID;
	private ReentrantLock lock = new ReentrantLock();//Ϊÿһ˫���Ӽ�һ��������
	//����
	public ChopStick(int ID){
		this.ID = ID;
	}
	//��ȡ��
	public ReentrantLock getLock(){
		return lock;
	}
	//��ȡID
	public int getID(){
		return ID;
	}
}
