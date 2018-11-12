package concurrent.booleanlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class BooleanLock implements Lock {
	/*当前拥有锁的线程*/
	private Thread currentThread;
	/*开关：false:当前锁没有被任何线程获取或已被释放，true:当前锁被线程获得，该线程就是currentThread;*/
	private boolean locked = false;
	/*blockedList用来存储哪些为了获取该锁而被阻塞的线程。*/
	private final List<Thread> blockedList = new ArrayList<>();

	@Override
	public void lock() throws InterruptedException {
		synchronized(this){
			while(locked){
				//暂存当前线程
				final Thread tempThread = currentThread();
				
				try {
					if(!blockedList.contains(currentThread())){
						blockedList.add(currentThread());
					}
					this.wait();
				} catch (InterruptedException e) {
					// 如果当前线在wait时被中断，则从blocklist中清除，避免内存泄露
					blockedList.remove(tempThread);
					//继续抛出中断异常
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
				long remainingMills = mills;//由其他线程传入进来。
				long endMills = System.currentTimeMillis() + remainingMills; //最终超时时间
				while(locked){
					//暂存当前线程
					final Thread tempThread = currentThread();
					try{
						if(remainingMills <= 0){
							throw new TimeoutException("cann't get the lock during "+mills+"ms.");
						}
						if(!blockedList.contains(currentThread())){
							blockedList.add(currentThread());
						}
						this.wait(remainingMills);
						remainingMills = endMills - System.currentTimeMillis();//被唤醒后重新计算剩余毫秒数
					}catch (InterruptedException e) {
						// 如果当前线在wait时被中断，则从blocklist中清除，避免内存泄露
						blockedList.remove(tempThread);
						//继续抛出中断异常
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
		/*将为其返回一个不可修改视图的列表*/
		return Collections.unmodifiableList(blockedList);
	}
	private Thread currentThread() {
		
		return Thread.currentThread();
	}
}
