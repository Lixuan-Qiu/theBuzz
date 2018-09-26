package cloud9.cse216.lehigh.edu.cloud9;

public class Message {
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
     * Construct a Datum by setting its index and text
     *
     * @param idx The index of this message
     * @param txt The string contents of this message
     * @param count The like count of this message
     */
    Message(int idx, String txt, int count) {
        mId = idx;
        mMessage = txt;
        mLikeCount = count;
    }
}
