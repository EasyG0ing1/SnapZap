package com.simtechdata;

import com.simtechdata.bash.SnapCommands;
import com.simtechdata.ui.Flow;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "App",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        usageHelpWidth = 190,
        description = {
                "",
                "SnapZap helps you clean up snapshots on APFS volumes in MacOS.",
                ""
        },
        footer = {
                "",
                "Minimal required argument is -v (lower case) which will then give you a menu of options.",
                "",
                "Examples:",
                "\tsnapzap -v /Volumes/MyVolume --list (Just provides the snapshot list and exits)",
                "\tsnapzap -v MyVolume                 (Shows a menu of options)",
                "\tsnapzap -v MyVolume --purgeAll      (purges all snapshots on the volume after you confirm)",
                "",
                "Typing '/Volumes/' before the volume name is optional as long as the volume exists in /Volumes",
                "",
                "So these will also work:",
                "\tsnapzap -v MyVolume",
                "\tsnapzap -v MyVolume --list",
                "\tsnapzap -v MyVolume --purgeAll",
                "",
                "etc.."
                
        }
)

public class App implements Callable<Integer> {

    /**
     * Represents the path to a specific volume that will be used in snapshot management operations.
     * <p>
     * This field is used as a command-line option and accepts an absolute or canonical path to the desired volume.
     * The value is provided via the `-v` or `--volume` command-line flags, followed by the path, such as `-v /Volumes/Name`.
     * <p>
     * Constraints:
     * - The path must not be null or empty.
     * - Must represent a valid directory accessible by the application.
     * <p>
     * Purpose:
     * - Used to identify the target volume for operations such as listing snapshots, purging snapshots, or managing snapshots interactively.
     */
    @CommandLine.Option(
            names = {"-v", "--volume"},
            paramLabel = "path",
            description = "Volume (ex: -v /Volumes/Name)"
    )
    private String volumePath;

    /**
     * Flag indicating whether to list snapshots for a specified volume.
     * <p>
     * This option can be activated using the `-l` or `--list` command-line arguments. 
     * When enabled, it instructs the application to display a list of all snapshots 
     * associated with the given volume path.
     * <p>
     * By default, this flag is set to `false`.
     */
    @CommandLine.Option(
            names = {"-l", "--list"},
            description = "List Snapshots (ex: -v /Volumes/Name -l)",
            defaultValue = "false")
    private boolean listSnapshots;

    /**
     * Flag to indicate whether all snapshots should be purged.
     * <p>
     * Command-line option:
     * - `-p` or `--purgeAll` - Specifies purging all snapshots for the configured volume.
     * <p>
     * Description:
     * - When set to `true`, all snapshots on the specified volume will be deleted after user confirmation.
     * - The default value for this flag is `false`, meaning snapshots will not be purged unless explicitly specified.
     * <p>
     * Effects:
     * - Influences the behavior of the snapshot management workflow when purging operations are performed.
     * - If the user confirms the operation, all snapshots will be removed from the volume.
     */
    @CommandLine.Option(
            names = {"-p", "--purgeAll"},
            description = "Purge ALL snapshots",
            defaultValue = "false")
    private boolean purgeAll;

    /**
     * Executes operations related to snapshot management for a specified volume.
     * This method validates input arguments and performs operations such as listing snapshots,
     * purging snapshots, or starting an interactive session, based on the current state and flags.
     * <p>
     * If the `listSnapshots` flag is set, it lists all snapshots for the provided volume path.
     * If the `purgeAll` flag is set, it attempts to purge all snapshots for the provided volume path.
     * If no flags are present, it initializes an interactive session for snapshot management.
     *
     * @return an integer status code:
     *         0 - if the operation completed successfully or was canceled by the user,
     *         1 - if the operation failed due to missing volume, invalid path, or other issues.
     */
    @Override
    public Integer call()  {
        if (listSnapshots && volumePath == null) {
            System.out.println("You must pass in a volume name (-v) with the -l argument");
            return 1;
        }
        if (volumePath != null) {
            if(!volumePath.toLowerCase().contains("/volumes/")) {
                volumePath = "/Volumes/" + volumePath;
            }
            Path path = Path.of(volumePath);
            if (Files.notExists(path)) {
                System.out.println("Volume does not exist: " + volumePath);
                return 1;
            }
            if (!SnapCommands.hasSnapshots(volumePath)) {
                System.out.println("\n" + volumePath + " does not have any snapshots");
                return 0;
            }
            Flow flow = new Flow(volumePath);
            if (listSnapshots) {
                flow.showFullList();
                return 0;
            }
            if (purgeAll) {
                return flow.purgeAll();
            }
            flow.start();
        }
        return 0;
    }

    /**
     * The entry point of the application.
     * <p>
     * This method sets up and executes the application using the command-line interface.
     * If no arguments are provided, it displays the usage instructions.
     * Otherwise, it processes the given arguments and exits with the corresponding status code.
     *
     * @param args command-line arguments passed to the program
     *             - If empty, usage instructions will be shown.
     *             - If provided, they will be passed to the application for execution.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            new CommandLine(new App()).usage(System.out);
            return;
        }
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
