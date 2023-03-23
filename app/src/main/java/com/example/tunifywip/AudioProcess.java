package com.example.tunifywip;

import android.graphics.Color;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

import java.text.DecimalFormat;

public class AudioProcess extends AppCompatActivity {

    private final double minC = 241.63;
    private final double maxC = 281.63;
    private final double minE = 309.63;
    private final double maxE = 349.63;
    private final double minG = 372;
    private final double maxG = 412;
    private final double minA = 420;
    private final double maxA = 460;
    private final double mindiff = 19;
    private final double maxdiff = 21;

     //Set Sampling Rate, Sample Buffer (2^10), Buffer Overlap
    private PitchDetectionHandler pdh;
    private double diff;
    private int diffPercent;
    private DecimalFormat dec = new DecimalFormat("#0.0");

    public void InitPitchHandler(TextView freqText, TextView toneText, SemiCircleArcProgressBar progress) {
        pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitch = result.getPitch(); //Pitch in Hertz
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pitch >= minC && pitch <= maxC)
                            HandleTone(freqText, toneText, progress, "C", pitch, maxC, minC);
                        else if (pitch >= minE && pitch <= maxE)
                            HandleTone(freqText, toneText, progress, "E", pitch, maxE, minE);
                        else if (pitch >= minG && pitch <= maxG)
                            HandleTone(freqText, toneText, progress, "G", pitch, maxG, minG);
                        else if (pitch >= minA && pitch <= maxA)
                            HandleTone(freqText, toneText, progress, "A", pitch, maxA, minA);
                        else {
                            freqText.setText("");
                            toneText.setText("");
                            progress.setPercent(0);
                        }
                    }
                });
            }
        };
    }

    public void InitPitchDetector()
    {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    private void HandleTone(TextView freqText, TextView toneText, SemiCircleArcProgressBar progress, String tone, double pitch, double maxHz, double minHz) {
        diff = (pitch - minHz);
        diffPercent = (int) (diff / (maxHz - minHz) * 100);

        if (pitch >= minHz && pitch <= maxHz) {
            if (diff >= mindiff && diff <= maxdiff) {
                freqText.setTextColor(Color.GREEN);
                toneText.setTextColor(Color.GREEN);
            }
            else {
                freqText.setTextColor(Color.parseColor("#9C9C9C"));
                toneText.setTextColor(Color.parseColor("#9C9C9C"));
            }

            freqText.setText(dec.format(pitch) + " Hz");
            toneText.setText(tone);
            progress.setPercent(diffPercent);
        }
    }
}
