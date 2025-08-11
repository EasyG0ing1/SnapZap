package com.simtechdata.ui;

import com.simtechdata.bash.SnapCommands;
import com.simtechdata.structure.Snapshot;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Flow {

    /**
     * Creates a new interactive flow for managing snapshots on the given volume path.
     * <p>
     * This constructor initializes internal state from the supplied path:
     * - Derives the human-friendly volume name from the last segment of the path.
     * - Retrieves the current snapshots for the volume and indexes them for menu operations.
     *
     * @param volumePath absolute or canonical path to the volume whose snapshots will be managed
     *                   (for example, "/path/to/volume"). Must not be null or empty.
     */
    public Flow(String volumePath) {
        this.volumePath = volumePath;
        this.volumeName = volumePath.substring(volumePath.lastIndexOf("/") + 1);
        this.snapMap    = SnapCommands.getSnapshots(volumePath);
    }

    private final String                 volumePath;
    private final String                 volumeName;
    private final Map<Integer, Snapshot> snapMap;

    /**
     * Starts the interactive console session for snapshot management.
     * <p>
     * Behavior:
     * - Repeatedly displays a main menu while there are snapshots to manage.
     * - Accepts user input from standard input.
     * - Supports listing snapshots, purging a single snapshot, purging all snapshots, or quitting.
     * - Returns immediately if the user chooses to quit or when there are no snapshots left.
     * <p>
     * Side effects:
     * - Writes menu prompts and status messages to standard output.
     * - May delete snapshots based on user choices.
     */
    public void start() {
        while (!snapMap.isEmpty()) {
            showMainMenu();
            String choice = new Scanner(System.in).nextLine();
            switch (choice) {
                case "Q", "q" -> {
                    return;
                }

                case "1" -> showFullList();

                case "2" -> {
                    while (true) {
                        showSingleMenu();
                        String purgeChoice = new Scanner(System.in).nextLine();
                        purgeChoice = purgeChoice.replaceAll("[^0-9]+", "");
                        int option = purgeChoice.isEmpty() ? 0 : Integer.parseInt(purgeChoice);
                        if (option == 0)
                            break;
                        if (!snapMap.containsKey(option)) {
                            System.out.println("Invalid choice");
                            sleep(1200, TimeUnit.MILLISECONDS);
                            continue;
                        }
                        Snapshot s = snapMap.get(option);
                        if (SnapCommands.purge(s)) {
                            snapMap.remove(option);
                        }
                        System.out.print("\n<Press Enter>");
                        new Scanner(System.in).nextLine();
                    }
                }

                case "3" -> purgeAll();

                default -> System.out.println("\nInvalid Choice\n");
            }
        }
    }

    public void showFullList() {
        SnapCommands.showSnapshotList(volumePath);
    }

    /**
     * Purges all snapshots for the configured volume after user confirmation.
     * <p>
     * Behavior:
     * - Prompts the user to confirm deletion of all snapshots.
     * - If confirmed, delegates to the snapshot command layer to perform the purge.
     * <p>
     * Returns:
     * - 0 if the user cancels or if the purge operation reports success.
     * - 1 if the purge operation reports failure.
     *
     * @return operation status code (0 = success or canceled by user, 1 = failure)
     */
    public int purgeAll() {
        int    count   = snapMap.size();
        String warning = "\nWARNING: This will DELETE all %d snapshots on volume: %s\n\nAre you sure you want to proceed (Y/N)? ";
        System.out.printf(warning, count, volumePath);
        String response = new Scanner(System.in).nextLine();
        if (!response.equalsIgnoreCase("Y")) {
            System.out.println("\nNo snapshots were deleted\n");
            return 0;
        }
        if (SnapCommands.purgeAll(volumePath)) {
            System.out.println("\n\nAll snapshots were deleted!\n");
            return 0;
        }
        else {
            System.out.println("One or more snapshots failed to be deleted, re-check the volume and try again.");
        }
        return 1;
    }

    /**
     * Prints the main menu to standard output.
     * <p>
     * The menu shows:
     * - Total number of snapshots on the volume.
     * - Options to list snapshots, purge a single snapshot, purge all snapshots, or quit.
     * <p>
     * This method does not read input; it only renders the menu and prompt.
     */
    private void showMainMenu() {
        String menu = """
                     \s
                      There are %d snapshots on volume: %s
                     \s
                      1) List Snapshots
                      2) Purge One Snapshot
                      3) Purge All Snapshots
                      Q) Quit
                     \s
                      Choice:\s""";
        String out = String.format(menu, snapMap.size(), volumeName);
        System.out.print(out);
    }

    /**
     * Prints a numbered list of snapshots and a prompt to select a single snapshot to purge.
     * <p>
     * The list maps each snapshot to a numeric option and includes an option to return
     * to the main menu (0). This method does not read input; it only renders the menu.
     */
    private void showSingleMenu() {
        StringBuilder sb   = new StringBuilder("\n");
        String        line = "%d) %s\n";
        for (Integer idx : snapMap.keySet()) {
            Snapshot s = snapMap.get(idx);
            sb.append(String.format(line, idx, s.getName()));
        }
        sb.append("\n").append("0) Main Menu").append("\n\n").append("Choice: ");
        System.out.print(sb);
    }

    /**
     * Sleeps for the specified duration using the provided time unit.
     * <p>
     * If the thread is interrupted while sleeping, the interrupted status is restored
     * and the method returns without throwing.
     *
     * @param time     the amount of time to sleep; must be non-negative
     * @param timeUnit the unit of the time argument; must not be null
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