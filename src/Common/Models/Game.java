package Common.Models;

import java.io.Serializable;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class Game implements Serializable {
    private int GameID;
    private String Host;
    private String Visitor;
    private boolean Completed;

    public Game(int id, String host, String visitor) {
        this.GameID = id;
        this.Host = host;
        this.Visitor = visitor;
    }

    public int getGameID() {
        return GameID;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getVisitor() {
        return Visitor;
    }

    public void setVisitor(String visitor) {
        Visitor = visitor;
    }

    public String toString() {
        return String.format("%s vs. %s", Host, Visitor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Game) {
            return ((Game) obj).getGameID() == GameID;
        } else
            return super.equals(obj);
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }
}
