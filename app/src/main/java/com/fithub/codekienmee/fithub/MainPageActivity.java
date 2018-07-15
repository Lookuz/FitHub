package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private DrawerLayout drawerLayout; // DrawerLayout for sliding menu
    private ActionBarDrawerToggle togglebar;
    private NavigationView navigationView;
    private android.support.v4.app.FragmentManager fragmentManager;
    // Container Fragment to hold child Location and Forum Fragments
    private ContainerFragment containerFragment;

    @Override
    public void onBackPressed() {
        if (this.containerFragment != null) {
            if (!this.containerFragment.onPostBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        this.firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if (savedInstanceState == null) {
            this.fragmentManager = getSupportFragmentManager();
            this.containerFragment = ContainerFragment.newInstance();

            this.fragmentManager.beginTransaction()
                    .replace(R.id.main_frag_view, this.containerFragment)
                    .commit();
            this.configureMenu();
        } else {
            this.containerFragment = (ContainerFragment) getSupportFragmentManager()
                    .getFragments().get(0);
        }
    }

    /**
     * Private method that configures the DrawerLayout, NavigationView and navigation ActionBar.
     */
    private void configureMenu() {
        Toolbar navHeader = (Toolbar) findViewById(R.id.main_tool_bar);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.main_page_activity);
        this.navigationView = findViewById(R.id.main_nav_view);

        setSupportActionBar(navHeader);
        if (navHeader != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        this.initNavMenu();

        this.togglebar = new ActionBarDrawerToggle(
                MainPageActivity.this, this.drawerLayout, navHeader, R.string.open, R.string.close);
        this.drawerLayout.addDrawerListener(togglebar);
        this.togglebar.syncState();
    }

    /**
     * Method that initializes the NavigationView menu items.
     */
    private void initNavMenu() {
        if (this.navigationView != null) {
            this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // TODO: Create Fragments for menu items.
                    switch ((item.getItemId())) {
                        case R.id.nav_account:
                        case R.id.nav_favourites:
                        case R.id.nav_locations:
                        case R.id.nav_schedule:
                        case R.id.nav_settings:
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Method to trigger sliding menu bar.
     *
     * @param item Menu Item to be displayed.
     * @return true if menu is displayed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.togglebar.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }
}