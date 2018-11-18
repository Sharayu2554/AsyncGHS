package asyncGHS;


import edu.princeton.cs.algs4.Edge;
import floodmax.MessageType;
import ghs.message.Initiate;
import ghs.message.MasterMessage;
import ghs.message.Message;
import ghs.message.Report;
import ghs.message.Test;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class Process extends Thread{

    private Logger log = Logger.getLogger(this.getName());
    public static Random random = new Random();
    // states
    private int uid;
    private int round = 0;  // initially round is 0, but everything starts from round 1 (master sends this)
    private int parentId = -1;
    private boolean selfKill = false;
    private int level;
    private CyclicBarrier barrier;
    private HashSet<Integer> children = new HashSet<>();

    private boolean leader;

    BlockingQueue<Message> inqueue = new PriorityBlockingQueue<>(30, Message::compareTo);
    BlockingQueue<Message> queue = new PriorityBlockingQueue<>(30, Message::compareTo);
    BlockingQueue<Message> masterQueue = new LinkedBlockingDeque<>(10);
    private MasterThread master;
    private NeighborObject core;
    //neighbor and its process
    private Map<Integer, Process> vertexToProcess = new HashMap<>();
    private Map<Integer, Integer> vertexToDelay = new HashMap<>();
    private HashSet<Edge> neighbors = new HashSet<>();
    private HashSet<Edge> rejected = new HashSet<>();
    private HashSet<Edge> branch = new HashSet<>();

    public Process(String name, int uid, CyclicBarrier barrier) {
        super(name);
        this.uid = uid;
        this.barrier = barrier;
        this.leader = true;
        this.level = 0;
        this.core = null;
    }

    public void setMaster(MasterThread master) {
        this.master = master;
    }

    public int getUid() {
        return this.uid;
    }


    public void setNeighborProcesses(HashMap<Edge, Process> neighborProcesses) {
        for (Edge edge : neighborProcesses.keySet()) {
            Process p = neighborProcesses.get(edge);
            neighbors.add(edge);
            vertexToProcess.put(p.getUid(), p);
            vertexToDelay.put(p.getUid(), 0);
        }
    }

    private void waitUntilMasterStartsNewRound() throws InterruptedException {
        while (true) {
            MasterMessage msg = ((MasterMessage) masterQueue.take());
            if (msg.getType().equals(MessageType.START_ROUND)) {
                if (msg.getMsg() > round) {
                    round = msg.getMsg();
                } else {
                    // This should never happen
                    throw new InterruptedException("Received round < current round");
                }
                return;
            } else if (msg.getType().equals(MessageType.KILL)) {
                selfKill = true;
                break;
            } else {
                masterQueue.add(msg);
            }
        }
    }

    private Integer getDelay() {
        return 1 + random.nextInt(19);
    }

    private void checkInQueue() throws InterruptedException {
        while (!inqueue.isEmpty() && inqueue.peek().getRound() <= round) {
            Message m = inqueue.take();
            pushToQueue(vertexToProcess.get(m.getReceiver()), m);
        }
    }

    private void pushToInQueue(Message m)
    {
        inqueue.add(m);
    }

    private int getRound(int receiver) {
        int prevDelay = vertexToDelay.get(receiver);
        int finalDelay = prevDelay + getDelay();
        vertexToDelay.put(receiver, finalDelay);
        return finalDelay;
    }

    private void pushToQueue(Process p, Message m) {
        p.queue.add(m);
    }

    private void pushToQueue(MasterThread p, MasterMessage m) {
        p.queue.add(m);
    }

    private void sendRoundCompletionToMaster() {
        pushToQueue(master, new MasterMessage(uid, round, MessageType.END_ROUND));
    }

    private void sendTerminationToMaster() {
        pushToQueue(master, new MasterMessage(uid, parentId, MessageType.TERMINATE));
    }


    private int getNeighborId (Edge edge) {
        return edge.either() == this.getUid() ? edge.other(this.getUid()) : edge.either();
    }

    private void sendMessages(HashSet<Edge> neighbors) {
        for(Edge neighbor: neighbors) {
            int neighborId = getNeighborId(neighbor);
            int round = getRound(neighborId);
            Message testMsg = new Test(uid, neighborId, round, 0, 1);
            pushToInQueue(testMsg);
        }
    }

    private void message() {
        // defining messages
        if(round <= 3) {
            sendMessages(neighbors);
        }
    }

    private void handleMessages() throws InterruptedException {
        Message msg;
        while (!queue.isEmpty()) {
            msg = queue.take();
            if(msg.getType() == MessageType.INITIATE) {

            }
        }
    }

    private void transition() throws InterruptedException, BrokenBarrierException {
        barrier.await();    // wait until all threads have sent explore messages
        checkInQueue();
        barrier.await();
        handleMessages();
    }

    @Override
    public void run() {
        try {
            while (true) {
                waitUntilMasterStartsNewRound();
                if (selfKill) {
                    break;
                }
                message();
                transition();

                if (round == 10) {
                    sendTerminationToMaster();
                }
                else {
                    sendRoundCompletionToMaster();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void broadCastToChildren() {
        if(branch.size() == 0) {
            pushToInQueue(new Initiate(this.getUid(), this.getUid(), null, this.level));
        }
        else {
            for (Edge edge: neighbors) {
                Integer neighborId = edge.other(this.getUid());
                Process neighborProcess = vertexToProcess.get(neighborId);
                Message msg = new Initiate(this.getUid(), this.getUid(), null, this.level);
                pushToInQueue(msg);
            }
        }
    }

    private boolean isLeader() {
        return this.leader;
    }

    @Override
    public String toString() {
        return "Process{" +
                "uid=" + uid +
                "} " + super.toString();
    }
}