package com.example.kr.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.kr.R;
import com.example.kr.activity.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.GeoObjectTapEvent;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.GeoObjectSelectionMetadata;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

public class MapFragment extends Fragment implements UserLocationObjectListener, GeoObjectTapListener, InputListener, Session.SearchListener, CameraListener {

    private UserLocationLayer userLocationLayer;
    private MapView mapView;
    private final String query = "Компьютерный ремонт и услуги";
    public MapFragment() {
    }
    private SearchManager searchManager;
    private Session searchSession;

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMapWindow().getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) getActivity()).hideFragment();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = root.findViewById(R.id.mapview);

        mapView.getMapWindow().getMap().setRotateGesturesEnabled(false);
        mapView.getMapWindow().getMap().addCameraListener(this);
        mapView.getMapWindow().getMap().move(new CameraPosition(new Point(55.669951, 37.480228), 14, 0, 0));


        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();

        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        mapView.getMapWindow().getMap().addTapListener(this);
        mapView.getMapWindow().getMap().addInputListener(this);
        userLocationLayer.setObjectListener(this);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.return_position);
        floatingActionButton.setOnClickListener(v ->{
            if(userLocationLayer.cameraPosition()!=null)
                mapView.getMapWindow().getMap().move(userLocationLayer.cameraPosition());
            else
                mapView.getMapWindow().getMap().move(new CameraPosition(new Point(55.669951, 37.480228), 14, 0, 0));
        });
        submitQuery(query);


        return root;
    }



    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                requireContext(), R.drawable.ic_account));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_edit_off),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_edit_on),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
        submitQuery(query);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
        submitQuery(query);
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        submitQuery(query);
    }

    @Override
    public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
        final GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent
                .getGeoObject()
                .getMetadataContainer()
                .getItem(GeoObjectSelectionMetadata.class);

        if (selectionMetadata != null) {
            mapView.getMapWindow().getMap().selectGeoObject(selectionMetadata);
        }
        submitQuery(query);
        return selectionMetadata != null;
    }

    @Override
    public void onMapTap(@NonNull Map map, @NonNull Point point) {
        mapView.getMapWindow().getMap().deselectGeoObject();
        submitQuery(query);
    }

    @Override
    public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
        submitQuery(query);

    }

    @Override
    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMapWindow().getMap().getMapObjects();
        mapObjects.clear();
        final ImageProvider searchResultImageProvider = ImageProvider.fromResource(requireContext(),  R.drawable.search_result);
        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            final Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                Log.i("Map", resultLocation.toString());
                mapObjects.addPlacemark(placemark -> {
                    placemark.setGeometry(resultLocation);
                    placemark.setIcon(searchResultImageProvider);
                });
            }
        }
    }


    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = "Ошибка";
        if (error instanceof RemoteError) {
            errorMessage = "Remote Error";
        } else if (error instanceof NetworkError) {
            errorMessage = "Ошибка подключения к сети";
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(
            Map map,
            CameraPosition cameraPosition,
            CameraUpdateReason cameraUpdateReason,
            boolean finished) {
        if(finished)
            submitQuery(query);
    }
}