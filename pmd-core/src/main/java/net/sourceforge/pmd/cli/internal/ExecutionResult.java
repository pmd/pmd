package net.sourceforge.pmd.cli.internal;

public enum ExecutionResult {
    OK(0),
    ERROR(1),
    VIOLATIONS_FOUND(4);
    
    private final int exitStatusCode;
    
    ExecutionResult(int exitStatusCode) {
        this.exitStatusCode = exitStatusCode;
    }
    
    public int getExitStatusCode() {
        return exitStatusCode;
    }
}
