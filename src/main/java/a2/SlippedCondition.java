package a2;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlippedCondition {
	private static final Queue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
	private static final long startMillis = System.currentTimeMillis();

	public Integer getNextInt() {
		Integer retVal = null;
		synchronized (queue) {
			try {
				while (queue.isEmpty()) {
					queue.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retVal = queue.poll();
			if (retVal == null) {
				System.err.println("retVal is null");
				throw new IllegalStateException();
			}
		}
		return retVal;
	}

	public void putInt(Integer value) {
		synchronized (queue) {
			queue.add(value);
			queue.notify();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final Random random = new Random(System.currentTimeMillis());
		final SlippedCondition queue = new SlippedCondition();
		Thread thread1 = new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while (System.currentTimeMillis() <= (startMillis + 10000)) {
					queue.putInt(i++);
					System.out.println("Put int: " + i);
					try {
						Thread.sleep(random.nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Thread threads[] = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					while (System.currentTimeMillis() <= (startMillis + 10000)) {
						Integer nextInt = queue.getNextInt();
						System.out.println("Next int: " + nextInt);
					}
					try {
						Thread.sleep(random.nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			threads[i].start();
		}
		thread1.start();
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		thread1.join();
	}
}
