package adappters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartinnovationtechnology.ishopping.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import datamodels.Product;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private Context context;
    private List<Product> data;
    private int layoutResourceId;

    private OnItemClickListener onItemClickListener;

    public ProductsAdapter(Context context, List<Product> data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = data.get(position);
        // set data
        if (!product.getImage().isEmpty()) {
            Picasso.with(context).load(product.getImage()).into(holder.imageImage);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                layoutResourceId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public void setOnItemClickListener(
            final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageImage;

        public ViewHolder(View v) {
            super(v);
            imageImage = (ImageView) v.findViewById(R.id.image_image);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    /**
     * method, used to add item to adapter
     */
    public void add(Product product) {
        data.add(product);
        notifyItemInserted(data.size() - 1);
    }
}