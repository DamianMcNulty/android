package org.sagemath.singlecellserver;

import org.json.JSONException;
import org.json.JSONObject;
import org.sagemath.droid.models.StreamReply;


/**
 * <h1>Streams (stdout,  stderr, etc)</h1>
 * <p/>
 * <pre><code>
 * Message type: ``stream``::
 *     content = {
 *        # The name of the stream is one of 'stdin', 'stdout', 'stderr'
 *        'name' : str,
 * <p/>
 *        # The data is an arbitrary string to be written to that stream
 *        'data' : str,
 *     }
 * </code></pre>
 * When a kernel receives a raw_input call, it should also broadcast it on the pub
 * socket with the names 'stdin' and 'stdin_reply'.  This will allow other clients
 * to monitor/display kernel interactions and possibly replay them to their user
 * or otherwise expose them.
 *
 * @author vbraun
 */
public class ResultStream extends CommandOutput {
    private final static String TAG = "SageDroid:ResultStream";

    protected JSONObject content;
    protected String data;

    protected ResultStream(JSONObject json) throws JSONException {
        super(json);
        content = json.getJSONObject("content");
        data = content.getString("data");
    }

    protected ResultStream(StreamReply reply) {
        super(reply);
        data = reply.getContent().getData();
    }

    public String toString() {
        return "Result: stream = >>>" + data + "<<<";
    }

    public String toShortString() {
        return "Stream output";
    }

    public String get() {
        return data;
    }

}
