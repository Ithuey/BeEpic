package com.beepic.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.beepic.PostActivity;
import com.beepic.Profile.EditProfile;
import com.beepic.R;
import com.beepic.Utils.FilePaths;
import com.beepic.Utils.FileSearch;
import com.beepic.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    private Context mContext;

    //Constants
    private static final int NUM_GRID_COLUMNS = 3;



    //Widgets
    private GridView gridView;
    private ImageView galleryImage;
    private Spinner directorySpinner;
    private ImageView galleryClose;
    private TextView galleryNext;

    //var
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(GalleryActivity.this));
        galleryImage = findViewById(R.id.galleryImageView);
        gridView = findViewById(R.id.gridView);
        directorySpinner = findViewById(R.id.spinnerDirectory);
        galleryClose = findViewById(R.id.galleryClose);
        galleryNext = findViewById(R.id.txtNext);
        directories = new ArrayList<>();
        mContext = GalleryActivity.this;


        galleryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment. ");
                mContext.fileList();
            }
        });

        galleryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        galleryNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to post activity. ");

                if(isRootTask()){
                    Intent intent = new Intent(GalleryActivity.this, PostActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(GalleryActivity.this, EditProfile.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), EditProfile.class);
                    startActivity(intent);
                    GalleryActivity.this.finish();
                }


            }
        });

        init();
        getTask();

    }

    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }



    private boolean isRootTask(){

        if(getTask()== 0){
            return true;

        }else{
            return false;
        }
    }


    private void init() {
        FilePaths filePaths = new FilePaths();

        //check for other folders inside "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
            Log.d(TAG, "init: directories: " + directories);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i = 0; i < directories.size(); i++) {

            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index).replace("/", "");
            directoryNames.add(string);
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                R.layout.post_spinner_text, directoryNames);
        adapter.setDropDownViewResource(R.layout.post_spinner_text);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected " + directories.get(position));

                //setup our image for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: Directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, mAppend, imgURLs );
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity is inflated
        setImage(imgURLs.get(0), galleryImage, mAppend);
        mSelectedImage = imgURLs.get(0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: display clicked images: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });
    }


    private void setImage(String imgURL, ImageView image, String append){
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}













































