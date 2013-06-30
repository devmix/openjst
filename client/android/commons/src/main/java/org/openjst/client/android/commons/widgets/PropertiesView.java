package org.openjst.client.android.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import org.openjst.client.android.commons.R;
import org.openjst.client.android.commons.widgets.listview.CustomExpandableListAdapter;
import org.openjst.commons.dto.tuples.Pair;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public class PropertiesView extends ExpandableListView {

    private int groupCollapsedLayout;
    private int groupExpandedLayout;
    private int childHorizontalLayout;
    private int childVerticalLayout;
    private boolean preventCollapse = false;
    private List<Pair<String, List<Property>>> properties;

    public PropertiesView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.PropertiesView);
        for (int i = 0, count = styledAttributes.getIndexCount(); i < count; ++i) {
            final int attribute = styledAttributes.getIndex(i);
            if (R.styleable.PropertiesView_groupCollapsedLayout == attribute) {
                this.groupCollapsedLayout = styledAttributes.getResourceId(attribute, R.layout.ojst_properties_group_collapsed);
            } else if (R.styleable.PropertiesView_groupExpandedLayout == attribute) {
                this.groupExpandedLayout = styledAttributes.getResourceId(attribute, R.layout.ojst_properties_group_collapsed);
            } else if (R.styleable.PropertiesView_childHorizontalLayout == attribute) {
                this.childHorizontalLayout = styledAttributes.getResourceId(attribute, R.layout.ojst_properties_child_horizontal);
            } else if (R.styleable.PropertiesView_childVerticalLayout == attribute) {
                this.childVerticalLayout = styledAttributes.getResourceId(attribute, R.layout.ojst_properties_child_horizontal);
            }
        }
        styledAttributes.recycle();
    }

    public void setProperties(final List<Pair<String, List<Property>>> properties) {
        this.properties = properties;

        final CustomExpandableListAdapter<String, Property> listAdapter = new CustomExpandableListAdapter<String, Property>(this, properties,
                this.groupExpandedLayout, this.groupCollapsedLayout, this.childHorizontalLayout) {

            @Override
            public void onGroupCollapsed(final int groupPosition) {
                if (preventCollapse) {
                    listView.expandGroup(groupPosition);
                }
            }

            @Override
            public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    final Property property = getData().get(groupPosition).second().get(childPosition);
                    final View view = inflater.inflate(property.vertical ? childVerticalLayout : getChildLayout(), parent, false);

                    return onInitializeChildView(view, property);
                }
                return convertView;
            }

            @Override
            protected View onInitializeGroupView(final View view, final Pair<String, List<Property>> data) {
                ((TextView) view.findViewById(R.id.listGroup)).setText(data.first());
                return view;
            }

            @Override
            protected View onInitializeChildView(final View view, final Property data) {
                ((TextView) view.findViewById(R.id.listProperty)).setText(data.name);
                ((TextView) view.findViewById(R.id.listValue)).setText(data.value);
                return view;
            }
        };

        setGroupIndicator(null);
        setAdapter(listAdapter);
        expandIfRequired();
    }

    public boolean isPreventCollapse() {
        return preventCollapse;
    }

    public void setPreventCollapse(final boolean preventCollapse) {
        this.preventCollapse = preventCollapse;
        expandIfRequired();
    }

    private void expandIfRequired() {
        if (preventCollapse && properties != null) {
            for (int i = 0, length = properties.size(); i < length; i++) {
                this.expandGroup(i);
            }
        }
    }

    public static final class Property {
        public final String name;
        public final String value;
        public final boolean vertical;

        private Property(final String name, final String value, final boolean vertical) {
            this.name = name;
            this.value = value;
            this.vertical = vertical;
        }

        public static Property newHorizontal(final String name, final String value) {
            return new Property(name, value, false);
        }

        public static Property newVertical(final String name, final String value) {
            return new Property(name, value, true);
        }
    }
}
