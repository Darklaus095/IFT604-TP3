package Common.Models;

/**
 * Created by Michaël on 11/26/2015.
 */
public class HockeyEvent {
    private final int betId;
    private final int gameId;
    private final String description;

    public HockeyEvent(int gameId, String description, int betId) {
        this.gameId = gameId;
        this.description = description;
        this.betId = betId;
    }
}
