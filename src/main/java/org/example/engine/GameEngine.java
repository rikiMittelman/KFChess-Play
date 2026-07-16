package org.example.engine;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.realtime.Motion;
import org.example.realtime.RealTimeArbiter;
import org.example.rules.RuleEngine;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    /*
     * נעילה בתחילת המשחק.
     */
    private static final long INITIAL_LOCK_DURATION_MS = 3000;

    /*
     * משך תנועה רגילה.
     */
    private static final long MOVE_DURATION_MS = 3000;

    /*
     * משך קפיצה.
     */
    private static final long JUMP_DURATION_MS = 1000;

    /*
     * מנוחה לאחר קפיצה.
     */
    private static final long SHORT_REST_DURATION_MS = 500;

    /*
     * מנוחה לאחר תנועה רגילה.
     */
    private static final long LONG_REST_DURATION_MS = 3000;

    private final Board board;
    private final RuleEngine ruleEngine;
    private final RealTimeArbiter arbiter;

    /*
     * מחלקות העזר.
     */
    private final ScoreManager scoreManager;
    private final CooldownManager cooldownManager;
    private final JumpManager jumpManager;

    /*
     * זמן תחילת נעילת המשחק.
     */
    private final long initialLockStartTimeMillis;

    /*
     * הזמן שבו מסתיימת הנעילה
     * והמשחק באמת מתחיל.
     */
    private final long gameStartTimeMillis;

    private boolean gameOver;
    private char winnerColor;

    public GameEngine(Board board) {

        this.board = board;

        this.ruleEngine =
                new RuleEngine();

        this.arbiter =
                new RealTimeArbiter();

        this.scoreManager =
                new ScoreManager();

        this.cooldownManager =
                new CooldownManager();

        this.jumpManager =
                new JumpManager();

        this.initialLockStartTimeMillis =
                System.currentTimeMillis();

        this.gameStartTimeMillis =
                initialLockStartTimeMillis
                        + INITIAL_LOCK_DURATION_MS;

        this.gameOver = false;
        this.winnerColor = '\0';
    }

    /**
     * מחזיר את כל המשבצות שאליהן
     * הכלי יכול לנוע כרגע.
     */
    public List<Position> getValidMoves(
            Position from) {

        List<Position> validMoves =
                new ArrayList<>();

        if (cannotStartAction()
                || from == null) {

            return validMoves;
        }

        Piece selectedPiece =
                board.getPieceAt(from);

        if (!isPieceAvailable(
                selectedPiece,
                from)) {

            return validMoves;
        }

        for (int row = 0;
             row < board.getRows();
             row++) {

            for (int col = 0;
                 col < board.getCols();
                 col++) {

                Position destination =
                        new Position(row, col);

                /*
                 * משבצת המקור אינה מהלך.
                 */
                if (samePosition(
                        from,
                        destination)) {

                    continue;
                }

                /*
                 * לא מציגים יעד שבו נמצא כלי ידידותי,
                 * או שכלי ידידותי אחר כבר בדרך אליו.
                 */
                if (isDestinationBlockedByFriendlyPiece(
                        selectedPiece,
                        destination)) {

                    continue;
                }

                if (ruleEngine.validateMove(
                        from,
                        destination,
                        board)) {

                    validMoves.add(
                            destination
                    );
                }
            }
        }

        return validMoves;
    }

    /**
     * מתחיל תנועה רגילה.
     */
    public boolean makeMove(
            Position from,
            Position to) {

        if (cannotStartAction()
                || from == null
                || to == null) {

            return false;
        }

        Piece piece =
                board.getPieceAt(from);

        if (!isPieceAvailable(
                piece,
                from)) {

            return false;
        }

        if (isDestinationBlockedByFriendlyPiece(
                piece,
                to)) {

            return false;
        }

        if (!ruleEngine.validateMove(
                from,
                to,
                board)) {

            return false;
        }

        /*
         * בזמן התנועה הכלי אינו נמצא בלוח.
         * הוא נשמר בתוך Motion.
         */
        board.setPieceAt(
                from,
                null
        );

        long startTime =
                System.currentTimeMillis();

        Motion motion =
                new Motion(
                        piece,
                        from,
                        to,
                        startTime,
                        startTime
                                + MOVE_DURATION_MS
                );

        arbiter.addMotion(
                motion
        );

        return true;
    }

    /**
     * מתחיל קפיצה במקום.
     */
    public boolean makeJump(
            Position position) {

        if (cannotStartAction()
                || position == null) {

            return false;
        }

        Piece piece =
                board.getPieceAt(position);

        if (!isPieceAvailable(
                piece,
                position)) {

            return false;
        }

        long startTime =
                System.currentTimeMillis();

        Motion jumpMotion =
                new Motion(
                        piece,
                        position,
                        position,
                        startTime,
                        startTime
                                + JUMP_DURATION_MS
                );

        arbiter.addMotion(
                jumpMotion
        );

        jumpManager.addJump(
                jumpMotion
        );

        return true;
    }

    /**
     * מעדכן את מצב המשחק.
     *
     * המתודה מופעלת על ידי הטיימר
     * של המשחק.
     */
    public void tick() {

        if (gameOver) {
            return;
        }

        long currentTime =
                System.currentTimeMillis();

        List<Motion> arrivedMotions =
                arbiter.updateTime(
                        currentTime
                );

        /*
         * קודם מטפלים בקפיצות שהסתיימו.
         */
        for (Motion motion : arrivedMotions) {

            if (motion.isJump()) {

                handleJumpArrival(
                        motion,
                        currentTime
                );
            }
        }

        /*
         * לאחר מכן מטפלים בתנועות רגילות.
         */
        for (Motion motion : arrivedMotions) {

            if (!motion.isJump()) {

                handleMoveArrival(
                        motion,
                        currentTime
                );

                /*
                 * ייתכן שבמהלך הטיפול
                 * אחד המלכים נאכל.
                 */
                if (gameOver) {
                    break;
                }
            }
        }

        cooldownManager.update(
                currentTime
        );

        jumpManager.update(
                currentTime
        );
    }

    /**
     * טיפול בסיום קפיצה.
     */
    private void handleJumpArrival(
            Motion jumpMotion,
            long currentTime) {

        Piece jumpingPiece =
                jumpMotion.getPiece();

        Position position =
                jumpMotion.getTo();

        /*
         * אם הכלי עדיין נמצא במשבצת,
         * מתחילה עבורו מנוחה קצרה.
         */
        if (board.getPieceAt(position)
                == jumpingPiece) {

            cooldownManager.startCooldown(
                    jumpingPiece,
                    currentTime,
                    SHORT_REST_DURATION_MS
            );

            return;
        }

        /*
         * הכלי כבר נאכל או הוסר מהלוח.
         */
        cooldownManager.remove(
                jumpingPiece
        );

        jumpManager.removeForPiece(
                jumpingPiece
        );
    }

    /**
     * טיפול בהגעת כלי למשבצת היעד.
     *
     * אין כאן בדיקות של התנגשויות
     * באמצע מסלול התנועה.
     */
    private void handleMoveArrival(
            Motion movingMotion,
            long currentTime) {

        Piece movingPiece =
                movingMotion.getPiece();

        Position destination =
                movingMotion.getTo();

        /*
         * בודקים האם הכלי שהיה ביעד
         * ביצע קפיצת הגנה בזמן המתאים.
         */
        Motion protectingJump =
                jumpManager.findProtectingJump(
                        movingMotion,
                        board
                );

        if (protectingJump != null) {

            handleJumpCapture(
                    protectingJump,
                    movingPiece
            );

            return;
        }

        Piece pieceAtDestination =
                board.getPieceAt(
                        destination
                );

        /*
         * מצב הגנה:
         * כלי מאותו צבע נמצא ביעד בזמן ההגעה.
         */
        if (isFriendlyPiece(
                movingPiece,
                pieceAtDestination)) {

            returnPieceToSource(
                    movingMotion,
                    currentTime
            );

            return;
        }

        /*
         * כלי יריב נמצא במשבצת היעד.
         */
        if (isEnemyPiece(
                movingPiece,
                pieceAtDestination)) {

            handleNormalCapture(
                    movingPiece,
                    pieceAtDestination
            );
        }

        placeArrivingPiece(
                movingPiece,
                destination,
                currentTime
        );
    }

    /**
     * מטפל במצב שבו כלי קופץ
     * אוכל את הכלי שהגיע לתקוף אותו.
     */
    private void handleJumpCapture(
            Motion protectingJump,
            Piece attackingPiece) {

        Piece jumpingPiece =
                protectingJump.getPiece();

        registerCapture(
                jumpingPiece,
                attackingPiece
        );

        cooldownManager.remove(
                attackingPiece
        );

        jumpManager.remove(
                protectingJump
        );
    }

    /**
     * מטפל באכילה רגילה במשבצת היעד.
     */
    private void handleNormalCapture(
            Piece movingPiece,
            Piece capturedPiece) {

        registerCapture(
                movingPiece,
                capturedPiece
        );

        /*
         * אם לכלי שנאכל קיימת פעולה פעילה,
         * מבטלים אותה.
         */
        arbiter.removeMotionsForPiece(
                capturedPiece
        );

        cooldownManager.remove(
                capturedPiece
        );

        jumpManager.removeForPiece(
                capturedPiece
        );
    }

    /**
     * מוסיף ניקוד ומסיים את המשחק
     * במקרה שהכלי שנאכל הוא מלך.
     */
    private void registerCapture(
            Piece attacker,
            Piece capturedPiece) {

        scoreManager.addCaptureScore(
                attacker,
                capturedPiece
        );

        if (capturedPiece.getType() == 'K') {

            endGame(
                    attacker.getColor()
            );
        }
    }

    /**
     * מניח את הכלי שהגיע במשבצת היעד.
     */
    private void placeArrivingPiece(
            Piece movingPiece,
            Position destination,
            long currentTime) {

        Piece finalPiece =
                promotePawnIfNeeded(
                        movingPiece,
                        destination
                );

        board.setPieceAt(
                destination,
                finalPiece
        );

        /*
         * אם נאכל מלך, אין צורך
         * להתחיל מנוחה חדשה.
         */
        if (gameOver) {
            return;
        }

        /*
         * במקרה של קידום נוצר אובייקט Piece חדש.
         */
        if (finalPiece != movingPiece) {

            cooldownManager.remove(
                    movingPiece
            );
        }

        cooldownManager.startCooldown(
                finalPiece,
                currentTime,
                LONG_REST_DURATION_MS
        );
    }

    /**
     * מחזיר כלי למשבצת המקור,
     * אם בזמן הגעתו נמצא ביעד כלי ידידותי.
     */
    private void returnPieceToSource(
            Motion movingMotion,
            long currentTime) {

        Position source =
                movingMotion.getFrom();

        Piece movingPiece =
                movingMotion.getPiece();

        /*
         * מחזירים אותו רק אם המקור עדיין פנוי.
         */
        if (board.getPieceAt(source)
                != null) {

            return;
        }

        board.setPieceAt(
                source,
                movingPiece
        );

        cooldownManager.startCooldown(
                movingPiece,
                currentTime,
                LONG_REST_DURATION_MS
        );
    }

    /**
     * בודק האם אפשר כרגע להתחיל פעולה חדשה.
     */
    private boolean cannotStartAction() {

        return gameOver
                || isGameStarting();
    }

    /**
     * בודק האם כלי זמין לתנועה או לקפיצה.
     */
    private boolean isPieceAvailable(
            Piece piece,
            Position position) {

        return piece != null
                && !arbiter.isSourceBlocked(position)
                && !cooldownManager.isCoolingDown(piece);
    }

    /**
     * בודק האם משבצת היעד תפוסה
     * או שמורה על ידי כלי מאותו צבע.
     */
    private boolean isDestinationBlockedByFriendlyPiece(
            Piece movingPiece,
            Position destination) {

        Piece pieceAtDestination =
                board.getPieceAt(
                        destination
                );

        if (isFriendlyPiece(
                movingPiece,
                pieceAtDestination)) {

            return true;
        }

        for (Motion motion
                : arbiter.getActiveMotions()) {

            /*
             * קפיצה לא שומרת יעד חדש.
             */
            if (motion.isJump()) {
                continue;
            }

            if (!samePosition(
                    motion.getTo(),
                    destination)) {

                continue;
            }

            Piece otherPiece =
                    motion.getPiece();

            if (otherPiece != movingPiece
                    && otherPiece.getColor()
                    == movingPiece.getColor()) {

                return true;
            }
        }

        return false;
    }

    /**
     * בודק האם שני הכלים הם מאותו צבע.
     */
    private boolean isFriendlyPiece(
            Piece firstPiece,
            Piece secondPiece) {

        return firstPiece != null
                && secondPiece != null
                && firstPiece != secondPiece
                && firstPiece.getColor()
                == secondPiece.getColor();
    }

    /**
     * בודק האם הכלי השני הוא כלי יריב.
     */
    private boolean isEnemyPiece(
            Piece firstPiece,
            Piece secondPiece) {

        return firstPiece != null
                && secondPiece != null
                && firstPiece != secondPiece
                && firstPiece.getColor()
                != secondPiece.getColor();
    }

    /**
     * מקדם חייל למלכה כאשר הוא מגיע
     * לשורה האחרונה של הצד השני.
     */
    private Piece promotePawnIfNeeded(
            Piece piece,
            Position destination) {

        if (piece.getType() != 'P') {
            return piece;
        }

        boolean whiteReachedLastRow =
                piece.getColor() == 'w'
                        && destination.getRow() == 0;

        boolean blackReachedLastRow =
                piece.getColor() == 'b'
                        && destination.getRow()
                        == board.getRows() - 1;

        if (!whiteReachedLastRow
                && !blackReachedLastRow) {

            return piece;
        }

        return new Piece(
                piece.getColor(),
                'Q'
        );
    }

    /**
     * מסיים את המשחק.
     */
    private void endGame(
            char winningColor) {

        gameOver = true;
        winnerColor = winningColor;

        /*
         * עוצרים את כל הפעולות הפעילות.
         */
        for (Motion motion
                : arbiter.getActiveMotions()) {

            arbiter.removeMotion(
                    motion
            );
        }

        jumpManager.clear();
        cooldownManager.clear();
    }

    /**
     * משווה שתי משבצות.
     */
    private boolean samePosition(
            Position first,
            Position second) {

        if (first == null
                || second == null) {

            return false;
        }

        return first.getRow()
                == second.getRow()
                && first.getCol()
                == second.getCol();
    }

    /**
     * משמש את ה-UI כדי לבדוק
     * אם כלי נמצא במנוחה.
     */
    public boolean isPieceCoolingDown(
            Piece piece) {

        return cooldownManager.isCoolingDown(
                piece
        );
    }

    public boolean isGameStarting() {

        return System.currentTimeMillis()
                < gameStartTimeMillis;
    }

    public boolean isGameOver() {

        return gameOver;
    }

    public String getWinnerName() {

        if (winnerColor == 'w') {
            return "White wins!";
        }

        if (winnerColor == 'b') {
            return "Black wins!";
        }

        return "";
    }

    public long getInitialLockStartTimeMillis() {

        return initialLockStartTimeMillis;
    }

    public long getGameStartTimeMillis() {

        return gameStartTimeMillis;
    }

    public int getWhiteScore() {

        return scoreManager.getWhiteScore();
    }

    public int getBlackScore() {

        return scoreManager.getBlackScore();
    }

    public Board getBoard() {

        return board;
    }

    public RealTimeArbiter getArbiter() {

        return arbiter;
    }
}