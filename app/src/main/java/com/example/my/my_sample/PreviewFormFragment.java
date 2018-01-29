package com.example.my.my_sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

public class PreviewFormFragment extends Fragment {

    private ImageView photoImageView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView passwordTextView;
    private Button sendButton;

    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_PHOTOPATH = "photopath";

    private static final String TAG = "INFO_PREVIEW_FRAGMENT";

    private String emailString;
    private String phoneString;
    private String passwordString;
    private String photopathString;
    private android.net.Uri Uri;
    private File file;

    public static Fragment newInstance(String email, String phone, String password, String photopath){
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL,email);
        args.putString(ARG_PHONE,phone);
        args.putString(ARG_PASSWORD,password);
        args.putString(ARG_PHOTOPATH,photopath);

        PreviewFormFragment fragment = new PreviewFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.preview_form, container, false);

        photoImageView = (ImageView) view.findViewById(R.id.preview_image);

        emailTextView = (TextView) view.findViewById(R.id.preview_email);
        phoneTextView = (TextView) view.findViewById(R.id.preview_phone);
        passwordTextView = (TextView) view.findViewById(R.id.preview_password);

        file = new File(photopathString);
        final android.net.Uri photoPath = Uri.fromFile(file);

        sendButton = (Button) view.findViewById(R.id.preview_sendemail);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = getContext().getResources().getString(R.string.app_name)+": "+ getContext().getResources().getString(R.string.mail_subject_add);
                String body = getContext().getResources().getString(R.string.mail_body_email)+": "+emailString +"\n\r"
                        +getContext().getResources().getString(R.string.mail_body_phone)+": "+phoneString+"\n\r";
                Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
                sendMailIntent.setType("message/rfc822");

                sendMailIntent.putExtra(Intent.EXTRA_EMAIL,emailString);
                sendMailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                sendMailIntent.putExtra(Intent.EXTRA_TEXT,body);
                sendMailIntent.putExtra(Intent.EXTRA_STREAM,photoPath);

                startActivity(Intent.createChooser(sendMailIntent,"Send Email"));
            }
        });

        updateUI();
        return view;
    }

    private void updateUI(){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photopathString, bmOptions);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 0;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photopathString, bmOptions);
        photoImageView.setImageBitmap(bitmap);

        emailTextView.setText(emailString);
        phoneTextView.setText(phoneString);
        passwordTextView.setText(passwordString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        emailString =  getArguments().getString(ARG_EMAIL,null);
        phoneString = getArguments().getString(ARG_PHONE,null);
        passwordString =  getArguments().getString(ARG_PASSWORD,null);
        photopathString =  getArguments().getString(ARG_PHOTOPATH, null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        file.delete();
    }
}
