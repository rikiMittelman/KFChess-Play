package org.example.ui;

import org.example.model.Piece;
import org.example.model.Position;

public class PieceAnimation {

    /*
     * כל כמה מילישניות עוברים לתמונת האנימציה הבאה.
     * 100 מילישניות = 10 תמונות בשנייה.
     */
    private static final long FRAME_DURATION_MS = 100;

    private final Piece piece;
    private final Position from;
    private final Position to;
    private final AnimationState animationState;

    private final int totalFrames;
    private final long durationMs;

    /*
     * nanoTime מתאים למדידת משך זמן,
     * מפני שהוא אינו מושפע משינוי שעון המחשב.
     */
    private final long startTimeNanos;

    private boolean manuallyFinished;

    public PieceAnimation(
            Piece piece,
            Position from,
            Position to,
            AnimationState animationState,
            int totalFrames,
            long durationMs) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "piece cannot be null"
            );
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "positions cannot be null"
            );
        }

        if (animationState == null) {
            throw new IllegalArgumentException(
                    "animationState cannot be null"
            );
        }

        this.piece = piece;
        this.from = from;
        this.to = to;
        this.animationState = animationState;

        /*
         * הגנה מפני ערכים לא תקינים.
         */
        this.totalFrames =
                Math.max(1, totalFrames);

        this.durationMs =
                Math.max(1, durationMs);

        this.startTimeNanos =
                System.nanoTime();

        this.manuallyFinished = false;
    }

    /**
     * בנאי נוסף לתאימות לקוד ישן.
     * עדיף להשתמש בבנאי שמקבל גם durationMs.
     */
    public PieceAnimation(
            Piece piece,
            Position from,
            Position to,
            AnimationState animationState,
            int totalFrames) {

        this(
                piece,
                from,
                to,
                animationState,
                totalFrames,
                1000
        );
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public long getDurationMs() {
        return durationMs;
    }

    /**
     * מחזיר כמה מילישניות עברו
     * מאז תחילת האנימציה.
     */
    public long getElapsedTimeMs() {

        long elapsedNanos =
                System.nanoTime() - startTimeNanos;

        return elapsedNanos / 1_000_000;
    }

    /**
     * מחזיר את התקדמות האנימציה בין 0 ל־1.
     *
     * 0   = תחילת התנועה.
     * 0.5 = אמצע התנועה.
     * 1   = סוף התנועה.
     */
    public double getProgress() {

        if (manuallyFinished) {
            return 1.0;
        }

        double progress =
                getElapsedTimeMs()
                        / (double) durationMs;

        return Math.min(
                1.0,
                Math.max(0.0, progress)
        );
    }

    /**
     * מחזיר את מספר התמונה שצריך להציג כרגע.
     *
     * מיקום הכלי אינו תלוי במספר התמונה.
     */
    public int getCurrentFrame() {

        if (totalFrames <= 1) {
            return 0;
        }

        /*
         * בקפיצה מציגים את רצף התמונות פעם אחת,
         * מהתמונה הראשונה לאחרונה.
         */
        if (animationState == AnimationState.JUMP) {

            int frame =
                    (int) Math.floor(
                            getProgress() * totalFrames
                    );

            return Math.min(
                    totalFrames - 1,
                    frame
            );
        }

        /*
         * בתנועה ובמנוחה התמונות חוזרות בלולאה.
         *
         * לדוגמה:
         * 0, 1, 2, 3, 0, 1, 2, 3...
         */
        long frameNumber =
                getElapsedTimeMs()
                        / FRAME_DURATION_MS;

        return (int) (
                frameNumber % totalFrames
        );
    }

    /**
     * נשארה כדי שהקוד הקיים ב־BoardPanel
     * לא יקבל שגיאה.
     *
     * אין צורך לקדם ידנית פריים,
     * מפני שהפריים מחושב לפי הזמן שעבר.
     */
    public void advanceFrame() {
        // ההתקדמות מחושבת אוטומטית לפי הזמן.
    }

    /**
     * בודק האם זמן האנימציה הסתיים.
     */
    public boolean isFinished() {

        return manuallyFinished
                || getElapsedTimeMs() >= durationMs;
    }

    /**
     * מאפשר לסיים אנימציה באופן ידני.
     */
    public void finish() {

        manuallyFinished = true;
    }
}