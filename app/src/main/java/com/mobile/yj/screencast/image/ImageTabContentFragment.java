package com.mobile.yj.screencast.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.mobile.yj.screencast.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/3/29.
 */
public class ImageTabContentFragment extends Fragment {

    private String TAG = ImageTabContentFragment.class.getSimpleName();
    private Context mContext;

    public final long LIMIT_SIZE = 1024 * 200;

    final ArrayList<String> imagePathList = new ArrayList<String>();

    List<Map<String, Object>> imageList = new ArrayList<Map<String, Object>>();

    public ImageTabContentFragment() {
    }

    @SuppressLint("ValidFragment")
    public ImageTabContentFragment(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.image_content, container, false);

        final ArrayList<String> imagePathList = new ArrayList<String>();
        List<Map<String, Object>> imageList = new ArrayList<Map<String, Object>>();
        String[] mediaColumns = new String[]{
                MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE
        };
        String[] from = new String[]{"BITMAP"};
        int[] to = new int[]{R.id.image};

        Cursor mCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 1;
        int picNum = 24;
        while (mCursor.moveToNext() && picNum > 0) {
            ImageInfo info = new ImageInfo();
            Map<String, Object> map = new HashMap<String, Object>();
            info.id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            info.title = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            info.data = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            info.size = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

            if (info.size < LIMIT_SIZE) {
                continue;
            }

            Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), info.id, MediaStore.Images.Thumbnails.MINI_KIND, options);
            int width = bm.getWidth();
            int height = bm.getHeight();
            int len = width > height ? height : width;
            bm = Bitmap.createBitmap(bm, 0, 0, len, len);

            map.put("_ID", info.id);
            map.put("TITLE", info.title);
            map.put("DATA", info.data);
            map.put("BITMAP", bm);

            imagePathList.add(info.data);
            imageList.add(map);

            picNum--;
            Log.d(TAG, "num: " + picNum);
        }

        mCursor.close();
        GridView mGridView = (GridView) fragView.findViewById(R.id.gridview);
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, imageList,
                R.layout.image_content_grid, from, to);

        mSimpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ImageView iv = (ImageView) view;
                    Bitmap b = (Bitmap) data;
                    iv.setImageBitmap(b);
                    return true;
                }
                return false;
            }
        });

        mGridView.setAdapter(mSimpleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int postion, long id) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("imagePathList", imagePathList);
                bundle.putInt("position", postion);
                Log.d("YJ!", "" + postion);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return fragView;
    }
}
