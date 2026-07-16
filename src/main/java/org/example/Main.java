package org.example;

import org.example.engine.GameEngine;
import org.example.model.Board;
import org.example.model.BoardInitializer;
import org.example.ui.GamePanel;
import org.example.ui.ImageManager;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;

public class Main {

    /*
     * שומרים את הטיימר של המשחק הנוכחי.
     *
     * כשמתחילים משחק חדש,
     * עוצרים קודם את הטיימר הישן.
     */
    private static Timer gameTimer;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            /*
             * יצירת החלון הראשי.
             */
            JFrame frame =
                    new JFrame(
                            "KFChess"
                    );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setResizable(false);

            /*
             * יצירת המשחק הראשון.
             */
            startNewGame(frame);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * יוצר משחק חדש לחלוטין.
     */
    private static void startNewGame(
            JFrame frame) {

        /*
         * עוצרים את הטיימר של המשחק הקודם,
         * כדי שלא ימשיכו לפעול שני משחקים במקביל.
         */
        if (gameTimer != null) {

            gameTimer.stop();
        }

        /*
         * יצירת לוח חדש עם כל הכלים
         * במיקומים ההתחלתיים.
         */
        Board board =
                BoardInitializer.createInitialBoard();

        /*
         * יצירת מנוע משחק חדש.
         *
         * בתוך GameEngine מתחילה
         * נעילה של שלוש שניות.
         */
        GameEngine gameEngine =
                new GameEngine(board);

        /*
         * טעינת תמונות הכלים.
         */
        ImageManager imageManager =
                new ImageManager(
                        70,
                        70
                );

        /*
         * זמן תחילת המשחק האמיתי,
         * לאחר שלוש שניות הנעילה.
         *
         * הזמן הזה משמש את טבלאות המהלכים.
         */
        long gameStartTime =
                gameEngine.getGameStartTimeMillis();

        /*
         * יצירת המסך המלא.
         *
         * הפרמטר האחרון הוא הפעולה
         * שתופעל בלחיצה על New Game.
         */
        GamePanel gamePanel =
                new GamePanel(
                        gameEngine,
                        imageManager,
                        gameStartTime,
                        () -> startNewGame(frame)
                );

        /*
         * הכנסת המסך החדש לחלון.
         */
        frame.setContentPane(
                gamePanel
        );

        /*
         * התאמת גודל החלון לרכיבים החדשים.
         */
        frame.pack();

        frame.setLocationRelativeTo(null);

        /*
         * שומר האם צליל הפתיחה כבר הושמע.
         *
         * מערך משמש כאן כדי שאפשר יהיה
         * לשנות את הערך מתוך ה-lambda.
         */
        final boolean[] startSoundPlayed =
                {false};

        /*
         * טיימר המשחק.
         *
         * בכל 40 מילישניות:
         * 1. מעדכנים את מנוע המשחק.
         * 2. בודקים אם זמן הפתיחה הסתיים.
         * 3. מעדכנים ניקוד ו-Game Over.
         * 4. מציירים מחדש.
         */
        gameTimer =
                new Timer(
                        40,
                        event -> {

                            gameEngine.tick();

                            /*
                             * משמיעים את הצליל פעם אחת,
                             * מיד לאחר סיום שלוש שניות הנעילה.
                             */
                            if (!startSoundPlayed[0]
                                    && !gameEngine.isGameStarting()) {

                                playEnergeticStartSound();

                                startSoundPlayed[0] =
                                        true;

                                System.out.println(
                                        "[GAME STARTED]"
                                );
                            }

                            /*
                             * refreshScores מעדכן גם
                             * את מצב ה-Game Over.
                             */
                            gamePanel.refreshScores();

                            gamePanel.repaint();
                        }
                );

        gameTimer.start();
    }

    /**
     * משמיע צליל פתיחה שנוצר מתוך הקוד.
     *
     * אין צורך בקובץ WAV חיצוני.
     */
    private static void playEnergeticStartSound() {

        Thread soundThread =
                new Thread(
                        () -> {

                            SourceDataLine line = null;

                            try {

                                float sampleRate =
                                        44_100f;

                                AudioFormat format =
                                        new AudioFormat(
                                                sampleRate,
                                                16,
                                                1,
                                                true,
                                                false
                                        );

                                line =
                                        AudioSystem.getSourceDataLine(
                                                format
                                        );

                                line.open(format);
                                line.start();

                                /*
                                 * שלוש פעימות עולות.
                                 */
                                playTone(
                                        line,
                                        sampleRate,
                                        350,
                                        100
                                );

                                Thread.sleep(40);

                                playTone(
                                        line,
                                        sampleRate,
                                        550,
                                        100
                                );

                                Thread.sleep(40);

                                playTone(
                                        line,
                                        sampleRate,
                                        900,
                                        220
                                );

                                line.drain();

                            } catch (Exception exception) {

                                System.out.println(
                                        "Could not play sound: "
                                                + exception.getMessage()
                                );

                            } finally {

                                if (line != null) {

                                    line.stop();
                                    line.close();
                                }
                            }
                        },
                        "KFChess-start-sound"
                );

        soundThread.setDaemon(true);
        soundThread.start();
    }

    /**
     * יוצר צליל בודד בתדר מסוים.
     */
    private static void playTone(
            SourceDataLine line,
            float sampleRate,
            double frequency,
            int durationMs) {

        int sampleCount =
                (int) (
                        sampleRate
                                * durationMs
                                / 1000.0
                );

        byte[] buffer =
                new byte[
                        sampleCount * 2
                        ];

        for (int index = 0;
             index < sampleCount;
             index++) {

            double time =
                    index / sampleRate;

            double wave =
                    Math.sin(
                            2.0
                                    * Math.PI
                                    * frequency
                                    * time
                    );

            /*
             * הנמכה קלה בתחילת ובסוף הצליל,
             * כדי למנוע רעש חד.
             */
            double progress =
                    index
                            / (double) sampleCount;

            double envelope =
                    Math.min(
                            1.0,
                            Math.min(
                                    progress * 20,
                                    (1.0 - progress) * 20
                            )
                    );

            short sample =
                    (short) (
                            wave
                                    * envelope
                                    * 0.7
                                    * Short.MAX_VALUE
                    );

            /*
             * שמירת דגימת 16 ביט
             * בשני בתים.
             */
            buffer[index * 2] =
                    (byte) (
                            sample & 0xff
                    );

            buffer[index * 2 + 1] =
                    (byte) (
                            (sample >> 8)
                                    & 0xff
                    );
        }

        line.write(
                buffer,
                0,
                buffer.length
        );
    }
}