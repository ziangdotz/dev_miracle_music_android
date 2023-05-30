package cn.miracle.dev_miracle_music_android.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.util.ArrayList;

import cn.miracle.dev_miracle_music_android.R;
import cn.miracle.dev_miracle_music_android.data.model.AudioLocalModel;
import cn.miracle.dev_miracle_music_android.utils.AudioLocalUtils;
import cn.miracle.dev_miracle_music_android.utils.CommonUtils;
import cn.miracle.dev_miracle_music_android.view.AudioRoundProgressView;
import cn.miracle.dev_miracle_music_android.view.IconFontTextView;

public class AudioLocalActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private TextView labelAudioLocalTextView;

    /* loading view */
    private RelativeLayout loadingRelativeLayout;
    private LottieAnimationView loadingLottieAnimationView;

    /* no view */
    private RelativeLayout noRelativeLayout;
    private LottieAnimationView noLottieAnimationView;
    private RelativeLayout retryRelativeLayout;
    private TextView retryTextView;

    /* yes view */
    private RelativeLayout yesRelativeLayout;
    private RelativeLayout positionRelativeLayout;
    private IconFontTextView positionIconFontTextView;
    private RelativeLayout inPlayingRelativeLayout;
    private RelativeLayout infoInPlayingRelativeLayout;
    private RelativeLayout albumCoverInPlayingRelativeLayout;
    private AudioRoundProgressView progressInPlayingAudioRoundProgressView;
    private ImageView albumCoverInPlayingImageView;
    private TextView displayNameInPlayingTextView;
    private IconFontTextView prevInPlayingIconFontTextView;
    private IconFontTextView playOrPauseInPlayingIconFontTextView;
    private IconFontTextView nextInPlayingIconFontTextView;
    private SwipeRefreshLayout audioLocalSwipeRefreshLayout;
    private RecyclerView audioLocalRecyclerView;

    /* object and data */
    private MediaPlayer mMediaPlayer = null;

    private ObjectAnimator mAlbumCoverInPlayingObjectAnimator = null;
    private LinearLayoutManager mAudioLocalRecyclerViewLinearLayoutManager = null;
    private AudioLocalRecyclerViewAdapter mAudioLocalRecyclerViewAdapter = null;

    private static ArrayList<AudioLocalModel> mAudioLocalModelArrayList = new ArrayList<>();
    private AudioLocalModel currAudioLocalModelInPlaying = null;
    private int currPositionInPlaying = -1;
    private boolean isAudioLocalModelInPlaying = false;

    public AudioLocalActivity() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_local);

        bindView();
        initBasicView();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAudioLocalHandler.removeCallbacksAndMessages(null);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_layout_retry:
                GradientDrawable gradientDrawableRetryRelativeLayout = (GradientDrawable) retryRelativeLayout.getBackground();
                gradientDrawableRetryRelativeLayout.setColor(getResources().getColor(R.color.pinkFFEAED));

                mAudioLocalHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gradientDrawableRetryRelativeLayout.setColor(Color.TRANSPARENT);
                        recreate();
                    }
                }, 50);

                break;
            case R.id.relative_layout_position:
                mAudioLocalRecyclerViewLinearLayoutManager.scrollToPositionWithOffset(currPositionInPlaying, 0);

                break;
            case R.id.relative_layout_in_playing:
                break;
            case R.id.relative_layout_info_in_playing:
                break;
            case R.id.textview_prev_in_playing:
                if (currPositionInPlaying != -1) {
                    if (currPositionInPlaying == 0) {
                        currPositionInPlaying = mAudioLocalModelArrayList.size() - 1;
                    } else {
                        currPositionInPlaying--;
                    }
                    changeAudio(0);
                }

                break;
            case R.id.textview_play_or_pause_in_playing:
                if (currPositionInPlaying != -1) {
                    if (isAudioLocalModelInPlaying) {
                        isAudioLocalModelInPlaying = false;
                        playOrPauseInPlayingIconFontTextView.setText(Html.fromHtml("&#xe65e;"));
                        mMediaPlayer.pause();
                        mAlbumCoverInPlayingObjectAnimator.pause();
                    } else {
                        isAudioLocalModelInPlaying = true;
                        playOrPauseInPlayingIconFontTextView.setText(Html.fromHtml("&#xe65d;"));
                        mMediaPlayer.start();
                        mAlbumCoverInPlayingObjectAnimator.resume();
                    }

                    mAudioLocalRecyclerViewAdapter.notifyDataSetChanged();
                }

                break;
            case R.id.textview_next_in_playing:
                if (currPositionInPlaying != -1) {
                    if (currPositionInPlaying == mAudioLocalModelArrayList.size() - 1) {
                        currPositionInPlaying = 0;
                    } else {
                        currPositionInPlaying++;
                    }

                    changeAudio(0);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mAlbumCoverInPlayingObjectAnimator.end();

        if (currPositionInPlaying == mAudioLocalModelArrayList.size() - 1) {
            currPositionInPlaying = 0;
        } else {
            currPositionInPlaying++;
        }

        changeAudio(0);
    }

    public void bindView() {
        labelAudioLocalTextView = findViewById(R.id.textview_label_audio_local);

        loadingRelativeLayout = findViewById(R.id.relative_layout_middle_audio_local_loading);
        loadingLottieAnimationView = findViewById(R.id.lottie_animation_view_loading);

        noRelativeLayout = findViewById(R.id.relative_layout_middle_audio_local_no);
        noLottieAnimationView = findViewById(R.id.lottie_animation_view_no);
        retryRelativeLayout = findViewById(R.id.relative_layout_retry);
        retryTextView = findViewById(R.id.textview_retry);

        yesRelativeLayout = findViewById(R.id.relative_layout_middle_audio_local_yes);
        positionRelativeLayout = findViewById(R.id.relative_layout_position);
        positionIconFontTextView = findViewById(R.id.textview_position);
        inPlayingRelativeLayout = findViewById(R.id.relative_layout_in_playing);
        infoInPlayingRelativeLayout = findViewById(R.id.relative_layout_info_in_playing);
        albumCoverInPlayingRelativeLayout = findViewById(R.id.relative_layout_album_cover_in_playing);
        progressInPlayingAudioRoundProgressView = findViewById(R.id.view_progress_in_playing);
        albumCoverInPlayingImageView = findViewById(R.id.imageview_album_cover_in_playing);
        displayNameInPlayingTextView = findViewById(R.id.textview_display_name_in_playing);
        prevInPlayingIconFontTextView = findViewById(R.id.textview_prev_in_playing);
        playOrPauseInPlayingIconFontTextView = findViewById(R.id.textview_play_or_pause_in_playing);
        nextInPlayingIconFontTextView = findViewById(R.id.textview_next_in_playing);
        audioLocalSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_audio_local);
        audioLocalRecyclerView = findViewById(R.id.recyclerview_audio_local);
    }

    public void initBasicView() {
        labelAudioLocalTextView.setTextSize(18);
        labelAudioLocalTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        labelAudioLocalTextView.setTypeface(Typeface.DEFAULT);
        labelAudioLocalTextView.setText(R.string.audio_local_label);
    }

    public void initData() {
        initLoadingView();
        mAudioLocalModelArrayList = AudioLocalUtils.getLocalAudioInfo(AudioLocalActivity.this);

        mAudioLocalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingLottieAnimationView.cancelAnimation();
            }
        }, 500);
    }

    public void initLoadingView() {
        loadingRelativeLayout.setVisibility(View.VISIBLE);
        noRelativeLayout.setVisibility(View.INVISIBLE);
        yesRelativeLayout.setVisibility(View.INVISIBLE);

        loadingLottieAnimationView.setAnimation("json/loading.json");
        loadingLottieAnimationView.setSpeed(1);
        loadingLottieAnimationView.playAnimation();
        loadingLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mAudioLocalModelArrayList.size() > 0) {
                    initYesView();
                } else {
                    initNoView();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void initNoView() {
        noRelativeLayout.setVisibility(View.VISIBLE);
        loadingRelativeLayout.setVisibility(View.INVISIBLE);
        yesRelativeLayout.setVisibility(View.INVISIBLE);

        noLottieAnimationView.setAnimation("json/no.json");
        noLottieAnimationView.setSpeed(3);
        noLottieAnimationView.playAnimation();
        noLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                retryRelativeLayout.setOnClickListener(AudioLocalActivity.this);
                GradientDrawable gradientDrawableRetryRelativeLayout = (GradientDrawable) retryRelativeLayout.getBackground();
                gradientDrawableRetryRelativeLayout.setColor(ContextCompat.getColor(AudioLocalActivity.this, R.color.pinkFF708F));

                retryTextView.setTextSize(16);
                retryTextView.setTextColor(ContextCompat.getColor(AudioLocalActivity.this, R.color.white));
                retryTextView.setTypeface(Typeface.DEFAULT);
                retryTextView.setMaxLines(1);
                retryTextView.setText(R.string.retry_label);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void initYesView() {
        yesRelativeLayout.setVisibility(View.VISIBLE);
        loadingRelativeLayout.setVisibility(View.INVISIBLE);
        noRelativeLayout.setVisibility(View.INVISIBLE);

        positionRelativeLayout.setOnClickListener(this);
        GradientDrawable gradientDrawablePositionRelativeLayout = (GradientDrawable) positionRelativeLayout.getBackground();
        gradientDrawablePositionRelativeLayout.setColor(ContextCompat.getColor(this, R.color.grayF3F3F3_70));

        positionIconFontTextView.setTextSize(20);
        positionIconFontTextView.setTextColor(ContextCompat.getColor(this, R.color.pinkFF708F_70));
        positionIconFontTextView.setText(Html.fromHtml("&#xe67c;"));

        inPlayingRelativeLayout.setOnClickListener(this);
        GradientDrawable gradientDrawableInPlayingRelativeLayout = (GradientDrawable) inPlayingRelativeLayout.getBackground();
        gradientDrawableInPlayingRelativeLayout.setColor(ContextCompat.getColor(this, R.color.grayF3F3F3));

        infoInPlayingRelativeLayout.setOnClickListener(this);
        GradientDrawable gradientDrawableInfoInPlayingRelativeLayout = (GradientDrawable) infoInPlayingRelativeLayout.getBackground();
        gradientDrawableInfoInPlayingRelativeLayout.setColor(Color.TRANSPARENT);

        GradientDrawable gradientDrawableAlbumCoverInPlayingRelativeLayout = (GradientDrawable) albumCoverInPlayingRelativeLayout.getBackground();
        gradientDrawableAlbumCoverInPlayingRelativeLayout.setColor(ContextCompat.getColor(this, R.color.gray808080_10));

        albumCoverInPlayingImageView.setImageResource(R.drawable.png_default_cover_audio);
        mAlbumCoverInPlayingObjectAnimator = ObjectAnimator.ofFloat(albumCoverInPlayingImageView, "rotation", 0.0f, 360.0f);
        mAlbumCoverInPlayingObjectAnimator.setDuration(6000);
        mAlbumCoverInPlayingObjectAnimator.setInterpolator(new LinearInterpolator());
        mAlbumCoverInPlayingObjectAnimator.setRepeatCount(-1);
        mAlbumCoverInPlayingObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);

        displayNameInPlayingTextView.setTextSize(15);
        displayNameInPlayingTextView.setTextColor(ContextCompat.getColor(this, R.color.black_70));
        displayNameInPlayingTextView.setTypeface(Typeface.DEFAULT);
        displayNameInPlayingTextView.setMaxLines(1);
        displayNameInPlayingTextView.setMaxEms(12);
        displayNameInPlayingTextView.setEllipsize(TextUtils.TruncateAt.END);
        displayNameInPlayingTextView.setText(R.string.audio_in_playing_show_label);

        prevInPlayingIconFontTextView.setOnClickListener(this);
        prevInPlayingIconFontTextView.setTextSize(23);
        prevInPlayingIconFontTextView.setTextColor(ContextCompat.getColor(this, R.color.pinkFF708F));
        prevInPlayingIconFontTextView.setText(Html.fromHtml("&#xe62f;"));

        playOrPauseInPlayingIconFontTextView.setOnClickListener(this);
        playOrPauseInPlayingIconFontTextView.setTextSize(23);
        playOrPauseInPlayingIconFontTextView.setTextColor(ContextCompat.getColor(this, R.color.pinkFF708F));
        playOrPauseInPlayingIconFontTextView.setText(Html.fromHtml("&#xe65e;"));

        nextInPlayingIconFontTextView.setOnClickListener(this);
        nextInPlayingIconFontTextView.setTextSize(23);
        nextInPlayingIconFontTextView.setTextColor(ContextCompat.getColor(this, R.color.pinkFF708F));
        nextInPlayingIconFontTextView.setText(Html.fromHtml("&#xe635;"));

        audioLocalSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.pinkFF708F));
        audioLocalSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                changeDataAndViewWhenRefreshing();

                mAudioLocalHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioLocalSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });

        mAudioLocalRecyclerViewLinearLayoutManager = new LinearLayoutManager(this);
        mAudioLocalRecyclerViewLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioLocalRecyclerView.setLayoutManager(mAudioLocalRecyclerViewLinearLayoutManager);
        mAudioLocalRecyclerViewAdapter = new AudioLocalRecyclerViewAdapter();
        audioLocalRecyclerView.setAdapter(mAudioLocalRecyclerViewAdapter);
    }

    public Handler mAudioLocalHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            progressInPlayingAudioRoundProgressView.setAudioProgress(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
            updateAudioProgress();
            return true;
        }
    });

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void changeAudio(int currProgress) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
        }

        try {
            isAudioLocalModelInPlaying = true;
            currAudioLocalModelInPlaying = mAudioLocalModelArrayList.get(currPositionInPlaying);

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(currAudioLocalModelInPlaying.getAudioData());

            albumCoverInPlayingImageView.setImageBitmap(AudioLocalUtils.getLocalAudioAlbumCover(this, currAudioLocalModelInPlaying.getAudioData()));
            displayNameInPlayingTextView.setText(currAudioLocalModelInPlaying.getAudioDisplayName());

            playOrPauseInPlayingIconFontTextView.setText(Html.fromHtml("&#xe65e;"));

            mAudioLocalHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playOrPauseInPlayingIconFontTextView.setText(Html.fromHtml("&#xe65d;"));
                }
            }, 234);

            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.seekTo(currProgress);
            mMediaPlayer.start();

            progressInPlayingAudioRoundProgressView.setAudioProgress(currProgress, mMediaPlayer.getDuration());
            updateAudioProgress();

            mAlbumCoverInPlayingObjectAnimator.start();
            mAudioLocalRecyclerViewAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateAudioProgress() {
        Message emptyMessage = Message.obtain();
        emptyMessage.arg1 = mMediaPlayer.getCurrentPosition();
        mAudioLocalHandler.sendMessageDelayed(emptyMessage, 1000);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeDataAndViewWhenRefreshing() {
        mAudioLocalModelArrayList = AudioLocalUtils.getLocalAudioInfo(AudioLocalActivity.this);

        if (mAudioLocalModelArrayList.size() == 0) {
            progressInPlayingAudioRoundProgressView.setAudioProgress(0, 0);
            mAlbumCoverInPlayingObjectAnimator.end();

            mAudioLocalHandler.removeCallbacksAndMessages(null);
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            currPositionInPlaying = -1;
            initNoView();
        } else {
            if (currAudioLocalModelInPlaying != null) {
                int position = 0;

                for (AudioLocalModel audioLocalModel : mAudioLocalModelArrayList) {
                    if (audioLocalModel.getAudioId().equals(currAudioLocalModelInPlaying.getAudioId())) {
                        currPositionInPlaying = mAudioLocalModelArrayList.indexOf(audioLocalModel);
                        changeAudio(mMediaPlayer.getCurrentPosition());

                        break;
                    } else {
                        position++;
                    }
                }

                if (position == mAudioLocalModelArrayList.size()) {
                    progressInPlayingAudioRoundProgressView.setAudioProgress(0, 0);
                    mAlbumCoverInPlayingObjectAnimator.end();

                    mAudioLocalHandler.removeCallbacksAndMessages(null);
                    if (mMediaPlayer != null) {
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }

                    currPositionInPlaying = -1;
                    initYesView();
                }
            }
        }
    }

    public class AudioLocalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_audio, parent, false);
             return new AudioLocalRecyclerViewItemViewHolder(parent, view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AudioLocalRecyclerViewItemViewHolder audioLocalRecyclerViewItemViewHolder = (AudioLocalRecyclerViewItemViewHolder) holder;

            AudioLocalModel audioLocalModel = mAudioLocalModelArrayList.get(position);

            if (currPositionInPlaying == position) {
                audioLocalRecyclerViewItemViewHolder.numberItemTextView.setVisibility(View.INVISIBLE);
                audioLocalRecyclerViewItemViewHolder.playingItemLottieAnimationView.setVisibility(View.VISIBLE);
                audioLocalRecyclerViewItemViewHolder.playingItemLottieAnimationView.setAnimation("json/playing.json");
                if (isAudioLocalModelInPlaying) {
                    audioLocalRecyclerViewItemViewHolder.playingItemLottieAnimationView.playAnimation();
                } else {
                    audioLocalRecyclerViewItemViewHolder.playingItemLottieAnimationView.pauseAnimation();
                }
            } else {
                audioLocalRecyclerViewItemViewHolder.playingItemLottieAnimationView.setVisibility(View.INVISIBLE);
                audioLocalRecyclerViewItemViewHolder.numberItemTextView.setVisibility(View.VISIBLE);
                audioLocalRecyclerViewItemViewHolder.numberItemTextView.setText(Integer.toString(position + 1));
            }
            audioLocalRecyclerViewItemViewHolder.audioTitleTextView.setText(audioLocalModel.getAudioTitle());
            audioLocalRecyclerViewItemViewHolder.audioAlbumArtistTextView.setText("《" + audioLocalModel.getAudioAlbum() + "》·" + audioLocalModel.getAudioArtist());
            audioLocalRecyclerViewItemViewHolder.audioDurationTextView.setText(CommonUtils.formatDurationToTime(audioLocalModel.getAudioDuration()));

            audioLocalRecyclerViewItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    currPositionInPlaying = position;
                    changeAudio(0);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAudioLocalModelArrayList.size();
        }

        public class AudioLocalRecyclerViewItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView numberItemTextView;
            private final LottieAnimationView playingItemLottieAnimationView;
            private final TextView audioTitleTextView;
            private final TextView audioAlbumArtistTextView;
            private final TextView audioDurationTextView;

            public AudioLocalRecyclerViewItemViewHolder(@NonNull ViewGroup parent, @NonNull View itemView) {
                super(itemView);

                numberItemTextView = itemView.findViewById(R.id.textview_number_item);
                playingItemLottieAnimationView = itemView.findViewById(R.id.lottie_animation_view_playing_item);
                audioTitleTextView = itemView.findViewById(R.id.textview_title_audio);
                audioAlbumArtistTextView = itemView.findViewById(R.id.textview_album_artist_audio);
                audioDurationTextView = itemView.findViewById(R.id.textview_duration_audio);

                numberItemTextView.setTextSize(15);
                numberItemTextView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.gray808080_70));
                numberItemTextView.setTypeface(Typeface.DEFAULT_BOLD);

                audioTitleTextView.setTextSize(16);
                audioTitleTextView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.black_70));
                audioTitleTextView.setTypeface(Typeface.DEFAULT);
                audioTitleTextView.setMaxLines(1);
                audioTitleTextView.setMaxEms(11);
                audioTitleTextView.setEllipsize(TextUtils.TruncateAt.END);

                audioAlbumArtistTextView.setTextSize(12);
                audioAlbumArtistTextView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.gray808080));
                audioAlbumArtistTextView.setTypeface(Typeface.DEFAULT);
                audioAlbumArtistTextView.setMaxLines(1);
                audioAlbumArtistTextView.setMaxEms(17);
                audioAlbumArtistTextView.setEllipsize(TextUtils.TruncateAt.END);

                audioDurationTextView.setTextSize(13);
                audioDurationTextView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.gray808080));
                audioDurationTextView.setTypeface(Typeface.DEFAULT);
            }
        }
    }

}
