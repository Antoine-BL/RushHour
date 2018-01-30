package donnees;

import java.util.concurrent.PriorityBlockingQueue;

public class Explorateur implements Runnable {
    Solutionneur parent;
    private final PriorityBlockingQueue<Configuration> quAExplorer;
    private final PriorityBlockingQueue<Configuration> quAVerifier;

    public Explorateur(Solutionneur parent) {
        this.parent = parent;
        quAExplorer = parent.getQuAExplorer();
        quAVerifier = parent.getQuAVerifier();
        new Thread(this, "exp").start();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " start");
        try {
            while (!parent.estResous()) {
                System.out.println("explored config");
                Configuration cfg = quAExplorer.take();
                cfg.explorerConfigurations().forEach(c -> quAVerifier.put(c));
                System.out.println("verify queue size: " + quAVerifier.size());
                System.out.println("explore queue size: " + quAExplorer.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
