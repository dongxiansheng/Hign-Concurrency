package concurrent.booleanlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class BooleanLock implements Lock {
	/*��ǰӵ�������߳�*/
	private Thread currentThread;
	/*���أ�false:��ǰ��û�б��κ��̻߳�ȡ���ѱ��ͷţ�true:��ǰ�����̻߳�ã����߳̾���currentThread;*/
	private boolean locked = false;
	/*blockedList�����洢��ЩΪ�˻�ȡ���������������̡߳�*/
	private final List<Thread> blockedList = new ArrayList<>();

	@Override
	public void lock() throws InterruptedException {
		synchronized(this){
			while(locked){
				//�ݴ浱ǰ�߳�
				final Thread tempThread = currentThread();
				
				try {
					if(!blockedList.contains(currentThread())){
						blockedList.add(currentThread());
					}
					this.wait();
				} catch (InterruptedException e) {
					// �����ǰ����waitʱ���жϣ����blocklist������������ڴ�й¶
					blockedList.remove(tempThread);
					//�����׳��ж��쳣
					throw e;
				}
			}
			blockedList.remove(currentThread());
			this.locked = true;
			this.currentThread = currentThread();
		}
		
	}

	@Override
	public void lock(long mills) throws InterruptedException, TimeoutException {
		synchronized(this){
			if(mills<=0){
				this.lock();
			}else{
				long remainingMills = mills;//�������̴߳��������
				long endMills = System.currentTimeMillis() + remainingMills; //���ճ�ʱʱ��
				while(locked){
					//�ݴ浱ǰ�߳�
					final Thread tempThread = currentThread();
					try{
						if(remainingMills <= 0){
							throw new TimeoutException("cann't get the lock during "+mills+"ms.");
						}
						if(!blockedList.contains(currentThread())){
							blockedList.add(currentThread());
						}
						this.wait(remainingMills);
						remainingMills = endMills - System.currentTimeMillis();//�����Ѻ����¼���ʣ�������
					}catch (InterruptedException e) {
						// �����ǰ����waitʱ���жϣ����blocklist������������ڴ�й¶
						blockedList.remove(tempThread);
						//�����׳��ж��쳣
						throw e;
					}
					
				}
				blockedList.remove(currentThread());
				this.locked = true;
				this.currentThread = currentThread();
			}
		}
		
	}

	@Override
	public void unlock() {
		synchronized(this){
			if(currentThread == currentThread()){
				this.locked = false;
				Optional.of(currentThread().getName() + "release the lock.").ifPresent(System.out::println);
				this.notifyAll();
			}
		}
		
	}

	@Override
	public List<Thread> getBlockedThreads() {
		/*��Ϊ�䷵��һ�������޸���ͼ���б�*/
		return Collections.unmodifiableList(blockedList);
	}
	private Thread currentThread() {
		
		return Thread.currentThread();
	}
}
