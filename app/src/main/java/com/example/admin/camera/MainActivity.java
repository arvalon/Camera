package com.example.admin.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

// FIXME: 06.02.2019 FileUriExposedException: file:///storage/emulated/0/Pictures/picture.jpg
// exposed beyond app through ClipData.Item.getUri() at android.os.StrictMode.onFileUriExposed
// on SDK 24+ (Android 7.0 Nougat)

// https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed

/**
 * Work with camera
 * @author arvalon
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "camera.log";

    private static final String FILENAME = "picture.jpg";

    private static final String VIDEOFILENAME = "video.mp4";

    private static final int TAKE_PHOTO = 1;
    private static final int PICK_IMAGE = 2;
    private static final int TAKE_VIDEO = 3;

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PICK_IMAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                pickImageWrapper();
            }
        }
    }

    public void takePhoto(View view) {
        takePhotoWrapper();
    }

    private void takePhotoWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    TAKE_PHOTO);
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!dir.isDirectory())
            dir.mkdirs();

        File file = new File(dir, FILENAME);

        // Intent на получение фотографии камерой
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(i, TAKE_PHOTO);
    }

    public void scan(View view) {
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        scan(picDir);
    }

    private void scan(File picsDir) {
        if(picsDir != null)
        {
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{
                            picsDir.getAbsolutePath()
                    },
                    null,
                    (path, uri) -> {
                        Log.d(LOGTAG, "Scanned " + path);
                        Log.d(LOGTAG, "Uri: " + uri);
                    }
            );
        }
    }

    public void pickImage(View view) {
        pickImageWrapper();
    }

    private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    PICK_IMAGE);
        } else {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            startActivityForResult(intent, PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == RESULT_OK)
            {
                if(data != null && data.getData() != null)
                {
                    Uri  uri = data.getData();

                    Picasso
                            .with(this)
                            .load(uri)
                            .fit()
                            .into(image);
                }
            }
        }
        else if(requestCode == TAKE_PHOTO)
        {
            if(resultCode == RESULT_OK)
            {
                File dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                File file = new File(dir, FILENAME);
                if(file != null)
                {
                    Picasso
                            .with(this)
                            .load(file)
                            .fit()
                            .into(image);
                }
            }
        }
        else if(requestCode == TAKE_VIDEO)
        {
            if(resultCode == RESULT_OK)
            {
                /* ... */
                File dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                scan(dir);
            }
        }
    }

    public void takeVideo(View view) {
        takeVideoWrapper();
    }

    private void takeVideoWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    TAKE_VIDEO);
        } else {
            takeVideo();
        }
    }

    private void takeVideo() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!dir.isDirectory())
            dir.mkdirs();



        File file = new File(dir, VIDEOFILENAME);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)
        );
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startActivityForResult(intent, TAKE_VIDEO);

    }
}