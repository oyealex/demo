package com.oye.common.prototype.multi.level.pipeline.stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pipeline Test
 *
 * @author oyealex
 * @since 2022-12-07
 */
class PipelineTest {
    private final VisitRecorder recorder = new VisitRecorder();

    private final StrStreamPump starter = new StrStreamPump();

    @BeforeEach
    void setUp() {
        recorder.reset();
    }

    @Test
    void pipeline1_should_visit_every_element_rightly() {
        new L1Pipeline<>(null, List.of(recorder), starter).visitAll();
        assertEquals(starter.buildExpectedVisited1(), recorder.getVisited1());
        assertEquals(starter.buildExpectedAllVisited1(), recorder.getAllVisited());
    }

    @Test
    void pipeline2_should_visit_every_element_rightly() {
        new L2Pipeline<>(null, List.of(recorder), starter).visitAll();
        assertEquals(starter.buildExpectedVisited1(), recorder.getVisited1());
        assertEquals(starter.buildExpectedVisited2(), recorder.getVisited2());
        assertEquals(starter.buildExpectedAllVisited2(), recorder.getAllVisited());
    }

    @Test
    void pipeline3_should_visit_every_element_rightly() {
        new L3Pipeline<>(null, List.of(recorder), starter).visitAll();
        assertEquals(starter.buildExpectedVisited1(), recorder.getVisited1());
        assertEquals(starter.buildExpectedVisited2(), recorder.getVisited2());
        assertEquals(starter.buildExpectedVisited3(), recorder.getVisited3());
        assertEquals(starter.buildExpectedAllVisited3(), recorder.getAllVisited());
    }

    private static Stream<String> buildStream() {
        return IntStream.range(0, 3).mapToObj(Integer::toString);
    }

    @Getter
    @EqualsAndHashCode
    private static class VisitRecorder implements L3Pipeline.Visitor<String, String, String> {
        private final List<String> visited1 = new ArrayList<>();

        private final List<List<String>> visited2 = new ArrayList<>();

        private final List<List<String>> visited3 = new ArrayList<>();

        private final List<List<String>> allVisited = new ArrayList<>();

        public void reset() {
            visited1.clear();
            visited2.clear();
            visited3.clear();
            allVisited.clear();
        }

        @Override
        public void visit(String data) {
            visited1.add(data);
            allVisited.add(List.of(data));
        }

        @Override
        public void visit(String data1, String data2) {
            visited2.add(List.of(data1, data2));
            allVisited.add(List.of(data1, data2));
        }

        @Override
        public void visit(String data1, String data2, String data3) {
            visited3.add(List.of(data1, data2, data3));
            allVisited.add(List.of(data1, data2, data3));
        }
    }

    private static class StrStreamPump implements L3Pipeline.StreamPump<String, String, String> {
        @Override
        public Stream<String> pump() {
            return buildStream();
        }

        private List<String> buildExpectedVisited1() {
            return pump().collect(toList());
        }

        private List<List<String>> buildExpectedAllVisited1() {
            return pump().map(List::of).collect(toList());
        }

        @Override
        public Stream<String> start(String data1) {
            return buildStream().map(str -> data1 + "." + str);
        }

        private List<List<String>> buildExpectedVisited2() {
            List<List<String>> res = new ArrayList<>();
            pump().forEach(data1 -> start(data1).forEach(data2 -> res.add(List.of(data1, data2))));
            return res;
        }

        private List<List<String>> buildExpectedAllVisited2() {
            List<List<String>> res = new ArrayList<>();
            pump().forEach(data1 -> {
                res.add(List.of(data1));
                start(data1).forEach(data2 -> {
                    res.add(List.of(data1, data2));
                });
            });
            return res;
        }

        @Override
        public Stream<String> start(String data1, String data2) {
            return buildStream().map(str -> data1 + "." + data2 + "." + str);
        }

        private List<List<String>> buildExpectedVisited3() {
            List<List<String>> res = new ArrayList<>();
            pump().forEach(data1 -> start(data1).forEach(
                data2 -> start(data1, data2).forEach(data3 -> res.add(List.of(data1, data2, data3)))));
            return res;
        }

        private List<List<String>> buildExpectedAllVisited3() {
            List<List<String>> res = new ArrayList<>();
            pump().forEach(data1 -> {
                res.add(List.of(data1));
                start(data1).forEach(data2 -> {
                    res.add(List.of(data1, data2));
                    start(data1, data2).forEach(data3 -> res.add(List.of(data1, data2, data3)));
                });
            });
            return res;
        }
    }
}