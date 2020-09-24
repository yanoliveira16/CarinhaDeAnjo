package com.example.carinhadeanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.face.FaceDetector;
import com.lyft.android.scissors.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;


import java.io.FileNotFoundException;
import java.io.InputStream;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.content.Intent.ACTION_PICK;

public class enviar_foto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_foto);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("porra", 2);
        editor.apply();

        final TextView a2 = (TextView) findViewById(R.id.text_informativo);
        a2.setText("ESCOLHA SUA IMAGEM\nDeve contér um rosto!");
    }

    public void escolher_imagem(View view){
        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //startActivityForResult(gallery, PICK_IMAGE);
        final TextView a2 = (TextView) findViewById(R.id.text_informativo);
        a2.setText("ABRINDO GALERIA, AGUARDE...");
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE);
    }

    private static final int PICK_IMAGE = 1;
    Uri imageUri;

    CropView cropView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            final TextView a2 = (TextView) findViewById(R.id.text_informativo);
            a2.setText("PROCESSANDO FOTO...");
            try {
                View a = findViewById(R.id.imageView32);
                a.setVisibility(View.INVISIBLE);
                imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
               // bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                cropView = findViewById(R.id.crop_view);
                cropView.setImageBitmap(getRoundedCornerBitmap(bitmap, 400));
                a2.setText("Arraste a foto com os dedos para deixar do jeito que quiser.");
            } catch (IOException e) {
                e.printStackTrace();
                a2.setText("ERRO\nTente novamente ou entre em contato com o suporte.");
            }
        }
    }



    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void enviar_foto(View view) throws FileNotFoundException {
        final TextView a2 = (TextView) findViewById(R.id.text_informativo);
        a2.setText("ANALISANDO FOTO...\nIsso pode demorar um pouco...");

        View b10 = findViewById(R.id.button10);
        b10.setVisibility(View.INVISIBLE);
        View b11 = findViewById(R.id.button11);
        b11.setVisibility(View.INVISIBLE);

        View a = findViewById(R.id.imc);
        a.setVisibility(View.VISIBLE);
        View b = findViewById(R.id.pgc);
        b.setVisibility(View.VISIBLE);

        setUpFaceDetector(imageUri);
    }


    private void setUpFaceDetector(Uri selectedImage) throws FileNotFoundException {
        com.google.android.gms.vision.face.FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()){
            new android.app.AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;

        InputStream ims = getContentResolver().openInputStream(selectedImage);

        Bitmap myBitmap = BitmapFactory.decodeStream(ims);

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d("TEST", "Num faces = " + faces.size());

        detectedResponse(myBitmap, faces);
    }


    public void detectedResponse(Bitmap myBitmap, SparseArray<Face> faces) {
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }

        cropView = findViewById(R.id.crop_view);
        cropView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

        if (faces.size() < 1) {
            new AlertDialog.Builder(this).setMessage("ERRO\nNenhum rosto detectado.").show();
            final TextView a2 = (TextView) findViewById(R.id.text_informativo);
            a2.setText("ERRO\nNenhum rosto foi detectado.");

            View b10 = findViewById(R.id.button10);
            b10.setVisibility(View.VISIBLE);
            View b11 = findViewById(R.id.button11);
            b11.setVisibility(View.VISIBLE);

            View a = findViewById(R.id.imc);
            a.setVisibility(View.INVISIBLE);
            View b = findViewById(R.id.pgc);
            b.setVisibility(View.INVISIBLE);
        }
        else if (faces.size() == 1) {
           // new AlertDialog.Builder(this).setMessage("Rosto detectado\nQue coisa lindaaaa.").show();

            Intent intent = new Intent(getBaseContext(), tela_de_carregamento.class);
            startActivity(intent);
        }
        else if (faces.size() > 1) {
            //new AlertDialog.Builder(this).setMessage("Mais de um rosto foi detectado.").show();
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder(enviar_foto.this).create();
        alertDialog.setTitle("OPA");
        alertDialog.setMessage("Você não pode voltar pois já começou seu processo de cadastro!\nEnvia uma foto do aluno para continuar.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}