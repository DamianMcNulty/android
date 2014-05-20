package org.sagemath.droid;

import java.util.LinkedList;
import java.util.ListIterator;

import junit.framework.Assert;

import org.sagemath.droid.cells.CellCollection;
import org.sagemath.droid.cells.CellData;
import org.sagemath.droid.interacts.InteractView;
import org.sagemath.singlecellserver.CommandOutput;
import org.sagemath.singlecellserver.CommandReply;
import org.sagemath.singlecellserver.Interact;
import org.sagemath.singlecellserver.SageSingleCell;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class OutputView 
		extends LinearLayout 
		implements SageSingleCell.OnSageListener, InteractView.OnInteractListener {
	private final static String TAG = "OutputView";
	
	public interface onSageListener {
		public void onSageInteractListener(Interact interact, String name, Object value);
		public void onSageFinishedListener();
	}
	
	private onSageListener listener;
	
	public void setOnSageListener(onSageListener listener) {
		this.listener = listener;
	}
		
	private LinkedList<OutputBlock> blocks = new LinkedList<OutputBlock>();
	
	private Context context;
	private CellData cell;
	
	public OutputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(VERTICAL);
        setFocusable(true);
        setFocusableInTouchMode(true);
	}
	
	@Override
	public void onSageOutputListener(CommandOutput output) {
		UpdateResult task = new UpdateResult();
		task.output = output;
		handler.post(task);
	}

	@Override
	public void onSageAdditionalOutputListener(CommandOutput output) {
		UpdateResult task = new UpdateResult();
		task.additionalOutput = output;
		handler.post(task);
	}

	@Override
	public void onSageInteractListener(Interact interact) {
		UpdateResult task = new UpdateResult();
		task.interact = interact;
		handler.post(task);
	}

	@Override
	public void onSageFinishedListener(CommandReply reason) {
		UpdateResult task = new UpdateResult();
		task.finished = reason;
		handler.post(task);
	}	
	
	private Handler handler = new Handler();
	
	/** 
	 * Retrieve the OutputBlock representing the output. Creates a new OutputBlock if necessary.
	 * @param output
	 * @return
	 */
	private OutputBlock getOutputBlock(CommandOutput output) {
		//Log.i(TAG, "getOutputBlock(): " + output.outputBlock());
		return getOutputBlock(output.outputBlock());
	}
	
	private OutputBlock getOutputBlock(String output_block) {
		ListIterator<OutputBlock> iter = blocks.listIterator();
		//Log.i(TAG, "getOutputBlock(String output_block): " + output_block);
		try {
		while (iter.hasNext()) {
			OutputBlock block = iter.next();
			if (block.getOutputBlock().equals(output_block)) {
				//Log.i(TAG, "getOutputBlock().equals(output_block): " + block + ", " + output_block);
				Log.i(TAG,"Returning block " + block.name);
				return block;
			}
		}
		} catch (Exception e) {
			Log.e(TAG, "Error getting output block.");
			return newOutputBlock();
		}
		
		Log.i(TAG, "Returning newOutputBlock()");
		
		return newOutputBlock();
	}
	
	public void setOutputBlocks(String html) {
		blocks.clear();

		OutputBlock newBlock = new OutputBlock(context, cell, html);
		newBlock.reload();
		Log.i(TAG, "Creatng new block with HTML: " + html);
		blocks.add(newBlock);
	}
	
	private OutputBlock newOutputBlock() {
		Log.i(TAG, "Creating newOutputBlock");
		OutputBlock block = new OutputBlock(context, cell);
		Log.i(TAG, "Block data: " + block.getHtml());
		Log.i(TAG, "Block data: " + block.getHTML());
		addView(block);
		//Log.i(TAG, "Creating newOutputBlock: addview: " + block.toString());
		blocks.add(block);
		block.setHistoryHTML();
		return block;
	}
	
	private class UpdateResult implements Runnable {
		private CommandOutput output;
		private CommandOutput additionalOutput;
		private Interact interact;
		private CommandReply finished;
		@Override
		public void run() {
			
			if (output != null) {
				// Log.d(TAG, "set "+output.toShortString());
				OutputBlock block = getOutputBlock(output);
				//Log.i(TAG, "Setting block output: " + output);
				block.set(output);
			}
			if (additionalOutput != null ) {
				OutputBlock block = getOutputBlock(additionalOutput);
				Log.i(TAG, "Adding additionalOutput: " + additionalOutput);
				//block.clearBlocks();
				block.add(additionalOutput);
			}
			if (interact != null) {

				InteractView interactView = new InteractView(context);
				interactView.set(interact);
				interactView.setOnInteractListener(OutputView.this);
				Log.i(TAG, "Adding interact view: " +interact.toString());
				addView(interactView, 0);
			}
			if (finished != null) 
				Log.i(TAG, "onSageFinishedListener called.");
				listener.onSageFinishedListener();
		}
	}
	
	
	/**
	 * Called during onResume. Reloads the embedded web views from cache.  
	 */
	public void onResume() {
		removeAllViews();
		blocks.clear();
		cell = CellCollection.getInstance().getCurrentCell();
		Assert.assertNotNull(cell);	
		for (String block : cell.getOutputBlocks()) {
			if (cell.hasCachedOutput(block)) {
				OutputBlock outputBlock = newOutputBlock();
				outputBlock.set(block);
			}
		}			
	}

	public void clear() {
		removeAllViews();
		blocks.clear();
		try {
			if (!blocks.isEmpty())
				blocks.getFirst().clearBlocks();
		} catch (Exception e) {
			Log.e(TAG, "Error clearing output blocks " + e.getLocalizedMessage());
		}
		if (cell != null)
			cell.clearCache();
	}

	@Override
	public void onInteractListener(Interact interact, String name, Object value) {
		listener.onSageInteractListener(interact, name, value);
	}
	
}
