package com.simtechdata.bash;

import com.simtechdata.process.ProcBuilder;
import com.simtechdata.process.ProcResult;


public class JProcs {

    /**
     * Executes an external command with the given arguments and returns its captured standard output.
     * <p>
     * The process exit status is not enforced; non-zero exit codes are allowed and do not cause an
     * exception. This method simply returns the text captured from the process's standard output
     * stream. If the process writes nothing to standard output, an empty string is returned.
     *
     * @param command the executable to run (for example, a binary or shell command); must not be {@code null} or empty
     * @param args    the arguments to pass to the executable; must not be {@code null} (may be empty)
     * @return the captured standard output of the process; never {@code null} but may be empty
     * @throws IllegalStateException   if the process cannot be started or fails during execution
     * @throws IllegalArgumentException if {@code command} is invalid for the underlying process launcher
     * @throws NullPointerException    if {@code command} or {@code args} is {@code null}
     */
    public static String getOutputString(String command, String[] args) {
        ProcBuilder pb = new ProcBuilder(command).withArgs(args).ignoreExitStatus();
        ProcResult pr = pb.run();
        return pr.getOutputString();
    }
    
    /**
     * Executes an external command with the given arguments and returns the full process result.
     * <p>
     * The returned {@link ProcResult} contains the exit code, standard output, and standard error
     * captured from the process. The process exit status is not enforced; non-zero exit codes are
     * allowed and included in the result for the caller to inspect.
     *
     * @param command the executable to run (for example, a binary or shell command); must not be {@code null} or empty
     * @param args    the arguments to pass to the executable; must not be {@code null} (may be empty)
     * @return the {@link ProcResult} containing exit value, stdout, and stderr from the execution
     * @throws IllegalStateException   if the process cannot be started or fails during execution
     * @throws IllegalArgumentException if {@code command} is invalid for the underlying process launcher
     * @throws NullPointerException    if {@code command} or {@code args} is {@code null}
     */
    public static ProcResult getResultsOf(String command, String[] args) {
        ProcBuilder pb = new ProcBuilder(command).withArgs(args).ignoreExitStatus();
        return pb.run();
    }
}