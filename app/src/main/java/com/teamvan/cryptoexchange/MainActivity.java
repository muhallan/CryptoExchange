package com.teamvan.cryptoexchange;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.teamvan.fragments.CoinFragment;
import com.teamvan.pojos.Globals;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private static int int_items = 2;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabs_home);
        viewPager = findViewById(R.id.viewpager_home);

        /**
         *Set an Adapter for the View Pager
         *
         */

        myAdapter = new MyAdapter(getSupportFragmentManager());

        viewPager.setAdapter(myAdapter);

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager doesn't work without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

    private class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = new Bundle();

            // use the same fragment but with different arguments for the two coins
            if (position == 0) {
                args.putString("coin_name", Globals.bitcoin_name);
                fragment = new CoinFragment();
                fragment.setArguments(args);
            }
            if (position == 1) {
                args.putString("coin_name", Globals.ethereum_name);
                fragment = new CoinFragment();
                fragment.setArguments(args);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Bitcoin (BTC)" : "Ethereum (ETH)";
        }

    }
}
