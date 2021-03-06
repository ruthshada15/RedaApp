package com.example.redaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.UUID;

public class CustomerSignupActivity extends AppCompatActivity {
    private EditText stremail, strpassword, strfname, strlname, strphonenum, strconfirmpass;
    private Button getstarted;

    private ImageView customerprofilepic;
    private Uri imageUri = null;
    private final int select_photo = 1;

    private ProgressDialog loadingBar;

    private FirebaseAuth myAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        myAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        loadingBar = new ProgressDialog(this);

        stremail = (EditText) findViewById(R.id.signuptxtemail);
        strpassword = (EditText) findViewById(R.id.signuptxtpassword);
        strfname = (EditText) findViewById(R.id.txtcustomerfname);
        strlname = (EditText) findViewById(R.id.txtcustomerlname);
        strphonenum = (EditText) findViewById(R.id.txtcustomerphonenumber);
        strconfirmpass = (EditText) findViewById(R.id.txtcustomerconfirmpassword);
        getstarted = (Button) findViewById(R.id.btngetstarted);

        customerprofilepic = (ImageView) findViewById(R.id.customerprofilepicture);

        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = stremail.getText().toString().trim();
                String password = strpassword.getText().toString().trim();
                String fname = strfname.getText().toString().trim();
                String lname = strlname.getText().toString().trim();
                String phonenum = strphonenum.getText().toString().trim();
                String confirmpass = strconfirmpass.getText().toString().trim();

                validation(email,password,fname,lname,phonenum,confirmpass);


            }
        });
            onclickimage();

    }


    private void validation(String a, String b, String c, String d, String e, String f) {
        if (TextUtils.isEmpty(a)) {
            stremail.setError("Enter your Email Adress");
            stremail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(c)) {
            strfname.setError("Enter your First Name");
            strfname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(d)) {
            strlname.setError("Enter your Last Name");
            strlname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(e)) {
            strphonenum.setError("Enter your Phone Number");
            strphonenum.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(f)) {
            strconfirmpass.setError("You need to confirm your password");
            strconfirmpass.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher((CharSequence) a).matches()) {
            stremail.setError("Enter a Valid* Email Adress");
            stremail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(b)) {
                strpassword.setError("Enter your Password");
                strpassword.requestFocus();
                return;
        }

        if (b.length() < 6) {
            strpassword.setError("Password is too short");
            strpassword.requestFocus();
            return;
        }
        if (!b.equals(f)) {
            strconfirmpass.setError("Passwords don't match");
            strpassword.setError("Passwords don't match");
            strconfirmpass.requestFocus();
            strpassword.requestFocus();
            return;
        }

        else {
            loadingBar.setTitle("User Registration");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();
            myAuth.createUserWithEmailAndPassword(a, b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String id = myAuth.getCurrentUser().getUid();


                    if (task.isSuccessful()) {
                        // Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                            saveCustomerInformation();
                            uploadImage();

                        startActivity(new Intent(getApplicationContext(), LandingPageActivity.class));
                    } else {
                        // Toast.makeText(getApplicationContext(), "nooo", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }

    }

    private void saveCustomerInformation (){

        String fname = strfname.getText().toString().trim();
        String lname = strlname.getText().toString().trim();
        String phonenum = strphonenum.getText().toString().trim();

        CustomerInformation customerInformation = new CustomerInformation(fname,lname,phonenum);
        FirebaseUser user = myAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(customerInformation);

    }


    //selecting and displaying image

    private void onclickimage(){
        customerprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);
            }
        });
    }


    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);

        if (requestcode == select_photo)
        {
            if (resultcode == RESULT_OK) {
                try {

                    imageUri = imagereturnintent.getData();

                    Bitmap bitmap = Utils.decodeUri(getApplicationContext(),
                            imageUri, 200);// call

                    if (bitmap != null) {
                        customerprofilepic.setImageBitmap(bitmap);


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error while decoding image.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File not found.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

private void uploadImage() {

    if (imageUri != null) {
        StorageReference riversRef = storageReference.child("images/" + UUID.randomUUID().toString());

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(getApplicationContext(), "Image Upload Failed!" + exception.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }

}
}
