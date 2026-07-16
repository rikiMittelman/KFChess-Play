package org.example.ui;

import org.example.Img;
import org.example.engine.GameEngine;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {

    private static final int MOVE_DURATION_MS = 3000;
    private static final int JUMP_DURATION_MS = 1000;

    private static final int SHORT_REST_DURATION_MS = 500;
    private static final int LONG_REST_DURATION_MS = 3000;

    private static final int REFRESH_DELAY_MS = 40;

    private final GameEngine gameEngine;
    private final Board board;
    private final ImageManager imageManager;
    private final MoveListener moveListener;
    private final Runnable newGameAction;

    private final Img boardImage;
    private final Timer idleAnimationTimer;
    private Position selectedPosition;

    /*
     * המשבצות הירוקות של הכלי המסומן.
     */
    private List<Position> highlightedMoves;

    /*
     * האנימציות הפעילות.
     */
    private final List<PieceAnimation> activeAnimations;

    /*
     * הצביעה בזמן מנוחה ובתחילת המשחק.
     */
    private final Map<Piece, RestHighlight> restHighlights;

    private final Timer restHighlightTimer;

    /*
     * כפתור למשחק חדש לאחר Game Over.
     */
    private final JButton newGameButton;

    public BoardPanel(
            GameEngine gameEngine,
            ImageManager imageManager,
            MoveListener moveListener,
            Runnable newGameAction) {
        idleAnimationTimer =
                new Timer(
                        40,
                        event -> repaint()
                );

        idleAnimationTimer.start();
        this.gameEngine = gameEngine;
        this.board = gameEngine.getBoard();
        this.imageManager = imageManager;
        this.moveListener = moveListener;
        this.newGameAction = newGameAction;

        this.selectedPosition = null;

        this.highlightedMoves =
                new ArrayList<>();

        this.activeAnimations =
                new ArrayList<>();

        this.restHighlights =
                new IdentityHashMap<>();

        this.boardImage =
                new Img().read(
                        "src/main/images/board.png"
                );

        setPreferredSize(
                new Dimension(
                        boardImage.get().getWidth(),
                        boardImage.get().getHeight()
                )
        );

        /*
         * מאפשר לשים את כפתור New Game
         * מעל ציור הלוח.
         */
        setLayout(null);

        newGameButton =
                new JButton(
                        "New Game"
                );

        newGameButton.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        20
                )
        );

        newGameButton.setFocusable(false);
        newGameButton.setVisible(false);

        newGameButton.addActionListener(
                event -> {

                    if (this.newGameAction != null) {

                        this.newGameAction.run();
                    }
                }
        );

        add(
                newGameButton
        );

        /*
         * טיימר של הצביעה הירוקה.
         */
        restHighlightTimer =
                new Timer(
                        REFRESH_DELAY_MS,
                        event -> {

                            restHighlights
                                    .entrySet()
                                    .removeIf(
                                            entry -> {

                                                Piece piece =
                                                        entry.getKey();

                                                RestHighlight highlight =
                                                        entry.getValue();

                                                return highlight.isFinished()
                                                        || board.getPieceAt(
                                                        highlight.getPosition()
                                                ) != piece;
                                            }
                                    );

                            repaint();

                            if (restHighlights.isEmpty()) {

                                ((Timer) event.getSource())
                                        .stop();
                            }
                        }
                );

        /*
         * מתחילים את שלוש שניות הנעילה.
         */
        startInitialGameLockHighlights();

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent event) {

                        /*
                         * לחיצה ימנית:
                         * קפיצה בלבד.
                         */
                        if (SwingUtilities
                                .isRightMouseButton(event)) {

                            handleRightClick(
                                    event.getX(),
                                    event.getY()
                            );

                            return;
                        }

                        /*
                         * לחיצה שמאלית:
                         * בחירה ותנועה.
                         */
                        if (SwingUtilities
                                .isLeftMouseButton(event)) {

                            handleLeftClick(
                                    event.getX(),
                                    event.getY()
                            );
                        }
                    }
                }
        );
    }

    /**
     * ממקם את כפתור המשחק החדש.
     */
    @Override
    public void doLayout() {

        super.doLayout();

        int buttonWidth = 190;
        int buttonHeight = 48;

        int buttonX =
                (getWidth() - buttonWidth) / 2;

        int buttonY =
                getHeight() / 2 + 65;

        newGameButton.setBounds(
                buttonX,
                buttonY,
                buttonWidth,
                buttonHeight
        );
    }

    /**
     * מתחיל צביעה ירוקה לכל הכלים
     * בשלוש שניות הראשונות.
     */
    private void startInitialGameLockHighlights() {

        long startTime =
                gameEngine
                        .getInitialLockStartTimeMillis();

        int duration =
                (int) (
                        gameEngine
                                .getGameStartTimeMillis()
                                - startTime
                );

        for (int row = 0;
             row < board.getRows();
             row++) {

            for (int col = 0;
                 col < board.getCols();
                 col++) {

                Position position =
                        new Position(row, col);

                Piece piece =
                        board.getPieceAt(position);

                if (piece == null) {
                    continue;
                }

                restHighlights.put(
                        piece,
                        new RestHighlight(
                                position,
                                startTime,
                                duration
                        )
                );
            }
        }

        if (!restHighlights.isEmpty()) {

            restHighlightTimer.start();
        }
    }

    /**
     * טיפול בלחיצה ימנית.
     *
     * לחיצה ימנית על כלי מבצעת קפיצה.
     */
    private void handleRightClick(
            int mouseX,
            int mouseY) {

        if (gameEngine.isGameOver()) {
            return;
        }

        if (gameEngine.isGameStarting()) {

            clearSelection();
            repaint();

            return;
        }

        Position clickedPosition =
                getClickedPosition(
                        mouseX,
                        mouseY
                );

        if (clickedPosition == null) {
            return;
        }

        Piece clickedPiece =
                board.getPieceAt(
                        clickedPosition
                );

        if (clickedPiece == null) {
            return;
        }

        clearSelection();

        if (isUnavailable(
                clickedPiece,
                clickedPosition)) {

            repaint();
            return;
        }

        tryStartJump(
                clickedPiece,
                clickedPosition
        );

        repaint();
    }

    /**
     * טיפול בלחיצה שמאלית.
     *
     * לחיצה שמאלית משמשת רק
     * לבחירה ולביצוע תנועה.
     */
    private void handleLeftClick(
            int mouseX,
            int mouseY) {

        if (gameEngine.isGameOver()) {
            return;
        }

        if (gameEngine.isGameStarting()) {

            clearSelection();
            repaint();

            return;
        }

        Position clickedPosition =
                getClickedPosition(
                        mouseX,
                        mouseY
                );

        if (clickedPosition == null) {
            return;
        }

        Piece clickedPiece =
                board.getPieceAt(
                        clickedPosition
                );

        /*
         * עדיין לא נבחר כלי.
         */
        if (selectedPosition == null) {

            if (clickedPiece == null) {
                return;
            }

            if (isUnavailable(
                    clickedPiece,
                    clickedPosition)) {

                return;
            }

            selectPiece(
                    clickedPosition
            );

            repaint();
            return;
        }

        Piece selectedPiece =
                board.getPieceAt(
                        selectedPosition
                );

        /*
         * הכלי שנבחר כבר לא נמצא שם.
         */
        if (selectedPiece == null) {

            clearSelection();
            repaint();

            return;
        }

        /*
         * הכלי כבר התחיל פעולה
         * או נכנס למנוחה.
         */
        if (isUnavailable(
                selectedPiece,
                selectedPosition)) {

            clearSelection();
            repaint();

            return;
        }

        /*
         * לחיצה שמאלית נוספת על אותו כלי
         * לא עושה קפיצה.
         *
         * הכלי נשאר מסומן.
         */
        if (samePosition(
                selectedPosition,
                clickedPosition)) {

            repaint();
            return;
        }

        /*
         * לחיצה על כלי אחר מאותו צבע:
         * עוברים לבחור את הכלי החדש.
         */
        if (clickedPiece != null
                && clickedPiece.getColor()
                == selectedPiece.getColor()) {

            if (isUnavailable(
                    clickedPiece,
                    clickedPosition)) {

                return;
            }

            selectPiece(
                    clickedPosition
            );

            repaint();
            return;
        }

        Position from =
                selectedPosition;

        Position to =
                clickedPosition;

        /*
         * בודקים לפני שינוי הלוח
         * אם היעד מכיל כלי יריב.
         */
        boolean isCapture =
                clickedPiece != null
                        && clickedPiece.getColor()
                        != selectedPiece.getColor();

        boolean moveAccepted =
                gameEngine.makeMove(
                        from,
                        to
                );

        clearSelection();

        if (moveAccepted) {

            startMoveAnimation(
                    selectedPiece,
                    from,
                    to
            );

            String separator =
                    isCapture ? "x" : "-";

            String moveText =
                    pieceToHistoryCode(
                            selectedPiece
                    )
                            + " "
                            + positionToChessNotation(from)
                            + separator
                            + positionToChessNotation(to);

            notifyMoveListener(
                    selectedPiece.getColor(),
                    moveText
            );

        } else {

            System.out.println(
                    "Invalid move: "
                            + from
                            + " -> "
                            + to
            );
        }

        repaint();
    }

    /**
     * ממיר את מיקום העכבר למשבצת.
     */
    private Position getClickedPosition(
            int mouseX,
            int mouseY) {

        int col =
                mouseX
                        * board.getCols()
                        / boardImage.get().getWidth();

        int row =
                mouseY
                        * board.getRows()
                        / boardImage.get().getHeight();

        if (row < 0
                || row >= board.getRows()
                || col < 0
                || col >= board.getCols()) {

            return null;
        }

        return new Position(
                row,
                col
        );
    }

    /**
     * מסמן כלי ומציג את המהלכים שלו.
     */
    private void selectPiece(
            Position position) {

        selectedPosition =
                position;

        highlightedMoves =
                gameEngine.getValidMoves(
                        position
                );
    }

    /**
     * מרענן את המשבצות האפשריות
     * לפי מצב המשחק הנוכחי.
     */
    private void refreshHighlightedMoves() {

        if (gameEngine.isGameOver()
                || gameEngine.isGameStarting()) {

            clearSelection();
            return;
        }

        if (selectedPosition == null) {

            highlightedMoves =
                    new ArrayList<>();

            return;
        }

        Piece selectedPiece =
                board.getPieceAt(
                        selectedPosition
                );

        if (selectedPiece == null
                || isUnavailable(
                selectedPiece,
                selectedPosition)) {

            clearSelection();
            return;
        }

        highlightedMoves =
                gameEngine.getValidMoves(
                        selectedPosition
                );
    }

    /**
     * מבטל את בחירת הכלי.
     */
    private void clearSelection() {

        selectedPosition = null;

        highlightedMoves =
                new ArrayList<>();
    }

    /**
     * מתחיל קפיצה.
     */
    private void tryStartJump(
            Piece piece,
            Position position) {

        boolean jumpAccepted =
                gameEngine.makeJump(
                        position
                );

        if (!jumpAccepted) {
            return;
        }

        startJumpAnimation(
                piece,
                position
        );

        notifyMoveListener(
                piece.getColor(),
                pieceToHistoryCode(piece)
                        + " jump "
                        + positionToChessNotation(position)
        );
    }

    /**
     * מעביר מהלך לטבלת המהלכים.
     */
    private void notifyMoveListener(
            char color,
            String moveText) {

        if (moveListener != null) {

            moveListener.onMove(
                    color,
                    moveText
            );
        }
    }

    /**
     * בודק האם כרגע אסור להשתמש בכלי.
     */
    private boolean isUnavailable(
            Piece piece,
            Position position) {

        return gameEngine.isGameStarting()
                || gameEngine.isGameOver()
                || isPieceAnimated(piece)
                || gameEngine.isPieceCoolingDown(piece)
                || gameEngine
                .getArbiter()
                .isSourceBlocked(position);
    }

    /**
     * בודק אם לכלי יש אנימציה פעילה.
     */
    private boolean isPieceAnimated(
            Piece piece) {

        for (PieceAnimation animation
                : activeAnimations) {

            if (animation.getPiece() == piece) {
                return true;
            }
        }

        return false;
    }

    /**
     * מתחיל אנימציית תנועה רגילה.
     */
    private void startMoveAnimation(
            Piece piece,
            Position from,
            Position destination) {

        AnimationState state =
                AnimationState.MOVE;

        int frameCount =
                imageManager.getFrameCount(
                        piece,
                        state
                );

        if (frameCount == 0) {

            state =
                    AnimationState.IDLE;

            frameCount =
                    imageManager.getFrameCount(
                            piece,
                            state
                    );
        }

        if (frameCount == 0) {
            return;
        }

        PieceAnimation animation =
                new PieceAnimation(
                        piece,
                        from,
                        destination,
                        state,
                        frameCount,
                        MOVE_DURATION_MS
                );

        activeAnimations.add(
                animation
        );

        Timer timer =
                new Timer(
                        REFRESH_DELAY_MS,
                        event -> {

                            boolean motionStillActive =
                                    gameEngine
                                            .getArbiter()
                                            .hasMotionForPiece(
                                                    piece
                                            );

                            /*
                             * התנועה הסתיימה או שהכלי נאכל.
                             */
                            if (!motionStillActive) {

                                activeAnimations.remove(
                                        animation
                                );

                                ((Timer) event.getSource())
                                        .stop();

                                Piece destinationPiece =
                                        board.getPieceAt(
                                                destination
                                        );

                                if (!gameEngine.isGameOver()
                                        && isOriginalOrPromotedPiece(
                                        piece,
                                        destinationPiece)) {

                                    startRestAnimation(
                                            destinationPiece,
                                            destination,
                                            AnimationState.LONG_REST,
                                            LONG_REST_DURATION_MS
                                    );
                                }
                            }

                            repaint();
                        }
                );

        timer.start();
    }

    /**
     * בודק האם הכלי ביעד הוא הכלי המקורי
     * או מלכה שנוצרה מקידום חייל.
     */
    private boolean isOriginalOrPromotedPiece(
            Piece originalPiece,
            Piece destinationPiece) {

        if (destinationPiece == originalPiece) {
            return true;
        }

        if (destinationPiece == null) {
            return false;
        }

        return originalPiece.getType() == 'P'
                && destinationPiece.getType() == 'Q'
                && originalPiece.getColor()
                == destinationPiece.getColor();
    }

    /**
     * מתחיל אנימציית קפיצה.
     */
    private void startJumpAnimation(
            Piece piece,
            Position position) {

        AnimationState state =
                AnimationState.JUMP;

        int frameCount =
                imageManager.getFrameCount(
                        piece,
                        state
                );

        if (frameCount == 0) {

            state =
                    AnimationState.IDLE;

            frameCount =
                    imageManager.getFrameCount(
                            piece,
                            state
                    );
        }

        if (frameCount == 0) {
            return;
        }

        PieceAnimation animation =
                new PieceAnimation(
                        piece,
                        position,
                        position,
                        state,
                        frameCount,
                        JUMP_DURATION_MS
                );

        activeAnimations.add(
                animation
        );

        Timer timer =
                new Timer(
                        REFRESH_DELAY_MS,
                        event -> {

                            boolean jumpStillActive =
                                    gameEngine
                                            .getArbiter()
                                            .hasMotionForPiece(
                                                    piece
                                            );

                            if (!jumpStillActive) {

                                activeAnimations.remove(
                                        animation
                                );

                                ((Timer) event.getSource())
                                        .stop();

                                if (!gameEngine.isGameOver()
                                        && board.getPieceAt(position)
                                        == piece) {

                                    startRestAnimation(
                                            piece,
                                            position,
                                            AnimationState.SHORT_REST,
                                            SHORT_REST_DURATION_MS
                                    );
                                }
                            }

                            repaint();
                        }
                );

        timer.start();
    }

    /**
     * מתחיל מנוחה של כלי.
     */
    private void startRestAnimation(
            Piece piece,
            Position position,
            AnimationState restState,
            int durationMs) {

        startRestHighlight(
                piece,
                position,
                durationMs
        );

        AnimationState state =
                restState;

        int frameCount =
                imageManager.getFrameCount(
                        piece,
                        state
                );

        if (frameCount == 0) {

            state =
                    AnimationState.IDLE;

            frameCount =
                    imageManager.getFrameCount(
                            piece,
                            state
                    );
        }

        if (frameCount == 0) {
            repaint();
            return;
        }

        PieceAnimation animation =
                new PieceAnimation(
                        piece,
                        position,
                        position,
                        state,
                        frameCount,
                        durationMs
                );

        activeAnimations.add(
                animation
        );

        Timer timer =
                new Timer(
                        REFRESH_DELAY_MS,
                        event -> {

                            if (board.getPieceAt(position)
                                    != piece) {

                                activeAnimations.remove(
                                        animation
                                );

                                restHighlights.remove(
                                        piece
                                );

                                ((Timer) event.getSource())
                                        .stop();

                                repaint();
                                return;
                            }

                            if (animation.isFinished()) {

                                activeAnimations.remove(
                                        animation
                                );

                                ((Timer) event.getSource())
                                        .stop();
                            }

                            repaint();
                        }
                );

        timer.start();
    }

    /**
     * מפעיל את הצביעה הירוקה של המנוחה.
     */
    private void startRestHighlight(
            Piece piece,
            Position position,
            int durationMs) {

        restHighlights.put(
                piece,
                new RestHighlight(
                        position,
                        durationMs
                )
        );

        if (!restHighlightTimer.isRunning()) {

            restHighlightTimer.start();
        }

        repaint();
    }

    /**
     * מעדכן את תצוגת Game Over.
     */
    public void refreshGameState() {

        boolean finished =
                gameEngine.isGameOver();

        newGameButton.setVisible(
                finished
        );

        if (finished) {

            clearSelection();
            restHighlights.clear();
        }

        repaint();
    }

    /**
     * מצייר את הלוח.
     */
    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        graphics.drawImage(
                boardImage.get(),
                0,
                0,
                null
        );

        refreshHighlightedMoves();

        drawValidMoveHighlights(
                graphics
        );

        drawRestHighlights(
                graphics
        );

        /*
         * ציור הכלים שנמצאים בלוח.
         */
        for (int row = 0;
             row < board.getRows();
             row++) {

            for (int col = 0;
                 col < board.getCols();
                 col++) {

                Position position =
                        new Position(row, col);

                Piece piece =
                        board.getPieceAt(
                                position
                        );

                if (piece == null
                        || isPieceAnimated(piece)) {

                    continue;
                }

                int idleFrame =
                        getIdleFrame(piece);

                Img image =
                        imageManager.getImage(
                                piece,
                                AnimationState.IDLE,
                                idleFrame
                        );

                if (image == null
                        || image.get() == null) {

                    continue;
                }

                drawPieceInsideCell(
                        graphics,
                        image,
                        row,
                        col
                );
            }
        }

        /*
         * ציור האנימציות הפעילות.
         */
        List<PieceAnimation> animationsToDraw =
                new ArrayList<>(
                        activeAnimations
                );

        for (PieceAnimation animation
                : animationsToDraw) {

            drawAnimation(
                    graphics,
                    animation
            );
        }

        /*
         * מסגרת סביב הכלי המסומן.
         */
        if (selectedPosition != null
                && board.getPieceAt(
                selectedPosition) != null) {

            drawSelectionFrame(
                    graphics
            );
        }

        /*
         * שכבת Game Over.
         */
        if (gameEngine.isGameOver()) {

            drawGameOverOverlay(
                    graphics
            );
        }
    }

    /**
     * מצייר בירוק את המהלכים האפשריים.
     */
    private void drawValidMoveHighlights(
            Graphics graphics) {

        if (highlightedMoves == null
                || highlightedMoves.isEmpty()) {

            return;
        }

        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setColor(
                new Color(
                        80,
                        220,
                        100,
                        125
                )
        );

        for (Position position
                : highlightedMoves) {

            int left =
                    getCellLeft(
                            position.getCol()
                    );

            int right =
                    getCellRight(
                            position.getCol()
                    );

            int top =
                    getCellTop(
                            position.getRow()
                    );

            int bottom =
                    getCellBottom(
                            position.getRow()
                    );

            graphics2D.fillRect(
                    left,
                    top,
                    right - left,
                    bottom - top
            );
        }

        graphics2D.dispose();
    }

    /**
     * מצייר את הצביעה הירוקה של המנוחה.
     */
    private void drawRestHighlights(
            Graphics graphics) {

        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setColor(
                new Color(
                        144,
                        238,
                        144,
                        160
                )
        );

        List<RestHighlight> highlights =
                new ArrayList<>(
                        restHighlights.values()
                );

        for (RestHighlight highlight
                : highlights) {

            Position position =
                    highlight.getPosition();

            int left =
                    getCellLeft(
                            position.getCol()
                    );

            int right =
                    getCellRight(
                            position.getCol()
                    );

            int top =
                    getCellTop(
                            position.getRow()
                    );

            int bottom =
                    getCellBottom(
                            position.getRow()
                    );

            int cellHeight =
                    bottom - top;

            int clearedHeight =
                    (int) Math.round(
                            cellHeight
                                    * highlight.getProgress()
                    );

            int greenY =
                    top + clearedHeight;

            int greenHeight =
                    cellHeight - clearedHeight;

            if (greenHeight > 0) {

                graphics2D.fillRect(
                        left,
                        greenY,
                        right - left,
                        greenHeight
                );
            }
        }

        graphics2D.dispose();
    }

    /**
     * מצייר כלי בתוך משבצת.
     */
    private void drawPieceInsideCell(
            Graphics graphics,
            Img image,
            int row,
            int col) {

        int left =
                getCellLeft(col);

        int right =
                getCellRight(col);

        int top =
                getCellTop(row);

        int bottom =
                getCellBottom(row);

        int imageX =
                left
                        + (
                        right
                                - left
                                - image.get().getWidth()
                ) / 2;

        int imageY =
                top
                        + (
                        bottom
                                - top
                                - image.get().getHeight()
                ) / 2;

        graphics.drawImage(
                image.get(),
                imageX,
                imageY,
                null
        );
    }

    /**
     * מצייר אנימציה פעילה.
     */
    private void drawAnimation(
            Graphics graphics,
            PieceAnimation animation) {

        Img image =
                imageManager.getImage(
                        animation.getPiece(),
                        animation.getAnimationState(),
                        animation.getCurrentFrame()
                );

        if (image == null
                || image.get() == null) {

            return;
        }

        Position from =
                animation.getFrom();

        Position to =
                animation.getTo();

        int startX =
                getCellCenterX(
                        from.getCol()
                )
                        - image.get().getWidth() / 2;

        int startY =
                getCellCenterY(
                        from.getRow()
                )
                        - image.get().getHeight() / 2;

        int endX =
                getCellCenterX(
                        to.getCol()
                )
                        - image.get().getWidth() / 2;

        int endY =
                getCellCenterY(
                        to.getRow()
                )
                        - image.get().getHeight() / 2;

        double progress =
                animation.getProgress();

        int currentX =
                (int) Math.round(
                        startX
                                + (endX - startX)
                                * progress
                );

        int currentY =
                (int) Math.round(
                        startY
                                + (endY - startY)
                                * progress
                );

        graphics.drawImage(
                image.get(),
                currentX,
                currentY,
                null
        );
    }

    /**
     * מצייר מסגרת סביב כלי מסומן.
     */
    private void drawSelectionFrame(
            Graphics graphics) {

        if (selectedPosition == null) {
            return;
        }

        int left =
                getCellLeft(
                        selectedPosition.getCol()
                );

        int right =
                getCellRight(
                        selectedPosition.getCol()
                );

        int top =
                getCellTop(
                        selectedPosition.getRow()
                );

        int bottom =
                getCellBottom(
                        selectedPosition.getRow()
                );

        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setColor(
                Color.YELLOW
        );

        graphics2D.setStroke(
                new BasicStroke(2)
        );

        graphics2D.drawRect(
                left + 3,
                top + 3,
                right - left - 7,
                bottom - top - 7
        );

        graphics2D.dispose();
    }
    /**
     * מחזיר את הפריים הנוכחי של אנימציית העמידה.
     */
    private int getIdleFrame(Piece piece) {

        int frameCount =
                imageManager.getFrameCount(
                        piece,
                        AnimationState.IDLE
                );

        if (frameCount <= 1) {
            return 0;
        }

        /*
         * מעבר לפריים הבא כל 180 מילישניות.
         */
        long frameDurationMs = 180;

        long currentTime =
                System.currentTimeMillis();

        return (int) (
                currentTime / frameDurationMs
                        % frameCount
        );
    }
    /**
     * מצייר את שכבת Game Over.
     */
    private void drawGameOverOverlay(
            Graphics graphics) {

        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setColor(
                new Color(
                        0,
                        0,
                        0,
                        185
                )
        );

        graphics2D.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        String gameOverText =
                "GAME OVER";

        graphics2D.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        58
                )
        );

        FontMetrics gameOverMetrics =
                graphics2D.getFontMetrics();

        int gameOverX =
                (getWidth()
                        - gameOverMetrics.stringWidth(
                        gameOverText
                )) / 2;

        int gameOverY =
                getHeight() / 2 - 35;

        graphics2D.setColor(
                Color.WHITE
        );

        graphics2D.drawString(
                gameOverText,
                gameOverX,
                gameOverY
        );

        String winnerText =
                gameEngine.getWinnerName();

        graphics2D.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        28
                )
        );

        FontMetrics winnerMetrics =
                graphics2D.getFontMetrics();

        int winnerX =
                (getWidth()
                        - winnerMetrics.stringWidth(
                        winnerText
                )) / 2;

        int winnerY =
                getHeight() / 2 + 15;

        graphics2D.drawString(
                winnerText,
                winnerX,
                winnerY
        );

        graphics2D.dispose();
    }

    /**
     * משווה שתי משבצות.
     */
    private boolean samePosition(
            Position first,
            Position second) {

        if (first == null || second == null) {
            return false;
        }

        return first.getRow()
                == second.getRow()
                &&
                first.getCol()
                        == second.getCol();
    }

    /**
     * ממיר משבצת לסימון שחמט.
     */
    private String positionToChessNotation(
            Position position) {

        char column =
                (char) (
                        'a'
                                + position.getCol()
                );

        int row =
                board.getRows()
                        - position.getRow();

        return String.valueOf(column)
                + row;
    }

    /**
     * מחזיר קוד כלי לטבלת המהלכים.
     */
    private String pieceToHistoryCode(
            Piece piece) {

        return String.valueOf(
                piece.getType()
        )
                + Character.toUpperCase(
                piece.getColor()
        );
    }

    private int getCellLeft(int col) {

        return col
                * boardImage.get().getWidth()
                / board.getCols();
    }

    private int getCellRight(int col) {

        return (col + 1)
                * boardImage.get().getWidth()
                / board.getCols();
    }

    private int getCellTop(int row) {

        return row
                * boardImage.get().getHeight()
                / board.getRows();
    }

    private int getCellBottom(int row) {

        return (row + 1)
                * boardImage.get().getHeight()
                / board.getRows();
    }

    private int getCellCenterX(int col) {

        return (
                getCellLeft(col)
                        + getCellRight(col)
        ) / 2;
    }

    private int getCellCenterY(int row) {

        return (
                getCellTop(row)
                        + getCellBottom(row)
        ) / 2;
    }

    /**
     * שומר נתוני צביעה ירוקה.
     */
    private static class RestHighlight {

        private final Position position;
        private final long startTime;
        private final int durationMs;

        public RestHighlight(
                Position position,
                int durationMs) {

            this(
                    position,
                    System.currentTimeMillis(),
                    durationMs
            );
        }

        public RestHighlight(
                Position position,
                long startTime,
                int durationMs) {

            this.position = position;
            this.startTime = startTime;
            this.durationMs = durationMs;
        }

        public Position getPosition() {

            return position;
        }

        public double getProgress() {

            long elapsed =
                    System.currentTimeMillis()
                            - startTime;

            double progress =
                    elapsed
                            / (double) durationMs;

            return Math.max(
                    0.0,
                    Math.min(
                            1.0,
                            progress
                    )
            );
        }

        public boolean isFinished() {

            return getProgress() >= 1.0;
        }
    }
}