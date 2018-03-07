package comtechmaina.kirshworkouts;

public class DemoThread {
	public static void main(String[] args) {
		for (int i = 0; i < 10000000; i++) {
			SampleThread t1 = new SampleThread();
			new Thread(t1).start();
		}
	}
}

class SampleThread implements Runnable {

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName());
	}

}
