package com.example.damatch.flickr;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.damatch.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* PhotoFragment that creates the recycler view. In addition to the code, able to get user interaction and
 * save ID and Directory path of code.
 * Taken from Android Programming The Big Nerd Ranch Guide (3rd ed) Chp.25, 26, and 27
* */


public class PhotoGalleryFragment extends Fragment {

    public static final int MAX_IMAGES_IN_SET = 31;
    public static final int EXTRACT_LAST_FOUR_DIGITS = 4;
    public static final int ZERO = 0;
    private static final String ID_PREFS = "IDPrefs";
    private static final String DIRECTORY_PREFS = "DirPrefs";
    private static final String ID_KEY = "ID";
    private static final String DIRECTORY_KEY = "Directory";
    private static final String ID_KEY_SIZE = "ID_KEY_SIZE";
    private static final String TAG = "PhotoGalleryFragment";
    private static final int REQUEST_CODE = 100;
    private int chosenImageID;

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private FlickrManager customImageManager = FlickrManager.getInstance();


    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    private void setupRemoveButton(View v) {
        ImageButton btn = v.findViewById(R.id.remove_edit_img_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customImageManager.imageIDArraySize() != ZERO) {
                    Intent intent = RemoveImagesActivity.makeIntent(getActivity());
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "No images in set!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupCustomButton(View v) {
        ImageButton btn = v.findViewById(R.id.custom_img_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhotos = new Intent(Intent.ACTION_PICK);
                pickPhotos.setType("image/*");
                pickPhotos.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(pickPhotos, REQUEST_CODE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        GridLayoutManager grid = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(grid);
        setupAdapter();
        setupRemoveButton(v);
//      Code for getting application gallery:
//      https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
        setupCustomButton(v);
        return v;
    }

    //https://stackoverflow.com/questions/23426113/how-to-select-multiple-images-from-gallery-in-android/23426985%23:~:text=setAction(Intent.-,ACTION_GET_CONTENT)%253B%2520startActivityForResult(Intent.,Android%2520API%252018%2520and%2520higher.&text=Here%2520is%2520the%2520code%2520for,and%2520video%2520from%2520Default%2520Gallery    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // clip data problem for one image https://stackoverflow.com/questions/51471647/getclipdata-getitemcount-nullpointer-exception/51471711
            if(data.getClipData() == null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                    // Random generator for ID https://stackoverflow.com/questions/7815196/how-to-generate-a-random-five-digit-number-java
                    Random r = new Random( System.currentTimeMillis() );
                    int customImageID = 10000 + r.nextInt(20000);
                    if (customImageManager.imageIDArraySize() < MAX_IMAGES_IN_SET) {
                        customImageManager.addImageID(customImageID);
                        saveImageIDArray(customImageManager.getImageIDArray());
                        storeToStorage(bitmap, customImageID);
                        Toast.makeText(getActivity(), "Images Added to Custom Set!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "Custom Set is Full (Max Images: 31)", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri selectedImage = data.getClipData().getItemAt(i).getUri();//As of now use static position 0 use as per itemcount.
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                        // Random generator for ID https://stackoverflow.com/questions/7815196/how-to-generate-a-random-five-digit-number-java
                        Random r = new Random( System.currentTimeMillis() );
                        int customImageID = 10000 + r.nextInt(20000);
                        if (customImageManager.imageIDArraySize() < MAX_IMAGES_IN_SET) {
                            customImageManager.addImageID(customImageID);
                            saveImageIDArray(customImageManager.getImageIDArray());
                            storeToStorage(bitmap, customImageID);
                            Toast.makeText(getActivity(), "Images Added to Custom Set!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "Custom Set is Full (Max Images: 31)", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void storeToStorage(Bitmap bitmap, int ID) {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("image_dir", Context.MODE_PRIVATE);
        File filePath = new File(directory, ID +".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        customImageManager.setPathToImageStorage(directory.getAbsolutePath());
        saveDirectory(directory.getAbsolutePath());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getContext(), query);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    // Chapter 8 Taking Presses for RecyclerView
    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mItemImageView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            String chosenImageURL = mItems.get(getLayoutPosition()).getUrl();
            String imageID = mItems.get(getLayoutPosition()).getId();
            String splitID = imageID.substring(imageID.length() - EXTRACT_LAST_FOUR_DIGITS);
            chosenImageID = Integer.parseInt(splitID);

            if(!(customImageManager.doesDuplicateIDExist(chosenImageID)) && customImageManager.imageIDArraySize() < MAX_IMAGES_IN_SET) {
                customImageManager.addImageID(chosenImageID);
                new DownloadFile().execute(chosenImageURL);
                saveImageIDArray(customImageManager.getImageIDArray());
                Toast.makeText(getActivity(), "Images Added to Flickr Set!", Toast.LENGTH_SHORT).show();
            }else if((customImageManager.doesDuplicateIDExist(chosenImageID))) {
                Toast.makeText(getActivity(), "Images Already Exist in Flickr Set!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "Flickr Set is Full (Max Images: 31)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // DownloadFile AsyncTask
    // Code below taken from https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private class DownloadFile extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Bitmap doInBackground(String... params) {

            String imageURL = params[0];
            Bitmap bitmap = null;

            try{
                InputStream in = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        // Code below taken from https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        @Override
        protected void onPostExecute(Bitmap result) {

            ContextWrapper cw = new ContextWrapper(getContext());
            File directory = cw.getDir("image_dir", Context.MODE_PRIVATE);
            File filePath = new File(directory, chosenImageID +".jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath);
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fos.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            customImageManager.setPathToImageStorage(directory.getAbsolutePath());
            saveDirectory(directory.getAbsolutePath());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.loading_text);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            // SEARCH FOR THE GIVEN
            if(mQuery == null) {
                return new FlickrFetch().fetchRecentPhotos();
            } else {
                return new FlickrFetch().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
     }

    public void saveImageIDArray(List<Integer> idImagesArray) {
        SharedPreferences prefsID = getActivity().getSharedPreferences(ID_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsID.edit();
        editor.putInt(ID_KEY_SIZE, idImagesArray.size());

        for(int i = 0; i < idImagesArray.size(); i++) {
            editor.putInt(ID_KEY + i, idImagesArray.get(i));
        }
        editor.apply();
    }

    private void saveDirectory(String absolutePath) {
        SharedPreferences prefsStorage = getActivity().getSharedPreferences(DIRECTORY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsStorage.edit();

        editor.putString(DIRECTORY_KEY, absolutePath);
        editor.apply();
    }

    private static PhotoGalleryFragment instance;
    private PhotoGalleryFragment() {
    }

    public static PhotoGalleryFragment getInstance() {
        if (instance == null) {
            instance = new PhotoGalleryFragment();
        }
        return instance;
    }
}
