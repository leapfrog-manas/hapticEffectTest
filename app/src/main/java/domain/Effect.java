package domain;

import com.immersion.uhl.MagSweepEffectDefinition;
import com.immersion.uhl.PeriodicEffectDefinition;
import com.immersion.uhl.WaveformEffectDefinition;

/**
 * Created by Eugen Oleynik oe@dalivsoft.com on 05.02.15.
 */
public class Effect {
    public int trackNum;
    public int time;
    public int effectId;
    public PeriodicEffectDefinition ped;
    public MagSweepEffectDefinition med;
    public WaveformEffectDefinition wed;

    public Effect(int trackNum, int time, int effectId) {
        this.trackNum = trackNum;
        this.time = time;
        this.effectId = effectId;
    }

    public Effect(int trackNum, int time, int effectId, PeriodicEffectDefinition ped) {
        this.trackNum = trackNum;
        this.time = time;
        this.effectId = effectId;
        this.ped = ped;
    }

    public Effect(int trackNum, int time, int effectId, MagSweepEffectDefinition med) {
        this.trackNum = trackNum;
        this.time = time;
        this.effectId = effectId;
        this.med = med;
    }

    public Effect(int trackNum, int time, int effectId, WaveformEffectDefinition wed) {
        this.trackNum = trackNum;
        this.time = time;
        this.effectId = effectId;
        this.wed = wed;
    }
}
