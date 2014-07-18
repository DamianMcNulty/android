package org.sagemath.droid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import org.sagemath.droid.R;
import org.sagemath.droid.constants.IntConstants;
import org.sagemath.droid.dialogs.CellDialogFragment;
import org.sagemath.droid.fragments.CellListFragment;
import org.sagemath.droid.utils.ChangeLog;


/**
 * CellListActivity - when the CellListFragment has its own activity (phones)
 *
 * @author Rasmi.Elasmar
 * @author Ralf.Stephan
 */
public class CellListActivity extends ActionBarActivity {
    private static final String TAG = "SageDroid:CellListActivity";
    private static String ARG_GROUP = "group";

    private static final String DIALOG_NEW_CELL = "newCell";
    private ChangeLog changeLog;
    private String group;

    private CellListFragment cellListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cell_list);

        cellListFragment = (CellListFragment)
                getSupportFragmentManager().findFragmentById(R.id.cell_list_fragment);

        Intent intent = getIntent();
        if (intent == null)
            cellListFragment.switchToGroup(null);
        else {
            group = intent.getStringExtra(CellActivity.INTENT_SWITCH_GROUP);
            Log.i(TAG, "Got group:" + group);
            cellListFragment.setGroup(group);
        }

        if (savedInstanceState != null) {
            group = savedInstanceState.getString(ARG_GROUP);
            cellListFragment.setGroup(group);
        }

        setTitle(group);

        changeLog = new ChangeLog(this);
        if (changeLog.firstRun())
            changeLog.getLogDialog().show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_GROUP, group);
    }

    @Override
    public void onResume() {
        super.onResume();
        cellListFragment.setGroup(group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add: {
                FragmentManager fm = this.getSupportFragmentManager();
                CellDialogFragment dialog = CellDialogFragment.newInstance(null, IntConstants.DIALOG_NEW_CELL);
                dialog.setOnActionCompleteListener(new CellDialogFragment.OnActionCompleteListener() {
                    @Override
                    public void onActionCompleted() {
                        cellListFragment.refreshAdapter();
                    }
                });
                dialog.show(fm, DIALOG_NEW_CELL);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
