package com.upscapesoft.whatssaverapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.upscapesoft.whatssaverapp.adapters.WAVideosAdapter;
import com.upscapesoft.whatssaverapp.helper.AdController;
import com.upscapesoft.whatssaverapp.helper.StorageFunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;

public class WAVideosFragment extends Fragment {

    RelativeLayout content_loaderLay;
    RelativeLayout content_emptyLay;
    RecyclerView rvStatuses;
    Context ctx;
    RecyclerView.LayoutManager layoutManager;
    WAVideosAdapter rv_adapter;
    private Activity mActivity;
    DocumentFile[] fileListed2;
    FloatingActionButton fab_save_video;
    FloatingActionButton fab_delete_video;
    StorageFunctions storageHelper;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content_loaderLay = view.findViewById(R.id.contentLoaderLay);
        content_emptyLay = view.findViewById(R.id.contentEmptyLay);
        rvStatuses = view.findViewById(R.id.rv_videos);
        fab_save_video = view.findViewById(R.id.fab_save_videos);
        fab_delete_video = view.findViewById(R.id.fab_delete_videos);

        if (WAStatusActivity.sharedPreferences != null) {
            editor = WAStatusActivity.sharedPreferences.edit();
        }
        content_emptyLay.setVisibility(View.GONE);
        layoutManager = new GridLayoutManager(ctx,3);
        storageHelper = new StorageFunctions();

        fab_save_video.setOnClickListener(view1 -> {
            if (WAStatusActivity.filePathsVideos != null) {
                for(int i = 0; i < WAStatusActivity.filePathsVideos.size(); i++){
                    if (WAStatusActivity.filePathsVideosChecked != null && !WAStatusActivity.filePathsVideosChecked.get(i).equals("0")) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            displayAlerter(storageHelper.saveVideoQ(Uri.parse(WAStatusActivity.filePathsVideos.get(i)), ctx));
                        } else {
                            displayAlerter(storageHelper.save(new File(WAStatusActivity.filePathsVideos.get(i)), 1, ctx));
                        }
                    }
                }
            }

            if(WAStatusActivity.sharedPreferences.getInt("counter",0) >= 5) {
                showFullscreenAd();
                editor.putInt("counter", 0);
            }else{
                editor.putInt("counter", WAStatusActivity.sharedPreferences.getInt("counter",0) + 1);
            }
            editor.apply();
            reset();
        });

        fab_delete_video.setOnClickListener( view1 -> {
            if (WAStatusActivity.filePathsVideos != null) {
                for(int i = 0; i < WAStatusActivity.filePathsVideos.size(); i++) {

                    if (WAStatusActivity.filePathsVideosChecked != null && !WAStatusActivity.filePathsVideosChecked.get(i).equals("0")) {
                        deleteMyFile(Uri.parse(WAStatusActivity.filePathsVideos.get(i)));
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
        if (WAStatusActivity.filePathsVideos != null) {
            WAStatusActivity.filePathsVideos.clear();
        }
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        content_loaderLay.setVisibility(View.VISIBLE);
        new Thread(() -> {
            if (loadVideos()) {
                mActivity.runOnUiThread(() -> {
                    if (WAStatusActivity.filePathsVideos != null) {
                        if (WAStatusActivity.filePathsVideos.size() == 0) {
                            content_emptyLay.setVisibility(View.VISIBLE);
                        }else{
                            content_emptyLay.setVisibility(View.GONE);
                        }
                    }

                    rvStatuses.setLayoutManager(layoutManager);
                    rv_adapter = new WAVideosAdapter(mActivity, WAVideosFragment.this);
                    rvStatuses.setAdapter(rv_adapter);

                    content_loaderLay.setVisibility(View.GONE);
                });

            }
        }).start();
    }

    public boolean loadVideos() {

        WAStatusActivity.statusMode = 0;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                //Didn't work as expected, that's why we grab it from the first fragment.
                fileListed2 = WAPhotosFragment.dir.listFiles();

                for (DocumentFile documentFile : fileListed2) {
                    try {
                        if (Objects.requireNonNull(documentFile.getName()).contains(".mp4") && !Objects.requireNonNull(documentFile.getName()).contains(".nomedia")) {

                            if (WAStatusActivity.filePathsVideos != null) {
                                WAStatusActivity.filePathsVideos.add(String.valueOf(documentFile.getUri()));
                            }

                        }

                    } catch (Exception e) {
                        //
                    }

                }

                if (WAStatusActivity.filePathsVideos != null) {
                    for (int y = 0; y < WAStatusActivity.filePathsVideos.size(); y++){
                        if (WAStatusActivity.filePathsVideosChecked != null) {
                            WAStatusActivity.filePathsVideosChecked.add("0");
                        }
                    }
                }


            }else{
                File oldStatusPath = new File("/storage/emulated/0/WhatsApp/Media/.Statuses");
                File[] files = oldStatusPath.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.getAbsolutePath().contains("nomedia") && file.getAbsolutePath().endsWith(".mp4")) {
                            if (WAStatusActivity.filePathsVideos != null) {
                                WAStatusActivity.filePathsVideos.add(file.getAbsolutePath());
                            }
                        }
                    }
                }
                if (WAStatusActivity.filePathsVideos != null) {
                    for (int y = 0; y < WAStatusActivity.filePathsVideos.size(); y++){
                        if (WAStatusActivity.filePathsVideosChecked != null) {
                            WAStatusActivity.filePathsVideosChecked.add("0");
                        }
                    }
                }


            }
            return true;

        }catch (Exception e){
            Log.d("ssw videos ", "stacktrace: " + getStackTrace(e));
            return false;
        }
    }

    public void checkFAB() {
        int anzahl = 0;
        if (WAStatusActivity.filePathsVideosChecked != null) {
            for(int i = 0; i < WAStatusActivity.filePathsVideosChecked.size(); i++){
                anzahl = anzahl + Integer.parseInt(WAStatusActivity.filePathsVideosChecked.get(i));
            }
        }

        if (anzahl != 0) {
            fab_save_video.show();
            fab_delete_video.show();
        }else{
            fab_save_video.hide();
            fab_delete_video.hide();
        }
    }

    private void reset() {
        WAStatusActivity.filePathsVideos = new ArrayList<>();
        WAStatusActivity.filePathsVideosChecked = new ArrayList<>();
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
            mActivity = (Activity) context;
            ctx = context;
        }

    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private void displayAlerter(boolean hasSaved) {
        if(hasSaved){
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