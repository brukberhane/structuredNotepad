package life.sucks.org.structutrednotepad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by brukb on 6/3/2017.
 */

public class ImageFragment extends DialogFragment{

    public final static String  ARG_PATH = "life.sucks.org.structurednotepad.ARG_PATH";

    public static ImageFragment newInstance(String path){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATH, path);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog dialog = new Dialog(getActivity());
        String path = (String) getArguments().getSerializable(ARG_PATH);
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.image_view_detail, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_view_detail);

        final Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
        imageView.setImageBitmap(bitmap);
        /*
        Glide
                .with(getActivity())
                .load(path)
                .into(imageView);
                */
        PhotoViewAttacher pAttacher =  new PhotoViewAttacher(imageView);
        pAttacher.update();
        pAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(imageView);
        return dialog;
    }
}
