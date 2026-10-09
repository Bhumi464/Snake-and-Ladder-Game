
package com.snakeandladder;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundManager {

    private boolean enabled = true;

    private static final float SAMPLE_RATE = 44100f;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Dice rolling sound
    public void playDiceRoll() {
        playTones(
                new double[]{900, 650, 1000, 750, 1100},
                55
        );
    }

    // Ladder climbing sound
    public void playLadder() {
        playTones(
                new double[]{500, 650, 800, 1000, 1250},
                110
        );
    }

    // Snake sliding sound
    public void playSnake() {
        playTones(
                new double[]{1000, 800, 650, 450, 300},
                120
        );
    }

    // Winner celebration sound
    public void playWin() {
        playTones(
                new double[]{
                    523, 659, 784, 1047, 784, 1047, 1319
                },
                180
        );
    }

    private void playTones(
            double[] frequencies,
            int toneDurationMs
    ) {

        if (!enabled) {
            return;
        }

        // Play audio on a separate thread
        // so game animations remain responsive.
        Thread soundThread = new Thread(() -> {

            Clip clip = null;

            try {

                int totalSamples =
                        (int) (
                                SAMPLE_RATE
                                * toneDurationMs
                                * frequencies.length
                                / 1000.0
                        );

                byte[] audioData =
                        new byte[totalSamples * 2];

                int sampleIndex = 0;

                for (double frequency : frequencies) {

                    int samplesPerTone =
                            (int) (
                                    SAMPLE_RATE
                                    * toneDurationMs
                                    / 1000.0
                            );

                    for (int i = 0;
                         i < samplesPerTone
                         && sampleIndex < totalSamples;
                         i++) {

                        double time =
                                (double) i / SAMPLE_RATE;

                        double envelope =
                                Math.min(
                                        1.0,
                                        Math.min(
                                                i / 150.0,
                                                (samplesPerTone - i) / 150.0
                                        )
                                );

                        double wave =
                                Math.sin(
                                        2.0 * Math.PI
                                        * frequency
                                        * time
                                );

                        short sample =
                                (short) (
                                        wave
                                        * envelope
                                        * 10000
                                );

                        audioData[sampleIndex * 2] =
                                (byte) (sample & 0xff);

                        audioData[sampleIndex * 2 + 1] =
                                (byte) (
                                        (sample >> 8) & 0xff
                                );

                        sampleIndex++;
                    }
                }

                AudioFormat format =
                        new AudioFormat(
                                SAMPLE_RATE,
                                16,
                                1,
                                true,
                                false
                        );

                try (
                    ByteArrayInputStream byteStream =
                            new ByteArrayInputStream(audioData);

                    AudioInputStream audioStream =
                            new AudioInputStream(
                                    byteStream,
                                    format,
                                    totalSamples
                            )
                ) {

                    clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    Clip playingClip = clip;

                    LineListener listener = event -> {
                        if (event.getType()
                                == LineEvent.Type.STOP) {

                            playingClip.close();
                        }
                    };

                    clip.addLineListener(listener);
                    clip.start();
                }

            } catch (Exception exception) {

                System.err.println(
                        "Unable to play sound: "
                        + exception.getMessage()
                );

                if (clip != null && clip.isOpen()) {
                    clip.close();
                }
            }

        });

        soundThread.setDaemon(true);
        soundThread.start();
    }
}
