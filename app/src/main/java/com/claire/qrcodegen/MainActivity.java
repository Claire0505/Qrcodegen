package com.claire.qrcodegen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnScanner, btnQRCode, btnScanQRCode;
    private TextView txtScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initHandler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
            }else {
                //掃描完成，將文字顯示出來
                txtScan.setText(result.getContents());
                Toast.makeText(this, "掃描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initHandler() {
        //掃條碼
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanner(v);
            }
        });

        //產出QRCode
        btnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputQRCode(v);
            }
        });

        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanQRCode(v);
            }
        });
    }

    private void findViews() {
        btnScanner = findViewById(R.id.btnScanner);
        btnQRCode = findViewById(R.id.btnQRCode);
        btnScanQRCode = findViewById(R.id.btnScanQRCode);
        txtScan = findViewById(R.id.textScan);
    }

    public void onScanner(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("請將紅線對準條碼進行掃描");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void onScanQRCode(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("請將紅線對準條碼進行掃描");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void outputQRCode(View v)
    {
        // QR code 的內容
        String QRCodeContent = "QR code test";
        // QR code 寬度
        int QRCodeWidth = 200;
        // QR code 高度
        int QRCodeHeight = 200;
        // QR code 內容編碼
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
            // 設定 QR code 容錯率為 H
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            // 建立 QR code 的資料矩陣
            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

            //建立點陣圖
            Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
            // 將 QR code 資料矩陣繪製到點陣圖上
            for (int y = 0; y<QRCodeHeight; y++)
            {
                for (int x = 0;x<QRCodeWidth; x++)
                {
                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ImageView imgView = findViewById(R.id.imageView);
            // 設定為 QR code 影像
            imgView.setImageBitmap(bitmap);
        }
        catch (WriterException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

