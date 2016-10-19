package com.androidrecipes.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorfulAdapter extends ArrayAdapter<String> {
    private static final int[] IMAGES = {
            R.drawable.bricks, R.drawable.flower,
            R.drawable.grass, R.drawable.stones,
            R.drawable.wood, R.drawable.dog
    };

    private static final String[] NAMES = {
            "Bricks", "Flower",
            "Grass", "Stones",
            "Wood", "Dog"
    };

    private SparseArray<Bitmap> mImages;
    private SparseArray<Palette.Swatch> mBackgroundColors;

    public ColorfulAdapter(Context context) {
        super(context, R.layout.item_list, NAMES);
        mImages = new SparseArray<Bitmap>(IMAGES.length);
        mBackgroundColors = new SparseArray<Palette.Swatch>(IMAGES.length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list, parent, false);
        }

        View root = convertView.findViewById(R.id.root);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        int imageId = IMAGES[position];
        if (mImages.get(imageId) == null) {
            new ImageTask().execute(imageId);

            text.setTextColor(Color.BLACK);
        } else {
            image.setImageBitmap(mImages.get(imageId));

            Palette.Swatch colors = mBackgroundColors.get(imageId);
            if (colors != null) {
                root.setBackgroundColor(colors.getRgb());
                text.setTextColor(colors.getTitleTextColor());
            }
        }

        text.setText(NAMES[position]);

        return convertView;
    }

    private class ImageResult {
        public int imageId;
        public Bitmap image;
        public Palette.Swatch colors;

        public ImageResult(int imageId, Bitmap image, Palette.Swatch colors) {
            this.imageId = imageId;
            this.image = image;
            this.colors = colors;
        }
    }

    private void updateImageItem(ImageResult result) {
        mImages.put(result.imageId, result.image);
        mBackgroundColors.put(result.imageId, result.colors);
    }

    private class ImageTask extends AsyncTask<Integer, Void, ImageResult> {

        @Override
        protected ImageResult doInBackground(Integer... params) {
            int imageId = params[0];
            //Make sure our image thumbnails aren't too large
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap image = BitmapFactory.decodeResource(getContext().getResources(),
                    imageId, options);

            Palette colors = Palette.generate(image);
            Palette.Swatch selected = colors.getVibrantSwatch();
            if (selected == null) {
                selected = colors.getMutedSwatch();
            }

            return new ImageResult(imageId, image, selected);
        }

        @Override
        protected void onPostExecute(ImageResult result) {
            updateImageItem(result);
            notifyDataSetChanged();
        }
    }
}
