package concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest {
	public static void main(String[] args) {
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(1,2,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(3));
		tpe.execute(new TaskThread("任务1"));
		tpe.execute(new TaskThread("任务2"));
		tpe.execute(new TaskThread("任务3"));
		tpe.execute(new TaskThread("任务4"));
		tpe.execute(new TaskThread("任务5"));
		tpe.execute(new TaskThread("任务6"));
	}
}

//线程执行任务
class TaskThread implements Runnable {
	private String taskName;

	public TaskThread(String taskName) {
		this.taskName = taskName;
	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + taskName);
	}
}