package com.example.manas.haptictesttemp;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.immersion.uhl.IVTBuffer;
import com.immersion.uhl.IVTElement;
import com.immersion.uhl.IVTMagSweepElement;
import com.immersion.uhl.IVTPeriodicElement;
import com.immersion.uhl.IVTWaveformElement;
import com.immersion.uhl.ImmVibe;
import com.immersion.uhl.Launcher;
import com.immersion.uhl.MagSweepEffectDefinition;
import com.immersion.uhl.PeriodicEffectDefinition;
import com.immersion.uhl.WaveformEffectDefinition;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import domain.Effect;
import immersion.HapticContentSDK;
import immersion.HapticContentSDKFactory;
import utils.HapticsManager;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    Launcher launcher;
    Button play, pause, seek, resume;
    private HapticContentSDK mHaptics;
    private HapticsManager mHapticManager;
    private IVTBuffer mHapticBuffer;
    Uri mHapticUri;
    private Thread mSync;
    private EffectScheduler effectScheduler = new EffectScheduler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHaptics = HapticContentSDKFactory.GetNewSDKInstance(HapticContentSDK.SDKMODE_MEDIAPLAYBACK, this);
        mHapticManager = HapticsManager.getInstance(this);
        mHapticBuffer = mHapticManager.getCurrentIVTBuffer();

        mHapticUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.haptic_gallery_1);

        generateHapticBuffer();
        initializeButtons();
    }

    private void initializeButtons() {
        launcher = new Launcher(this);
        play = (Button) findViewById(R.id.btn_play);
        pause = (Button) findViewById(R.id.btn_pause);
        seek = (Button) findViewById(R.id.bnt_seek);
        resume = (Button) findViewById(R.id.bnt_resume);

        play.setOnClickListener(this);

        seek.setOnClickListener(this);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:

                mSync = new Thread(playingCheck);
                mSync.start();

                break;
            case R.id.btn_pause:
                launcher.play(Launcher.TRANSITION_BUMP_33);
                break;
            case R.id.bnt_seek:
                launcher.play(Launcher.TEXTURE8);
                break;
            case R.id.bnt_resume:
                launcher.play(Launcher.SLOW_PULSE_33);
                break;
        }

    }

    /**
     * initialize buffer.
     */
    public void generateHapticBuffer() {

        if (mHapticUri == null) {
            return;
        }

        byte[] myBuffer = new byte[1024];
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        try {
            InputStream is = getContentResolver().openInputStream(mHapticUri);//new BufferedInputStream(new FileInputStream(mHapticUri));
            int iLen = is.available();

            while (iLen > 0) {
                iLen = is.read(myBuffer);
                byteOS.write(myBuffer, 0, iLen);
                iLen = is.available();
            }
            mHapticBuffer = new IVTBuffer(byteOS.toByteArray());
        } catch (Exception e) {
            Log.e("Error buffer", "Error buffer");
            e.printStackTrace();
        }
    }

    protected Runnable playingCheck = new Runnable() {
        public void run() {
            playHaptic(mHapticBuffer);
        }
    };

    protected void playHaptic(IVTBuffer ivt) {
        try {
            stopHaptic();
            loadHaptics();

            Log.w("tag", "playIVT " + ivt.getEffectName(0));
        } catch (Exception e) {
            Log.w("error tag", "playIVT error " + e.getMessage());
        }
    }

    protected void stopHaptic() {
        if (mHapticManager != null)
            mHapticManager.stop();
    }

    private void loadHaptics() {
        loadEffects("", 0);
    }

    private void loadEffects(String timelineName, int track) {

        int timelineIndex = 0;//mHapticBuffer.getEffectIndexFromName(timelineName);

        int i = 0;

        while (true) {

            IVTElement elem = mHapticBuffer.readElement(timelineIndex, i);
            int time = elem.getTime();
            switch (elem.getType()) {
                case ImmVibe.VIBE_ELEMTYPE_PERIODIC: {
                    PeriodicEffectDefinition ped = ((IVTPeriodicElement) (elem)).getDefinition();
                    effectScheduler.addEffect(new Effect(track, time, 0, ped));
                }
                break;
                case ImmVibe.VIBE_ELEMTYPE_MAGSWEEP: {
                    MagSweepEffectDefinition ped = ((IVTMagSweepElement) (elem)).getDefinition();
                    effectScheduler.addEffect(new Effect(track, time, 0, ped));
                }
                break;
                case ImmVibe.VIBE_ELEMTYPE_WAVEFORM: {
                    WaveformEffectDefinition ped = ((IVTWaveformElement) (elem)).getDefinition();
                    effectScheduler.addEffect(new Effect(track, time, 0, ped));
                }
                break;
                case ImmVibe.VIBE_ELEMTYPE_REPEAT:
                    Log.i("asd", "Repeat events not supported by this scheduler");
                    break;
            }
            ++i;
        }
    }

    private class EffectScheduler {
        private LinkedList<Effect> effects = new LinkedList<Effect>();
        private long startStamp = 0;
        private long syncStamp = 0;
        private long syncOffsetMillis = 0;
        private boolean isPlaying = false;
        private Timer timer;

        public void clearEffects() {
            effects.clear();

            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }

        public void addEffect(Effect e) {
            effects.add(e);

            if (isPlaying && timer != null) {

                // Only schedule if it is in the future...
                long target = syncStamp - startStamp + e.time;
                if (target > (System.currentTimeMillis() + syncOffsetMillis)) {
                    timer.schedule(new EffectPlayTask(e), target - (System.currentTimeMillis() + syncOffsetMillis));
                }
            }
        }


        private class EffectPlayTask extends TimerTask {

            private Effect effect;

            public EffectPlayTask(Effect e) {
                super();
                effect = e;
            }

            public void run() {
                if (!mHapticManager.isMute()) {
                    if (effect.ped != null) {
                        mHapticManager.playPeriodicEffect(effect.ped);
                    } else if (effect.med != null) {
                        mHapticManager.playMagSweepEffect(effect.med);
                    } else if (effect.wed != null) {
                        mHapticManager.playWaveformEffect(effect.wed);
                    } else {
                        mHapticManager.playFromIVT(effect.effectId, Launcher.BOUNCE_100);
                    }
                }
            }
        }
    }
}
