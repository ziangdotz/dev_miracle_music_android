package cn.miracle.dev_miracle_music_android.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import java.util.ArrayList;

import cn.miracle.dev_miracle_music_android.R;
import cn.miracle.dev_miracle_music_android.data.model.AudioLocalModel;

public class AudioLocalUtils {

    public static ArrayList<AudioLocalModel> getLocalAudioInfo(Context context) {
        String[] mAudioLocalAttribute = new String[] {
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
        };
        ArrayList<AudioLocalModel> mAudioLocalModelArrayList = new ArrayList<>();

        try (Cursor mCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mAudioLocalAttribute, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)) {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    if (mCursor.getInt(0) != 0 && mCursor.getLong(7) != 0) {
                        AudioLocalModel mAudioLocalModel = new AudioLocalModel();

                        mAudioLocalModel.setAudioId(mCursor.getString(1));
                        mAudioLocalModel.setAudioDisplayName(mCursor.getString(2));
                        mAudioLocalModel.setAudioTitle(mCursor.getString(3));
                        mAudioLocalModel.setAudioAlbum(mCursor.getString(4));
                        mAudioLocalModel.setAudioArtist(mCursor.getString(5));
                        mAudioLocalModel.setAudioData(mCursor.getString(6));
                        mAudioLocalModel.setAudioSize(mCursor.getLong(7));
                        mAudioLocalModel.setAudioDuration(mCursor.getLong(8));

                        mAudioLocalModelArrayList.add(mAudioLocalModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mAudioLocalModelArrayList;
    }

    public static Bitmap getLocalAudioAlbumCover(Context context, String audioData) {
        Bitmap localAudioAlbumCoverBitmap = null;
        MediaMetadataRetriever mMediaMetadataRetriever = new MediaMetadataRetriever();
        mMediaMetadataRetriever.setDataSource(audioData);
        byte[] localAudioAlbumCoverData = mMediaMetadataRetriever.getEmbeddedPicture();

        if (localAudioAlbumCoverData != null) {
            localAudioAlbumCoverBitmap = CommonUtils.getRoundedCornerCustomDPBitmap(BitmapFactory.decodeByteArray(localAudioAlbumCoverData, 0, localAudioAlbumCoverData.length), 3000);
        } else {
            localAudioAlbumCoverBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.png_default_cover_audio);
        }

        return localAudioAlbumCoverBitmap;
    }

}
