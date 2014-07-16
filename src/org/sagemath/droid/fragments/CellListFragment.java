package org.sagemath.droid.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import com.github.johnpersano.supertoasts.SuperToast;
import org.sagemath.droid.R;
import org.sagemath.droid.activities.SageActivity;
import org.sagemath.droid.adapters.CellListAdapter;
import org.sagemath.droid.constants.StringConstants;
import org.sagemath.droid.database.SageSQLiteOpenHelper;
import org.sagemath.droid.dialogs.DeleteCellDialogFragment;
import org.sagemath.droid.dialogs.EditCellDialogFragment;
import org.sagemath.droid.models.database.Cell;
import org.sagemath.droid.utils.ToastUtils;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import java.util.ArrayList;
import java.util.List;


/**
 * CellListFragment - fragment containing list of cells in current group
 * shown in CellActivity (tablets) or CellListActivity (phones)
 *
 * @author Rasmi.Elasmar
 * @author Ralf.Stephan
 */
public class CellListFragment extends BaseListFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SageDroid:CellListFragment";

    private static final String DIALOG_DELETE_CELL = "deleteCell";
    private static final String DIALOG_EDIT_CELL = "editCell";
    private static final String ARG_GROUP = "group";

    private boolean isEditEnabled;

    private Drawable enableEditIcon, disableEditIcon;

    private List<Cell> cells = new ArrayList<Cell>();
    private SageSQLiteOpenHelper helper;
    private String group;

    private CellListAdapter adapter;
    private StickyListHeadersListView list;
    private TextView emptyView;

    public CellListFragment() {
        helper = SageSQLiteOpenHelper.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (group != null && adapter != null) {
            Log.i(TAG, "Updating Cells with group:" + group);
            cells = helper.getCellsWithGroup(group);
            adapter.updateCellList(cells);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_GROUP, group);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "In onCreate");
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.getString(ARG_GROUP) != null)) {
            this.group = savedInstanceState.getString(ARG_GROUP);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_cell_list, getContainer());

        list = (StickyListHeadersListView) view.findViewById(R.id.cell_list);
        list.setOnItemClickListener(this);

        emptyView = (TextView) view.findViewById(R.id.cell_list_empty);

        return root;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        list.setOnItemClickListener(this);
        list.setAreHeadersSticky(true);
        list.setDrawingListUnderStickyHeader(false);
        list.setFastScrollEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

            list.getWrappedList().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                private int count;

                @Override
                public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                    MenuInflater menuInflater = mode.getMenuInflater();
                    menuInflater.inflate(R.menu.menu_cell_list_action_mode, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                    Log.i(TAG, "In onPrepareActionMode");
                    if (enableEditIcon == null) {
                        enableEditIcon = getResources().getDrawable(R.drawable.ic_action_content_edit_enabled);
                        disableEditIcon = getResources().getDrawable(R.drawable.ic_action_content_edit);
                    }
                    MenuItem editItem = menu.findItem(R.id.menu_edit);
                    if (count == 1) {
                        editItem.setIcon(enableEditIcon);
                        isEditEnabled = true;
                    } else {
                        editItem.setIcon(disableEditIcon);
                        isEditEnabled = false;
                    }

                    return true;
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {


                    switch (item.getItemId()) {
                        case R.id.menu_toggle_fav:
                            toggleFavorites(getAdapter().getSelectedItemList());
                            mode.finish();
                            break;

                        case R.id.menu_delete:
                            showDeleteCellDialog(mode);
                            break;

                        case R.id.menu_edit:
                            showEditCellDialog();
                            mode.finish();
                            break;
                    }
                    return true;
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode mode) {
                    adapter.clearSelection();
                }

                @Override
                public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                    adapter.setSelection(position, checked);
                    count = adapter.getSelectedItemCount();
                    mode.setTitle(String.valueOf(count));
                    mode.invalidate();
                }
            });

        } else {
            //Show default context menu
            registerForContextMenu(list);
            isEditEnabled = true;
        }

        setContentShown(false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        //Inflate the same menu, only the actions are different
        inflater.inflate(R.menu.menu_cell_list_action_mode, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_fav:
                toggleFavorites(getAdapter().getSelectedItemList());
                break;

            case R.id.menu_delete:
                ArrayList<Cell> cellsToDelete = adapter.getSelectedItemList();
                DeleteCellDialogFragment deleteDialog = DeleteCellDialogFragment.newInstance(cellsToDelete);
                deleteDialog.setOnCellDeleteListener(new DeleteCellDialogFragment.OnCellDeleteListener() {
                    @Override
                    public void onCellDeleted() {
                        refreshAdapter();
                    }
                });
                deleteDialog.show(getActivity().getSupportFragmentManager(), DIALOG_DELETE_CELL);
                break;

            case R.id.menu_edit:
                Cell editCell = getEditCell();
                if (editCell != null) {
                    EditCellDialogFragment editDialog = EditCellDialogFragment.newInstance(editCell);
                    editDialog.show(getActivity().getSupportFragmentManager(), DIALOG_EDIT_CELL);
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cell cell = cells.get(position);
        Intent i = new Intent(getActivity().getApplicationContext(), SageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(StringConstants.ID, cell.getID());
        startActivity(i);
    }

    private void showDeleteCellDialog(final ActionMode mode) {
        final ArrayList<Cell> cellsToDelete = adapter.getSelectedItemList();
        DeleteCellDialogFragment deleteDialog = DeleteCellDialogFragment.newInstance(cellsToDelete);
        deleteDialog.setOnCellDeleteListener(new DeleteCellDialogFragment.OnCellDeleteListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onCellDeleted() {
                Log.i(TAG, "Deleting " + cellsToDelete.size() + " cells");
                if (helper.deleteCells(cellsToDelete)) {
                    refreshAdapter();
                    mode.finish();
                }
            }
        });
        deleteDialog.show(getActivity().getSupportFragmentManager(), DIALOG_DELETE_CELL);
    }


    private void showEditCellDialog() {
        if (isEditEnabled) {
            Cell editCell = getEditCell();
            if (editCell != null) {
                EditCellDialogFragment editDialog = EditCellDialogFragment.newInstance(editCell);
                editDialog.show(getActivity().getSupportFragmentManager(), DIALOG_EDIT_CELL);
            }
        } else {
            ToastUtils.getAlertToast(getActivity()
                    , R.string.toast_edit_multiple_error
                    , SuperToast.Duration.LONG).show();
        }
    }

    public void refreshAdapter() {
        if (group != null) {
            adapter.updateCellList(helper.getCellsWithGroup(group));
        }
    }

    public void setGroup(String group) {
        this.group = group;
        getActivity().setTitle(group);
        cells = helper.getCellsWithGroup(group);
        adapter = new CellListAdapter(getActivity(), cells);
        list.setAdapter(adapter);
        list.setEmptyView(emptyView);

        setContentShownNoAnimation(true);
    }

    public void switchToGroup(String group) {
        cells.clear();
        if (group == null)
            group = helper.getGroups().get(0);
        cells.addAll(helper.getCellsWithGroup(group));
        if (cells.size() > 0) {
            getActivity().setTitle(cells.get(0).getTitle());
        } else
            getActivity().getSupportFragmentManager().popBackStack();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public CellListAdapter getAdapter() {
        return adapter;
    }

    public void toggleFavorites(ArrayList<Cell> itemsToModify) {
        for (Cell cell : itemsToModify) {
            cell.setFavorite(!cell.isFavorite());
        }
        helper.saveEditedCells(itemsToModify);
        refreshAdapter();
    }

    public Cell getEditCell() {
        return adapter.getSelectedItemList().get(0);
    }

    //TODO move the options menu here, don't let activity handle

}
