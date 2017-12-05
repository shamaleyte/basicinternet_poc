package hypelabs.com.hypepubsub;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.hypelabs.hype.Instance;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * This class represents a service subscription. Each object of this class contains the name and the
 * key of the subscribed service (which is generated by hashing the service name using the SHA-1
 * hashing algorithm) and the ID of the Hype clients responsible for managing that service.
 */
public class Subscription {

    String serviceName;
    byte serviceKey[];
    Instance manager;
    ArrayList<String> receivedMsg;
    private ArrayAdapter<String> receivedMsgAdapter;

    public Subscription(String serviceName, Instance manager) throws NoSuchAlgorithmException
    {
        this.serviceName = serviceName;
        this.serviceKey = HpsGenericUtils.getStrHash(serviceName);
        this.manager = manager;
        this.receivedMsg = new ArrayList<>();
    }

    public ArrayAdapter<String> getReceivedMsgAdapter(Context context)
    {
        if (receivedMsgAdapter == null)
        {
            receivedMsgAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, receivedMsg);
        }
        return receivedMsgAdapter;
    }
}
