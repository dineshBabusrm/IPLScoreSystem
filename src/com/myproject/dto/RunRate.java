package com.myproject.dto;

public class RunRate {

	private long totalRuns;
	private long totalRunsConceded;
	private int oversFaced;
	private int oversBowled;
	public long getTotalRuns() {
		return totalRuns;
	}
	public void setTotalRuns(long totalRuns) {
		this.totalRuns = totalRuns;
	}
	public long getTotalRunsConceded() {
		return totalRunsConceded;
	}
	public void setTotalRunsConceded(long totalRunsConceded) {
		this.totalRunsConceded = totalRunsConceded;
	}
	public int getOversFaced() {
		return oversFaced;
	}
	public void setOversFaced(int oversFaced) {
		this.oversFaced = oversFaced;
	}
	public int getOversBowled() {
		return oversBowled;
	}
	public void setOversBowled(int oversBowled) {
		this.oversBowled = oversBowled;
	}
	
}
