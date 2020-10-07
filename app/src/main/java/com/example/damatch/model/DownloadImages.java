package com.example.damatch.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.ImageView;

import com.example.damatch.GameActivity;
import com.example.damatch.MainActivity;
import com.example.damatch.R;
import com.example.damatch.SettingsActivity;
import com.example.damatch.flickr.FlickrManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadImages {
    private FlickrManager flickrManager = FlickrManager.getInstance();
    private Context context = MainActivity.getContext();
    private int order = SettingsActivity.getOrderNum(context);

    //creates bitmap of card to store
    //todo resource:
    public Bitmap createBitmapFromCard(Card card) {

        //get images, image locations and sizes
        int[] cardImages = card.getCardImages();

        String leftName = "left_distances_" + order;
        int leftId = context.getResources().getIdentifier(leftName, "array", context.getPackageName());

        String topName = "top_distances_" + order;
        int topId = context.getResources().getIdentifier(topName, "array", context.getPackageName());

        int[] leftDistances = context.getResources().getIntArray(leftId);
        int[] topDistances = context.getResources().getIntArray(topId);
        int imageSize;

        //Allocate a new Bitmap at 400 x 400 px
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //set background image
        ImageView background = new ImageView(context);
        background.setImageResource(R.drawable.deck_front);

        //Lay the background out at the rect width and height
        background.layout(0, 0, 400, 400);

        //Translate the Canvas into position and draw it
        canvas.save();
        canvas.translate(0, 0);
        background.draw(canvas);

        //create/scale images and draw on canvas
        Bitmap image;
        for(int i = 0; i < cardImages.length; i++){
            imageSize = getImageSizes();

            if(SettingsActivity.getCardTypeEnum() == Card.DeckTypes.FLICKR_DECK){
                String drawCardImageFormat = cardImages[i] + ".jpg";
                image = GameActivity.getImageBitmap(flickrManager.getPathToImageStorage(), drawCardImageFormat);
            }else{
                image = BitmapFactory.decodeResource(context.getResources(), cardImages[i]);
            }
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

            //rotate images
            //resource: https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
            if(SettingsActivity.getGameLevelEnum() == GameActivity.GameLevel.NORMAL_MODE || SettingsActivity.getGameLevelEnum() == GameActivity.GameLevel.HARD_MODE) {
                image = rotateBitmap(image);
            }
            canvas.drawBitmap(image, (float) leftDistances[i], (float) topDistances[i], null);
        }

        canvas.restore();

        return bitmap;
    }

    private Bitmap rotateBitmap(Bitmap image) {
        Matrix matrix = new Matrix();
        float angle = (float) (Math.random() * 360);
        matrix.postRotate(angle);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        //redraw canvas to remove background for flickr and word images
        Bitmap newBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(image, 0, 0, null);
        image = newBitmap;

        return image;
    }

    private int getImageSizes() {
        int[] imageSizes = context.getResources().getIntArray(R.array.image_sizes);
        int size;

        if(SettingsActivity.getGameLevelEnum() == GameActivity.GameLevel.HARD_MODE){
            size = (int)((Math.random()*65)+55);
        } else
        if(order <= 3){
            size = imageSizes[order - 2];
        } else {
            size = imageSizes[2];
        }
        return size;
    }

    //stores bitmap in internal storage (android/com.example.damatch/data/files/Pictures)
    //TODO resource:
    public  void saveImageToExternalStorage(Context context, String newDirectoryName, String filename, Bitmap image) throws IOException {
        //get apps picture directory
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //create new directory
        File newDirectory = new File(directory, File.separator + newDirectoryName);
        newDirectory.mkdirs();

        //create new file within directory
        File file = makeFilename(newDirectory, filename);

        FileOutputStream outputStream = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        outputStream.flush();
        outputStream.getFD().sync();
        outputStream.close();
        MediaScannerConnection.scanFile(context, new String[] {file.getAbsolutePath()}, null, null);
    }


    //resource: https://stackoverflow.com/questions/20162447/why-does-fileoutputstream-throw-filenotfoundexception
    private  File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }
}
