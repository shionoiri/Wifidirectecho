package com.example.wifidirectecho;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wifidirectecho.router.AllEncompasingP2PClient;
import com.example.wifidirectecho.router.MeshNetworkManager;
import com.example.wifidirectecho.router.Packet;
import com.example.wifidirectecho.router.Sender;
import com.example.wifidirectecho.wifi.WiFiDirectBroadcastReceiver;

public class MessageActivity extends Activity {
    public static AllEncompasingP2PClient RECIPIENT = null;

    private static TextView messageView;

    /**
     * Add appropriate listeners on creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        messageView = (TextView) findViewById(R.id.message_view);

        final Button button = (Button) findViewById(R.id.btn_send);
        final EditText message = (EditText) findViewById(R.id.edit_message);

        this.setTitle("Group Chat");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msgStr = message.getText().toString();
                addMessage("This phone", msgStr);
                message.setText("");

                // Send to other clients as a group chat message
                for (AllEncompasingP2PClient c : MeshNetworkManager.routingTable.values()) {
                    if (c.getMac().equals(MeshNetworkManager.getSelf().getMac()))
                        continue;
                    Sender.queuePacket(new Packet(Packet.TYPE.MESSAGE, msgStr.getBytes(), c.getMac(),
                            WiFiDirectBroadcastReceiver.MAC));
                }

            }
        });
    }

    /**
     * Add a message to the view
     */
    public static void addMessage(String from, String text) {

        messageView.append(from + " says " + text + "\n");
        final int scrollAmount = messageView.getLayout().getLineTop(messageView.getLineCount())
                - messageView.getHeight();
        // if there is no need to scroll, scrollAmount will be <=0
        if (scrollAmount > 0)
            messageView.scrollTo(0, scrollAmount);
        else
            messageView.scrollTo(0, 0);
    }

}
