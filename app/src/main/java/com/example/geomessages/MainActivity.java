package com.example.geomessages;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.geomessages.data.AppExecutors;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.databinding.ActivityMainBinding;
import com.example.geomessages.ui.liste.ListeFragment;
import com.example.geomessages.ui.liste.ListeViewModel;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MessagesRoomDatabase mDb;
    private ListeViewModel listeViewModel;
    private TextView tvNom;
    private TextView tvPrenom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = MessagesRoomDatabase.getDatabase(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_liste, R.id.nav_maps)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        listeViewModel = new ViewModelProvider(this).get(ListeViewModel.class);

        Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        assert navHostFragment != null;
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (currentFragment instanceof ListeFragment) {
            ListeFragment frag = (ListeFragment) currentFragment;
            frag.loadMessages();
        }

//        tvPrenom = findViewById(R.id.tv_prenom);
//        tvPrenom.setText("Cegep");
//        tvNom = findViewById(R.id.tv_nom);
//        tvNom.setText("Garneau");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AppExecutors.getInstance().diskIO().execute(() -> {
                    mDb.messageDao().deleteAll();
                });
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//
//    public void showModal(String title, int id) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title);
//
//        Context context = getContext();
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        final EditText titre = new EditText(context);
//        final EditText info = new EditText(context);
//
//        titre.setSingleLine();
//        info.setSingleLine();
//
//        if (id == -1) {
//            titre.setHint("Titre");
//            info.setHint("Description");
//        } else {
//            AppExecutors.getInstance().diskIO().execute(() -> {
//                Tache tache = mDb.todoDao().getTodo(id);
//                titre.setText(tache.getTitle());
//                info.setText(tache.getInfo());
//            });
//        }
//
//        layout.addView(titre);
//        layout.addView(info);
//        builder.setView(layout);
//        builder.setPositiveButton("Valider", (dialog, which) -> {
//        });
//        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
//                !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());
//
//        TextWatcher watcher = new TextWatcher() {
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
//                        !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        };
//
}