package com.zg.quickbase.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    public static final int UPDATE = 0x01;
    public static final String TAG = ProgressResponseBody.class.getName();
    private ResponseBody responseBody;
    private ApiClient.ProgressListener mListener;
    private BufferedSource bufferedSource;
    private Handler myHandler;
    public ProgressResponseBody(ResponseBody body, ApiClient.ProgressListener listener) {
        responseBody = body;
        mListener = listener;
        if (myHandler==null){
            myHandler = new MyHandler();
        }
    }

    class MyHandler extends Handler {

        public MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    ApiClient.ProgressModel progressModel = (ApiClient.ProgressModel) msg.obj;
                    if (mListener!=null)mListener.onProgress(progressModel.getCurrentBytes(),progressModel.getContentLength(),progressModel.isDone());
                    break;

            }
        }


    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (bufferedSource==null){
            bufferedSource = Okio.buffer(mySource(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source mySource(Source source) {

        return  new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead +=bytesRead!=-1?bytesRead:0;

                Message msg = Message.obtain();
                msg.what = UPDATE;
                msg.obj =  new ApiClient.ProgressModel(totalBytesRead,contentLength(),totalBytesRead==contentLength());
                myHandler.sendMessage(msg);
                Log.i(TAG,"currentBytes=="+totalBytesRead+"==contentLength=="+contentLength());
                return bytesRead;
            }
        };


    }
}

