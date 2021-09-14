package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Foot;
import com.mycompany.myapp.domain.enumeration.Position;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Player.
 */
@Entity
@Table(name = "player")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "player")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "age")
    private Long age;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(name = "foot")
    private Foot foot;

    @Column(name = "signed")
    private Instant signed;

    @Column(name = "contract_until")
    private Instant contractUntil;

    @Column(name = "value")
    private Long value;

    @JsonIgnoreProperties(value = { "league" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Team team;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Player name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return this.country;
    }

    public Player country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getAge() {
        return this.age;
    }

    public Player age(Long age) {
        this.age = age;
        return this;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Position getPosition() {
        return this.position;
    }

    public Player position(Position position) {
        this.position = position;
        return this;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Foot getFoot() {
        return this.foot;
    }

    public Player foot(Foot foot) {
        this.foot = foot;
        return this;
    }

    public void setFoot(Foot foot) {
        this.foot = foot;
    }

    public Instant getSigned() {
        return this.signed;
    }

    public Player signed(Instant signed) {
        this.signed = signed;
        return this;
    }

    public void setSigned(Instant signed) {
        this.signed = signed;
    }

    public Instant getContractUntil() {
        return this.contractUntil;
    }

    public Player contractUntil(Instant contractUntil) {
        this.contractUntil = contractUntil;
        return this;
    }

    public void setContractUntil(Instant contractUntil) {
        this.contractUntil = contractUntil;
    }

    public Long getValue() {
        return this.value;
    }

    public Player value(Long value) {
        this.value = value;
        return this;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Team getTeam() {
        return this.team;
    }

    public Player team(Team team) {
        this.setTeam(team);
        return this;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        return id != null && id.equals(((Player) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", country='" + getCountry() + "'" +
            ", age=" + getAge() +
            ", position='" + getPosition() + "'" +
            ", foot='" + getFoot() + "'" +
            ", signed='" + getSigned() + "'" +
            ", contractUntil='" + getContractUntil() + "'" +
            ", value=" + getValue() +
            "}";
    }
}
