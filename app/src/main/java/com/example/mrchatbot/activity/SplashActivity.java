package com.example.mrchatbot.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mrchatbot.R;
import com.example.mrchatbot.model.UserModel;
import com.example.mrchatbot.utils.AndroidUtils;
import com.example.mrchatbot.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.ktx.Firebase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtils.isLoggedIn() && getIntent().getExtras()!=null){
            //from notification
            String userID=getIntent().getExtras().getString("userID");
            FirebaseUtils.allUserCollectionReference().document(userID).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel model=task.getResult().toObject(UserModel.class);
                            Intent mainIntent=new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);
                            Intent intent=new Intent(this, ChatActivity.class);
                            AndroidUtils.passUserModelAsIntent(intent,model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtils.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, LoginPhoneActivity.class));
                    }
                    finish();
                }
            }, 2000);
        }


    }
}
