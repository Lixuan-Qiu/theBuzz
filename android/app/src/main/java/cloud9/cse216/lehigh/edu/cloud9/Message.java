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
     * The current like count
     */
    int mLikeCount;

    /**
     * The current dislike count
     */
    int mDislikeCount;

    /**
     * Construct a Datum by setting its index and text
     *
     * @param idx The index of this message
     * @param txt The string contents of this message
     * @param likeCount The like count of this message
     * @param dislikeCount The dislike count of this message
     */
    Message(int idx, String txt, int likeCount, int dislikeCount) {
        mId = idx;
        mMessage = txt;
        mLikeCount = likeCount;
        mDislikeCount = dislikeCount;
    }

    public Message(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.mId = Integer.parseInt(data[0]);
        this.mMessage = data[1];
        this.mLikeCount = Integer.parseInt(data[2]);
        this.mDislikeCount = Integer.parseInt(data[3]);
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
                Integer.toString(mDislikeCount)});
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
