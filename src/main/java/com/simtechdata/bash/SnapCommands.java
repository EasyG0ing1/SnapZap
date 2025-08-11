package com.simtechdata.bash;

import com.simtechdata.process.ProcResult;
import com.simtechdata.structure.Snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SnapCommands class provides utilities to interact with APFS snapshots on a specified volume.
 * It enables operations such as listing, parsing, and deleting snapshots.
 * Snapshots are managed using the macOS `diskutil` command-line utility.
 */
public class SnapCommands {

    /**
     * Retrieves the raw output produced by listing APFS snapshots for the given volume path.
     * <p>
     * The returned string is the unparsed text from the underlying system command.
     *
     * @param volumePath the absolute path to the APFS volume for which to list snapshots; must not be null or empty
     * @return the raw textual output from the snapshot listing command (never null, may be empty)
     * @throws NullPointerException if {@code volumePath} is null
     */
    private static String getSnapString(String volumePath) {
        String   command = "diskutil";
        String[] args    = {"apfs", "listSnapshots", volumePath};
        return JProcs.getOutputString(command, args);
    }

    /**
     * Prints a human-readable list of snapshots for the specified APFS volume.
     * <p>
     * This method invokes the system command to obtain snapshot data, parses it into snapshot objects,
     * and writes each snapshot's formatted representation to standard output.
     *
     * @param volumePath the absolute path to the APFS volume; must not be null or empty
     * @throws NullPointerException if {@code volumePath} is null
     */
    public static void showSnapshotList(String volumePath) {
        String                 command       = "diskutil";
        String[]               args          = {"apfs", "listSnapshots", volumePath};
        String                 commandOutput = JProcs.getOutputString(command, args);
        Map<Integer, Snapshot> snaps         = parseSnapshots(commandOutput);
        for (Snapshot s : snaps.values()) {
            System.out.println(s.toString());
        }
    }

    /**
     * Returns a mapping of index numbers to APFS snapshots discovered on the given volume.
     * <p>
     * This method invokes the system utility to list snapshots for the specified volume path,
     * parses the output, and builds a map keyed by a 1-based index in discovery order.
     * The values contain snapshot metadata such as disk identifier, UUID, name, XID, and purgeable flag.
     *
     * @param volumePath the absolute path to the APFS volume whose snapshots should be listed; must not be null or empty
     * @return a map of 1-based indices to snapshot descriptors; the map is empty if none are found or on parse failure
     * @throws NullPointerException if {@code volumePath} is null
     */
    public static Map<Integer, Snapshot> getSnapshots(String volumePath) {
        return parseSnapshots(getSnapString(volumePath));
    }

    /**
     * Attempts to delete the provided snapshot if it is marked as purgeable.
     * <p>
     * If purgeable, a delete command is executed and its output or error is printed to standard output.
     * If not purgeable, a message is printed and the method returns {@code false} after a short delay.
     *
     * @param snapshot the snapshot candidate for deletion; must not be null
     * @return {@code true} if the deletion command completes successfully; {@code false} if the snapshot is not purgeable or deletion fails
     * @throws NullPointerException if {@code snapshot} is null
     */
    public static boolean purge(Snapshot snapshot) {
        if (snapshot.isPurgeable()) {
            String     command = "diskutil";
            String[]   args    = {"apfs", "deleteSnapshot", snapshot.getDisk(), "-xid", snapshot.getXID()};
            ProcResult pr      = JProcs.getResultsOf(command, args);
            if (pr.getExitValue() != 0) {
                System.out.println("\nError deleting snapshot: " + snapshot.getName() + "\n");
                System.out.println(pr.getErrorString());
                return false;
            }
            System.out.println("\n" + pr.getOutputString());
            System.out.println("SUCCESS!");
            return true;
        }
        System.out.println("The snapshot:\n\t" + snapshot.getName() + "\nIs not purgeable");
        sleep(1500, TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * Parses the textual output of a snapshot listing into a map of snapshot objects.
     * <p>
     * The returned map is keyed by a 1-based index corresponding to the discovery order in the input text.
     * Each entry includes snapshot metadata extracted from the provided string.
     *
     * @param parseString the raw text output containing snapshot information; must not be null
     * @return a map of 1-based indices to parsed snapshots; an empty map if no snapshots are found
     * @throws NullPointerException if {@code parseString} is null
     */
    private static Map<Integer, Snapshot> parseSnapshots(String parseString) {
        String  disk  = "";
        Matcher diskM = Pattern.compile("^Snapshots\\s+for\\s+(\\S+)", Pattern.MULTILINE).matcher(parseString);
        if (diskM.find()) disk = diskM.group(1);

        Pattern entry = Pattern.compile(
                "^\\+--\\s+([0-9A-Fa-f-]{36})\\R" +                  // UUID
                "^(?:\\|\\s+|\\s{2,})Name:\\s+(.*?)\\R" +            // Name
                "^(?:\\|\\s+|\\s{2,})XID:\\s+(\\d+)\\R" +            // XID
                "^(?:\\|\\s+|\\s{2,})Purgeable:\\s+(Yes|No)\\b",     // Purgeable
                Pattern.MULTILINE
                                       );

        Map<Integer, Snapshot> snapMap = new HashMap<>();
        Matcher                m       = entry.matcher(parseString);
        int                    idx     = 1;
        while (m.find()) {
            String  uuid      = m.group(1);
            String  name      = m.group(2).trim();
            String  xid       = m.group(3).trim();
            boolean purgeable = m.group(4).equalsIgnoreCase("Yes");
            snapMap.put(idx, new Snapshot(disk, uuid, name, xid, purgeable));
            idx++;
        }
        return snapMap;
    }

    /**
     * Attempts to delete all snapshots present on the specified APFS volume.
     * <p>
     * This method retrieves the current snapshot list for the volume and calls {@link #purge(Snapshot)}
     * for each snapshot. Processing continues even if individual deletions fail.
     *
     * @param volumePath the absolute path to the APFS volume; must not be null or empty
     * @return {@code true} if every snapshot was deleted successfully; {@code false} if any deletion failed
     * @throws NullPointerException if {@code volumePath} is null
     */
    public static boolean purgeAll(String volumePath) {
        boolean success = true;
        Map<Integer, Snapshot> map = getSnapshots(volumePath);
        int start = map.size() -1;
        int end = 0;
        for (int x = start; x>= end ; x--) {
            Snapshot snapshot = map.get(x);
            if (!purge(snapshot)) {
                success = false;
            }
        }
        return success;
    }
    
    /**
     * Sleeps for the specified duration, preserving the thread's interrupted status if interrupted.
     * <p>
     * If the thread is interrupted while sleeping, this method catches the {@link InterruptedException},
     * re-interrupts the current thread, and returns without throwing.
     *
     * @param time the amount of time to sleep; must be non-negative
     * @param timeUnit the unit of the {@code time} argument; must not be null
     */
    private static void sleep(long time, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(time);
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

}
