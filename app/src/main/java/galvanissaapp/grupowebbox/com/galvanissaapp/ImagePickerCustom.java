package galvanissaapp.grupowebbox.com.galvanissaapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.mvc.imagepicker.ImageRotator;
import com.mvc.imagepicker.R.string;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ImagePickerCustom {
    private static final int PICK_IMAGE_ID = 234;
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static final String TAG = ImagePickerCustom.class.getSimpleName();
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private static int minWidthQuality = 400;
    private static int minHeightQuality = 400;

    private ImagePickerCustom() {
    }

    public static void pickImage(Activity activity) {
        String chooserTitle = activity.getString(string.pick_image_intent_text);
        pickImage(activity, chooserTitle);
    }

    public static void pickImage(Activity activity, String chooserTitle) {
        Intent chooseImageIntent = getPickImageIntent(activity, chooserTitle);
        activity.startActivityForResult(chooseImageIntent, 234);
    }

    public static Intent getPickImageIntent(Context context, String chooserTitle) {
        Intent chooserIntent = null;
        ArrayList intentList = new ArrayList();
        Intent pickIntent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra("output", Uri.fromFile(getTemporalFile(context)));
        List intentList1 = addIntentsToList(context, intentList, pickIntent);
        intentList1 = addIntentsToList(context, intentList1, takePhotoIntent);
        if(intentList1.size() > 0) {
            chooserIntent = Intent.createChooser((Intent)intentList1.remove(intentList1.size() - 1), chooserTitle);
            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[])intentList1.toArray(new Parcelable[intentList1.size()]));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        Log.i(TAG, "Adding intents of type: " + intent.getAction());
        List resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        Iterator var4 = resInfo.iterator();

        while(var4.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo)var4.next();
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.i(TAG, "App package: " + packageName);
        }

        return list;
    }

    public static Bitmap getImageFromResult(Context context, int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.i(TAG, "getImageFromResult() called with: resultCode = [" + resultCode + "]");
        Bitmap bm = null;
        if(resultCode == -1 && requestCode == 234) {
            File imageFile = getTemporalFile(context);
            boolean isCamera = imageReturnedIntent == null || imageReturnedIntent.getData() == null || imageReturnedIntent.getData().toString().contains(imageFile.toString());
            Uri selectedImage;
            if(isCamera) {
                selectedImage = Uri.fromFile(imageFile);
            } else {
                selectedImage = imageReturnedIntent.getData();
            }

            Log.i(TAG, "selectedImage: " + selectedImage);
            bm = getImageResized(context, selectedImage);
            int rotation = ImageRotator.getRotation(context, selectedImage, isCamera);
            bm = ImageRotator.rotate(bm, rotation);
        }

        return bm;
    }

    private static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "tempImage");
    }

    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;

        Bitmap bm;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            ++i;
        } while(bm != null && (bm.getWidth() < minWidthQuality || bm.getHeight() < minHeightQuality) && i < sampleSizes.length);

        Log.i(TAG, "Final bitmap width = " + (bm != null?Integer.valueOf(bm.getWidth()):"No final bitmap"));
        return bm;
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        Bitmap actuallyUsableBitmap = null;
        AssetFileDescriptor fileDescriptor = null;
        Options options = new Options();
        options.inSampleSize = sampleSize;

        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        }

        if(fileDescriptor != null) {
            actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), (Rect)null, options);
            Log.i(TAG, "Trying sample size " + options.inSampleSize + "\t\tBitmap width: " + actuallyUsableBitmap.getWidth() + "\theight: " + actuallyUsableBitmap.getHeight());
        }

        return actuallyUsableBitmap;
    }

    public static void setMinQuality(int minWidthQuality, int minHeightQuality) {
        minWidthQuality = minWidthQuality;
        minHeightQuality = minHeightQuality;
    }
}
