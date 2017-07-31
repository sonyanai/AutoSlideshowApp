package jp.techacademy.taison.yanai.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    int mTimerSec = 0;

    Button mBackButton;
    Button mStopButton;
    Button mForwardButton;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                firstContentsInfo();
                //firstContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            firstContentsInfo();
        }


        mBackButton = (Button) findViewById(R.id.back_button);
        mStopButton = (Button) findViewById(R.id.startStop_button);
        mForwardButton = (Button) findViewById(R.id.forward_button);


        mBackButton.setOnClickListener(new View.OnClickListener(){//押したときの処理
            @Override
           public void onClick(View v){//もし最初だったら一番後ろに戻る//最初ではなかったら前に戻る
                if (cursor.moveToPrevious()){
                    previousContentsInfo();
                }else{
                    lastContentsInfo();
                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener(){//押したときの処理
            @Override
            public void onClick(View v){
                if(mTimer == null){
                    mStopButton.setText("stop");
                    mForwardButton.setEnabled(false);//trueで表示,falseで非表示
                    mBackButton.setEnabled(false);
                    mTimer = new Timer();//タイマーの作成
                    mTimer.schedule(new TimerTask(){
                        @Override
                        public void run(){
                            mTimerSec +=1;
                            //ここで次の写真に移動させる
                            mHandler.post(new Runnable(){
                                @Override
                                public void run(){
                                    nextContentsInfo();
                                }
                            });
                        }
                    },2000,2000);
                }else if(mTimer != null) {//動いてたら止める//再生中はstop
                    mStopButton.setText("start");
                    mForwardButton.setEnabled(true);
                    mBackButton.setEnabled(true);
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });



        mForwardButton.setOnClickListener(new View.OnClickListener(){//押したときの処理
            @Override
            public void onClick(View v){//もし一番後ろだったら最初に進む//それ以外は後に進む
                if (cursor.moveToNext()){
                    nextContentsInfo();
                }else{
                    firstContentsInfo();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firstContentsInfo();
                }else{
                    mForwardButton.setEnabled(false);//trueで表示,falseで非表示
                    mBackButton.setEnabled(false);
                    mStopButton.setEnabled(false);

                    mDialog();




                }
                break;
            default:
                //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                //break;
        }
    }


    ContentResolver resolver;
    Cursor cursor;



    private void nextContentsInfo(){
        if (cursor.moveToNext()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }
    private void previousContentsInfo(){
        if (cursor.moveToPrevious()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }
    private void lastContentsInfo(){

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToLast()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }
    private void firstContentsInfo(){

        this.resolver = getContentResolver();
        this.cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }


    private void mDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("再起動して許可してください");
        builder.show();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        //cursor.close();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
