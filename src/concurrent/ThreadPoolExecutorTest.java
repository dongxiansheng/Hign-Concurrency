package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest {
	public static void main(String[] args) {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(1,2,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(3));
		tpe.execute(new TaskThread("����1"));
		tpe.execute(new TaskThread("����2"));
		tpe.execute(new TaskThread("����3"));
		tpe.execute(new TaskThread("����4"));
		tpe.execute(new TaskThread("����5"));
		tpe.execute(new TaskThread("����6"));
	}
}

//�߳�ִ������
class TaskThread implements Runnable {
	private String taskName;

	public TaskThread(String taskName) {
		this.taskName = taskName;
	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + taskName);
	}
}