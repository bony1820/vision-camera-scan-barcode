package com.visioncamerascanbarcode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

@ReactModule(name = VisionCameraScanBarcodeModule.NAME)
public class VisionCameraScanBarcodeModule extends ReactContextBaseJavaModule {
    public static final String NAME = "VisionCameraScanBarcode";
    private int REQUEST_CODE = 21001;
    private ReactApplicationContext reactContext;
    private Promise promise;

    public VisionCameraScanBarcodeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
        public void scanCodeFromLibrary(Promise promise) {
        this.promise = promise;
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) return;
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        final Intent chooserIntent = Intent.createChooser(intent, "Pick an image");
        currentActivity.startActivityForResult(chooserIntent, REQUEST_CODE);
        }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        BarcodeScannerOptions options =
        new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
            )
            .build();

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
        Uri selectedImage = data.getData();
        InputImage image;
        try {
            image = InputImage.fromFilePath(activity.getApplicationContext(), selectedImage);
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner.process(image)
            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                @Override
                public void onSuccess(List<Barcode> barcodes) {
                WritableArray writableArray = Arguments.createArray();
                if (barcodes.size() > 0) {
                    for (int i = 0; i < barcodes.size(); i++) {
                    writableArray.pushString((String) barcodes.get(i).getRawValue());
                    }
                }
                promise.resolve(writableArray);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                promise.reject(e.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        promise.reject("You were canceled");
        } else {
        promise.reject("An unknown error");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

}
