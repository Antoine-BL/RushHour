package donnees;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;

public class Evaluateur implements Runnable {
    Solutionneur parent;
    private final PriorityBlockingQueue<Configuration> quAExplorer;
    private final PriorityBlockingQueue<Configuration> quAVerifier;
    private final HashSet<Configuration> setVisite = new HashSet<Configuration>();

    public Evaluateur(Solutionneur parent) {
        this.parent = parent;
        quAExplorer = parent.getQuAExplorer();
        quAVerifier = parent.getQuAVerifier();
        new Thread(this, "Eval").start();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " start");

        try {
            while (!parent.estResous()) {
                Configuration cfg = quAVerifier.take();
                System.out.println("while iteration Eval");
                if (cfg.evaluer() && !parent.estResous()) {
                    parent.trouveSolution(cfg);
//                    while(!quAVerifier.isEmpty()) {
//                        System.out.println(quAVerifier.take().intIndexConf);
//                    }
                    System.out.println("lol wut");
                } else {
                    parent.ajouterCfg(cfg);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
