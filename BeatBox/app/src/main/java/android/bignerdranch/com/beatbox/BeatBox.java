package android.bignerdranch.com.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private List<Sound> mSounds = new ArrayList<>();
    private AssetManager mAssets;
    private SoundPool mSoundPool;
    private float mRate = 1.0f;

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS,
                AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }


    private void loadSounds() {
        String[] soundNames;

        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
        } catch (IOException ioe) {
            return;
        }

        for (String fileName : soundNames) {
            try {
                String assetPath = SOUNDS_FOLDER + "/" + fileName;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "Coulnd't load sounds: BeatBox.java ->" + fileName, ioe);
            }

        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);


    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();

        if (soundId == null) {
            return;
        }

        mSoundPool.play(soundId, 1.0f,
                1.0f, 1, 0, mRate);
    }

    public void release() {
        mSoundPool.release();
    }

    public void setRate(float rate) {
        mRate = rate;


    }

}
