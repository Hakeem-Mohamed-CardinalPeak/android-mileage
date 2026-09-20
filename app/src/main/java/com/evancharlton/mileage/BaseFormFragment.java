
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.Dao;
import com.evancharlton.mileage.exceptions.InvalidFieldException;
import com.evancharlton.mileage.provider.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class BaseFormFragment extends Fragment {
    protected SharedPreferences mPreferences;

    private Button mSaveBtn;

    private Bundle mArguments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mArguments = getArguments();
        mPreferences = requireContext().getSharedPreferences(Settings.NAME, android.content.Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.base_form_fragment, container, false);
        LinearLayout stub = root.findViewById(R.id.contents);
        LayoutInflater.from(requireContext()).inflate(getContentLayoutResId(), stub);
        BaseActivity.applyBottomInset(root.findViewById(R.id.save_btn));
        return root;
    }

    protected abstract int getContentLayoutResId();

    @Override
    public void onResume() {
        super.onResume();

        initUI();

        mSaveBtn = requireView().findViewById(R.id.save_btn);
        mSaveBtn.setText(getString(getCreateString()));
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setFields();
                    if (getDao().save(requireContext())) {
                        if (postSaveValidation()) {
                            saved();
                        }
                    }
                } catch (InvalidFieldException e) {
                    handleInvalidField(e);
                }
            }
        });

        Long id = mArguments != null ? mArguments.getLong(BaseFormActivity.EXTRA_ITEM_ID,
                getDao().getId()) : getDao().getId();
        if (id != null && id != getDao().getId()) {
            Uri uri = getUri(id);
            Cursor cursor = requireContext().getContentResolver().query(uri, getProjectionArray(),
                    null, null, null);
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                getDao().load(cursor);
                populateUI();
                mSaveBtn.setText(R.string.save_changes);
            }
        }
    }

    protected void handleInvalidField(InvalidFieldException e) {
        TextView field = e.getField();
        if (field == null) {
            Toast.makeText(requireContext(), getString(e.getErrorMessage()), Toast.LENGTH_LONG)
                    .show();
        } else {
            field.setError(getString(e.getErrorMessage()));
            field.requestFocus();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.base_form, menu);
        menu.findItem(R.id.menu_delete).setVisible(getDao().isExistingObject() && canDelete());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_title_delete)
                    .setMessage(R.string.dialog_message_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getDao().delete(requireContext())) {
                                deleted();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void deleted() {
        requireActivity().finish();
    }

    protected boolean postSaveValidation() {
        return true;
    }

    protected void saved() {
        requireActivity().finish();
    }

    protected boolean canDelete() {
        return true;
    }

    abstract protected int getCreateString();

    abstract protected Dao getDao();

    abstract protected void initUI();

    abstract protected void populateUI();

    abstract protected void setFields() throws InvalidFieldException;

    abstract protected String[] getProjectionArray();

    abstract protected Uri getUri(long id);
}
