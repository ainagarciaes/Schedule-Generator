package model;

import java.util.ArrayList;

public class Subgrup extends Grup {

    private Grup grup;


    /**
     * Crea un nou subgrup
     *
     * @param capacitat   capacitat del grup
     * @param num         nom del grup
     * @param subgrups    subgrups del qual es composa el grup
     */
    public Subgrup(int num, int capacitat, int subgrups, Grup grup) {
        super(capacitat, num, subgrups);
        this.grup = grup;
    }
}
