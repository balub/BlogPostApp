package com.example.balu.blogpostapp;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostID {
    @Exclude
    public String BlogPostId;
    public <T extends BlogPostID > T withId(@NonNull final String id){

        this.BlogPostId=id;
        return (T) this;
    }
}
