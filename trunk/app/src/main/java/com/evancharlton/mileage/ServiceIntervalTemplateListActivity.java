
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.ServiceIntervalTemplate;
import com.evancharlton.mileage.provider.FillUpsProvider;
import com.evancharlton.mileage.provider.tables.ServiceIntervalTemplatesTable;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ServiceIntervalTemplateListActivity extends BaseListActivity implements
        View.OnClickListener {
    @Override
    protected String[] getFrom() {
        return new String[] {
                ServiceIntervalTemplate.TITLE,
                ServiceIntervalTemplate.DESCRIPTION
        };
    }

    @Override
    protected Uri getUri() {
        return Uri.withAppendedPath(FillUpsProvider.BASE_URI, ServiceIntervalTemplatesTable.URI);
    }

    @Override
    public void onItemClick(long id) {
        loadItem(id, ServiceIntervalTemplateActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.service_interval_template_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_service_interval_template) {
            startActivity(new Intent(this, ServiceIntervalTemplateActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupEmptyView() {
        mEmptyView.removeAllViews();
        View emptyView = LayoutInflater.from(this).inflate(
                R.layout.empty_service_interval_templates, mEmptyView);
        emptyView.findViewById(R.id.empty_add_default_templates).setOnClickListener(this);
        emptyView.findViewById(R.id.empty_add_interval_template).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_add_interval_template:
                startActivity(new Intent(ServiceIntervalTemplateListActivity.this,
                        ServiceIntervalTemplateActivity.class));
                break;
            case R.id.empty_add_default_templates:
                final ContentResolver resolver = getContentResolver();
                new Thread() {
                    @Override
                    public void run() {
                        resolver.bulkInsert(ServiceIntervalTemplatesTable.BASE_URI,
                                ServiceIntervalTemplatesTable.TEMPLATES);
                    }
                }.start();
                break;
        }
    }
}
