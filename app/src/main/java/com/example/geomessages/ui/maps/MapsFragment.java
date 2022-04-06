package com.example.geomessages.ui.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.geomessages.R;
import com.example.geomessages.databinding.FragmentMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int LOCATION_PERMISSION_CODE = 1234;
    private FragmentMapsBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MapsViewModel mapsViewModel =
                new ViewModelProvider(this).get(MapsViewModel.class);

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(this);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableLocation();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                    }
                });

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in sydney"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setTitle("Accès à la localisation");
                    dialog.setMessage("L'accès à la localisation est nécéssaire pour la géolocalisation");
                    dialog.setPositiveButton("Ok", (dialog1, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE));
                    dialog.setNegativeButton("Annuler", (dialog12, which) -> Toast.makeText(requireActivity(), "Impossible de géolocaliser.", Toast.LENGTH_SHORT).show());
                    dialog.show();
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.d("TAG", "Info window clicked");
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.marker_layout, null);
        TextView tvMarker = view.findViewById(R.id.tv_marker);
        tvMarker.setText(marker.getTitle());
        ImageView ivMarker = view.findViewById(R.id.iv_marker);
        ivMarker.setImageResource(R.drawable.ic_launcher_foreground);
        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}