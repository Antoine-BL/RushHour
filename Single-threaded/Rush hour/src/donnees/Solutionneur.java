package donnees;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Stack;

/**
 * Lorsqu'initialis�, explore les it�rations de la configuration initiale jusqu'�-ce qu'une solution
 * soit trouv�e
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Solutionneur{
    private final Stack<Configuration> stackSolution = new Stack<Configuration>();
    private long tempsExec = 0;

    /**
     * Trouve les �tapes qui m�ne � la solution d'une grille � partir d'une configuration
     * initiale. Ces �tapes sont stock�es dans stackSolution et le temps d'ex�cution n�cessaire
     * est conserv� dans tempsExec.
     * @param cfgInitiale l'objet Configuration � partir duquel le solutionneur commence pour trouver sa solution
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
            System.out.println("===============�tape " + intCompteurEtape++ + "===============");
            System.out.println(stackAffichage.pop());
        }

        System.out.println(stackSolution.size());
        for (Vehicule v : adCfgsDeTravail.getFirst().getTabVoitures()) {
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
}
