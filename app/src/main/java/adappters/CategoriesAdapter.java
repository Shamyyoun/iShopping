package adappters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartinnovationtechnology.ishopping.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import datamodels.Category;

public class CategoriesAdapter extends ArrayAdapter<Category> {
    private Context context;
    private int layoutResourceId;
    private List<Category> data = null;

    public CategoriesAdapter(Context context, int layoutResourceId, List<Category> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.imageDefault = (ImageView) row.findViewById(R.id.image_default);
            holder.imageImage = (ImageView) row.findViewById(R.id.image_image);
            holder.textTitle = (TextView) row.findViewById(R.id.text_title);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Category category = data.get(position);

        // set data
        holder.textTitle.setText(category.getTitle());
        if (!category.getImage().isEmpty()) {
            final ViewHolder finalHolder = holder;
            Picasso.with(context).load(category.getImage()).into(holder.imageImage, new Callback() {
                @Override
                public void onSuccess() {
                    finalHolder.imageDefault.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                }
            });
        }

        return row;
    }

    static class ViewHolder {
        ImageView imageDefault;
        ImageView imageImage;
        TextView textTitle;
    }
}
