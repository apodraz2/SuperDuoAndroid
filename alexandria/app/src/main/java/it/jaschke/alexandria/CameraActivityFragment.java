package it.jaschke.alexandria;

import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;

import it.jaschke.alexandria.CameraPreview.CameraPreview;


/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivityFragment extends Fragment {



    public CameraActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_camera, container, false);




        return rootView;
    }

}
