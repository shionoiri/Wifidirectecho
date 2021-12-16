package com.example.wifidirectecho.router;

import com.example.wifidirectecho.config.Configuration;
import com.example.wifidirectecho.router.tcp.TcpSender;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender implements Runnable {

    /**
     * Queue for packets to send
     */
    private static ConcurrentLinkedQueue<Packet> ccl;

    /**
     * Constructor
     */
    public Sender() {
        if (ccl == null)
            ccl = new ConcurrentLinkedQueue<Packet>();
    }

    /**
     * Enqueue a packet to send
     * @param p
     * @return
     */
    public static boolean queuePacket(Packet p) {
        if (ccl == null)
            ccl = new ConcurrentLinkedQueue<Packet>();
        return ccl.add(p);
    }

    @Override
    public void run() {
        TcpSender packetSender = new TcpSender();

        while (true) {
            //Sleep to give up CPU cycles
            while (ccl.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Packet p = ccl.remove();
            String ip = MeshNetworkManager.getIPForClient(p.getMac());
            packetSender.sendPacket(ip, Configuration.RECEIVE_PORT, p);

        }
    }

}
