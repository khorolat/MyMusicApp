package buildguide.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;
    int positionPrev;
    int positionNext;

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final ImageButton play = (ImageButton) findViewById(R.id.play);
        play.setVisibility(View.INVISIBLE);
        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        pause.setVisibility(View.INVISIBLE);

        final ImageButton skip= (ImageButton) findViewById(R.id.skip);
        skip.setVisibility(View.INVISIBLE);
        final ImageButton previous = (ImageButton) findViewById(R.id.previous);
        previous.setVisibility(View.INVISIBLE);


        final ArrayList<word> words = new ArrayList<word>();
        words.add(new word("Track one", "cansa_copingkit1", R.drawable.sq, R.raw.cansa_copingkit1));
        words.add(new word("Track two", "cansa_copingkit2", R.drawable.sq, R.raw.cansa_copingkit2));
        words.add(new word("Track three", "cansa_copingkit3", R.drawable.sq, R.raw.cansa_copingkit3));
        words.add(new word("Track four", "cansa_copingkit4", R.drawable.sq, R.raw.cansa_copingkit4));
        words.add(new word("Track five", "cansa_copingkit5", R.drawable.sq, R.raw.cansa_copingkit5));
        words.add(new word("Track six", "cansa_copingkit6", R.drawable.sq, R.raw.cansa_copingkit6));
        words.add(new word("Track seven", "cansa_copingkit7", R.drawable.sq, R.raw.cansa_copingkit7));


        WordAdapter adapter = new WordAdapter(this, words, R.color.colorAccent);

        ListView listView = (ListView) findViewById(R.id.list);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                releaseMediaPlayer();

//                Intent i= new Intent(MainActivity.this,PlaySingle.class);
//                startActivity(i);


                word word = words.get(position);


                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {


                    mMediaPlayer = MediaPlayer.create(MainActivity.this, word.getAudioResourceId());

                    mMediaPlayer.start();
                    play.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    previous.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.VISIBLE);

                    mMediaPlayer.setOnCompletionListener(mCompletionListener);



                }
        }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }

                word word = words.get(positionPrev--);
                mMediaPlayer =mMediaPlayer.create(MainActivity.this, word.getAudioResourceId());


                mMediaPlayer.start();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }

                word word = words.get(positionNext++);
                mMediaPlayer =mMediaPlayer.create(MainActivity.this, word.getAudioResourceId());


                mMediaPlayer.start();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        releaseMediaPlayer();
    }


    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {

            mMediaPlayer.isLooping();

            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}