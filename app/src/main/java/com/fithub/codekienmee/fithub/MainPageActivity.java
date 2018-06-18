package com.fithub.codekienmee.fithub;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainPageActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; // DrawerLayout for sliding menu
    private ActionBarDrawerToggle mTogglebar;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private Fragment currFrag;
    private Button mainLocButton;
    private Button mainForumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        this.configureMenu();
        this.configureWidgets();

        this.mFragmentManager = getSupportFragmentManager();
        if(this.currFrag == null) {
            this.currFrag = new LocationFragment();
            this.mFragmentManager.beginTransaction()
                    .add(R.id.main_frag_view, currFrag)
                    .commit();
        }
    }

    /**
     * Private method that configures the DrawerLayout, NavigationView and navigation ActionBar.
     */
    private void configureMenu() {
        Toolbar navHeader = (Toolbar)findViewById(R.id.main_tool_bar);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.main_page_activity);
        setSupportActionBar(navHeader);
        if (navHeader != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        this.mTogglebar = new ActionBarDrawerToggle(MainPageActivity.this,
               this.mDrawerLayout ,navHeader, R.string.open, R.string.close);
        this.mDrawerLayout.addDrawerListener(mTogglebar);
        this.mTogglebar.syncState();
    }

    /**
     * Private method that configures widgets in the Activity.
     * Widgets: Location Button, Forum Button, Search Bar(?)
     * TODO: Enable smooth switching between fragments without destroying state.
     */
    private void configureWidgets() {
        this.mainLocButton = (Button)findViewById(R.id.main_location_button);
        this.mainLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currFrag = new LocationFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.main_frag_view, currFrag)
                        .commit();
                Toast.makeText(MainPageActivity.this,
                        "LocationFragment activated" ,Toast.LENGTH_SHORT).show();
            }
        });
        this.mainForumButton = (Button)findViewById(R.id.main_forum_button);
        this.mainForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currFrag = new ForumFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.main_frag_view, currFrag)
                        .commit();
                Toast.makeText(MainPageActivity.this,
                        "ForumFragment activated" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to trigger sliding menu bar.
     * @param item Menu Item to be displayed.
     * @return true if menu is displayed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mTogglebar.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
