package com.rimas.explorenepal.mlkit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.rimas.explorenepal.R;

import java.util.List;

public class BarcodeDetection extends AppCompatActivity {

    ImageView imgResult;
    Button btnCapture, btnDetect, btnCamera, btnFile;
    TextView txtResult;
    Bitmap imageBitmap;
    String text="";
    String finalFilePath;
    boolean value;
    Toolbar toolbar;
    StringBuilder stringBuilder= new StringBuilder();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);
        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Barcode Detection");
        setSupportActionBar(toolbar);
        imgResult=findViewById(R.id.imgResult);
        btnCapture=findViewById(R.id.btnCapture);
        btnDetect=findViewById(R.id.btnDetect);
        txtResult=findViewById(R.id.txtResult);
        btnDetect.setEnabled(false);
        btnDetect.setText("DETECT DETAILS");

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPopup();

            }
        });

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgResult!=null) {
                    if (value) {
                        detectTextFromImage();
                    }
                    else{
                        detectText();
                    }
                }
                else{
                    Toast.makeText(BarcodeDetection.this, "Image field is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dialogPopup() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.camera_dialog, null);
        btnCamera =mView.findViewById(R.id.btnCamera);
        btnFile=mView.findViewById(R.id.btnFile);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                value=true;
                txtResult.setText("");
                dispatchTakePictureIntent();

            }
        });

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                value=false;
                txtResult.setText("");
                pickImage();
            }
        });


//                mBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//
//                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });

    }

    private void pickImage(){
        text="";
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3);
    }




    private void dispatchTakePictureIntent() {
        text="";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imgResult.setImageBitmap(imageBitmap);
            btnDetect.setEnabled(true);

        }

        if (resultCode==RESULT_OK){
            if (requestCode==3){
                Uri uri= data.getData();
//                Cursor cursor= null;
                if (uri != null) {

                    String[] filepath={MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,filepath ,null,null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int index= cursor.getColumnIndex(filepath[0]);
                        finalFilePath= cursor.getString(index);
                        cursor.close();

                        imgResult.setImageBitmap(BitmapFactory.decodeFile(finalFilePath));
                        btnDetect.setEnabled(true);
                    }


                }

            }
        }
    }


    private void detectTextFromImage(){
        text="";
        FirebaseVisionImage firebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionBarcodeDetector detector= FirebaseVision.getInstance().getVisionBarcodeDetector();
        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

                for (FirebaseVisionBarcode barcode: firebaseVisionBarcodes){
//                    text+=barcode.getRawValue()+"\n";

                    switch (barcode.getValueType()){
                        case  FirebaseVisionBarcode.TYPE_WIFI:
                            text+= "SSID: "+ barcode.getWifi().getSsid()+"\n"+ "Password: "+barcode.getWifi().getPassword()+"\n"+ "Encryption Type: "+barcode.getWifi().getEncryptionType();
                            break;

                        case FirebaseVisionBarcode.TYPE_URL:
                            text+= "Title: "+ barcode.getUrl().getTitle()+"\n"
                                    + "URL: "+barcode.getUrl().getUrl();
                            break;


                        default:
                            text+=barcode.getDisplayValue()+"\n";
                            break;
                    }

                }

//
//                for (int i = 0; i < firebaseVisionBarcodes.size(); ++i) {
//                    FirebaseVisionBarcode barcode = firebaseVisionBarcodes.get(i);
//                    text+=barcode.getRawValue();
//                }

                txtResult.setText(text);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(BarcodeDetection.this, "Error in detecting details."+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error", e.getMessage());
            }
        });

    }
    private void detectText(){
        text="";
        FirebaseVisionImage firebaseVisionImage= FirebaseVisionImage.fromBitmap(BitmapFactory.decodeFile(finalFilePath));
        FirebaseVisionBarcodeDetector detector= FirebaseVision.getInstance().getVisionBarcodeDetector();
        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

                for (FirebaseVisionBarcode barcode: firebaseVisionBarcodes){
//                    text+=barcode.getRawValue()+"\n";

                    switch (barcode.getValueType()){
                        case  FirebaseVisionBarcode.TYPE_WIFI:
                            text+= "SSID: "+ barcode.getWifi().getSsid()+"\n"+ "Password: "+barcode.getWifi().getPassword()+"\n"+ "Encryption Type: "+barcode.getWifi().getEncryptionType();
                            break;

                        case FirebaseVisionBarcode.TYPE_URL:
                            text+= "Title: "+ barcode.getUrl().getTitle()+"\n"
                                    + "URL: "+barcode.getUrl().getUrl();
                            break;


                        default:
                            text+=barcode.getDisplayValue()+"\n";
                            break;
                    }

                }

//
//                for (int i = 0; i < firebaseVisionBarcodes.size(); ++i) {
//                    FirebaseVisionBarcode barcode = firebaseVisionBarcodes.get(i);
//                    text+=barcode.getRawValue();
//                }

                txtResult.setText(text);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(BarcodeDetection.this, "Error in detecting details."+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error", e.getMessage());
            }
        });
    }
}
