package adgarcis.com.adgarcisacceso;

import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.service.dreams.DreamService;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NavigationDrawerFragment extends Fragment {

 
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

 
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

 
    public static NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    public static ListView mDrawerListView;
    private View mFragmentContainerView;

    public static int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private String[] tagTitles;
    ArrayList<DrawerItem> items;

    DrawerListAdapter drawerListAdapter;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        tagTitles = getResources().getStringArray(R.array.Tags);

        items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(tagTitles[0], R.drawable.ic_drawer));
        items.add(new DrawerItem(tagTitles[1], R.drawable.ic_enter_menu));
        items.add(new DrawerItem(tagTitles[2], R.drawable.ic_exit_menu));
        items.add(new DrawerItem(tagTitles[5], R.drawable.ic_settings_menu));


        drawerListAdapter = new DrawerListAdapter(getContext(), items);
        mDrawerListView.setAdapter(drawerListAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int numeroElementos = mDrawerListView.getChildCount();
                for (int i = 0; i < numeroElementos; i++) {
                    if (i == position) {
                        mDrawerListView.getChildAt(i).setBackgroundColor(getContext().getResources().getColor(R.color.gris_claro));
                    } else {
                        mDrawerListView.getChildAt(i).setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    }
                }
                if (position !=4 && position != 0) {
                    selectItem(position);
                       switch (position) {
                            case 1:
                                items.set(position, new DrawerItem(tagTitles[position], R.drawable.ic_enter_menu_azul_claro));
                                items.set(2, new DrawerItem(tagTitles[2], R.drawable.ic_exit_menu));
                                items.set(3, new DrawerItem(tagTitles[5], R.drawable.ic_settings_menu));
                                break;
                            case 2:
                                items.set(1, new DrawerItem(tagTitles[1], R.drawable.ic_enter_menu));
                                items.set(position, new DrawerItem(tagTitles[position], R.drawable.ic_exit_menu_azul_claro));
                                items.set(3, new DrawerItem(tagTitles[5], R.drawable.ic_settings_menu));

                                break;
                            case 3:
                                items.set(1, new DrawerItem(tagTitles[1], R.drawable.ic_enter_menu));
                                items.set(2, new DrawerItem(tagTitles[2], R.drawable.ic_exit_menu));
                                items.set(position, new DrawerItem(tagTitles[5], R.drawable.ic_settings_menu_azul_claro));
                                break;
                       }
                } else {
                    items.set(0, new DrawerItem(tagTitles[0], R.drawable.ic_drawer));
                    items.set(1, new DrawerItem(tagTitles[1], R.drawable.ic_enter_menu));
                    items.set(2, new DrawerItem(tagTitles[2], R.drawable.ic_exit_menu));
                    items.set(3, new DrawerItem(tagTitles[5], R.drawable.ic_settings_menu));
                    selectItem(position);
                }
                drawerListAdapter.notifyDataSetChanged();
            }
        });
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
                mDrawerListView.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.white));
            }
        };


        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }

}
