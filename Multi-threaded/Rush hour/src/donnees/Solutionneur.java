package donnees;


import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Lorsqu'initialis�, explore les it�rations de la configuration initiale jusqu'�-ce qu'une solution
 * soit trouv�e
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Solutionneur{
    /**
     * Les configurations qui m�ne � la solution, de d�but � fin
     */
    private final Stack<Configuration> stackSolution = new Stack<Configuration>();
    /**
     * le temps d'ex�cution en nanosecondes de la solution
     */
    private long tempsExec = 0;
    private final PriorityBlockingQueue<Configuration> quAVerifier = new PriorityBlockingQueue<Configuration>(100, (a,b) -> a.intIndexConf - b.intIndexConf);
    private final PriorityBlockingQueue<Configuration> quAExplorer = new PriorityBlockingQueue<Configuration>(10, (a,b) -> a.intIndexConf - b.intIndexConf);
    private boolean resous = false;
    private final Set<Configuration> setCfgVisitees = Collections.synchronizedSet(new HashSet<Configuration>());
    private Configuration configurationGagnante;


    /**
     * Trouve les �tapes qui m�ne � la solution d'une grille � partir d'une configuration
     * initiale. Ces �tapes sont stock�es dans stackSolution et le temps d'ex�cution n�cessaire
     * est conserv� dans tempsExec.
     * @param cfgInitiale l'objet Configuration � partir duquel le solutionneur commence pour trouver sa solution
     */
    public Solutionneur(Configuration cfgInitiale){
        final long tempsDebut = System.nanoTime();
        final ArrayDeque<Configuration> adCfgsDeTravail = new ArrayDeque<Configuration>();

        quAVerifier.put(cfgInitiale);

        new Evaluateur(this);
        new Evaluateur(this);
        new Evaluateur(this);
        new Evaluateur(this);
        new Evaluateur(this);
        new Evaluateur(this);
        new Evaluateur(this);
        new Explorateur(this);
        new Explorateur(this);
        new Explorateur(this);
        new Explorateur(this);
        new Explorateur(this);
        new Explorateur(this);
        attendreSolution(false);

        System.out.println("Solution: " + configurationGagnante);

        Configuration etapeCourante = configurationGagnante;
        stackSolution.push(etapeCourante);
        while (etapeCourante.getConfPrecedente() != null) {
            stackSolution.push(etapeCourante.getConfPrecedente());
            etapeCourante = etapeCourante.getConfPrecedente();
        }

        int intCompteurEtape = 1;
        System.out.println(stackSolution.size());
        Stack<Configuration> stackAffichage = (Stack<Configuration>)stackSolution.clone();
        while (!stackAffichage.isEmpty()) {
            System.out.println("===============�tape " + intCompteurEtape++ + "===============");
            System.out.println(stackAffichage.pop());
        }

        System.out.println("nombre de solutions: " + stackSolution.size());
        for (Vehicule v : configurationGagnante.getTabVoitures()) {
            System.out.println(v.getCouleur() + "," + v.getLongueur() + "," + v.getPtPosition().x + ","
                    + v.getPtPosition().y + "," + v.getOrientation());
        }

        long tempsFin = System.nanoTime();

        System.out.println("temps d'ex�cution: " + ((tempsFin - tempsDebut) / 1000000) + "ms");
        tempsExec = (tempsFin - tempsDebut) / 1000000;
    }

    /**
     * retourn le temps d'exc�ution
     * @return temps d'ex�cution (long)
     */
    public long getTempsExec() {
        return tempsExec;
    }

    /**
     * retourn les �tapes de la configuration intiale jusqu'� la solution sous
     * forme de stack.
     * @return Stack de configurations repr�sentant la solution
     */
    public Stack<Configuration> getStackSolution() {
        return (Stack<Configuration>) stackSolution.clone();
    }

    public boolean estResous() {
        return resous;
    }

    public PriorityBlockingQueue<Configuration> getQuAExplorer() {
        return quAExplorer;
    }

    public PriorityBlockingQueue<Configuration> getQuAVerifier() {
        return quAVerifier;
    }

    public Set<Configuration> getSetVisitee() {
        return setCfgVisitees;
    }

    public synchronized void trouveSolution(Configuration cfg) {
        resous = true;
        configurationGagnante = cfg;
        attendreSolution(true);
    }

    private synchronized void attendreSolution(boolean blnWake){
        if (blnWake)
            notify();
        else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void ajouterCfg(Configuration cfg) {
        if (!setCfgVisitees.contains(cfg)) {
            setCfgVisitees.add(cfg);
            quAExplorer.put(cfg);
        }
    }
}
