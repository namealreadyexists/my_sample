package com.example.my.my_sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputFormFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "INPUT_INFO_FRAGMENT";

    private android.support.design.widget.TextInputEditText emailEditText;
    private android.support.design.widget.TextInputEditText phoneEditText;
    private android.support.design.widget.TextInputEditText passwordEditText;
    private Button viewButton;
    private ImageButton cameraButton;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout phoneTextInputLayout;

    private String photoPath;
    private String email, phone, password;

    private static final int REQUEST_PHOTO = 1; // to work with camera
    private final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    private SharedPreferences myFormPreferences;    // for saving some form info
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    private int minPasswordLength; // for password

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.input_form,container,false);

        minPasswordLength = getContext().getResources().getInteger(R.integer.min_password_length);

        /**** email part ****/
        emailTextInputLayout = view.findViewById(R.id.textInputLayoutEmail);

        emailEditText = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.input_email);
        if (email!=null){emailEditText.setText(email);}
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !validateEmail(email)){
                    emailTextInputLayout.setError(getContext().getResources().getString(R.string.error_email));
                    emailEditText.setError(getContext().getResources().getString(R.string.error_help));
                } else {
                    emailTextInputLayout.setError(null);
                }
            }
        });
        /*********************/
        /**** phone number part ****/
        phoneTextInputLayout = view.findViewById(R.id.textInputLayoutPhone);

        phoneEditText = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.input_phone);
        if(phone!=null){
            phoneEditText.setText(phone);
        }
        phoneEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    phone = makeInternationalNumber(phone);
                    phoneEditText.setText(phone);
                        if(!validatePhone(phone)){
                            phoneTextInputLayout.setError(getContext().getResources().getString(R.string.error_phone));
                            phoneEditText.setError(getContext().getResources().getString(R.string.error_help));
                        }else{
                            phoneTextInputLayout.setError(null);
                        }
                }
            }
        });
        /*********************/
        /**** password part ****/
        passwordTextInputLayout = (TextInputLayout) view.findViewById(R.id.textInputLayoutPassword);

        passwordEditText = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.input_password);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (password != null) {
                        if (!validatePassword(password)) {
                            passwordTextInputLayout.setError(getContext().getResources().getString(R.string.error_password));

                        } else {
                            passwordTextInputLayout.setError(null);
                        }
                    } else {
                        passwordTextInputLayout.setError(getContext().getResources().getString(R.string.error_password));
                    }
                }
            }
        });




        /*********************/
        if(pictureIntent!=null){
            if((ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.CAMERA)) //call camera permission
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {android.Manifest.permission.CAMERA},0);
            }
        }
        /**** camera part ****/
        cameraButton = (ImageButton) view.findViewById(R.id.input_camera);
        cameraButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        cameraButton.setColorFilter(getContext().getResources().getColor(R.color.colorWhite));
                        cameraButton.setBackground(getResources().getDrawable(R.drawable.round_shape));
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        cameraButton.setColorFilter(getContext().getResources().getColor(R.color.colorHighLight));
                        cameraButton.setBackground(getResources().getDrawable(R.drawable.round_shape_pressed));
                        takePhoto();
                        startActivityForResult(pictureIntent, REQUEST_PHOTO);
                        return true;
                }
                return false;
            }
        });

        /*********************/
        /**** view input ****/
        viewButton = (Button) view.findViewById(R.id.input_button_view);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    String formatedPhone = formatPhoneNumber(phone);
                    Intent previewIntent = PreviewFormActivity.newIntent(getContext(),email,formatedPhone,password,photoPath);
                    startActivity(previewIntent);
                }
            }
        });
        /*********************/
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_PHOTO){
                Log.i(TAG,"Requesting photo");
                if (data != null){
                    Bundle dataExtras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) dataExtras.get("data");
                }else{
                    Log.i(TAG,"Data is null, using MediaProvider");
                }
            }
        }else{
            photoPath = null;
            Toast.makeText(getContext(), "You didn't took picture",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateEmail(String string){
        if(string!=null){
            return Patterns.EMAIL_ADDRESS.matcher(string).matches();
        }
        return false;
    }

    private String makeInternationalNumber(String string){
        char[] temp = string.toCharArray();
        if(temp[0]!='+'){
            string = "+"+string;}
        return string;
    }

    private boolean validatePhone(String string){
        if(string!=null&&!string.equals("")){
            return true;
        }
        return false;
    }
    private boolean validatePassword(String string){
        if(string!=null&&!string.equals("")) {
            if(string.length()>=minPasswordLength){
                if(string.matches("[a-zA-Z]+\\d+.*")||(string.matches("\\d+[a-zA-Z]+.*"))){return true;}
                return false;
            }
            return false;
        }
        return false;
    }
    private boolean validatePhoto(String string){
        File file = new File(string);
        if(file.exists()) {return true;}
        return false;
    }
    private boolean validate(){
        if(photoPath!=null&&email!=null&&phone!=null&&password!=null){
            if(validateEmail(email)&&validatePhone(phone)&&validatePassword(password)&&validatePhoto(photoPath)){
                return true;
            } else {
                if (!validateEmail(email)){
                    Toast.makeText(getContext(),"Email is incorrect",Toast.LENGTH_SHORT).show();
                }
                if(!validatePhone(phone)){
                    Toast.makeText(getContext(),"Form input is incorrect",Toast.LENGTH_SHORT).show();
                }
                if (!validatePassword(password)){
                    Toast.makeText(getContext(),"Form input is incorrect",Toast.LENGTH_SHORT).show();
                }
                if(!validatePhone(photoPath)){
                    Toast.makeText(getContext(),"Take photo",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }else {
            if (photoPath==null){
                Toast.makeText(getContext(),"Take photo",Toast.LENGTH_SHORT).show();
            }
            if (email==null){
                Toast.makeText(getContext(),"Write email",Toast.LENGTH_SHORT).show();
            }
            if(phone==null){
                Toast.makeText(getContext(),"Write phone",Toast.LENGTH_SHORT).show();
            }
            if(password==null){
                Toast.makeText(getContext(),"Write password",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }

    private String formatPhoneNumber(String number){

        Phonenumber.PhoneNumber phoneNumber = null;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String resultingNumber = null;
        boolean isValid = false;
        PhoneNumberUtil.PhoneNumberType isMobile = null;
        try{
            phoneNumber = phoneNumberUtil.parse(number,"");
            isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            isMobile = phoneNumberUtil.getNumberType(phoneNumber);
        }
        catch (NumberParseException e){
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        if (isValid && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile|| PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE==isMobile)){
            resultingNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).substring(1);
            resultingNumber = resultingNumber.replace(phoneNumber.getCountryCode()+" ", "+"+phoneNumber.getCountryCode()+"(");
            resultingNumber = resultingNumber.replace(" ",")");
            resultingNumber = resultingNumber.replace("("," (");
            resultingNumber = resultingNumber.replace(")",") ");
            resultingNumber = resultingNumber.replace("-"," ");
        }
        return resultingNumber;
    }

    private void takePhoto(){
        if (pictureIntent.resolveActivity(getContext().getPackageManager())!=null){
            File photo = null;
            try{
                photo = createFile();
            }catch (IOException e){
                Toast.makeText(getContext(),"Error while creating file",Toast.LENGTH_SHORT).show();
            }
            if (photo!=null){
                Uri uri = FileProvider.getUriForFile(getContext(),"com.example.android.fileprovider",photo);
                if(uri!=null) {
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
            }
        }
    }

    private File createFile() throws IOException{
        String time = new SimpleDateFormat("yyyyMMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + time;
        File dir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(fileName,".jpg",dir);
        photoPath = photo.getAbsolutePath();
        return photo;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        if(email!=null){savedInstanceState.putString(KEY_EMAIL,email);}
        if(phone!=null){savedInstanceState.putString(KEY_PHONE,phone);}
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myFormPreferences = getContext().getSharedPreferences(getContext().getResources().getString(R.string.package_name), Context.MODE_PRIVATE);
        if (savedInstanceState!=null){ // restoring fields
            email = savedInstanceState.getString(KEY_EMAIL,null);
            phone = savedInstanceState.getString(KEY_PHONE,null);
        } else{ // pull it from preferences
            email = myFormPreferences.getString(KEY_EMAIL,null);
            phone = myFormPreferences.getString(KEY_PHONE,null);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (email!=null){myFormPreferences.edit().putString(KEY_EMAIL,email).apply();}
        if (phone!=null){myFormPreferences.edit().putString(KEY_PHONE,phone).apply();}
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}