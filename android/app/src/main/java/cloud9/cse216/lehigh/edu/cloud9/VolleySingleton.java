package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
public class VolleySingleton {
    private RequestQueue mRequestQueue;
    private static Context mContext;
    private static VolleySingleton mInstance;

    public VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();

    }

    public VolleySingleton  getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}


