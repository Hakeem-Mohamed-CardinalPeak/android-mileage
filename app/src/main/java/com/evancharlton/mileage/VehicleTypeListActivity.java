
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.VehicleType;
import com.evancharlton.mileage.provider.FillUpsProvider;
import com.evancharlton.mileage.provider.tables.VehicleTypesTable;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

public class VehicleTypeListActivity extends BaseListActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vehicle_type_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_vehicle_type) {
            startActivity(new Intent(this, VehicleTypeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String[] getFrom() {
        return new String[] {
                VehicleType.TITLE,
                VehicleType.DESCRIPTION
        };
    }

    @Override
    protected Uri getUri() {
        return Uri.withAppendedPath(FillUpsProvider.BASE_URI, VehicleTypesTable.URI);
    }

    @Override
    public void onItemClick(long id) {
        loadItem(id, VehicleTypeActivity.class);
    }

    @Override
    protected boolean canDelete(int position) {
        return getAdapter().getCount() > 1;
    }
}
