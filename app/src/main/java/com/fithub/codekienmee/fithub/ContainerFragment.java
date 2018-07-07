package com.fithub.codekienmee.fithub;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that holds the fragments for ViewPager to use in MainPageActivity.
 */
public class ContainerFragment extends Fragment {

    /**
     * Custom ViewPagerAdapter class made to synchronize the ViewPager class
     * with FitLocation and FitForum
     */
    private class FitViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;
        private List<String> fragmentTitles;
        private FragmentManager fragmentManager;
        private Fragment locationFragment;
        private Fragment forumFragment;

        public FitViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
            this.fragmentList = new ArrayList<>();
            this.fragmentTitles = new ArrayList<>();
            this.locationFragment = new LocationFragment();
            this.forumFragment = new ForumFragment();

        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.locationFragment;
                case 1:
                    return this.forumFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof ForumFragment) {
                return POSITION_UNCHANGED;
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getString(R.string.fitlocation);
                case 1:
                    return getString(R.string.fitforum);
                default:
                    return null;
            }
        }

        private void replaceFragment(Fragment fragment, int position) {
            switch (position) {
                case 0:
                    if (this.locationFragment != null) {
                        this.fragmentManager.beginTransaction()
                                .remove(this.locationFragment).commit();
                        this.locationFragment = fragment;
                        notifyDataSetChanged();
                    }
                    break;
                case 1:
                    if (this.forumFragment != null) {
                        this.fragmentManager.beginTransaction()
                                .remove(this.forumFragment).commit();
                        this.forumFragment = fragment;
                        notifyDataSetChanged();
                    }
                    break;
            }
            notifyDataSetChanged();
        }
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FitViewPagerAdapter viewPagerAdapter;

    public static ContainerFragment newInstance() {
         Bundle args = new Bundle();
         ContainerFragment fragment = new ContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        this.viewPager = view.findViewById(R.id.container_view_pager);
        this.tabLayout = view.findViewById(R.id.container_tab_layout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initWidgets();
    }

    /**
     * Method that initializes widgets.
     * Widgets: ViewPager, ViewPagerAdapter, TabLayout.
     */
    private void initWidgets() {
        this.viewPagerAdapter = new FitViewPagerAdapter(getChildFragmentManager());
        this.viewPagerAdapter.addFragment(new LocationFragment(), "FitLocation");
        this.viewPagerAdapter.addFragment(new ForumFragment(), "FitForum");
        this.viewPager.setAdapter(viewPagerAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    /**
     * Method that overlays a fragment on top of another using a slide animation.
     */
    public void overlayFragment(Fragment fragment) {
        this.getChildFragmentManager().beginTransaction()
                .add(R.id.fragment_forum, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Method that replaces an existing fragment with a new one
     */
    public void replaceFragment(Fragment fragment, int position) {
        this.viewPagerAdapter.replaceFragment(fragment, position);
    }
}