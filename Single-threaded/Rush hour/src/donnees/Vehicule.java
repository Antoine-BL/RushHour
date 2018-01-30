package donnees;

import java.awt.*;

/**
 * Version plus l�g�re de VehiculeApp utilis�e dans le calcul de solutions
 * et d'it�rations de Configuration.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Vehicule implements Cloneable{
    private int noVehicule;
    private int longueur;
    private Point ptPosition;
    private Orientation orientation;
    private String couleur;

    /**
     * Initialise les variables d'instances du Vehicule.
     * @param longueur int qui repr�sente la longueur en cases du v�hicule (2 pour une voiture, 3 pour un camion)
     * @param noVehicule Index + 1 de ce v�hicule dans tabV�hicules
     * @param ptPosition Point qui repr�sente la position sur la grille du v�hicule
     * @param orientation Valeur de l'enum Orientation qui repr�sente l'orientation Verticale ou Horizontale du v�hicule
     * @param couleur String qui repr�sente la couleur du v�hicule
     */
    public Vehicule(int longueur, int noVehicule, Point ptPosition, Orientation orientation, String couleur) {
        this.longueur = longueur;
        this.noVehicule = noVehicule;
        this.ptPosition = ptPosition;
        this.orientation = orientation;
        this.couleur = couleur;
    }

    /**
     * Cr�� un nouvel objet V�hicule (new Vehicule()) avec les m�mes param�tres au constructeur.
     * @return Copie conforme de ce v�hicule
     */
    @Override
    public Vehicule clone(){
        return new Vehicule(longueur, noVehicule, new Point(ptPosition.x, ptPosition.y), orientation, couleur);
    }

    /**
     * Retourne la longueur en cases du v�hicule.
     * @return longueur en cases
     */
    public int getLongueur() {
        return longueur;
    }

    /**
     * Retourne le num�ro de v�hicule. Ce num�ro correspond � l'index + 1 du v�hicule
     * dans tabVoitures d'une configuration ce v�hicule. Correspond aussi � l'index + 1
     * du VehiculeApp qui correspond � ce v�hicule dans lstVehicules de l'objet Grille
     * qui contient la configuration � laquelle ce V�hicule est associ�.
     * @return no du v�hicule (index - 1)
     */
    public int getNoVehicule() {
        return noVehicule;
    }

    /**
     * Retourne les coordonn�es sur la grille de ce v�hicule.
     * IMPORTANT: ces coordonn�es utilisent la case en haut � gauche de la grille
     * comme origine, le x croissant vers la droite et le y croissant vers le bas.
     * @return Point qui repr�sente les coordonn�es du v�hicule sur la grille.
     */
    public Point getPtPosition() {
        return ptPosition;
    }

    /**
     * Mets � jour la position de cet objet � partir du Point re�u en param�tre en copiant dernier.
     * @param ptPosition nouvelle position
     */
    public void setPtPosition(Point ptPosition) {
        this.ptPosition = new Point(ptPosition);
    }

    /**
     * Retourne l'orientation du v�hicule: V (verticale) ou H (horizontale)
     * @return valeur de l'enum Orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Retourne la couleur de ce v�hicule
     * @return String qui repr�sente la couleur
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Retourne en string le type de v�hicule (d�termin� par la longueur), la couleur et la position.
     * @return String qui repr�sente cet objet.
     */
    @Override
    public String toString() {
        return ((longueur == 2) ? "voiture " : "camion ") + couleur + " (no. "+ noVehicule +") � (" + ptPosition.x + "; " + ptPosition.y + ")";
    }
}
