package org.example.ui;

import org.example.engine.GameEngine;
import org.example.model.Board;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final GameEngine gameEngine;

    private final BoardPanel boardPanel;

    private final BoardWithCoordinatesPanel
            boardWithCoordinatesPanel;

    private final MoveHistoryPanel blackMovesPanel;
    private final MoveHistoryPanel whiteMovesPanel;

    /*
     * תוויות הניקוד מעל הטבלאות.
     */
    private final JLabel blackScoreLabel;
    private final JLabel whiteScoreLabel;

    public GamePanel(
            GameEngine gameEngine,
            ImageManager imageManager,
            long gameStartTime,Runnable newGameAction) {

        this.gameEngine = gameEngine;

        /*
         * מבנה המסך:
         *
         * טבלת שחורים | לוח | טבלת לבנים
         */
        setLayout(
                new BorderLayout(
                        15,
                        0
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        setBackground(
                new Color(
                        150,
                        150,
                        150
                )
        );

        Board board =
                gameEngine.getBoard();

        /*
         * יצירת טבלאות המהלכים.
         */
        blackMovesPanel =
                new MoveHistoryPanel(
                        "Black",
                        gameStartTime
                );

        whiteMovesPanel =
                new MoveHistoryPanel(
                        "White",
                        gameStartTime
                );

        /*
         * יצירת תוויות הניקוד.
         *
         * לשתי התוויות יש רקע לבן.
         */
        blackScoreLabel =
                createScoreLabel(
                        "Black score: 0"
                );

        whiteScoreLabel =
                createScoreLabel(
                        "White score: 0"
                );

        /*
         * BoardPanel מודיע על כל מהלך,
         * והמהלך נכנס לטבלה המתאימה.
         */
        MoveListener moveListener =
                (color, move) -> {

                    if (color == 'w') {

                        whiteMovesPanel.addMove(
                                move
                        );

                    } else if (color == 'b') {

                        blackMovesPanel.addMove(
                                move
                        );
                    }
                };

        /*
         * יצירת לוח המשחק.
         */
        boardPanel =
                new BoardPanel(
                        gameEngine,
                        imageManager,
                        moveListener,
                        newGameAction
                );

        /*
         * הוספת האותיות והמספרים מסביב ללוח.
         */
        boardWithCoordinatesPanel =
                new BoardWithCoordinatesPanel(
                        boardPanel,
                        board.getRows(),
                        board.getCols()
                );

        /*
         * צד שמאל:
         *
         * ניקוד שחור
         * טבלת שחורים
         */
        JPanel blackContainer =
                createSideContainer(
                        blackScoreLabel,
                        blackMovesPanel
                );

        /*
         * צד ימין:
         *
         * ניקוד לבן
         * טבלת לבנים
         */
        JPanel whiteContainer =
                createSideContainer(
                        whiteScoreLabel,
                        whiteMovesPanel
                );

        /*
         * מעטפת הלוח שומרת שהלוח
         * לא יימתח בתוך החלון.
         */
        JPanel boardContainer =
                new JPanel(
                        new GridBagLayout()
                );

        boardContainer.setOpaque(false);

        boardContainer.add(
                boardWithCoordinatesPanel
        );

        /*
         * הוספת החלקים למסך.
         */
        add(
                blackContainer,
                BorderLayout.WEST
        );

        add(
                boardContainer,
                BorderLayout.CENTER
        );

        add(
                whiteContainer,
                BorderLayout.EAST
        );

        refreshScores();
    }

    /**
     * יוצר תווית ניקוד לבנה.
     */
    private JLabel createScoreLabel(
            String text) {

        JLabel label =
                new JLabel(
                        text,
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        20
                )
        );

        label.setOpaque(true);

        /*
         * רקע לבן לשני הצדדים.
         */
        label.setBackground(
                Color.WHITE
        );

        label.setForeground(
                new Color(
                        35,
                        35,
                        35
                )
        );

        label.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.DARK_GRAY,
                                2
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                10,
                                9,
                                10
                        )
                )
        );

        return label;
    }

    /**
     * יוצר צד אחד של המסך:
     *
     * ניקוד למעלה
     * טבלת מהלכים מתחת
     */
    private JPanel createSideContainer(
            JLabel scoreLabel,
            MoveHistoryPanel moveHistoryPanel) {

        JPanel container =
                new JPanel(
                        new BorderLayout(
                                0,
                                8
                        )
                );

        container.setOpaque(false);

        container.add(
                scoreLabel,
                BorderLayout.NORTH
        );

        container.add(
                moveHistoryPanel,
                BorderLayout.CENTER
        );

        return container;
    }

    /**
     * מעדכן את הניקוד מעל שתי הטבלאות.
     */
    public void refreshScores() {

        blackScoreLabel.setText(
                "Black score: "
                        + gameEngine.getBlackScore()
        );

        whiteScoreLabel.setText(
                "White score: "
                        + gameEngine.getWhiteScore()
        );

        boardPanel.refreshGameState();
    }

    /**
     * הוספת מהלך לטבלה המתאימה.
     */
    public void addMove(
            char color,
            String move) {

        if (color == 'w') {

            whiteMovesPanel.addMove(
                    move
            );

        } else if (color == 'b') {

            blackMovesPanel.addMove(
                    move
            );
        }
    }

    /**
     * מחיקת כל היסטוריית המהלכים.
     */
    public void clearMoveHistory() {

        blackMovesPanel.clearMoves();
        whiteMovesPanel.clearMoves();
    }

    public BoardPanel getBoardPanel() {

        return boardPanel;
    }

    public MoveHistoryPanel getBlackMovesPanel() {

        return blackMovesPanel;
    }

    public MoveHistoryPanel getWhiteMovesPanel() {

        return whiteMovesPanel;
    }
}