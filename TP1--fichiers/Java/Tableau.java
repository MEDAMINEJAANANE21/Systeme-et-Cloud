class Tableau {

    private static final int TAILLETAB = 100;
    private double tab[];

    Tableau() {
        tab=new double[TAILLETAB];
        for (int i = 0; i< TAILLETAB; i++)
            tab[i]=0;
    }

    void incTab() {
        for (int i = 0; i< TAILLETAB; i++)
            tab[i]=tab[i]+10.0;
    }

    void decTab() {
        for (int i = 0; i< TAILLETAB; i++)
            tab[i]=tab[i]-10.0;
    }


    void printTab() {
        for (int i = 0; i< TAILLETAB; i++)
            System.out.println("tab["+ i + "] =" +tab[i]);
    }
}