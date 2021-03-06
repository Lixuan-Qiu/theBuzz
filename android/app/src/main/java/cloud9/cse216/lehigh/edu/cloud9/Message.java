package cloud9.cse216.lehigh.edu.cloud9;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
    /**
     * The message id of the object
     */
    int mId;

    /**
     * The string body containing the message
     */
    String mMessage;

    /**
     * The owner of message
     */
    String username;

    /**
     * The current like count
     */
    int mLikeCount;

    /**
     * The current dislike count
     */
    int mDislikeCount;
    /**
     * The current image
     */
    String mImage;

    /**
     * The current image
     */
    double latitude;

    /**
     * The current image
     */
    double longitude;



    /**
     * Construct a Datum by setting its index and text
     *
     * @param idx The index of this message
     * @param txt The string contents of this message
     * @param likeCount The like count of this message
     * @param dislikeCount The dislike count of this message
     * @param img The string for img
     */
    Message(int idx, String txt, String username, int likeCount, int dislikeCount, String mimage, double latitude, double longitude) {
        mId = idx;
        mMessage = txt;
        this.username = username;
        mLikeCount = likeCount;
        mDislikeCount = dislikeCount;
        this.mImage = mimage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Message(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.mId = Integer.parseInt(data[0]);
        this.mMessage = data[1];
        this.mLikeCount = Integer.parseInt(data[2]);
        this.mDislikeCount = Integer.parseInt(data[3]);
        this.mImage = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                Integer.toString(mId),
                mMessage,
                Integer.toString(mLikeCount),
                Integer.toString(mDislikeCount),mImage});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
