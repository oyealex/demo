package oyealex.common.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author oye
 * @since 2020-06-02 22:43:22
 */
//@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@BenchmarkMode({Mode.AverageTime})
@Fork(1)
@Threads(4)
@State(Scope.Thread)
@Warmup(iterations = 2, batchSize = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, batchSize = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LoopVsStreamTest {
    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(LoopVsStreamTest.class.getSimpleName()).build()).run();
    }

    private static final int AMOUNT = 10_000_000;

    private final List<Integer> list = new ArrayList<>(AMOUNT);

    private int sum = 0;

    @Setup
    public void init() {
        for (int i = 0; i < AMOUNT; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public void testLoop() {
        int tmpSum = 0;
        for (Integer i : list) {
            tmpSum += i;
        }
        sum = tmpSum;
    }

    @Benchmark
    public void testStream() {
        sum = list.stream().reduce(Integer::sum).get();
    }

    @Benchmark
    public void testParallelStream() {
        sum = list.stream().parallel().reduce(Integer::sum).get();
    }

    private void print(Object o) {

    }
}
