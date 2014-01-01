/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.benchmark;


class BenchmarkResult implements Comparable<BenchmarkResult> {

    public final Benchmark type;
    public final String name;
    private long time;
    private long count;
    
    public BenchmarkResult(Benchmark type, String name) {
        this.type = type;
        this.name = name;
    }
    public BenchmarkResult(Benchmark type, long time, long count) {
        this(type, type.name);
        this.time = time;
        this.count = count;
    }

    public long getTime() {     return time;   }
    public long getCount() {    return count;  }
    
    public void update(long time, long count) {
        this.time += time;
        this.count += count;
    }

    public int compareTo(BenchmarkResult benchmarkResult) {
        int cmp = type.index - benchmarkResult.type.index;
        if (cmp == 0) {
            long delta = this.time - benchmarkResult.time;
            cmp = delta > 0 ? 1 : (delta < 0 ? -1 : 0);
        }
        return cmp;
    }
}