
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.Vehicle;
import com.evancharlton.mileage.provider.tables.VehiclesTable;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class VehicleListFragment extends BaseListFragment {
    public VehicleListFragment() {
        super();
    }

    protected VehicleListFragment(BaseAdapter adapter) {
        super(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vehicle_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_edit_vehicle_types) {
            startActivity(new Intent(requireContext(), VehicleTypeListActivity.class));
            return true;
        } else if (id == R.id.menu_add_vehicle) {
            startActivity(new Intent(requireContext(), VehicleActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String[] getFrom() {
        return new String[] {
                Vehicle.TITLE,
                Vehicle.DESCRIPTION
        };
    }

    @Override
    protected Uri getUri() {
        return VehiclesTable.BASE_URI;
    }

    @Override
    public void onItemClick(long id) {
        loadItem(id, VehicleActivity.class);
    }

    @Override
    protected void addContextMenuItems(ContextMenu menu, AdapterContextMenuInfo info, long id) {
        menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.set_vehicle_as_default_menu).setIntent(
                createContextMenuIntent(Intent.ACTION_DEFAULT, id));
        super.addContextMenuItems(menu, info, id);
    }

    @Override
    protected boolean handleContextMenuSelection(Intent intent, final long itemId) {
        if (intent.getAction().equals(Intent.ACTION_DEFAULT)) {
            ContentValues values = new ContentValues();
            values.put(Vehicle.DEFAULT_TIME, System.currentTimeMillis());
            Uri uri = ContentUris.withAppendedId(VehiclesTable.BASE_URI, itemId);
            requireContext().getContentResolver().update(uri, values, null, null);
            Toast.makeText(requireContext(), getString(R.string.toast_vehicle_set_as_default),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.handleContextMenuSelection(intent, itemId);
    }

    @Override
    protected boolean canDelete(int position) {
        return getAdapter().getCount() > 1;
    }
}
