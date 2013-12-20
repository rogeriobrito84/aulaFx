package org.persistence;

import javax.persistence.*;


@Entity 
@Table(name="T_ENTITY2")
public class Entity2 {

  @Id 
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

}