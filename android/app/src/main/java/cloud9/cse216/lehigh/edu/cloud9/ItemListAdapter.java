package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

DisplayActivity displayActivity;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mMessage;
        TextView mLikeCount;
        TextView mDislikeCount;
        Button like;
        Button dislike;
        /**
         * View holder assigns each aspect of each list item to the appropriate view id
         * */
        ViewHolder(View itemView) {
            super(itemView);
            this.mMessage = (TextView) itemView.findViewById(R.id.listItemText);
            this.mLikeCount = (TextView) itemView.findViewById(R.id.likeCount);
            this.mDislikeCount = (TextView) itemView.findViewById(R.id.dislikeCount);
            this.like = (Button) itemView.findViewById(R.id.likeButton);
            this.dislike = (Button) itemView.findViewById(R.id.dislikeButton);
        }
    }

    private ArrayList<Message> mData;
    private LayoutInflater mLayoutInflater;

    ItemListAdapter(Context context, ArrayList<Message> data, DisplayActivity displayActivity) {
        mData = data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.displayActivity = displayActivity;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item, null);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder takes the assigned view id's and inserts values and logic into them
     * the listItemText value is set to the mMessage of a message, likeCount to the mLikeCount of a message
     * the Like button calls to the putLike method when pressed.
     * @param holder stores the information of where each vale is located on the screen
     * @param position the index of the current message in the ArrayList
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message d = mData.get(position);
        holder.mMessage.setText(d.mMessage);
        holder.mLikeCount.setText(Integer.toString(d.mLikeCount));
        holder.mDislikeCount.setText(Integer.toString(d.mDislikeCount));

        holder.mMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                displayActivity.getComment(d);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                displayActivity.putLikeCount(d);
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                displayActivity.putDislikeCount(d);
            }
        });
    }


}
