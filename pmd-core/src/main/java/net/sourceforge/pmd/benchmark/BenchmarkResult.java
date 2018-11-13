/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

@Deprecated
class BenchmarkResult implements Comparable<BenchmarkResult> {

    public final Benchmark type;
    public final String name;
    private long time;
    private long count;

    BenchmarkResult(Benchmark type, String name) {
        this.type = type;
        this.name = name;
    }

    BenchmarkResult(Benchmark type, long time, long count) {
        this(type, type.name);
        this.time = time;
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public long getCount() {
        return count;
    }

    public void update(long time, long count) {
        this.time += time;
        this.count += count;
    }

    @Override
    public int compareTo(BenchmarkResult benchmarkResult) {
        int cmp = Integer.compare(type.index, benchmarkResult.type.index);
        if (cmp == 0) {
            cmp = Long.compare(this.time, benchmarkResult.time);
        }
        return cmp;
    }
}
