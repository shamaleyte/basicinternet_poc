package hypelabs.com.hypepubsub;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    private HypePubSub hpb = null;
    private Network network = null;

    private Button subscribeButton;
    private Button unsubscribeButton;
    private Button publishButton;
    private Button checkOwnIdButton;
    private Button checkHypeDevicesButton;
    private Button checkOwnSubscriptionsButton;
    private Button checkManagedServicesButton;

    private static MainActivity instance; // Way of accessing the application context from other classes

    public MainActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHypeSdk();
        getSingletonInstances();
        setButtonListeners();
    }

    private void initHypeSdk()
    {
        HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
        try
        {
            hypeSdkInterface.requestHypeToStart(getApplicationContext());
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getSingletonInstances()
    {
        // Get required singletons
        hpb = HypePubSub.getInstance();
        network = Network.getInstance();
    }

    private void setButtonListeners()
    {
        initButtonsFromResourceIDs();

        setListenerSubscribeButton();
        setListenerUnsubscribeButton();
        setListenerPublishButton();
        setListenerCheckOwnIdButton();
        setListenerCheckHypeDevicesButton();
        setListenerCheckOwnSubscriptionsButton();
        setListenerCheckManagedServicesButton();
    }

    private void initButtonsFromResourceIDs()
    {
        subscribeButton = findViewById(R.id.activity_main_subscribe_button);
        unsubscribeButton = findViewById(R.id.activity_main_unsubscribe_button);
        publishButton = findViewById(R.id.activity_main_publish_button);
        checkOwnIdButton = findViewById(R.id.activity_main_check_own_id_button);
        checkHypeDevicesButton = findViewById(R.id.activity_main_check_hype_devices_button);
        checkOwnSubscriptionsButton = findViewById(R.id.activity_main_check_own_subscriptions_button);
        checkManagedServicesButton = findViewById(R.id.activity_main_check_managed_services_button);
    }

    private void setListenerSubscribeButton()
    {
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.SingleInputDialog subscribeInput = new AlertDialogUtils.SingleInputDialog() {

                    @Override
                    public void onOk(String service) throws IOException, NoSuchAlgorithmException
                    {
                        service = service.toLowerCase().trim();
                        if(service.length() > 0)
                        {
                            if(hpb.ownSubscriptions.find(GenericUtils.getStrHash(service)) == null)
                            {
                                hpb.issueSubscribeReq(service);
                            }
                            else
                            {
                                AlertDialogUtils.showOkDialog(MainActivity.this, "INFO", "Service already subscribed");
                            }
                        }
                    }

                    @Override
                    public void onCancel(){
                        // do nothing;
                    }
                };

                AlertDialogUtils.showSingleInputDialog(MainActivity.this,
                                                        "SUBSCRIBE SERVICE" ,
                                                        "service",
                                                        subscribeInput);

            }
        });
    }

    private void setListenerUnsubscribeButton()
    {
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(hpb.ownSubscriptions.size() == 0){
                    AlertDialogUtils.showOkDialog(MainActivity.this, "INFO", "No services subscribed");
                    return;
                }

                AlertDialogUtils.ListViewInputDialog unsubscribeList = new AlertDialogUtils.ListViewInputDialog() {

                    @Override
                    public void onItemClick(Object listItem, Dialog dialog) throws IOException, NoSuchAlgorithmException
                    {
                        Subscription subscription = (Subscription) listItem;
                        String serviceName = subscription.serviceName;
                        hpb.issueUnsubscribeReq(serviceName);

                        AlertDialogUtils.showOkDialog(MainActivity.this,
                                "INFO",
                                "Service " + serviceName + " unsubscribed");

                        dialog.dismiss();
                    }
                };

                AlertDialogUtils.showListViewInputDialog(MainActivity.this,
                        "UNSUBSCRIBE SERVICE" ,
                        hpb.ownSubscriptions.getSubscriptionsAdapter(MainActivity.this),
                        unsubscribeList);
            }
        });
    }

    private void setListenerPublishButton()
    {
        publishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.DoubleInputDialog publishInput = new AlertDialogUtils.DoubleInputDialog() {

                    @Override
                    public void onOk(String service, String msg) throws IOException, NoSuchAlgorithmException
                    {
                        service = service.toLowerCase().trim();
                        msg = msg.trim();
                        if(service.length() > 0 && msg.length() > 0)
                            hpb.issuePublishReq(service, msg);
                        else
                            AlertDialogUtils.showOkDialog(MainActivity.this,
                                                            "WARNING",
                                                            "A service and a message must be specified");
                    }

                    @Override
                    public void onCancel() {
                        // do nothing;
                    }
                };

                AlertDialogUtils.showDoubleInputDialog(MainActivity.this,
                        "PUBLISH IN SERVICE" ,
                        "service",
                        "message",
                        publishInput);
            }
        });
    }

    private void setListenerCheckOwnIdButton()
    {
        checkOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                try
                {
                    AlertDialogUtils.showOkDialog(MainActivity.this,"Own Device",
                                                GenericUtils.getInstanceAnnouncementStr (network.ownClient.instance) + "\n"
                                                + "Id: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.instance.getIdentifier()) + "\n"
                                                + "Key: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.key));
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setListenerCheckHypeDevicesButton()
    {
        final Intent intent = new Intent(this, ClientsListActivity.class);

        checkHypeDevicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerCheckOwnSubscriptionsButton()
    {
        final Intent intent = new Intent(this, SubscriptionsListActivity.class);

        checkOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(hpb.ownSubscriptions.size() == 0){
                    AlertDialogUtils.showOkDialog(MainActivity.this, "INFO", "No services subscribed");
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerCheckManagedServicesButton()
    {
        final Intent intent = new Intent(this, ServiceManagersListActivity.class);

        checkManagedServicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private boolean isHypeSdkReady()
    {
        if(HypeSdkInterface.isHypeFail){
            AlertDialogUtils.showOkDialog(MainActivity.this, "Warning", "Hype SDK could not be started");
            return false;
        }
        else if( ! HypeSdkInterface.isHypeReady){
            AlertDialogUtils.showOkDialog(MainActivity.this, "Warning", "Hype SDK is not ready yet");
            return false;
        }

        return true;
    }
}
