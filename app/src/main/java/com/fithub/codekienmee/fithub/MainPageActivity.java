package com.fithub.codekienmee.fithub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Stack;

public class MainPageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser fbUser;
    private FitUser user; // Current User.
    private Stack<Fragment> fragmentStack;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private DrawerLayout drawerLayout; // DrawerLayout for sliding menu
    private ActionBarDrawerToggle togglebar;
    private NavigationView navigationView;
    private android.support.v4.app.FragmentManager fragmentManager;
    // Container Fragment to hold child Location and Forum Fragments
    private ContainerFragment containerFragment;

    public FitUser getUser() {
        return user;
    }

    private class SyncUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    // If user not in Firebase DB, update.
                    if (!dataSnapshot.child("users").hasChild(fbUser.getUid())) {
                        user.setName(fbUser.getDisplayName());
                        user.setEmail(fbUser.getEmail());
                        ProfileManager.signedUp(MainPageActivity.this, user);
                        databaseReference.child("users").child(fbUser.getUid()).setValue(user);
                    } else {
                        // Else pull information from DB
//                        user = dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class);
                        user.setName(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getName());
                        user.setEmail(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getEmail());
                        user.setTimeline(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getTimeline());
                        user.setFavouritePostKeys(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getFavouritePostKeys());
                        user.setFavouriteLocationsKey(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getFavouriteLocationsKey());
                        user.setPostsKeys(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getPostsKeys());
                        user.setUserSettings(new HashMap<String, Boolean>()); // TODO: Create and load user settings.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawers();
        } else if (this.fragmentStack.size() > 0 &&
                !(this.fragmentStack.peek() instanceof ContainerFragment)) {

            if (this.fragmentStack.peek() instanceof FavouritesFragment &&
                    ((FavouritesFragment) this.fragmentStack.peek()).onBackPressed()) {
                return;
            } else if (this.fragmentStack.peek() instanceof ProfileFragment &&
                    ((ProfileFragment) this.fragmentStack.peek()).onBackPressed()) {
                return;
            } else {
                this.fragmentManager.popBackStack();
                this.fragmentStack.pop();
            }

        } else if (this.containerFragment != null) {
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
        this.fragmentStack = new Stack<>();
        this.user = new FitUser();
        this.initFirebase();
        this.configureMenu();

        if (savedInstanceState == null) {
            this.fragmentManager = getSupportFragmentManager();
            this.containerFragment = ContainerFragment.newInstance();

            this.fragmentManager.beginTransaction()
                    .replace(R.id.main_frag_view, this.containerFragment)
                    .commit();
            this.fragmentStack.push(this.containerFragment);
        } else {
            this.containerFragment = (ContainerFragment) this.fragmentManager
                    .getFragments()
                    .get(0);
        }
    }

    /**
     * Initializes Firebase tools.
     */
    private void initFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.fbUser = this.firebaseAuth.getCurrentUser();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference();
        new SyncUser().execute();
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

        this.togglebar = new ActionBarDrawerToggle(MainPageActivity.this, this.drawerLayout,
                navHeader, R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.bringToFront();
                drawerView.requestLayout();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        this.drawerLayout.addDrawerListener(togglebar);
        this.togglebar.syncState();
    }

    /**
     * Method that initializes the NavigationView menu items.
     */
    private void initNavMenu() {
        if (this.navigationView != null) {
            this.navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch ((item.getItemId())) {
                        case R.id.nav_account:
                            onMenuItemClick(ProfileFragment.newInstance(user));
                            break;
                        case R.id.nav_favourites:
                            onMenuItemClick(FavouritesFragment.newInstance());
                            break;
//                        case R.id.nav_schedule:
//                            break;
                        case R.id.nav_settings:
//                            onMenuItemClick(SettingsFragment.newInstance(user));
                            break;
                        case R.id.nav_exit:
                            Log.d("NavMenu", "Clicked");
                            firebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainPageActivity.this, StartUpActivity.class));
                            finish();
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Method to switch menu item fragments on click.
     */
    private void onMenuItemClick(Fragment fragment) {
        if (!this.fragmentStack.isEmpty()) {
            // Same item
            if (this.fragmentStack.peek().getClass().equals(fragment.getClass())) {
                Log.d("MenuItemClick: ", "Same Item");
                this.drawerLayout.closeDrawers();
                return;
            } else {
                // Remove previous menu item.
                Log.d("MenuItemClick: ", "Remove previous item");
                this.fragmentManager.popBackStack();
                this.fragmentStack.pop();
            }
        }

        ForumFragment.setSlideAnim(Gravity.BOTTOM, fragment);
        this.fragmentManager.beginTransaction()
                .add(R.id.main_frag_view, fragment)
                .addToBackStack(null)
                .commit();
        this.fragmentStack.push(fragment);
        this.drawerLayout.closeDrawers();
    }

    public boolean hasUser() {
        return this.user != null;
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

    public void makeSnackBar(String message) {
        // TODO: Style snackbar.
        Snackbar.make(findViewById(R.id.main_page_activity), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.child("users").child(fbUser.getUid()).child("timeline")
                .setValue(user.getTimeline());
        databaseReference.child("users").child(fbUser.getUid())
                .child("postsKeys").setValue(user.getPostsKeys());
        databaseReference.child("users").child(fbUser.getUid())
                .child("favouritePostKeys").setValue(user.getFavouritePostKeys());
//        databaseReference.child("users").child(fbUser.getUid())
//                .child("favouriteLocationKeys").setValue(user.getFavouriteLocationsKey());
//        databaseReference.child("users").child(fbUser.getUid()).setValue(FitUser.class);
    }

    @Override
    protected void onDestroy() {
        onPause();
        super.onDestroy();
    }
}