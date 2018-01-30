package donnees;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Classe qui représente une configuration de grille.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Configuration implements Comparable<Configuration>{
    /**
     * Un tableau deux dimensionnel d'entiers représentant l'état de la grille. 0 représente une case vide, le reste
     * des voitures. La voiture rouge n'a pas de nombre réservé.
     */
    private int[][] grille = new int[6][6];
    /**
     * Tableau contenant toutes les voitures de cette grille.
     */
    private Vehicule[] tabVoitures;
    /**
     * Si applicable, la voiture qui a été déplacée par rapport à la configuration précédente.
     */
    private Vehicule vehiculeDeplace;
    /**
     * Si applicable, la configuration précédente.
     */
    private Configuration confPrecedente;
    public int intIndexConf;

    /**
     * Utilisé lorsqu'une configuration est une itération d'une autre configuration.
     * Créé une copie de la configuration précédente, puis mets la grille de celle-ci
     * à jour en tenant compte véhicule déplacé.
     * @param vehiculeDeplace l'objet Vehicule qui a été déplacé dans la configuration précédente
     * @param confPrecedente la configuration précédant celle-ci.
     */
    public Configuration(Vehicule vehiculeDeplace, Configuration confPrecedente) {
        this.confPrecedente = confPrecedente;
        this.intIndexConf = confPrecedente.intIndexConf + 1;
        for (int i = 0; i < 6; i++)
            grille[i] = Arrays.copyOf(confPrecedente.getGrille()[i], 6);
        this.tabVoitures = confPrecedente.tabVoitures.clone();
        this.vehiculeDeplace = vehiculeDeplace;

        if (vehiculeDeplace.getOrientation() == Orientation.V) {
            for (int i = 0; i < vehiculeDeplace.getLongueur(); i++) {
                grille[tabVoitures[vehiculeDeplace.getNoVehicule() - 1].getPtPosition().x]
                        [tabVoitures[vehiculeDeplace.getNoVehicule() - 1].getPtPosition().y + i] = 0;
            }
            this.tabVoitures[vehiculeDeplace.getNoVehicule()-1] = vehiculeDeplace;
            for (int i = 0; i < vehiculeDeplace.getLongueur(); i++) {
                grille[vehiculeDeplace.getPtPosition().x]
                        [vehiculeDeplace.getPtPosition().y + i] = vehiculeDeplace.getNoVehicule();
            }
        } else {
            for (int i = 0; i < vehiculeDeplace.getLongueur(); i++) {
                grille[tabVoitures[vehiculeDeplace.getNoVehicule() - 1].getPtPosition().x + i]
                        [tabVoitures[vehiculeDeplace.getNoVehicule() - 1].getPtPosition().y] = 0;
            }
            this.tabVoitures[vehiculeDeplace.getNoVehicule()-1] = vehiculeDeplace;
            for (int i = 0; i < vehiculeDeplace.getLongueur(); i++) {
                grille[vehiculeDeplace.getPtPosition().x + i]
                        [vehiculeDeplace.getPtPosition().y] = vehiculeDeplace.getNoVehicule();
            }
        }
    }

    /**
     * Utilisé pour la création de la configuration initiale. Initialise la grille
     * à partir d'un tableau de véhicules.
     * @param tabVoitures tableaux de véhicules quis dans leur configuration initiale.
     */
    public Configuration(Vehicule[] tabVoitures) {
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 6; j++){
                grille[i][j] = 0;
            }
        }

        int intCompteurVeh = 1;
        for (Vehicule v : tabVoitures) {
            if (v.getOrientation() == Orientation.V) {
                for (int i = 0; i < v.getLongueur(); i++) {
                    grille[v.getPtPosition().x][v.getPtPosition().y + i] = intCompteurVeh;
                }
            } else {
                for (int i = 0; i < v.getLongueur(); i++) {
                    grille[v.getPtPosition().x + i][v.getPtPosition().y] = intCompteurVeh;
                }
            }
            intCompteurVeh++;
        }

        intIndexConf = 0;

        this.tabVoitures = tabVoitures;
    }

    /**
     * Explore les itérations possibles de cette configuration puis retourne une LinkedList qui
     * représente celles-ci.
     * @return LinkedList des itérations possibles de cette configuration
     */
    public LinkedList<Configuration> explorerConfigurations() {
        LinkedList<Configuration> lstConf = new LinkedList<Configuration>();
        for (Vehicule v : tabVoitures) {
            if (v.getOrientation() == Orientation.V) {
                for (int i = v.getPtPosition().y + v.getLongueur(); i < grille[v.getPtPosition().x].length; i++){
                    if (!positionEstOccupee(v.getPtPosition().x, i)) {
                        Vehicule clone = v.clone();
                        clone.setPtPosition(new Point(v.getPtPosition().x, i - v.getLongueur() + 1));
                        lstConf.add(new Configuration(clone, this));
                    } else {
                        i = grille[v.getPtPosition().x].length;
                    }
                }
                for (int i = v.getPtPosition().y - 1; i >= 0; i--) {
                    if (!positionEstOccupee(v.getPtPosition().x, i)) {
                        Vehicule clone = v.clone();
                        clone.setPtPosition(new Point(v.getPtPosition().x, i));
                        lstConf.add(new Configuration(clone, this));
                    } else {
                        i = -1;
                    }
                }
            } else {
                for (int i = v.getPtPosition().x + v.getLongueur(); i < grille.length; i++) {
                    if (!positionEstOccupee(i, v.getPtPosition().y)) {
                        Vehicule clone = v.clone();
                        clone.setPtPosition(new Point(i - v.getLongueur() + 1, v.getPtPosition().y));
                        lstConf.add(new Configuration(clone, this));
                    } else {
                        i = grille.length;
                    }
                }
                for (int i = v.getPtPosition().x - 1; i >= 0; i--) {
                    if (!positionEstOccupee(i, v.getPtPosition().y)) {
                        Vehicule clone = v.clone();
                        clone.setPtPosition(new Point(i, v.getPtPosition().y));
                        lstConf.add(new Configuration(clone, this));
                    } else {
                        i = -1;
                    }
                }
            }
        }

        return lstConf;
    }

    /**
     * vérifie si la position (x, y) est occupée par une voiture dans cette configuration
     * @param x la coordonnée x (0 = gauche, 5 = droite) sur la grille
     * @param y la coordonnée y (0 = haut, 5 = bas) sur la grille
     * @return true si la position est occupe, false si non
     */
    public boolean positionEstOccupee(int x, int y){
        return grille[x][y] != 0;
    }

    /**
     * Retourne le tableau de ints qui représente cette configuration.
     * Les 0 représentent des cases vides, puis les chiffres représentent
     * chacun des véhicules dans l'ordre où ils ont été lus dans le fichier.
     * @return tableau de ints représentant cette configuration
     */
    public int[][] getGrille() {
        return grille;
    }

    /**
     * Construit en string avec en en-tête le véhicule déplacé ou
     * "Configuration initiale" s'il n'y en a pas. Affiche ensuite
     * le tableau d'ints "grille" qui représente cette configuration.
     * @return la représentation de cette grille sous forme de String
     */
    public String toString(){
        String str = "";

        if (vehiculeDeplace != null)
            str += "Vehicule deplace: " + vehiculeDeplace;
        else
            str += "configuration initiale";
        for (int i = 0; i < grille.length; i++) {
            str += "\n";
            for (int j = 0; j < grille[i].length; j++) {
                str += grille[j][i] + " ";
            }
        }
        return str;
    }

    /**
     * Retourne le tableau des voitures que cette configuration contient
     * @return tableau d'objets Vehicule
     */
    public Vehicule[] getTabVoitures() {
        return tabVoitures;
    }

    /**
     * Le vehicule qui a été déplacé pour passer de la configuration précédente à
     * celle-ci.
     * @return l'objet Vehicule qui a été déplacé.
     */
    public Vehicule getVehiculeDeplace() {
        return vehiculeDeplace;
    }

    /**
     * Détermine si cette Configuration est une solution à la grille. Ceci est fait
     * en vérifiant si la voiture rouge a un x = 4. Si c'est le cas, c'est que la
     * voiture rouge est en mesure de sortir de la grille.
     * @return True si cette Configuration est une solution, false si ce n'est pas le cas
     */
    public boolean evaluer() {
        boolean resous = false;
        for (int i = 0; i < tabVoitures.length && !resous; i++) {
                resous =  tabVoitures[i].getCouleur().equals("rouge") && tabVoitures[i].getPtPosition().x == 4;
        }
        return resous;
    }

    /**
     * Vérifie si un objet est égal à celui-ci. Ne prends en compte que le tableau
     * "grille". Donc, si deux configurations sont dans un état identique, elles sont
     * considérées identiques même si la voiture déplacée n'est pas la même ou si la
     * configuration précédente est différente.
     * @param o Objet à comparer
     * @return vrai si les positions de tout les véhicules sont égaux dans les deux cas, faux sinon.
     */
    @Override
    public boolean equals(Object o) {
        boolean blnRetour = false;
        if (this == o) {
            blnRetour = true;
        } else if (o != null && getClass() == o.getClass()){
            Configuration autreConf = (Configuration) o;
            blnRetour = Arrays.deepEquals(grille, autreConf.grille);
        }

        return blnRetour;
    }

    /**
     * Retourne le deepHashCode du tableau "grille"
     * @return le deepHashCode du tableau "grille"
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grille);
    }

    /**
     * Retourne la configuration précédente
     * @return la configuration précédente
     */
    public Configuration getConfPrecedente() {
        return confPrecedente;
    }

    public int getIntIndexConf() {
        return intIndexConf;
    }

    @Override
    public int compareTo(Configuration o) {
        return o.intIndexConf - intIndexConf;
    }
}
