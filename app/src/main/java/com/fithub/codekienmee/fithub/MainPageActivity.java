package com.fithub.codekienmee.fithub;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    @Override
    public void onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawers();
        } else if (this.fragmentStack.size() > 0 &&
                !(this.fragmentStack.peek() instanceof ContainerFragment)) {

            if (this.fragmentStack.peek() instanceof FavouritesFragment &&
                    ((FavouritesFragment) this.fragmentStack.peek()).onBackPressed()) {
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
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If user not in Firebase DB, update.
                if (!dataSnapshot.child("users").hasChild(fbUser.getUid())) {
                    user.setName(fbUser.getDisplayName());
                    user.setEmail(fbUser.getEmail());
                    databaseReference.child("users").child(fbUser.getUid()).setValue(user);
                } else {
                    // Else pull information from DB.
                    user.setName(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getName());
                    user.setEmail(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getEmail());
                    user.setTimeline(dataSnapshot.child("users").child(fbUser.getUid()).getValue(FitUser.class).getTimeline());
                    user.setFavouriteLocations(new ArrayList<FitLocation>());
                    user.setFavouritePosts(new ArrayList<FitPost>());
                    user.setUserSettings(new HashMap<String, Boolean>());
                    databaseReference.child("users").child(fbUser.getUid()).setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                    // TODO: Create Fragments for menu items.
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
                            onMenuItemClick(SettingsFragment.newInstance(user));
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

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.child("users").child(fbUser.getUid()).setValue(user);
    }

    @Override
    protected void onDestroy() {
        onPause();
        super.onDestroy();
    }
}