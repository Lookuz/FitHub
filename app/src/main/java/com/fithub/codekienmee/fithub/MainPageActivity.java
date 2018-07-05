package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private OnPostBackPressed onPostBackPressed;

    private DrawerLayout mDrawerLayout; // DrawerLayout for sliding menu
    private ActionBarDrawerToggle mTogglebar;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private Fragment mainLocationFrag;
    private Fragment mainForumFrag;
    private TabLayout mainTabLayout;

    /**
     * Custom ViewPagerAdapter class made to synchronize the ViewPager class
     * with FitLocation and FitForum
     */
    private class FitViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;
        private List<String> fragmentTitles;

        public FitViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentList = new ArrayList<>();
            this.fragmentTitles = new ArrayList<>();

        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return this.fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.fragmentTitles.get(position).toString();
        }
    }

    public void setOnPostBackPressed(OnPostBackPressed onPostBackPressed) {
        this.onPostBackPressed = onPostBackPressed;
    }

    @Override
    public void onBackPressed() {
        if(this.onPostBackPressed != null) {
            this.onPostBackPressed.onPostBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        this.mFragmentManager = getSupportFragmentManager();
        this.mainLocationFrag = new LocationFragment();
        this.mainForumFrag = new ForumFragment();
        this.configureMenu();
        this.configureWidgets();
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
     * TODO: Enable separate backstacks for each ViewPager Tab
     */
    private void configureWidgets() {

        this.mainTabLayout = (TabLayout)findViewById(R.id.main_tab_layout);
        ViewPager viewPager = findViewById(R.id.main_view_pager);
        FitViewPagerAdapter viewPagerAdapter = new FitViewPagerAdapter(this.mFragmentManager);
        viewPagerAdapter.addFragment(this.mainLocationFrag, "FitLocation");
        viewPagerAdapter.addFragment(this.mainForumFrag, "FitForum");

        viewPager.setAdapter(viewPagerAdapter);
        this.mainTabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Method that initializes the functioning Maps View from Location Button Fragment.
     */
    public void initLocation() {
        if(this.mainLocationFrag != null) {
            MapsFragment mapsFragment = new MapsFragment();
            mapsFragment.setEnterTransition((Transition) new Fade().
                    setDuration(400).setStartDelay(1200));
            this.mainLocationFrag.setExitTransition((Transition) new Fade().
                    setDuration(1000).setInterpolator(new DecelerateInterpolator()));

            this.mFragmentManager.beginTransaction()
                    .replace(R.id.main_frag_view, mapsFragment).commit();
        }
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

    public void overlayFragment(int resource, Fragment fragment) {
        if (resource != 0) {
            Transition slideAnim = new Slide(resource).setDuration(200);
            fragment.setEnterTransition(slideAnim);
            fragment.setExitTransition(slideAnim);
        }
        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_frag_view, fragment)
                .addToBackStack(null)
                .commit();
    }
}
