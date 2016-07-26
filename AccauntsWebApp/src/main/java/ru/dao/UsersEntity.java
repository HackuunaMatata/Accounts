package ru.dao;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;


@Entity
@Table(name = "Users", schema = "Accaunts")
public class UsersEntity {
   private int id;
   private String name;
   private String surname;
   private Timestamp dateOfLastChange;
   private Collection<DateTableEntity> dateTablesById;
   private Collection<IntTableEntity> intTablesById;
   private Collection<StrTableEntity> strTablesById;

   @Id
   @GenericGenerator(name = "gen", strategy = "increment")
   @GeneratedValue(generator = "gen")
   @Column(name = "id", nullable = false)
   public int getId () {
      return id;
   }

   public void setId (int id) {
      this.id = id;
   }

   @Basic
   @Column(name = "name", nullable = false, length = 255)
   public String getName () {
      return name;
   }

   public void setName (String name) {
      this.name = name;
   }

   @Basic
   @Column(name = "surname", nullable = false, length = 255)
   public String getSurname () {
      return surname;
   }

   public void setSurname (String surname) {
      this.surname = surname;
   }

   @Basic
   @Column(name = "dateOfLastChange", nullable = false)
   public Timestamp getDateOfLastChange () {
      return dateOfLastChange;
   }

   public void setDateOfLastChange (Timestamp dateOfLastChange) {
      this.dateOfLastChange = dateOfLastChange;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UsersEntity that = (UsersEntity) o;

      if (id != that.id) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;
      if (surname != null ? !surname.equals(that.surname) : that.surname != null) return false;
      if (dateOfLastChange != null ? !dateOfLastChange.equals(that.dateOfLastChange) : that.dateOfLastChange != null)
         return false;

      return true;
   }

   @Override
   public int hashCode () {
      int result = id;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (surname != null ? surname.hashCode() : 0);
      result = 31 * result + (dateOfLastChange != null ? dateOfLastChange.hashCode() : 0);
      return result;
   }

   @OneToMany(mappedBy = "usersByIdUser")
   public Collection<DateTableEntity> getDateTablesById () {
      return dateTablesById;
   }

   public void setDateTablesById (Collection<DateTableEntity> dateTablesById) {
      this.dateTablesById = dateTablesById;
   }

   @OneToMany(mappedBy = "usersByIdUser")
   public Collection<IntTableEntity> getIntTablesById () {
      return intTablesById;
   }

   public void setIntTablesById (Collection<IntTableEntity> intTablesById) {
      this.intTablesById = intTablesById;
   }

   @OneToMany(mappedBy = "usersByIdUser")
   public Collection<StrTableEntity> getStrTablesById () {
      return strTablesById;
   }

   public void setStrTablesById (Collection<StrTableEntity> strTablesById) {
      this.strTablesById = strTablesById;
   }
}
