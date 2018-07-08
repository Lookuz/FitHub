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
import java.util.Stack;

/**
 * Fragment that holds the fragments for ViewPager to use in MainPageActivity.
 */
public class ContainerFragment extends Fragment implements OnPostBackPressed {

    private ForumFragment forumFragment;

    /**
     * Custom ViewPagerAdapter class made to synchronize the ViewPager class
     * with FitLocation and FitForum
     */
    private class FitViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;
        private List<String> fragmentTitles;
        private FragmentManager fragmentManager;

        public FitViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
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
            return this.fragmentTitles.get(position);
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
        if (this.forumFragment == null) {
            this.forumFragment = new ForumFragment();
            this.viewPagerAdapter.addFragment(this.forumFragment, "FitForum");
        }
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
        this.forumFragment.getPostStack().push(fragment);
    }

    @Override
    public boolean onPostBackPressed() {
        if (!this.forumFragment.getPostStack().isEmpty()) {
            ((OnPostBackPressed) this.forumFragment.getPostStack().pop()).onPostBackPressed();
            if (this.forumFragment.getPostStack().isEmpty()) {
                this.forumFragment.onResume();
            }
            return true;
        }
        return false;
    }
}