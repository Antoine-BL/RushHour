package donnees;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Stack;

/**
 * Lorsqu'initialisé, explore les itérations de la configuration initiale jusqu'à-ce qu'une solution
 * soit trouvée
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Solutionneur{
    private final Stack<Configuration> stackSolution = new Stack<Configuration>();
    private long tempsExec = 0;

    /**
     * Trouve les étapes qui mène à la solution d'une grille à partir d'une configuration
     * initiale. Ces étapes sont stockées dans stackSolution et le temps d'exécution nécessaire
     * est conservé dans tempsExec.
     * @param cfgInitiale l'objet Configuration à partir duquel le solutionneur commence pour trouver sa solution
     */
    public Solutionneur(Configuration cfgInitiale) {
        final long tempsDebut = System.nanoTime();
        final ArrayDeque<Configuration> adCfgsDeTravail = new ArrayDeque<Configuration>();
        final HashSet<Configuration> setCfgVisitees = new HashSet<Configuration>();

        adCfgsDeTravail.addFirst(cfgInitiale);
        setCfgVisitees.add(cfgInitiale);

        while (!adCfgsDeTravail.getFirst().evaluer()) {
            adCfgsDeTravail.removeFirst().explorerConfigurations().forEach(cfg -> {
                if (!setCfgVisitees.contains(cfg)) {
                    adCfgsDeTravail.addLast(cfg);
                    setCfgVisitees.add(cfg);
                }
            });
        }

        System.out.println("Solution: " + adCfgsDeTravail.getFirst());

        Configuration etapeCourante = adCfgsDeTravail.getFirst();
        stackSolution.push(etapeCourante);
        while (etapeCourante.getConfPrecedente() != null) {
            stackSolution.push(etapeCourante.getConfPrecedente());
            etapeCourante = etapeCourante.getConfPrecedente();
        }

        int intCompteurEtape = 1;
        System.out.println(stackSolution.size());
        Stack<Configuration> stackAffichage = (Stack<Configuration>)stackSolution.clone();
        while (!stackAffichage.isEmpty()) {
            System.out.println("===============Étape " + intCompteurEtape++ + "===============");
            System.out.println(stackAffichage.pop());
        }

        System.out.println(stackSolution.size());
        for (Vehicule v : adCfgsDeTravail.getFirst().getTabVoitures()) {
            System.out.println(v.getCouleur() + "," + v.getLongueur() + "," + v.getPtPosition().x + ","
                    + v.getPtPosition().y + "," + v.getOrientation());
        }

        long tempsFin = System.nanoTime();

        System.out.println("temps d'exécution: " + ((tempsFin - tempsDebut) / 1000000) + "ms");
        tempsExec = (tempsFin - tempsDebut) / 1000000;
    }

    /**
     * retourn le temps d'excéution
     * @return temps d'exécution (long)
     */
    public long getTempsExec() {
        return tempsExec;
    }

    /**
     * retourn les étapes de la configuration intiale jusqu'à la solution sous
     * forme de stack.
     * @return Stack de configurations représentant la solution
     */
    public Stack<Configuration> getStackSolution() {
        return (Stack<Configuration>) stackSolution.clone();
    }
}
