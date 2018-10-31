/**
 * @author Aina Garcia
 */

package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assignatura {

    private String nom;
    private InfoSessions teoria;
    private InfoSessions laboratori;
    private Map<Integer, Grup> grups = new HashMap<>();
    private ArrayList<Assignatura> correquisit = new ArrayList<>();
    private int quadrimestre;
    private PlaEstudis plaEstudis;

    /**
     * Crea una assignatura nova amb grups i la informació corresponent
     * @param nom nom de l'assignatura
     * @param t informació de les sessions de teoria
     * @param l informació de les sessions de laboratori
     */
    public Assignatura(String nom, int quadrimestre, Teoria t, Laboratori l){
        this.nom = nom;
        this.laboratori = l;
        this.teoria = t;
        this.quadrimestre = quadrimestre;
    }

    /********** GETTERS ********/

    /**
     * Obtenir nom de l'assignatura
     * @return nom de l'assignatura
     */
    public String getNom(){
        return nom;
    }

    public int getQuadrimestre() { return quadrimestre; }

    /**
     * Obtenir informació de les sessions de laboratori de l'assignatura
     * @return informació de laboratori
     */
    public InfoSessions getLaboratori() {
        return laboratori;
    }

    /**
     * Obtenir informació de les sessions de teoria de l'assignatura
     * @return informació de teoria
     */
    public InfoSessions getTeoria() {
        return teoria;
    }

    /**
     * Obtenir tots els grups de l'assignatura
     * @return grups de l'assignatura
     */
    public Map<Integer, Grup> getGrups() {
        return grups;
    }

    /**
     * Obtenir un grup concret d'aquesta assignatura
     * @param num nombre del grup
     * @return grup amb número identificador sol·licitat
     */
    public Grup getGrup(int num){
        return grups.get(num);
    }

    /****** SETTERS ********/

    /**
     * Actualitza la informació de les sessions de laboratori
     * @param numSessions sessions de laboratori
     * @param duracioSessions duracio de les sessions
     */
    public void setLaboratori(int numSessions, int duracioSessions) {
        this.laboratori = new Teoria(numSessions, duracioSessions);
    }

    /**
     * Actualitza el nom de l'assignatura
     * @param nom nom de l'assignatura
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Actualitza la informació de les sessions de teoria
     * @param numSessions numero de sessions setmanals
     * @param duracioSessions duracio de cada sessio
     */
    public void setTeoria(int numSessions, int duracioSessions) {
        this.teoria = new Teoria(numSessions, duracioSessions);
    }

    public void setQuadrimestre(int quadrimestre) { this.quadrimestre = quadrimestre; }

    /******* OTHER ******/

    /**
     * Modifica el nombre de grups disponibles, així com la seva capacitat i el numero de subgrups si escau
     * @param num_grups nombre de grups que es vol tenir per l'assignatura
     * @param grup_cap capacitat d'alumnes per cada grup
     * @param sgrup_num nombre de subgrups que es vol tenir per cada grup
     */
    public void modificarGrups(int num_grups, int grup_cap, int sgrup_num){
        this.grups = new HashMap<Integer, Grup>();
        for(int i = 10; i <= num_grups; i+=10){
            grups.put(i, new Grup(i, grup_cap, sgrup_num));
        }
    }

    /**
     * Assigna a una assignatura una altra assignatura com a correquisit
     * @param a Assignatura correquisit de self
     */
    public void afegeixCorrequisit(Assignatura a){
        correquisit.add(a);
    }

    /**
     * Esborra una assignatura com a correquisit d'aquesta
     * @param a Assignatura a esborrar de self
     */
    public void esborraCorrequisit(Assignatura a){
        correquisit.remove(a);
    }


    /**
     * Retorna una llista dels correquisits de l'assignatura
     * @return llista de correquisits
     */
    public ArrayList<Assignatura> getCorrequisits() {
        return correquisit;
    }

    /**
     * Diu si una assignatura és correquisit de la primera
     * @param a assignatura a comparar
     * @return cert si és correquisit, fals altrament
     */
    public boolean esCorrequisit(Assignatura a){
        return correquisit.contains(a);
    }

    public boolean hasGroups() {
        return !this.grups.isEmpty();
    }
}
