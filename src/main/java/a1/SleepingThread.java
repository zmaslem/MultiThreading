package a1;

public class SleepingThread implements Runnable {

	public void run() {
		while(true) {
			doSomethingUseful();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void doSomethingUseful()
	{
		// TODO Auto-generated method stub
	}
}
