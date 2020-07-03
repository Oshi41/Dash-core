package dashcore.maze;

/**
 * Description of room door
 */
public interface IDoor {
    /**
     * Check if door from other room can be connected
     *
     * @param other
     * @return
     */
    boolean isConnected(IDoor other);
}
