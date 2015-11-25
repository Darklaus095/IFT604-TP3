package Common.Models;

import java.io.Serializable;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class Penalty implements Serializable {
    public static final int LONG_PENALTY = 5;   // in minutes
    public static final int SHORT_PENALTY = 2;  // in minutes

    private String PenaltyHolder;
    private int TimeLeft;

    public Penalty(String holder, int time) {
        this.PenaltyHolder = holder;
        this.TimeLeft = time;
    }

    public String getPenaltyHolder() {
        return PenaltyHolder;
    }

    public void setPenaltyHolder(String penaltyHolder) {
        PenaltyHolder = penaltyHolder;
    }

    public int getTimeLeft() {
        return TimeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        TimeLeft = timeLeft;
    }

    public void incTimeLeft(int seconds) {
        TimeLeft += seconds;
    }

    public void decTimeLeft(int seconds) {
        TimeLeft -= seconds;
    }

    public String toString() {
        return String.format("%s, %d:%02d", PenaltyHolder, TimeLeft / 60, TimeLeft % 60);
    }
}
