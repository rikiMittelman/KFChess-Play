package org.example.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable movesTable;
    private final long gameStartTime;

    public MoveHistoryPanel(
            String title,
            long gameStartTime) {

        this.gameStartTime =
                gameStartTime;

        setLayout(
                new BorderLayout(
                        0,
                        8
                )
        );

        setPreferredSize(
                new Dimension(
                        210,
                        620
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        8,
                        10,
                        8
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title,
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        18
                )
        );

        titleLabel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.GRAY
                        ),
                        BorderFactory.createEmptyBorder(
                                6,
                                5,
                                6,
                                5
                        )
                )
        );

        add(
                titleLabel,
                BorderLayout.NORTH
        );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "Time",
                                "Move"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        movesTable =
                new JTable(
                        tableModel
                );

        movesTable.setRowHeight(25);

        movesTable.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        14
                )
        );

        movesTable
                .getTableHeader()
                .setFont(
                        new Font(
                                Font.SANS_SERIF,
                                Font.BOLD,
                                14
                        )
                );

        movesTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        movesTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        movesTable
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        movesTable
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(centerRenderer);

        movesTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(85);

        movesTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        JScrollPane scrollPane =
                new JScrollPane(
                        movesTable
                );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    /**
     * מוסיף מהלך עם הזמן שעבר מתחילת המשחק.
     */
    public void addMove(String move) {

        long elapsedTime =
                System.currentTimeMillis()
                        - gameStartTime;

        tableModel.addRow(
                new Object[]{
                        formatTime(elapsedTime),
                        move
                }
        );

        scrollToLastRow();
    }

    /**
     * מוסיף מהלך עם זמן נתון.
     */
    public void addMove(
            String time,
            String move) {

        tableModel.addRow(
                new Object[]{
                        time,
                        move
                }
        );

        scrollToLastRow();
    }

    private void scrollToLastRow() {

        int lastRow =
                tableModel.getRowCount() - 1;

        if (lastRow >= 0) {

            movesTable.scrollRectToVisible(
                    movesTable.getCellRect(
                            lastRow,
                            0,
                            true
                    )
            );
        }
    }

    public void clearMoves() {

        tableModel.setRowCount(0);
    }

    private String formatTime(
            long elapsedMilliseconds) {

        long minutes =
                elapsedMilliseconds / 60_000;

        long seconds =
                (elapsedMilliseconds % 60_000)
                        / 1_000;

        long milliseconds =
                elapsedMilliseconds % 1_000;

        return String.format(
                "%02d:%02d.%03d",
                minutes,
                seconds,
                milliseconds
        );
    }
}