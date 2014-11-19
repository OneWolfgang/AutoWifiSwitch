package com.ikeirnez.autowifiswitch.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ikeirnez.autowifiswitch.R;

import java.util.*;

/**
 * Created by iKeirNez on 18/11/2014.
 */
public class MainMenuFragment extends ListFragment {

    private MainMenuListAdapter listAdapter;
    private List<MainMenuItem> menuItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems.add(new MainMenuItem("settings", R.drawable.main_menu_icon_settings, new SettingsFragment()));
        menuItems.add(new MainMenuItem("zones", R.drawable.main_menu_icon_zones, new Fragment())); // todo replace with real fragment

        setListAdapter(listAdapter = new MainMenuListAdapter(menuItems));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView wipText = new TextView(getActivity());
        wipText.setText("WIP (Ugly)");
        wipText.setTextSize(15);
        getListView().addHeaderView(wipText, null, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment fragmentClicked = listAdapter.getItem(position - getListView().getHeaderViewsCount()).getFragment();
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(android.R.id.content, fragmentClicked).addToBackStack(null).commit();
    }

    private class MainMenuListAdapter extends BaseAdapter {

        private List<MainMenuItem> items;

        public MainMenuListAdapter(List<MainMenuItem> items) {
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.menu_item, (ViewGroup) getActivity().findViewById(R.id.mainMenu));
            }

            MainMenuItem mainMenuItem = getItem(position);

            ViewGroup layout = (ViewGroup) convertView;
            TextView titleView = (TextView) layout.findViewById(R.id.menu_item_title);
            TextView descriptionView = (TextView) layout.findViewById(R.id.menu_item_description);
            ImageView imageView = (ImageView) layout.findViewById(R.id.menu_item_image);

            String typeName = mainMenuItem.getTypeName();
            titleView.setText(getResources().getIdentifier(typeName + "_title", "string", getActivity().getPackageName()));
            descriptionView.setText(getResources().getIdentifier(typeName + "_summary", "string", getActivity().getPackageName()));
            imageView.setImageResource(mainMenuItem.getResId());
            return layout;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public MainMenuItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}
