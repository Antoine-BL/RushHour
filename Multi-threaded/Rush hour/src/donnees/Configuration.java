package donnees;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Classe qui repr�sente une configuration de grille.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Configuration implements Comparable<Configuration>{
    /**
     * Un tableau deux dimensionnel d'entiers repr�sentant l'�tat de la grille. 0 repr�sente une case vide, le reste
     * des voitures. La voiture rouge n'a pas de nombre r�serv�.
     */
    private int[][] grille = new int[6][6];
    /**
     * Tableau contenant toutes les voitures de cette grille.
     */
    private Vehicule[] tabVoitures;
    /**
     * Si applicable, la voiture qui a �t� d�plac�e par rapport � la configuration pr�c�dente.
     */
    private Vehicule vehiculeDeplace;
    /**
     * Si applicable, la configuration pr�c�dente.
     */
    private Configuration confPrecedente;
    public int intIndexConf;

    /**
     * Utilis� lorsqu'une configuration est une it�ration d'une autre configuration.
     * Cr�� une copie de la configuration pr�c�dente, puis mets la grille de celle-ci
     * � jour en tenant compte v�hicule d�plac�.
     * @param vehiculeDeplace l'objet Vehicule qui a �t� d�plac� dans la configuration pr�c�dente
     * @param confPrecedente la configuration pr�c�dant celle-ci.
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
     * Utilis� pour la cr�ation de la configuration initiale. Initialise la grille
     * � partir d'un tableau de v�hicules.
     * @param tabVoitures tableaux de v�hicules quis dans leur configuration initiale.
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
     * Explore les it�rations possibles de cette configuration puis retourne une LinkedList qui
     * repr�sente celles-ci.
     * @return LinkedList des it�rations possibles de cette configuration
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
     * v�rifie si la position (x, y) est occup�e par une voiture dans cette configuration
     * @param x la coordonn�e x (0 = gauche, 5 = droite) sur la grille
     * @param y la coordonn�e y (0 = haut, 5 = bas) sur la grille
     * @return true si la position est occupe, false si non
     */
    public boolean positionEstOccupee(int x, int y){
        return grille[x][y] != 0;
    }

    /**
     * Retourne le tableau de ints qui repr�sente cette configuration.
     * Les 0 repr�sentent des cases vides, puis les chiffres repr�sentent
     * chacun des v�hicules dans l'ordre o� ils ont �t� lus dans le fichier.
     * @return tableau de ints repr�sentant cette configuration
     */
    public int[][] getGrille() {
        return grille;
    }

    /**
     * Construit en string avec en en-t�te le v�hicule d�plac� ou
     * "Configuration initiale" s'il n'y en a pas. Affiche ensuite
     * le tableau d'ints "grille" qui repr�sente cette configuration.
     * @return la repr�sentation de cette grille sous forme de String
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
     * Le vehicule qui a �t� d�plac� pour passer de la configuration pr�c�dente �
     * celle-ci.
     * @return l'objet Vehicule qui a �t� d�plac�.
     */
    public Vehicule getVehiculeDeplace() {
        return vehiculeDeplace;
    }

    /**
     * D�termine si cette Configuration est une solution � la grille. Ceci est fait
     * en v�rifiant si la voiture rouge a un x = 4. Si c'est le cas, c'est que la
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
     * V�rifie si un objet est �gal � celui-ci. Ne prends en compte que le tableau
     * "grille". Donc, si deux configurations sont dans un �tat identique, elles sont
     * consid�r�es identiques m�me si la voiture d�plac�e n'est pas la m�me ou si la
     * configuration pr�c�dente est diff�rente.
     * @param o Objet � comparer
     * @return vrai si les positions de tout les v�hicules sont �gaux dans les deux cas, faux sinon.
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
     * Retourne la configuration pr�c�dente
     * @return la configuration pr�c�dente
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
