package it.unimi.di.islab.taitag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Place.
 */
@Entity
@Table(name = "place")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "place")
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "italian_name")
    private String italianName;

    @OneToMany(mappedBy = "arrived")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "written", "arrived", "originated", "receiveds", "quotedIns" }, allowSetters = true)
    private Set<Document> placeIns = new HashSet<>();

    @OneToMany(mappedBy = "originated")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "written", "arrived", "originated", "receiveds", "quotedIns" }, allowSetters = true)
    private Set<Document> placeOuts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Place id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public Place city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getItalianName() {
        return this.italianName;
    }

    public Place italianName(String italianName) {
        this.setItalianName(italianName);
        return this;
    }

    public void setItalianName(String italianName) {
        this.italianName = italianName;
    }

    public Set<Document> getPlaceIns() {
        return this.placeIns;
    }

    public void setPlaceIns(Set<Document> documents) {
        if (this.placeIns != null) {
            this.placeIns.forEach(i -> i.setArrived(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setArrived(this));
        }
        this.placeIns = documents;
    }

    public Place placeIns(Set<Document> documents) {
        this.setPlaceIns(documents);
        return this;
    }

    public Place addPlaceIn(Document document) {
        this.placeIns.add(document);
        document.setArrived(this);
        return this;
    }

    public Place removePlaceIn(Document document) {
        this.placeIns.remove(document);
        document.setArrived(null);
        return this;
    }

    public Set<Document> getPlaceOuts() {
        return this.placeOuts;
    }

    public void setPlaceOuts(Set<Document> documents) {
        if (this.placeOuts != null) {
            this.placeOuts.forEach(i -> i.setOriginated(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setOriginated(this));
        }
        this.placeOuts = documents;
    }

    public Place placeOuts(Set<Document> documents) {
        this.setPlaceOuts(documents);
        return this;
    }

    public Place addPlaceOut(Document document) {
        this.placeOuts.add(document);
        document.setOriginated(this);
        return this;
    }

    public Place removePlaceOut(Document document) {
        this.placeOuts.remove(document);
        document.setOriginated(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Place)) {
            return false;
        }
        return id != null && id.equals(((Place) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Place{" +
            "id=" + getId() +
            ", city='" + getCity() + "'" +
            ", italianName='" + getItalianName() + "'" +
            "}";
    }
}
