package com.myproject.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myproject.dto.DeliveryDto;
import com.myproject.dto.MatchDto;
import com.myproject.dto.RunRate;
import com.myproject.dto.ScoreSummary;

public class MatchEnquiryService {

	static int counter = 0;
	private List<DeliveryDto> deliveryList = new ArrayList<>();
	private List<MatchDto> matchList = new ArrayList<>();
	private Map<String, Integer> fieldTeamCountMap = new HashMap<>();
	private Map<Integer, Map<String, ScoreSummary>> finalScoreSummary = new HashMap<>();
	private Set<String> teamList = new HashSet<>();
	private Set<Integer> years = new HashSet<>();
	private Map<Integer, Integer> yearMatchMap = new HashMap<>();
	private Set<String> bowlers = new HashSet<>();
	private Map<Integer, Map<String, Float>> economyRateBowlers = new HashMap<>();
	private Map<Integer, Map<String, RunRate>> finalRunRateSummary = new HashMap<>();

	public void readDeliveryFile() {
		String csvFile = "deliveries.csv";
		String line = "";
		String cvsSplitBy = ",";
		boolean flag = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] deliveryArray = line.split(cvsSplitBy);
				if (deliveryArray.length > 0 && flag) {
					DeliveryDto delivery = new DeliveryDto();
					delivery.setMatchId(Integer.parseInt(deliveryArray[0]));
					delivery.setInning(Integer.parseInt(deliveryArray[1]));
					delivery.setBattingTeam(deliveryArray[2]);
					delivery.setBowlingTeam(deliveryArray[3]);
					delivery.setOver(Integer.parseInt(deliveryArray[4]));
					delivery.setBall(Integer.parseInt(deliveryArray[5]));
					delivery.setBatsman(deliveryArray[6]);
					delivery.setBowler(deliveryArray[7]);
					bowlers.add(delivery.getBowler());
					delivery.setWideRuns(Integer.parseInt(deliveryArray[8]));
					delivery.setByeRuns(Integer.parseInt(deliveryArray[9]));
					delivery.setLegByeRuns(Integer.parseInt(deliveryArray[10]));
					delivery.setNoBallRuns(Integer.parseInt(deliveryArray[11]));
					delivery.setPenaltyRuns(Integer.parseInt(deliveryArray[12]));
					delivery.setBatsmanRuns(Integer.parseInt(deliveryArray[13]));
					delivery.setExtraRuns(Integer.parseInt(deliveryArray[14]));
					delivery.setTotalRuns(Integer.parseInt(deliveryArray[15]));
					deliveryList.add(delivery);
				} else {
					flag = true;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Length of delivery file = " + deliveryList.size());
	}

