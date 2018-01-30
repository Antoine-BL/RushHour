package application;

import donnees.Configuration;
import donnees.Vehicule;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Runnable qui, par l'utilisation d'un thread, affiche chacune des �tapes d'une solution � l'�cran.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class AfficheurSolution implements Runnable{
    /**
     * Grille associ�e
     */
    private final Grille grille;
    /**
     * Ensemble de configurations qui constituent les �tapes de la solution
     */
    private final Stack<Configuration> solution;
    /**
     * Liste des v�hicules de la grille
     */
    private final ArrayList<VehiculeApp> lstVehicules;


    /**
     * Constructeur qui initialise un afficheur de solution puis le d�marre dans un Thread s�par�.
     * Celui-ci affiche la solution � partir d'un stack de Configurations. Il parcours de haut en
     * bas le stack en affichant les mouvements qui ont men�s vers la Configuration finale du stack,
     * la solution. Applique les Translation r�sultantes sur une liste de V�hiculeApp qui appartiennent
     * � une Grille.
     * @param solution un Stack de configurations qui, de haut en bas, va, �tape par �tape, de la premi�re configuration
     *                � la r�solution de la grille
     * @param lstVehicules ArrayList de VehiculeApp qui correspondent � la grille solutionn�e
     * @param grille l'objet Grille solutionn�
     */
    public AfficheurSolution(Stack<Configuration> solution, ArrayList<VehiculeApp> lstVehicules, Grille grille) {
        this.solution = solution;
        this.lstVehicules = lstVehicules;
        this.grille = grille;

        new Thread(this, "Afficheur de solution").start();
    }


    /**
     * Bloque les �v�nements des VehiculeApp, affiche la solution puis d�bloque
     * les �v�nements des VehiculeApp
     */
    @Override
    public void run() {
        VehiculeApp.bloquerInput();

        afficherSolution();

        VehiculeApp.debloquerInput();
    }

    /**
     * Parcourt le stack en affichant les configurations jusqu'� la solution.
     * Prends en compte la valeur du Slider de FenPrincipale pour la vitesse. Quand la
     * derni�re Configuration est atteinte, mets � jour la Grille avec la Configuration
     * finale.
     */
    private void afficherSolution() {
        double vitesse;
        solution.pop();

        while (!solution.isEmpty()) {
            vitesse = grille.getMain().getVitesseSelec();
            Configuration cfgCourante = solution.pop();
            TranslateTransition tt = new TranslateTransition(new Duration(vitesse),
                    lstVehicules.get(cfgCourante.getVehiculeDeplace().getNoVehicule() - 1));
            tt.setToX(Grille.convertiXEnXScene(cfgCourante.getVehiculeDeplace().getPtPosition().x));
            tt.setToY(Grille.convertiYEnYScene(cfgCourante.getVehiculeDeplace().getPtPosition().y));
            tt.play();
            try {
                Thread.sleep((long)vitesse);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (solution.isEmpty()) {
                this.grille.majGrille(cfgCourante);
                for (Vehicule v : cfgCourante.getTabVoitures()) {
                    lstVehicules.get(v.getNoVehicule() - 1).majPosition(new Point(v.getPtPosition()));
                }
            }
        }
    }
}
