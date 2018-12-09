package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

DisplayActivity displayActivity;

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView mMessage;
        TextView usernameBox;
        TextView mLikeCount;
        TextView mDislikeCount;
        Button like;
        Button dislike;
        ImageView image;
        /**
         * View holder assigns each aspect of each list item to the appropriate view id
         * */
        ViewHolder(View itemView) {
            super(itemView);
            this.mMessage = (TextView) itemView.findViewById(R.id.listItemText);
            this.usernameBox = itemView.findViewById(R.id.usernameText);
            this.mLikeCount = (TextView) itemView.findViewById(R.id.likeCount);
            this.mDislikeCount = (TextView) itemView.findViewById(R.id.dislikeCount);
            this.like = (Button) itemView.findViewById(R.id.likeButton);
            this.dislike = (Button) itemView.findViewById(R.id.dislikeButton);
            this.image = (ImageView) itemView.findViewById(R.id.image_message);
            this.container = itemView.findViewById(R.id.linearLayout);
        }
    }

    private ArrayList<Message> mData;
    private LayoutInflater mLayoutInflater;
    private Context myContext;

    ItemListAdapter(Context context, ArrayList<Message> data, DisplayActivity displayActivity) {
        myContext = context;
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

    /*
        code convert Base64 String to bitmaps from https://www.thepolyglotdeveloper.com/2015/06/from-bitmap-to-base64-and-back-with-native-android/
     */
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * onBindViewHolder takes the assigned view id's and inserts values and logic into them
     * the listItemText value is set to the mMessage of a message, likeCount to the mLikeCount of a message
     * the Like button calls to the putLike method when pressed.
     * @param holder stores the information of where each vale is located on the screen
     * @param position the index of the current message in the ArrayList
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Message d = mData.get(position);


        // populate the content of message
        holder.mMessage.setText(d.mMessage);
        holder.usernameBox.setText("by " + d.username);
        holder.mLikeCount.setText(Integer.toString(d.mLikeCount));
        holder.mDislikeCount.setText(Integer.toString(d.mDislikeCount));
        if(!d.mImage.equals("")) {
            holder.image.setImageBitmap(base64ToBitmap(d.mImage));
        }
        else {
            holder.image.setVisibility(View.GONE);
        }
//        holder.mMessage.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                displayActivity.getComment(d);
//            }
//        });

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

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("viewHolder", " ----------------------- Holder " + position + " is clicked--------------------");

                if(d.latitude != 360 && d.longitude != 360) {
                    // go to map
                    Intent intent = new Intent(myContext, MapsActivity.class);
                    intent.putExtra("latitude", d.latitude);
                    intent.putExtra("longitude", d.longitude);
                    myContext.startActivity(intent);
                }
                else {
                    // show toast if location is invalid
                    Toast.makeText(myContext, "There's no location for this message", Toast.LENGTH_LONG);
                }
            }
        });
    }


}
