package com.example.module2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.module2.databinding.ActivityRegisterBinding;
import com.example.module2.utilities.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    String encodedImage;
    ActivityRegisterBinding binding;
    com.example.module2.PreferenceManager preferenceManager;
    TextInputEditText editTextEmail, editTextPassword, editTextName, editTextConfirmPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot() );
        setListener();
        preferenceManager = new PreferenceManager(getApplicationContext());

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();

        }
    }


    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent>pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.profile.setImageBitmap(bitmap);
                            binding.addImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private  void loading(Boolean  isloading){
        if(isloading){
            binding.btnRegister.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);

        }else {
            binding.btnRegister.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void setListener(){
        binding.layoutImage.setOnClickListener(V -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });
        mAuth=FirebaseAuth.getInstance();
        editTextName=findViewById(R.id.name);
        editTextConfirmPassword=findViewById(R.id.confirm_password);
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        buttonReg=findViewById(R.id.btn_register);
        progressBar=findViewById(R.id.progressBar);




        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, name, confirm_password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                name = editTextEmail.getText().toString();
                confirm_password= editTextEmail.getText().toString();

                loading(true);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                HashMap<String, Object > user = new HashMap<>();
                user.put(Constant.KEY_NAME,binding.name.getText().toString().trim());
                user.put(Constant.KEY_EMAIL, binding.email.getText().toString().trim());
                user.put(Constant.KEY_PASSWORD, binding.password.getText().toString().trim());
                user.put(Constant.KEY_IMAGE, encodedImage);
                database.collection(Constant.KEY_COLLECTION_USER)
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            loading(false);
                            preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constant.KEY_USER_ID, documentReference.getId());
                            preferenceManager.putString(Constant.KEY_NAME,binding.name.getText().toString());
                            preferenceManager.putString(Constant.KEY_IMAGE,encodedImage);
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }).addOnFailureListener(exception->{
                            loading(false);
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();


                        });



                if(encodedImage==null){
                    Toast.makeText(Register.this,"select image",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(Register.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirm_password)){
                    Toast.makeText(Register.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Register Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    loading(false);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }


}