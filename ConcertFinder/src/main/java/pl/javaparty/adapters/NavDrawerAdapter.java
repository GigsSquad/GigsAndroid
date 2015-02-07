package pl.javaparty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.NavDrawerItem;

import java.util.ArrayList;

public class NavDrawerAdapter extends BaseExpandableListAdapter {
	ArrayList<NavDrawerItem> items;
	Context context;

	public NavDrawerAdapter(Context context, ArrayList<NavDrawerItem> items) {
		this.items = items;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return items.get(groupPosition).getSubmenu() == null ? 0 : items.get(groupPosition).getSubmenu().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return items.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (items.get(groupPosition).getSubmenu() == null)
			return null;
		else {
			return items.get(groupPosition).getSubmenu().get(childPosition);
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		//noone cares
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		//noone cares
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		NavDrawerItem group = (NavDrawerItem) getGroup(groupPosition);
		if (group.getSubmenu() == null) {
			/*
			 * W przypadku gdy brak submenu, czyli normalny przycisk.
			 */
			if (convertView == null) {
				LayoutInflater li = LayoutInflater.from(context);
				convertView = li.inflate(R.layout.drawer_list_item, null);
			}
			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
			TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
			TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

			imgIcon.setImageResource(items.get(groupPosition).getIcon());
			txtTitle.setText(items.get(groupPosition).getTitle());

			if (items.get(groupPosition).getCounterVisibility()) {
				txtCount.setText(items.get(groupPosition).getCount());
			} else {
				txtCount.setVisibility(View.GONE);
			}
			return convertView;
		}
		/*
		 * Rozwijane menu.
		 */
		else {
			if (convertView == null) {
				LayoutInflater li = LayoutInflater.from(context);
				convertView = li.inflate(R.layout.nav_drawer_expandable_group, null);
			}
			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.iconEx);
			TextView text = (TextView) convertView.findViewById(R.id.expTextView);
			ImageView indicator = (ImageView) convertView.findViewById(R.id.explist_indicator);
			TextView txtCount = (TextView) convertView.findViewById(R.id.counterEx);

			imgIcon.setImageResource(items.get(groupPosition).getIcon());
			text.setText(group.getTitle());

			if (isExpanded)
				indicator.getDrawable().setState(new int[] { android.R.attr.state_expanded });
			else
				indicator.getDrawable().setState(new int[] { });
			//text.setChecked(isExpanded);

			if (items.get(groupPosition).getCounterVisibility()) {
				txtCount.setText(items.get(groupPosition).getCount());
			} else {
				txtCount.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String children = (String) getChild(groupPosition, childPosition);
		TextView text = null;
		if (convertView == null) {
			LayoutInflater li = LayoutInflater.from(context);
			convertView = li.inflate(R.layout.nav_drawer_group_details, null);
		}
		text = (TextView) convertView.findViewById(R.id.textView1);
		text.setText(children);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
