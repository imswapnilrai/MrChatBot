package com.example.mrchatbot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrchatbot.R;
import com.example.mrchatbot.adapter.ChatRecyclerAdapter;
import com.example.mrchatbot.model.ChatMsgModel;
import com.example.mrchatbot.model.ChatroomModel;
import com.example.mrchatbot.model.UserModel;
import com.example.mrchatbot.utils.AndroidUtils;
import com.example.mrchatbot.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.ktx.Firebase;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    EditText inputMsg;
    String chatroomId;

    ChatRecyclerAdapter adapter;
    ImageButton sendMsgBtn,backBtn;
    ChatroomModel chatroomModel;
    TextView otherUsername;
    RecyclerView recyclerView;

    UserModel otherUser;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser= AndroidUtils.getUserModelFromIntent(getIntent());
        chatroomId= FirebaseUtils.getChatroomID(FirebaseUtils.currentUserId(),otherUser.getUserId());
        inputMsg=findViewById(R.id.chat_msg_input);
        sendMsgBtn=findViewById(R.id.msg_send_btn);
        imageView=findViewById(R.id.profile_pic_img_view);
        backBtn=findViewById(R.id.back_btn);
        otherUsername=findViewById(R.id.other_user_id);
        recyclerView=findViewById(R.id.recycler_chat_view);

        FirebaseUtils.getOtherProfilePicStorage(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri=t.getResult();
                        AndroidUtils.setProfilePic(this,uri,imageView);
                    }

                });

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());
        sendMsgBtn.setOnClickListener((v -> {
            String message=inputMsg.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);

        }));
        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView(){

        Query query = FirebaseUtils.getChatroomMsgReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMsgModel> options = new FirestoreRecyclerOptions.Builder<ChatMsgModel>()
                .setQuery(query,ChatMsgModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
        adapter.startListening();
    }

    void sendMessageToUser(String message){
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderID(FirebaseUtils.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtils.getChatRoomReference(chatroomId).set(chatroomModel);

        ChatMsgModel chatMsgModel=new ChatMsgModel(message,FirebaseUtils.currentUserId(),Timestamp.now());
        FirebaseUtils.getChatroomMsgReference(chatroomId).add(chatMsgModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            inputMsg.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtils.getChatRoomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    chatroomModel=task.getResult().toObject(ChatroomModel.class);
                    if(chatroomModel==null){
                        chatroomModel=new ChatroomModel(
                                chatroomId,
                                Arrays.asList(FirebaseUtils.currentUserId(),otherUser.getUserId()),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtils.getChatRoomReference(chatroomId).set(chatroomModel);
                    }
                }
            }
        });
    }

    void sendNotification(String message){
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser=task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject=new JSONObject();
                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("userID", currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch(Exception e){

                }
            }
        });
    }
    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer YOUR API KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}
