package mayphoo.mpk.padc_implicitintents;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button shareBtn, mapBtn, phoneCallBtn, sendEmailBtn, cameraBtn, selectPictureBtn, saveCalendarBtn;
    ImageView cameraImg, selectImg;

    private long startMillis = 0;
    private long endMillis = 0;
    Calendar beginTime = Calendar.getInstance();
    Calendar endtime = Calendar.getInstance();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareBtn = (Button) findViewById(R.id.btn_share);
        mapBtn = findViewById(R.id.btn_map);
        phoneCallBtn = findViewById(R.id.btn_phone_call);
        sendEmailBtn = findViewById(R.id.btn_send_email);
        cameraBtn = findViewById(R.id.btn_camera);
        selectPictureBtn = findViewById(R.id.btn_select_picture);
        saveCalendarBtn = findViewById(R.id.btn_calendar);
        cameraImg = findViewById(R.id.iv_camera_photo);
        selectImg = findViewById(R.id.iv_select_photo);

        shareBtn.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        phoneCallBtn.setOnClickListener(this);
        sendEmailBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        selectPictureBtn.setOnClickListener(this);
        saveCalendarBtn.setOnClickListener(this);

        beginTime.set(2017, 11, 20, 7,30);
        startMillis = beginTime.getTimeInMillis();

        endtime.set(2017, 11, 20, 10, 00);
        endMillis = endtime.getTimeInMillis();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                onTapShare();
                break;
            case R.id.btn_map:
                onTapMap(Uri.parse("geo:37.777749, -122.4194"));
                break;
            case R.id.btn_phone_call:
                onTapPhoneCall("111111");
                break;
            case R.id.btn_send_email:
                onTapSendEmails(new String[]{"firstperson@gmail.com", "secondperson@gmail.com"}, "Invitation");
                break;
            case R.id.btn_camera:
                onTapCamera();
                break;
            case R.id.btn_select_picture:
                onTapSelectPicture();
                break;
            case R.id.btn_calendar:
                onTapSaveEventInCalendar("Developer Conference", startMillis, endMillis);
                break;
            default:
                break;
        }
    }

    public void onTapShare() {
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("text/plain")
                .setText("Hello!")
                .getIntent(), "Share"));
    }

    public void onTapMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onTapPhoneCall(String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNo));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(getApplicationContext(), "Need ACTION_Call permission!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(intent);
        }
    }

    public void onTapSendEmails(String[] addresses, String subject){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    public void onTapCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onTapSelectPicture(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    public void onTapSaveEventInCalendar(String title, long begin, long end){
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cameraImg.setImageBitmap(imageBitmap);
            cameraImg.setVisibility(View.VISIBLE);
        }

        if(requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            Uri fullPhotoUri = data.getData();
            selectImg.setImageURI(fullPhotoUri);
            selectImg.setVisibility(View.VISIBLE);
        }
    }

}
