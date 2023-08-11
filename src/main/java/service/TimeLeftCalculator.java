package service;

import model.database.BatchOfBranchesConfiguration;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class TimeLeftCalculator {

    private final long factorForSeconds = 1000L;
    private final double dividerForMilliseconds = 1000.00;

    public double calculateLeftTime(int branchLeft, BatchOfBranchesConfiguration batchOfBranchesConfiguration, List<Long> elapsedTimes) {
        long allLeftTime = calculateAverage(elapsedTimes) * branchLeft + calculateAdditionTime(branchLeft, batchOfBranchesConfiguration);
        return convertMillisecondsToSeconds(allLeftTime);
    }

    private long calculateAverage(List<Long> elapsedTimes) {
        long sum = elapsedTimes.stream().reduce(0L, Long::sum);
        return sum / elapsedTimes.size();
    }

    private long calculateAdditionTime(int branchLeft, BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        int batchesLeft = branchLeft / batchOfBranchesConfiguration.getCountBranchInBatch();
        long timeBetweenBatch = (long) batchesLeft * batchOfBranchesConfiguration.getDelayBetweenBatches() * factorForSeconds;
        long timeBetweenBranches = (long) branchLeft * batchOfBranchesConfiguration.getDelayBetweenBranchesInBatch() * factorForSeconds;
        return timeBetweenBatch + timeBetweenBranches;
    }

    private double convertMillisecondsToSeconds(long milliseconds) {
        BigDecimal bigDecimalTime = new BigDecimal(milliseconds / dividerForMilliseconds);
        return bigDecimalTime.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

}
