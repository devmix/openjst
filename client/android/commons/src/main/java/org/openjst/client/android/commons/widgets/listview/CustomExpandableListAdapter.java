/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.client.android.commons.widgets.listview;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import org.openjst.commons.dto.tuples.Pair;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public abstract class CustomExpandableListAdapter<G, I> implements ExpandableListAdapter {

    protected final ExpandableListView listView;
    protected final List<Pair<G, List<I>>> data;
    protected final int groupExpandedView;
    protected final int groupCollapsedView;
    protected final int childLayout;
    protected final LayoutInflater inflater;

    public CustomExpandableListAdapter(final ExpandableListView listView, final List<Pair<G, List<I>>> data,
                                       final int groupExpandedView, final int groupCollapsedView, final int childLayout) {
        this.listView = listView;
        this.data = data;
        this.groupExpandedView = groupExpandedView;
        this.groupCollapsedView = groupCollapsedView;
        this.childLayout = childLayout;
        this.inflater = (LayoutInflater) this.listView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void registerDataSetObserver(final DataSetObserver observer) {

    }

    public void unregisterDataSetObserver(final DataSetObserver observer) {

    }

    public int getGroupCount() {
        return getData().size();
    }

    public int getChildrenCount(final int groupPosition) {
        return getData().get(groupPosition).second().size();
    }

    public Object getGroup(final int groupPosition) {
        return getData().get(groupPosition).first();
    }

    public Object getChild(final int groupPosition, final int childPosition) {
        return getData().get(groupPosition).second().get(childPosition);
    }

    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent) {
        if (convertView == null) {
            final View view = inflater.inflate(isExpanded ? getGroupExpandedView() : getGroupCollapsedView(), parent, false);
            return onInitializeGroupView(view, getData().get(groupPosition));
        }

        return convertView;
    }

    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
        if (convertView == null) {
            final View view = inflater.inflate(getChildLayout(), parent, false);
            return onInitializeChildView(view, getData().get(groupPosition).second().get(childPosition));
        }

        return convertView;
    }

    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEmpty() {
        return getData().isEmpty();
    }

    public void onGroupExpanded(final int groupPosition) {
        // override
    }

    public void onGroupCollapsed(final int groupPosition) {
        // override
    }

    public long getCombinedChildId(final long groupId, final long childId) {
        return (groupId << 32) + 0xFFFFFFFF;
    }

    public long getCombinedGroupId(final long groupId) {
        return groupId << 32;
    }

    public List<Pair<G, List<I>>> getData() {
        return data;
    }

    public int getGroupExpandedView() {
        return groupExpandedView;
    }

    public int getGroupCollapsedView() {
        return groupCollapsedView;
    }

    public int getChildLayout() {
        return childLayout;
    }

    protected abstract View onInitializeGroupView(View view, Pair<G, List<I>> data);

    protected abstract View onInitializeChildView(View view, I data);
}
