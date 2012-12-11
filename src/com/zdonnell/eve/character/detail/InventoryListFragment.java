package com.zdonnell.eve.character.detail;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zdonnell.eve.R;
import com.zdonnell.eve.api.APICallback;
import com.zdonnell.eve.api.ImageService;
import com.zdonnell.eve.api.ImageService.IconObtainedCallback;
import com.zdonnell.eve.api.character.AssetsEntity;
import com.zdonnell.eve.api.character.AssetsEntity.Item;
import com.zdonnell.eve.eve.Eve;

public class InventoryListFragment extends Fragment implements IAssetsSubFragment 
{
	
	/**
	 * Refrence to the layout to use for list item construction
	 */
	private final int stationRowResourceID = R.layout.char_detail_assets_list_item;
	
	/**
	 * Fragment Context (Activity Context)
	 */
	private Context context;
	
	/**
	 * Array of stations
	 */
	private AssetsEntity[] currentItemList;
	
	/**
	 * The main list view for the station list layout
	 */
	private GridView itemGrdiView;
	
	private boolean isFragmentCreated = false;
	
	private boolean initialLoadComplete = false;
	
	private ParentAssetsFragment parentFragment;
	
	@Override
	public void setParent(ParentAssetsFragment parent)
	{
		this.parentFragment = parent;
	}
	
	@Override
	public void assetsUpdated(AssetsEntity[] assets) 
	{	
		this.currentItemList = assets;
		if (isFragmentCreated) updateGridView();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	context = inflater.getContext();
    	LinearLayout inflatedView = (LinearLayout) inflater.inflate(R.layout.char_detail_assets, container, false);
    	
    	itemGrdiView = (GridView) inflatedView.findViewById(R.id.char_detail_assets_list);
    	
    	if (!initialLoadComplete && currentItemList != null) updateGridView();
    	
    	isFragmentCreated = true;
    	return inflatedView;
    }
	
	private void updateGridView()
	{
		itemGridView
	}
	
	private class InventoryArrayAdapter extends ArrayAdapter<AssetsEntity>
	{
		private int layoutResID;
		
		private LayoutInflater inflater;
		
		private AssetsEntity[] items;
    	
    	SparseArray<String> typeNames;
    	SparseArray<Bitmap> typeIcons;
    	
    	HashMap<TextView, Integer> typeNameMappings = new HashMap<TextView, Integer>();
    	HashMap<ImageView, Integer> typeImageMappings = new HashMap<ImageView, Integer>();
    	
    	boolean typeNamesLoaded = false;
    	
		public InventoryArrayAdapter(Context context, int layoutResourceID, AssetsEntity[] items) 
		{
			super(context, layoutResourceID, items);
			
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			this.items = items;
			this.layoutResID = layoutResourceID;
			
			typeNames = new SparseArray<String>(items.length);
			typeIcons = new SparseArray<Bitmap>(items.length);
			
			/* Pull the typeIDs from the array of assets into their own array */
			final int[] typeIDs = new int[items.length];
			for (int x = 0; x < items.length; x++) typeIDs[x] = items[x].attributes().typeID;
			
			/* get type names */
			new Eve(context).getTypeName(new APICallback<String[]>()
			{
				@Override
				public void onUpdate(String[] retTypeNames) 
				{
					/* 
					 * The returned String array matches the order provided by the input typeID array.
					 * This will pair them up in a SparseArray so the type name strings can be accessed by typeID
					 */
					for (int i = 0; i < typeIDs.length; i++) typeNames.put(typeIDs[i], retTypeNames[i]);
					obtainedTypeNames();
				}
			}, typeIDs);
			
			/* "cache" type icons */
			ImageService.getInstance(context).getTypes(null, typeIDs);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			LinearLayout itemView;
			
			/* Determine if we recyle the old view, or inflate a new one */
			if (convertView == null) itemView = (LinearLayout) inflater.inflate(layoutResID, parent, false);
			else itemView = (LinearLayout) convertView;
			
			final Item assetItem = (Item) getItem(position);
			
			setupAsset(itemView, assetItem);
			
			itemView.setOnClickListener(new View.OnClickListener() 
			{	
				@Override
				public void onClick(View v) 
				{
					if (assetItem.containsAssets()) 
					{
						ArrayList<AssetsEntity> containedAssets = assetItem.getContainedAssets();
						AssetsEntity[] assetsAsArray = new AssetsEntity[containedAssets.size()];
						containedAssets.toArray(assetsAsArray);
					}
				}
			});
			
			return itemView;
		}
		
		private void obtainedTypeNames()
		{
			typeNamesLoaded = true;
			
			/* Update Type Names */
			for (TextView textView : typeNameMappings.keySet())
			{
				int typeID = typeNameMappings.get(textView);
				textView.setText(typeNames.get(typeID));
			}
		}
		
		private void setupAsset(final LinearLayout rootView, AssetsEntity.Item item)
		{
			boolean isPackaged = !item.attributes().singleton;
			final int typeID = item.attributes().typeID;
			int count = item.attributes().quantity;
			
			/* Grab references to the views needing update */
			final TextView text = (TextView) rootView.findViewById(R.id.char_detail_assets_list_item_name);
			final ImageView icon = (ImageView) rootView.findViewById(R.id.char_detail_assets_list_item_typeIcon);
			final TextView quantity = (TextView) rootView.findViewById(R.id.char_detail_assets_list_item_count);
			final LinearLayout iconBorder = (LinearLayout) rootView.findViewById(R.id.char_detail_assets_list_item_typeIconBorder);
			
			typeNameMappings.put(text, typeID);
			if (typeNamesLoaded) text.setText(typeNames.get(typeID));
			
			icon.setTag(typeID);
			icon.setImageBitmap(null);
			
			ImageService.getInstance(context).getTypes(new IconObtainedCallback() 
			{
				@Override
				public void iconsObtained(SparseArray<Bitmap> bitmaps) 
				{
					if (((Integer) icon.getTag()).intValue() == typeID)
					{											
						//icon.setLayoutParams(new LinearLayout.LayoutParams((int) viewWidth,(int) viewWidth));
						icon.setImageBitmap(bitmaps.get(typeID));						
					}
				}
			}, typeID);
			
			quantity.setVisibility(isPackaged ? View.VISIBLE : View.INVISIBLE);
			iconBorder.setBackgroundColor(isPackaged ? Color.parseColor("#666666") : Color.parseColor("#aaaaaa"));
			
			if (isPackaged) quantity.setText(String.valueOf(count));
		}
	}
}
