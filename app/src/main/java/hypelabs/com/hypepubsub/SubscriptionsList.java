package hypelabs.com.hypepubsub;

import android.content.Context;

import com.hypelabs.hype.Instance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class SubscriptionsList
{

    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a SubscriptionsList.
    final private LinkedList<Subscription> subscriptions = new LinkedList<>();

    private SubscriptionsAdapter subscriptionsAdapter = null;

    public synchronized int add(String serviceName, Instance managerInstance) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpbConstants.HASH_ALGORITHM);
        byte serviceKey[] = md.digest(serviceName.getBytes());

        if(find(serviceKey) != null) {
            return -1;
        }

        subscriptions.add(new Subscription(serviceName, managerInstance));
        return 0;
    }

    public synchronized int remove(String serviceName) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpbConstants.HASH_ALGORITHM);
        byte serviceKey[] = md.digest(serviceName.getBytes());

        Subscription subscription = find(serviceKey);
        if(subscription == null) {
            return -1;
        }

        subscriptions.remove(subscription);
        return 0;
    }

    public synchronized Subscription find(byte serviceKey[])
    {
        ListIterator<Subscription> it = listIterator();
        while(it.hasNext())
        {
            Subscription currentSubs = it.next();
            if(Arrays.equals(currentSubs.serviceKey, serviceKey)) {
                return currentSubs;
            }
        }
        return null;
    }

    // Methods from LinkedList that we want to enable.
    public synchronized ListIterator<Subscription> listIterator()
    {
        return subscriptions.listIterator();
    }

    public synchronized int size()
    {
        return subscriptions.size();
    }

    public synchronized Subscription get(int index)
    {
        return subscriptions.get(index);
    }

    public synchronized SubscriptionsAdapter getSubscriptionsAdapter(Context context)
    {
        if(subscriptionsAdapter == null){
            subscriptionsAdapter = new SubscriptionsAdapter(context, subscriptions);
        }

        return  subscriptionsAdapter;
    }

}