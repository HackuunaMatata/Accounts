package ru.dao;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;


@Entity
@Table(name = "Questions", schema = "Accaunts")
public class QuestionsEntity {
   private int id;
   private String name;
   private int ttype;
   private int visible;
   private int cchange;
   private int formattype;
   private Collection<DateTableEntity> dateTablesById;
   private Collection<IntTableEntity> intTablesById;
   private Collection<ListsTableEntity> listsTablesById;
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
   @Column(name = "ttype", nullable = false)
   public int getTtype () {
      return ttype;
   }

   public void setTtype (int ttype) {
      this.ttype = ttype;
   }

   @Basic
   @Column(name = "visible", nullable = false)
   public int getVisible () {
      return visible;
   }

   public void setVisible (int visible) {
      this.visible = visible;
   }

   @Basic
   @Column(name = "cchange", nullable = false)
   public int getCchange () {
      return cchange;
   }

   public void setCchange (int cchange) {
      this.cchange = cchange;
   }

   @Basic
   @Column(name = "formattype", nullable = false)
   public int getFormattype () {
      return formattype;
   }

   public void setFormattype (int formattype) {
      this.formattype = formattype;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QuestionsEntity that = (QuestionsEntity) o;

      if (id != that.id) return false;
      if (ttype != that.ttype) return false;
      if (visible != that.visible) return false;
      if (cchange != that.cchange) return false;
      if (formattype != that.formattype) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;

      return true;
   }

   @Override
   public int hashCode () {
      int result = id;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + ttype;
      result = 31 * result + visible;
      result = 31 * result + cchange;
      result = 31 * result + formattype;
      return result;
   }

   @OneToMany(mappedBy = "questionsByIdQuestion")
   public Collection<DateTableEntity> getDateTablesById () {
      return dateTablesById;
   }

   public void setDateTablesById (Collection<DateTableEntity> dateTablesById) {
      this.dateTablesById = dateTablesById;
   }

   @OneToMany(mappedBy = "questionsByIdQuestion")
   public Collection<IntTableEntity> getIntTablesById () {
      return intTablesById;
   }

   public void setIntTablesById (Collection<IntTableEntity> intTablesById) {
      this.intTablesById = intTablesById;
   }

   @OneToMany(mappedBy = "questionsByIdQuestion")
   public Collection<ListsTableEntity> getListsTablesById () {
      return listsTablesById;
   }

   public void setListsTablesById (Collection<ListsTableEntity> listsTablesById) {
      this.listsTablesById = listsTablesById;
   }

   @OneToMany(mappedBy = "questionsByIdQuestion")
   public Collection<StrTableEntity> getStrTablesById () {
      return strTablesById;
   }

   public void setStrTablesById (Collection<StrTableEntity> strTablesById) {
      this.strTablesById = strTablesById;
   }
}
