package edu.gsu.sensordatacollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.gsu.sensordatacollection.R;
import edu.gsu.sensordatacollection.model.Item;

public class GridViewAdapter extends ArrayAdapter<Item> {

    public GridViewAdapter(@NonNull Context context, ArrayList<Item> itemList) {
        super(context, 0, itemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        Item item = getItem(position);
        TextView zoneId = listitemView.findViewById(R.id.zone_id);

        zoneId.setText(item.getZone());
        return listitemView;
    }
}
