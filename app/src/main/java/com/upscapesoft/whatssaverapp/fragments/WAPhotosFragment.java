package com.upscapesoft.whatssaverapp.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tapadoo.alerter.Alerter;
import com.upscapesoft.whatssaverapp.R;
import com.upscapesoft.whatssaverapp.activities.WAStatusActivity;
import com.upscapesoft.whatssaverapp.adapters.WAPhotosAdapter;
import com.upscapesoft.whatssaverapp.helper.AdController;
import com.upscapesoft.whatssaverapp.helper.StorageFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class WAPhotosFragment extends Fragment {

    RelativeLayout content_loaderLay;
    RelativeLayout content_emptyLay;
    RecyclerView rvStatuses;
    Context ctx;
    RecyclerView.LayoutManager layoutManager;
    WAPhotosAdapter rv_adapter;
    private Activity mActivity;
    static public DocumentFile dir;
    public DocumentFile[] fileListed;
    FloatingActionButton fab_save_photo;
    FloatingActionButton fab_delete_photo;
    StorageFunctions storageHelper;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content_loaderLay = view.findViewById(R.id.contentLoaderLay);
        content_emptyLay = view.findViewById(R.id.contentEmptyLay);
        rvStatuses = view.findViewById(R.id.rv_photos);
        fab_save_photo = view.findViewById(R.id.fab_save_photos);
        fab_delete_photo = view.findViewById(R.id.fab_delete_photos);

        content_emptyLay.setVisibility(View.GONE);
        layoutManager = new GridLayoutManager(ctx,3);

        if (WAStatusActivity.sharedPreferences != null) {
            editor = WAStatusActivity.sharedPreferences.edit();
        }
        storageHelper = new StorageFunctions();

        fab_save_photo.setOnClickListener(view1 -> {
            if (WAStatusActivity.filePathsPhotos != null) {
                for(int i = 0; i < WAStatusActivity.filePathsPhotos.size(); i++) {

                    if (WAStatusActivity.filePathsPhotosChecked != null && !WAStatusActivity.filePathsPhotosChecked.get(i).equals("0")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            displayAlerter(storageHelper.savePhotoQ(ctx, getBitmapOnAndroidQ(Uri.parse(WAStatusActivity.filePathsPhotos.get(i)))));
                        } else {
                            displayAlerter(storageHelper.save(new File(WAStatusActivity.filePathsPhotos.get(i)), 0, ctx));
                        }

                    }
                }
            }
            if(WAStatusActivity.sharedPreferences.getInt("counter",0) >= 5){
                showFullscreenAd();
                editor.putInt("counter", 0);
            }else{
                editor.putInt("counter", WAStatusActivity.sharedPreferences.getInt("counter",0) + 1);
            }
            editor.apply();
            reset();
        });

        fab_delete_photo.setOnClickListener( view1 -> {
            if (WAStatusActivity.filePathsPhotos != null) {
                for(int i = 0; i < WAStatusActivity.filePathsPhotos.size(); i++) {

                    if (WAStatusActivity.filePathsPhotosChecked != null && !WAStatusActivity.filePathsPhotosChecked.get(i).equals("0")) {
                        deleteMyFile(Uri.parse(WAStatusActivity.filePathsPhotos.get(i)));
                        WAStatusActivity.Companion.displayDeleteAlerter(true);
                    }
                }
            }
            if(WAStatusActivity.sharedPreferences.getInt("counter",0) >= 5){
                showFullscreenAd();
                editor.putInt("counter", 0);
            }else{
                editor.putInt("counter", WAStatusActivity.sharedPreferences.getInt("counter",0) + 1);
            }
            editor.apply();
            reset();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (WAStatusActivity.filePathsPhotos != null) {
            WAStatusActivity.filePathsPhotos.clear();
        }
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        content_loaderLay.setVisibility(View.VISIBLE);
        new Thread(() -> {
            if (loadPhotos()) {

                mActivity.runOnUiThread(() -> {
                    if (WAStatusActivity.filePathsPhotos != null) {
                        if (WAStatusActivity.filePathsPhotos.size() == 0) {
                            content_emptyLay.setVisibility(View.VISIBLE);
                        }else{
                            content_emptyLay.setVisibility(View.GONE);
                        }
                    }

                    rvStatuses.setLayoutManager(layoutManager);
                    rv_adapter = new WAPhotosAdapter(mActivity, WAPhotosFragment.this);
                    rvStatuses.setAdapter(rv_adapter);

                    content_loaderLay.setVisibility(View.GONE);
                });

            }
        }).start();
    }

    public boolean loadPhotos() {

        WAStatusActivity.statusMode = 0;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (WAStatusActivity.uri != null) {
                        dir = DocumentFile.fromTreeUri(ctx, WAStatusActivity.uri);
                    }
                    assert dir != null;
                    fileListed = dir.listFiles();

                    for (DocumentFile documentFile : fileListed) {
                        try {
                            if (!Objects.requireNonNull(documentFile.getName()).contains(".mp4") && !Objects.requireNonNull(documentFile.getName()).contains(".nomedia")) {
                                if (WAStatusActivity.filePathsPhotos != null) {
                                    WAStatusActivity.filePathsPhotos.add(String.valueOf(documentFile.getUri()));
                                }
                            }

                        } catch (NullPointerException e) {
                            //
                        }

                    }

                    if (WAStatusActivity.filePathsPhotos != null) {
                        for (int y = 0; y < WAStatusActivity.filePathsPhotos.size(); y++){
                            if (WAStatusActivity.filePathsPhotosChecked != null) {
                                WAStatusActivity.filePathsPhotosChecked.add("0");
                            }
                        }
                    }

                }else{
                    File oldStatusPath = new File("/storage/emulated/0/WhatsApp/Media/.Statuses");
                    File[] files = oldStatusPath.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (!file.getAbsolutePath().contains("nomedia") && !file.getAbsolutePath().endsWith(".mp4")) {
                                if (WAStatusActivity.filePathsPhotos != null) {
                                    WAStatusActivity.filePathsPhotos.add(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                    if (WAStatusActivity.filePathsPhotos != null) {
                        for (int y = 0; y < WAStatusActivity.filePathsPhotos.size(); y++){
                            if (WAStatusActivity.filePathsPhotosChecked != null) {
                                WAStatusActivity.filePathsPhotosChecked.add("0");
                            }
                        }
                    }

                }
                return true;

            }catch (NullPointerException e){
                //
            }
            return false;
    }

    public void checkFAB() {
        int anzahl = 0;
        if (WAStatusActivity.filePathsPhotosChecked != null) {
            for(int i = 0; i < WAStatusActivity.filePathsPhotosChecked.size(); i++){
                anzahl = anzahl + Integer.parseInt(WAStatusActivity.filePathsPhotosChecked.get(i));
            }
        }

        if (anzahl != 0) {
            fab_save_photo.show();
            fab_delete_photo.show();
        }else{
            fab_save_photo.hide();
            fab_delete_photo.hide();
        }
    }

    private void reset() {
        WAStatusActivity.filePathsPhotos = new ArrayList<>();
        WAStatusActivity.filePathsPhotosChecked = new ArrayList<>();
        checkFAB();

        content_loaderLay.setVisibility(View.VISIBLE);
        setUpRecyclerView();
    }

    private void deleteMyFile(Uri uri) {
        Uri uri2 = Uri.parse(uri.toString());

        try {
            DocumentsContract.deleteDocument(mActivity.getApplicationContext().getContentResolver(), uri2);
        } catch (FileNotFoundException e) {
            //
        } catch (UnsupportedOperationException unused) {
            tryForceRemove(uri);
        }

    }

    private void tryForceRemove(Uri uri) {
        boolean z = false;
        try {
            if (mActivity.getContentResolver().delete(uri, null, null) > 0) {
                z = true;
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "Unable to delete!", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException unused) {
            Toast.makeText(mActivity.getApplicationContext(), "Unable to delete, Please try again later!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
            ctx = context;
        }

    }

    public Bitmap getBitmapOnAndroidQ(Uri uri) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = mActivity.getContentResolver();
        try {
            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void displayAlerter(boolean hasSaved) {
        if(hasSaved) {
            Alerter.create(mActivity)
                    .setTitle(R.string.saved_s)
                    .setText(R.string.save_s_long)
                    .setBackgroundColorRes(R.color.tint_color)
                    .show();

            } else {
                Alerter.create(mActivity)
                        .setTitle(R.string.error)
                        .setText(R.string.error_long)
                        .setBackgroundColorRes(R.color.tint_color)
                        .show();
            }
    }

    public void showFullscreenAd() {
        AdController.adCounter++;
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(getActivity(), null, 0);
            Toast.makeText(ctx, R.string.saved_s, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("show fullscreen", "Cant display -> No ad available!");
        }
    }

}