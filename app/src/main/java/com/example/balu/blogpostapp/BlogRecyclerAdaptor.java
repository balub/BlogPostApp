package com.example.balu.blogpostapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import junit.framework.Test;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogRecyclerAdaptor extends RecyclerView.Adapter<BlogRecyclerAdaptor.ViewHolder> {
    private List<BlogPost> blog_list;
    private Context context;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    public BlogRecyclerAdaptor(List<BlogPost> blog_list){
        this.blog_list = blog_list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_file, parent, false);
        context = parent.getContext();
        mFirestore = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {  // this method is responsible for fetching data from the Blogpost model class and setting the views in the holder with the right data
        holder.setIsRecyclable(false);
        final String blogPostId=blog_list.get(position).BlogPostId;
        final String currentUserID = mAuth.getCurrentUser().getUid();
        String desc_data = blog_list.get(position).getDesc();//retrieve data from BlogPost model class
        holder.setDescText(desc_data);//execute the method defined below with the input being desc_data
        String Image_Url = blog_list.get(position).getImage_url();//retrieve data from BlogPost model class
        String thumb_Url = blog_list.get(position).getThumb();//retrieve data from BlogPost model class

        holder.setBlogImg(Image_Url,thumb_Url);//execute the method defined below with the input being desc_data
        final String UserID = blog_list.get(position).getUser_id();
        mFirestore.collection("Users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                     if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()) {
                            holder.setUserIdText((String) documentSnapshot.get("Name").toString());
                            holder.setUserProfile(documentSnapshot.get("ImgLink").toString());
                            /*holder.setUserIdText(task.getResult().getString("Name"));
                            holder.setUserProfile(task.getResult().getString("ImgLink"));*/
                        }
                    }

            }
        });
        holder.blogCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("blog_post_id", blogPostId);
                context.startActivity(intent);

            }
        });
        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTimeText(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        /*********************like count******************/
        mFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    holder.UpdateLikes(count);
                }else{
                    holder.UpdateLikes(0);

                }
            }
        });

        /******************Like icon feature***********/

        mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.blogLikeImgView.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }else{
                    holder.blogLikeImgView.setImageDrawable(context.getDrawable(R.mipmap.action_like_grey));

                }
            }
        });

        /*************Likes Feature***************/
        holder.blogLikeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String,Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserID).set(likesMap);
                        }else{
                            mFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserID).delete();

                        }
                    }
                });

            }
        });
        }



    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private View mView;
        private TextView descView,UserIdView,timeStampView;
        private ImageView blogImgView,UserProfileView,thumbImgView;
        private ImageView blogLikeImgView;
        private TextView  blogLikeCounter;
        private ImageView blogCommentImg;
        private TextView blogCommentCounter;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeImgView= (ImageView) mView.findViewById(R.id.blog_like);
            blogLikeCounter=(TextView) mView.findViewById(R.id.blog_like_count);
            blogCommentImg = (ImageView)mView.findViewById(R.id.blog_comment_img);
            blogCommentCounter = (TextView) mView.findViewById(R.id.blog_comment_count);
        }
        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }
        public  void setBlogImg(String downloadUri,String thumb_Url){
            blogImgView = mView.findViewById(R.id.blog_img);
            RequestOptions requestOptions = new RequestOptions() ;
            requestOptions.placeholder(R.drawable.erdtqth);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(Glide.with(context).load(thumb_Url))
                    .into(blogImgView);
        }
        public void setUserIdText(String descText){

            UserIdView = mView.findViewById(R.id.blog_user_name);
            UserIdView.setText(descText);

        }

        public void setUserProfile(String imgLink) {
            UserProfileView = mView.findViewById(R.id.blog_user_img);
            Glide.with(context).load(imgLink).into(UserProfileView);
        }
        public void setTimeText(String descText){

            timeStampView = mView.findViewById(R.id.blog_date);
            timeStampView.setText(descText);

        }

        public void UpdateLikes(int count) {
            blogLikeCounter= (TextView)mView.findViewById(R.id.blog_like_count);
            blogLikeCounter.setText(count + " Likes");
        }

    }
}
