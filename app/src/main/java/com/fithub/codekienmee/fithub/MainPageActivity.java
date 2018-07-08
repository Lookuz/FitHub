package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainPageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; // DrawerLayout for sliding menu
    private ActionBarDrawerToggle mTogglebar;
    private android.support.v4.app.FragmentManager mFragmentManager;
    // Container Fragment to hold child Location and Forum Fragments
    private ContainerFragment containerFragment;

    @Override
    public void onBackPressed() {
        if (this.containerFragment != null) {
            if (!this.containerFragment.onPostBackPressed()) {
                super.onBackPressed();
            }
        } else {
            // TODO: Double tap back to exit.
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        if (savedInstanceState == null) {
            this.mFragmentManager = getSupportFragmentManager();
            this.containerFragment = ContainerFragment.newInstance();

            this.mFragmentManager.beginTransaction()
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
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.main_page_activity);
        setSupportActionBar(navHeader);
        if (navHeader != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        this.mTogglebar = new ActionBarDrawerToggle(
                MainPageActivity.this, this.mDrawerLayout, navHeader, R.string.open, R.string.close);
        this.mDrawerLayout.addDrawerListener(mTogglebar);
        this.mTogglebar.syncState();
    }

    /**
     * Method to trigger sliding menu bar.
     *
     * @param item Menu Item to be displayed.
     * @return true if menu is displayed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mTogglebar.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }
}