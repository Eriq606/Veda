import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import veda.EntityTable;
import veda.godao.DAO;
import veda.godao.Look;
import veda.godao.annotations.Column;
import veda.godao.annotations.ForeignKey;
import veda.godao.annotations.PrimaryKey;
import veda.godao.annotations.Table;
import veda.godao.utils.Constantes;
import veda.godao.utils.DAOConnexion;

public class App {
    @Table("dept")
    public static class Dept{
        @PrimaryKey
        @Column("id")
        Integer iddept;
        @Column("label")
        String nom;
        @Column("numero")
        private Integer numero;

        public Integer getIddept() {
            return iddept;
        }
        public void setIddept(Integer iddept) {
            this.iddept = iddept;
        }
        public String getNom() {
            return nom;
        }
        public void setNom(String nom) {
            this.nom = nom;
        }
        
        public Dept(String nom, Integer numero) {
            this.nom = nom;
            this.numero = numero;
        }
        public Dept() {
        }
        public Integer getNumero() {
            return numero;
        }
        public void setNumero(Integer numero) {
            this.numero = numero;
        }
        public Dept(Integer iddept) {
            this.iddept = iddept;
        }
        @Override
        public String toString() {
            return "Dept [iddept=" + iddept + ", nom=" + nom + ", numero=" + numero + "]";
        }
        
    }
    @Table("emp")
    public static class Emp{
        @PrimaryKey
        @Column("idemp")
        Integer id;
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        @Column("label")
        String nom;
        // @Column("age")
        // Double age;
        @ForeignKey(recursive = false)
        @Column("iddeptemp")
        Dept dept;
        public Dept getDept() {
            return dept;
        }
        public void setDept(Dept dept) {
            this.dept = dept;
        }
        public String getNom() {
            return nom;
        }
        public void setNom(String nom) {
            this.nom = nom;
        }
        @Column("embauche")
        private LocalDateTime embauche;
        @Column("embauche_heure")
        private LocalDateTime embHeure;
        
        public LocalDateTime getEmbauche() {
            return embauche;
        }
        public void setEmbauche(LocalDateTime embauche) {
            this.embauche = embauche;
        }
        public Emp(String nom, App.Dept dept, LocalDateTime embauche) {
            this.nom = nom;
            this.dept = dept;
            this.embauche = embauche;
        }
        public Emp() {
        }
        public Emp(String string, App.Dept dept2) {
            setNom(string);
            setDept(dept2);
        }
        public LocalDateTime getEmbHeure() {
            return embHeure;
        }
        public void setEmbHeure(LocalDateTime embHeure) {
            this.embHeure = embHeure;
        }
        @Override
        public String toString() {
            return "Emp [id=" + id + ", nom=" + nom + ", dept=" + dept + ", embauche=" + embauche + ", embHeure="
                    + embHeure + "]";
        }
        
        // public Double getAge() {
        //     return age;
        // }
        // public void setAge(Double age) {
        //     this.age = age;
        // }
    }
    @Table("film")
    public static class Film{
        @PrimaryKey
        @Column("id")
        private Integer id;
        @Column("nom")
        private String nom;
        @Column("duree")
        private Double duree;
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public String getNom() {
            return nom;
        }
        public void setNom(String nom) {
            this.nom = nom;
        }
        public Double getDuree() {
            return duree;
        }
        public void setDuree(Double duree) {
            this.duree = duree;
        }
        
    }
    public static void main(String[] args) throws Exception {
        DAO dao=new DAO("org.postgresql.Driver", "postgresql", "scott", "localhost", "5432", "eriq", "root", false, true, 2);
        // try(Connection connect=DAOConnexion.getConnexion(dao)){
        //     // EntityTable table=new EntityTable();0
        //     HashMap<String, Object> contenu=dao.select(connect, String.format("select contenu_%s as contenu from contenu where idpage=%s", "de", "2"))[0];
        // }
        // Dept[] depts={
        //     new Dept("Marketing", 3),
        //     new Dept("Gestion", null)
        // };
        // Emp[] emps={
        //     new Emp("Luc", new Dept(100091)),
        //     new Emp("Jeanne", new Dept(100091)),
        //     new Emp("Bob", new Dept(100091))
        // };
        try(Connection connect=DAOConnexion.getConnexion(dao);
            PreparedStatement statement=connect.prepareStatement("select * from emp");
            ResultSet result=statement.executeQuery();
            PreparedStatement statement2=connect.prepareStatement("select count(*) from emp");
            ResultSet compte=statement2.executeQuery()){
            int size=compte.next()?compte.getInt(1):0;
            Emp[] emps=new Emp[size];
            for(int i=0;result.next();i++){
                emps[i]=new Emp(result.getString("label"), null, result.getTimestamp("embauche_heure").toLocalDateTime());
                System.out.println(emps[i]);
            }
        }
        // Emp where=new Emp(null, null, LocalDateTime.of(2024, 10, 10, 9, 0));
        // try{
        //     Emp[] emps=dao.select(connect, Emp.class, where, 2, 2);
        //     for(Emp e:emps){
        //         System.out.println(e.getNom());
        //     }
        // }catch(Exception e){
        //     // connect.rollback();
        //     throw e;
        // }finally{
        //     connect.close();
        // }

    }
    @Test
    public void insertEmp() throws Exception{
        Dept dept=new Dept();
        dept.setIddept(1);
        Emp e=new Emp();
        e.nom="Ferry";
        e.setDept(dept);
        DAO dao=new DAO("scott", "localhost", "5432", "eriq", "root", false, Constantes.PSQL_ID);
        dao.insertWithoutPrimaryKey(null, e);
    }
    @Test
    public void updateEmp() throws Exception{
        Emp where=new Emp();
        where.setId(2);
        Dept dept=new Dept();
        dept.setIddept(2);
        Emp e=new Emp();
        e.nom="Ferry2";
        e.setDept(dept);
        DAO dao=new DAO("scott", "localhost", "5432", "eriq", "root", false, Constantes.PSQL_ID);
        dao.update(null, e, where);
    }
    @Test
    public void insertDept() throws Exception{
        Dept dept=new Dept();
        dept.setNom("Finances");
        DAO dao=new DAO("vedatest", "localhost", "5432", "eriq", "root", false, Constantes.PSQL_ID);
        dao.insertWithoutPrimaryKey(null, dept);
    }
    @Test
    public void selectEmps() throws Exception{
        DAO dao=new DAO("vedatest", "localhost", "5432", "eriq", "root", false, Constantes.PSQL_ID);
        Emp[] emps=dao.select(null, Emp.class);
        for(Emp e:emps){
            System.out.println(e.getNom()+" "+e.getDept());
        }
    }
    @Test
    public void updateLook() throws Exception{
        DAO dao=new DAO("poketra", "localhost", "5432", "eriq", "root", false, Constantes.PSQL_ID);
        Look change=new Look();
        change.setId(1);
        change.setNom("debraille");
        Look where=new Look();
        where.setId(1);
        dao.update(null, change, where);
    }
    private void hell(int[][] values){

    }
    @Test
    public void testSwitch(){
        class Hell{
            Integer a;
        }
        Object e=null;
        Hell el=(Hell)e;
        System.out.println(el);
    }
}
