package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class BoardWithCoordinatesPanel extends JPanel {

    /*
     * הרוחב והגובה של אזור האותיות והמספרים.
     */
    private static final int COORDINATE_SIZE = 30;

    private final BoardPanel boardPanel;
    private final int rows;
    private final int cols;

    public BoardWithCoordinatesPanel(
            BoardPanel boardPanel,
            int rows,
            int cols) {

        this.boardPanel = boardPanel;
        this.rows = rows;
        this.cols = cols;

        /*
         * GridBagLayout מאפשר להציב:
         *
         * אותיות למעלה
         * מספרים משמאל
         * הלוח במרכז
         * מספרים מימין
         * אותיות למטה
         */
        setLayout(new GridBagLayout());

        GridBagConstraints constraints =
                new GridBagConstraints();

        /*
         * =========================
         * השורה העליונה
         * =========================
         */

        // פינה שמאלית עליונה
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;

        add(
                createCornerPanel(),
                constraints
        );

        // אותיות מעל הלוח
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        add(
                createColumnCoordinates(),
                constraints
        );

        // פינה ימנית עליונה
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;

        add(
                createCornerPanel(),
                constraints
        );

        /*
         * =========================
         * השורה האמצעית
         * =========================
         */

        // מספרים בצד שמאל
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.VERTICAL;

        add(
                createRowCoordinates(),
                constraints
        );

        // הלוח עצמו
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;

        add(
                boardPanel,
                constraints
        );

        // מספרים בצד ימין
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.VERTICAL;

        add(
                createRowCoordinates(),
                constraints
        );

        /*
         * =========================
         * השורה התחתונה
         * =========================
         */

        // פינה שמאלית תחתונה
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;

        add(
                createCornerPanel(),
                constraints
        );

        // אותיות מתחת ללוח
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        add(
                createColumnCoordinates(),
                constraints
        );

        // פינה ימנית תחתונה
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.NONE;

        add(
                createCornerPanel(),
                constraints
        );
    }

    /**
     * יוצר את שורת האותיות:
     *
     * a b c d e f g h
     */
    private JPanel createColumnCoordinates() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                cols
                        )
                );

        panel.setPreferredSize(
                new Dimension(
                        boardPanel
                                .getPreferredSize()
                                .width,
                        COORDINATE_SIZE
                )
        );

        for (int col = 0;
             col < cols;
             col++) {

            char columnLetter =
                    (char) ('a' + col);

            JLabel label =
                    createCoordinateLabel(
                            String.valueOf(
                                    columnLetter
                            )
                    );

            panel.add(label);
        }

        return panel;
    }

    /**
     * יוצר את עמודת המספרים:
     *
     * 8
     * 7
     * 6
     * 5
     * 4
     * 3
     * 2
     * 1
     */
    private JPanel createRowCoordinates() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                rows,
                                1
                        )
                );

        panel.setPreferredSize(
                new Dimension(
                        COORDINATE_SIZE,
                        boardPanel
                                .getPreferredSize()
                                .height
                )
        );

        for (int row = 0;
             row < rows;
             row++) {

            int rowNumber =
                    rows - row;

            JLabel label =
                    createCoordinateLabel(
                            String.valueOf(
                                    rowNumber
                            )
                    );

            panel.add(label);
        }

        return panel;
    }

    /**
     * יוצר תווית אחת של אות או מספר.
     */
    private JLabel createCoordinateLabel(
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
                        17
                )
        );

        label.setOpaque(true);

        label.setBackground(
                new Color(
                        235,
                        235,
                        235
                )
        );

        label.setForeground(
                new Color(
                        40,
                        40,
                        40
                )
        );

        return label;
    }

    /**
     * יוצר ריבוע ריק בפינות.
     *
     * הוא שומר שהאותיות יהיו בדיוק
     * מעל המשבצות ולא מעל המספרים.
     */
    private JPanel createCornerPanel() {

        JPanel cornerPanel =
                new JPanel();

        cornerPanel.setPreferredSize(
                new Dimension(
                        COORDINATE_SIZE,
                        COORDINATE_SIZE
                )
        );

        cornerPanel.setBackground(
                new Color(
                        235,
                        235,
                        235
                )
        );

        return cornerPanel;
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}