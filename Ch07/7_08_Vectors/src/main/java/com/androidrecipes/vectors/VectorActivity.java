package com.androidrecipes.vectors;

import android.app.Activity;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class VectorActivity extends Activity {

    private AnimatedVectorDrawable mAnimatedDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);

        //Set the converted SVG vector as a static image
        ImageView imageView = (ImageView) findViewById(R.id.image_static);
        imageView.setImageResource(R.drawable.svg_converted);

        //Create the vector path morph animation
        imageView = (ImageView) findViewById(R.id.image_animated);

        mAnimatedDrawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.animated_check);
        imageView.setImageDrawable(mAnimatedDrawable);
    }

    public void onMorphClick(View v) {
        mAnimatedDrawable.start();
    }
}
