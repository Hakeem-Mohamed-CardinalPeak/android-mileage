
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.Field;
import com.evancharlton.mileage.provider.FillUpsProvider;
import com.evancharlton.mileage.provider.tables.FieldsTable;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class FieldListActivity extends BaseListActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.field_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_field) {
            Intent intent = new Intent(this, FieldActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String[] getFrom() {
        return new String[] {
                Field.TITLE,
                Field.DESCRIPTION
        };
    }

    @Override
    protected Uri getUri() {
        return Uri.withAppendedPath(FillUpsProvider.BASE_URI, FieldsTable.URI_PATH);
    }

    @Override
    public void onItemClick(long id) {
        loadItem(id, FieldActivity.class);
    }

    @Override
    protected void setupEmptyView() {
        mEmptyView.removeAllViews();
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_fields, mEmptyView);
        emptyView.findViewById(R.id.empty_add_field).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FieldListActivity.this, FieldActivity.class));
            }
        });
    }
}