	public void readMatchFile() {
		String csvFile = "matches.csv";
		String line = "";
		String cvsSplitBy = ",";
		boolean flag = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				counter++;
				String[] matchArray = line.split(cvsSplitBy);
				if (matchArray.length > 0 && flag) {
					MatchDto match = new MatchDto();
					match.setMatchId(Integer.parseInt(matchArray[0]));
					match.setSeason(Integer.parseInt(matchArray[1]));
					years.add(match.getSeason());
					match.setCity(matchArray[2]);
					match.setTeam1(matchArray[4]);
					teamList.add(matchArray[4]);
					match.setTeam2(matchArray[5]);
					match.setTossWinner(matchArray[6]);
					match.setTossDecision(matchArray[7]);
					match.setResult(matchArray[8]);
					if (matchArray.length == 10) {
						match.setWinner(matchArray[9]);
					}
					matchList.add(match);
				} else {
					flag = true;
				}
			}

		} catch (Exception e) {
			System.out.println("counter = " + counter);
			e.printStackTrace();
		}
		System.out.println("Length of matches file = " + matchList.size());
	}

	public void groupByYear(int year, String toss) {

		for (MatchDto match : matchList) {
			if (match.getSeason() == year && match.getTossDecision().equalsIgnoreCase(toss)) {
				String key = match.getTossWinner();
				if (fieldTeamCountMap.containsKey(key)) {
					fieldTeamCountMap.put(key, (fieldTeamCountMap.get(key) + 1));
				} else {
					fieldTeamCountMap.put(key, 1);
				}
			}
		}
		List<Integer> counts = new ArrayList<>(fieldTeamCountMap.values());
		Set<Integer> top4Counts = new HashSet<>();
		for (int i = 0; i < counts.size(); i++) {
			for (int j = i + 1; j < counts.size(); j++) {
				if (counts.get(i) < counts.get(j)) {
					int temp = counts.get(i);
					counts.set(i, counts.get(j));
					counts.set(j, temp);
				}
			}
		}
		for (int i = 0; i < 3; i++) {
			top4Counts.add(counts.get(i));
		}
		System.out.println("Top 4 teams which elected to " + toss + " first in the year " + year + " :");
		for (Map.Entry<String, Integer> entry : fieldTeamCountMap.entrySet()) {
			for (Integer element : top4Counts) {
				if (entry.getValue() == element) {
					System.out.println("Year:" + year + ",Team name:" + entry.getKey() + ", Count=" + entry.getValue());
				}
			}
		}
	}

	public void updateScoreSummaryByYear() {
		this.populateYearToMatch();
		for (Integer year : years) {
			Map<String, ScoreSummary> scoreSummary = new HashMap<>();
			for (String team : teamList) {
				ScoreSummary summary = new ScoreSummary();
				scoreSummary.put(team, summary);
			}
			finalScoreSummary.put(year, scoreSummary);
		}
		for (Integer year : years) {
			Map<String, ScoreSummary> scoreSummary = finalScoreSummary.get(year);
			for (String team : teamList) {
				int fourCounter = 0;
				int sixCounter = 0;
				int totalScoreCounter = 0;
				ScoreSummary summary = scoreSummary.get(team);
				for (DeliveryDto delivery : deliveryList) {
					int matchYear = yearMatchMap.get(delivery.getMatchId());
					if (year == matchYear && team.equalsIgnoreCase(delivery.getBattingTeam())) {
						switch (delivery.getBatsmanRuns()) {
						case 4:
							fourCounter++;
							break;
						case 6:
							sixCounter++;
							break;
						}
						totalScoreCounter = totalScoreCounter + delivery.getTotalRuns();
					}
				}
				summary.setFourCount(summary.getFourCount() + fourCounter);
				summary.setSixCount(summary.getSixCount() + sixCounter);
				summary.setTotalScore(summary.getTotalScore() + totalScoreCounter);
			}
		}
	}

	public void findEconomyRateBowler() {

		for (Integer year : years) {
			Map<String, Float> bowlerDetails = new HashMap<>();
			for (String bowler : bowlers) {
				int totalRuns = 0;
				Set<Integer> overs = new HashSet<>();
				for (DeliveryDto delivery : deliveryList) {
					int matchYear = yearMatchMap.get(delivery.getMatchId());
					if (year == matchYear && bowler.equalsIgnoreCase(delivery.getBowler())) {
						totalRuns = totalRuns + delivery.getWideRuns() + delivery.getNoBallRuns()
								+ delivery.getPenaltyRuns() + delivery.getBatsmanRuns();
						overs.add(delivery.getOver());
					}
				}
				if (overs.size() >= 10) {
					float bowlingRate = totalRuns / overs.size();
					bowlerDetails.put(bowler, bowlingRate);
				}
			}
			economyRateBowlers.put(year, bowlerDetails);
		}

	}

	public void displayEconomyRateBowler() {
		for (Map.Entry<Integer, Map<String, Float>> entry : economyRateBowlers.entrySet()) {
			this.lineBreak();
			System.out.println("Economy Rate bowlers for the year:" + entry.getKey());
			for (Map.Entry<String, Float> bowlerDetails : entry.getValue().entrySet()) {
				System.out.println("year: " + entry.getKey() + " ,Bowler: " + bowlerDetails.getKey() + " ,Economy:"
						+ bowlerDetails.getValue());
			}
		}
	}

	public void populateYearToMatch() {
		for (MatchDto match : matchList) {
			yearMatchMap.put(match.getMatchId(), match.getSeason());
		}
	}

	public void displayScoreSummary() {
		for (Map.Entry<Integer, Map<String, ScoreSummary>> entry : finalScoreSummary.entrySet()) {
			this.lineBreak();
			System.out.println("Score card for year : " + entry.getKey());
			for (Map.Entry<String, ScoreSummary> entrySummary : entry.getValue().entrySet()) {
				ScoreSummary score = entrySummary.getValue();
				System.out.println("Team name: " + entrySummary.getKey() + ", Four Count: " + score.getFourCount()
						+ ", Six Count:" + score.getSixCount() + ", Total runs:" + score.getTotalScore());
			}
		}
	}

	public void updateRunsScoredByYear() {
		for (Integer year : years) {
			Map<String, RunRate> runSummary = new HashMap<>();
			for (String team : teamList) {
				RunRate summary = new RunRate();
				runSummary.put(team, summary);
			}
			finalRunRateSummary.put(year, runSummary);
		}
		for (Integer year : years) {
			Map<String, RunRate> runSummary = finalRunRateSummary.get(year);
			for (String team : teamList) {
				Set<Integer> overs = new HashSet<>();
				int totalScoreCounter = 0;
				RunRate summary = runSummary.get(team);
				for (DeliveryDto delivery : deliveryList) {
					int matchYear = yearMatchMap.get(delivery.getMatchId());
					if (year == matchYear && team.equalsIgnoreCase(delivery.getBattingTeam())) {
						totalScoreCounter = totalScoreCounter + delivery.getTotalRuns();
						overs.add(delivery.getOver());
					}
				}
				summary.setTotalRuns(totalScoreCounter);
				summary.setOversFaced(overs.size());
			}
		}
	}

	public void updateRunsConcededByYear() {
		for (Integer year : years) {
			Map<String, RunRate> runSummary = finalRunRateSummary.get(year);
			for (String team : teamList) {
				Set<Integer> overs = new HashSet<>();
				int totalScoreCounter = 0;
				RunRate summary = runSummary.get(team);
				for (DeliveryDto delivery : deliveryList) {
					int matchYear = yearMatchMap.get(delivery.getMatchId());
					if (year == matchYear && team.equalsIgnoreCase(delivery.getBowlingTeam())) {
						totalScoreCounter = totalScoreCounter + delivery.getTotalRuns();
						overs.add(delivery.getOver());
					}
				}
				summary.setTotalRunsConceded(totalScoreCounter);
				summary.setOversBowled(overs.size());
			}
		}
	}

	public void findTeamWithHighestRunRate() {
		this.updateRunsScoredByYear();
		this.updateRunsConcededByYear();
		
		for (Map.Entry<Integer, Map<String, RunRate>> entry : finalRunRateSummary.entrySet()) {
			this.lineBreak();
			String maxTeamName = null;
			float maxRate = 0.0f;
			System.out.println("Team name which has Highest Net Run Rate with respect to year: " + entry.getKey());
			for (Map.Entry<String, RunRate> runRateEntry : entry.getValue().entrySet()) {
				float runRate = 0.0f;
				RunRate runRate1 = runRateEntry.getValue();
				if (runRate1.getOversFaced() > 0 && runRate1.getOversBowled() > 0) {
					runRate = ((float) runRate1.getTotalRuns() / (float) runRate1.getOversFaced())
							- ((float) runRate1.getTotalRunsConceded() / (float) runRate1.getOversBowled());
					//System.out.println("Team ==="+ runRateEntry.getKey() + ", Runrate===="+ runRate);
					if (runRate > maxRate) {
						maxRate = runRate;
						runRate = 0.0f;
						maxTeamName = runRateEntry.getKey();
					}
				}
			}
			System.out.println("Year :" + entry.getKey() + ", Team: " + maxTeamName + ", Runrate:" + maxRate);
		}
	}

	public void lineBreak() {
		System.out.println(
				"----------------------------------------------------------------------------------------------");
	}

	public static void main(String arg[]) {
		MatchEnquiryService service = new MatchEnquiryService();
		service.readDeliveryFile();
		service.readMatchFile();
		service.lineBreak();
		service.groupByYear(2016, "field");
		service.updateScoreSummaryByYear();
		service.displayScoreSummary();
		service.lineBreak();
		service.findEconomyRateBowler();
		service.displayEconomyRateBowler();
		service.findTeamWithHighestRunRate();
	}
}
