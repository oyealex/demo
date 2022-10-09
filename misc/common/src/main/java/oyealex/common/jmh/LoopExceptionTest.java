package oyealex.common.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author oye
 * @since 2020-06-02 22:43:22
 */
@BenchmarkMode(Mode.Throughput)
@Fork(1)
@Threads(1)
@Warmup(iterations = 2, batchSize = 1, time = 1)
@Measurement(iterations = 10, batchSize = 1, time = 1)
public class LoopExceptionTest {
    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(LoopExceptionTest.class.getSimpleName()).build()).run();
    }

    @Benchmark
    public void testExceptionInLoop() {
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            try {
                sum += i * i;
            } catch (Exception e) {
                print(e);
            }
        }
        print(sum);
    }

    @Benchmark
    public void testLoopInException() {
        try {
            int sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += i * i;
            }
            print(sum);
        } catch (Exception e) {
            print(e);
        }
    }

    private void print(Object o) {

    }
}
