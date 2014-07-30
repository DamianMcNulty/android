package org.sagemath.droid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import org.sagemath.droid.R;
import org.sagemath.droid.activities.HelpActivity;
import org.sagemath.droid.activities.SettingsActivity;
import org.sagemath.droid.adapters.CellGroupsAdapter;
import org.sagemath.droid.database.SageSQLiteOpenHelper;
import org.sagemath.droid.dialogs.BaseDeleteDialogFragment;
import org.sagemath.droid.dialogs.DeleteGroupDialogFragment;
import org.sagemath.droid.dialogs.GroupDialogFragment;
import org.sagemath.droid.models.database.Group;

import java.util.List;


/**
 * CellGroupsFragment - fragment showing the group list
 *
 * @author Rasmi.Elasmar
 * @author Ralf.Stephan
 * @author Nikhil Peter Raj
 */
public class CellGroupsFragment extends ListFragment {
    private static final String TAG = "SageDroid:CellGroupsFragment";

    private static final String ARG_EDIT_GROUP_DIALOG = "groupDialog";
    private static final String ARG_DELETE_GROUP_DIALOG = "deleteGroupDialog";
    private static final String DIALOG_NEW_GROUP = "newGroup";

    private SageSQLiteOpenHelper helper;

    public interface OnGroupSelectedListener {
        public void onGroupSelected(Group group);
    }

    private OnGroupSelectedListener listener;

    public void setOnGroupSelected(OnGroupSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        Group group = groups.get(position);
        listener.onGroupSelected(group);
    }

    protected List<Group> groups;

    protected CellGroupsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = SageSQLiteOpenHelper.getInstance(getActivity());
        groups = helper.getGroups();
        adapter = new CellGroupsAdapter(getActivity().getApplicationContext(), groups);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        groups = helper.getGroups();
        adapter.refreshAdapter(groups);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_cell_group, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_cell, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                GroupDialogFragment dialog = GroupDialogFragment.newInstance(null);
                dialog.setOnActionCompleteListener(new GroupDialogFragment.OnActionCompleteListener() {
                    @Override
                    public void onActionCompleted() {
                        groups = helper.getGroups();
                        adapter.refreshAdapter(groups);
                    }
                });
                dialog.show(fm, DIALOG_NEW_GROUP);
                return true;
            }
            case R.id.menu_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.menu_group_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Group group = (Group) adapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.menu_group_edit:
                GroupDialogFragment dialog = GroupDialogFragment.newInstance(group);
                dialog.show(fm, ARG_EDIT_GROUP_DIALOG);
                return true;

            case R.id.menu_group_delete:
                DeleteGroupDialogFragment deleteDialog = DeleteGroupDialogFragment.newInstance(group);
                deleteDialog.setOnDeleteListener(new BaseDeleteDialogFragment.OnDeleteListener() {
                    @Override
                    public void onDelete() {
                        groups = helper.getGroups();
                        adapter.refreshAdapter(groups);
                    }
                });
                deleteDialog.show(fm, ARG_DELETE_GROUP_DIALOG);
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }
}
