package com.simtechdata.structure;

public class Snapshot {

    /**
     * Constructs an immutable descriptor of an APFS snapshot.
     * <p>
     * The provided values are stored as-is and are expected to represent a single snapshot
     * discovered on a specific APFS volume.
     *
     * @param disk       the disk or volume identifier where the snapshot resides (for example, a device node or mount path); should not be null or empty
     * @param UUID       the globally unique identifier of the snapshot in canonical string form; should not be null or empty
     * @param name       the human-readable name assigned to the snapshot; should not be null (may be empty)
     * @param XID        the snapshot transaction identifier as a string; should not be null or empty
     * @param purgeable  whether the snapshot is marked as purgeable at the time of creation
     */
    public Snapshot(String disk, String UUID, String name, String XID, boolean purgeable) {
        this.disk      = disk;
        this.UUID      = UUID;
        Name           = name;
        this.XID       = XID;
        this.purgeable = purgeable;
    }

    private final String  disk;
    private final String  UUID;
    private final String  Name;
    private final String  XID;
    private final boolean purgeable;

    /**
     * Returns the disk or volume identifier that hosts this snapshot.
     *
     * @return the disk identifier or mount path on which the snapshot exists (never null)
     */
    public String getDisk() {
        return disk;
    }

    /**
     * Returns the globally unique identifier of this snapshot.
     *
     * @return the snapshot UUID in string form (never null)
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * Returns the human-readable name of this snapshot.
     *
     * @return the snapshot name as provided by the source system (never null, may be empty)
     */
    public String getName() {
        return Name;
    }

    /**
     * Returns the transaction identifier (XID) associated with this snapshot.
     * <p>
     * The XID is typically a numeric string that uniquely identifies the filesystem transaction
     * from which the snapshot was created.
     *
     * @return the snapshot XID as a string (never null)
     */
    public String getXID() {
        return XID;
    }

    /**
     * Indicates whether this snapshot is marked as purgeable by the underlying APFS volume.
     * <p>
     * A purgeable snapshot is eligible for deletion by administrative tools and may be removed
     * without first satisfying additional retention constraints. This flag reflects the state
     * captured when this {@code Snapshot} instance was created and does not change thereafter.
     *
     * @return {@code true} if the snapshot is marked as purgeable; {@code false} otherwise
     * @see com.simtechdata.bash.SnapCommands#purge(Snapshot)
     */
    public boolean isPurgeable() {
        return purgeable;
    }
    
    @Override
    public String toString() {
        return String.format("Disk: %s\nUUID: %s\nName: %s\nXID: %s\nPurgeable: %s\n", disk, UUID, Name, XID, (purgeable ? "YES" : "NO"));
    }
}
