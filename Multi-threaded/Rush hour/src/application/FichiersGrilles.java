package application;

/**
 * Enumération d'URLs de fichiers utilisés pour construire des grilles.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public enum FichiersGrilles {
    grille1("f1"),
    grille2("f2"),
    grille3("f3");
    /**
     * L'addresse URL du fichier de grille
     */
    private String strURL;

    FichiersGrilles(String strNom) {
        this.strURL = "res/grille/" + strNom + ".txt";
    }

    /**
     * Retourne l'URL du fichier grille correspondant.
     * @return String qui représente l'URL d'un fichier grille
     */
    public String getStrURL() {
        return strURL;
    }
}
