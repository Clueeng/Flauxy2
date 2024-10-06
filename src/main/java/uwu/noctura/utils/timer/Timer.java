package uwu.noctura.utils.timer;

public class Timer {

	public long lastMS;
	
	public long getLastMS() {
		return lastMS;
	}

	public void setLastMS(long lastMS) {
		this.lastMS = lastMS;
	}

	public Timer() {}
	
	public boolean hasTimeElapsed(double ms, boolean reset) {
		long currentTime = getCurrentTime();
		
		if(lastMS == 0)
			lastMS = currentTime;
		
		boolean elapsed = currentTime - lastMS > ms;
		
		if(elapsed && reset)
			reset();
		
		return elapsed;
	}
	
	public long getCurrentTime() {
		return System.nanoTime() / 1000000;
	}
	
	public long getElapsedTime() {
		return getCurrentTime() - lastMS;
	}
	
	public void reset() {
		lastMS = getCurrentTime();
	}

}