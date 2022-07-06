package it.unimi.di.islab.taitag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "person")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "latin_name")
    private String latinName;

    @Column(name = "aka")
    private String aka;

    @Column(name = "birth")
    private Integer birth;

    @Column(name = "death")
    private Integer death;

    @Column(name = "affiliation")
    private String affiliation;

    @OneToMany(mappedBy = "written")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "written", "arrived", "originated", "receiveds", "quotedIns" }, allowSetters = true)
    private Set<Document> authors = new HashSet<>();

    @ManyToMany(mappedBy = "receiveds")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "written", "arrived", "originated", "receiveds", "quotedIns" }, allowSetters = true)
    private Set<Document> recipients = new HashSet<>();

    @ManyToMany(mappedBy = "quotedIns")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "written", "arrived", "originated", "receiveds", "quotedIns" }, allowSetters = true)
    private Set<Document> quoteds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Person id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Person name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatinName() {
        return this.latinName;
    }

    public Person latinName(String latinName) {
        this.setLatinName(latinName);
        return this;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public String getAka() {
        return this.aka;
    }

    public Person aka(String aka) {
        this.setAka(aka);
        return this;
    }

    public void setAka(String aka) {
        this.aka = aka;
    }

    public Integer getBirth() {
        return this.birth;
    }

    public Person birth(Integer birth) {
        this.setBirth(birth);
        return this;
    }

    public void setBirth(Integer birth) {
        this.birth = birth;
    }

    public Integer getDeath() {
        return this.death;
    }

    public Person death(Integer death) {
        this.setDeath(death);
        return this;
    }

    public void setDeath(Integer death) {
        this.death = death;
    }

    public String getAffiliation() {
        return this.affiliation;
    }

    public Person affiliation(String affiliation) {
        this.setAffiliation(affiliation);
        return this;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Set<Document> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Document> documents) {
        if (this.authors != null) {
            this.authors.forEach(i -> i.setWritten(null));
        }
        if (documents != null) {
            documents.forEach(i -> i.setWritten(this));
        }
        this.authors = documents;
    }

    public Person authors(Set<Document> documents) {
        this.setAuthors(documents);
        return this;
    }

    public Person addAuthor(Document document) {
        this.authors.add(document);
        document.setWritten(this);
        return this;
    }

    public Person removeAuthor(Document document) {
        this.authors.remove(document);
        document.setWritten(null);
        return this;
    }

    public Set<Document> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(Set<Document> documents) {
        if (this.recipients != null) {
            this.recipients.forEach(i -> i.removeReceived(this));
        }
        if (documents != null) {
            documents.forEach(i -> i.addReceived(this));
        }
        this.recipients = documents;
    }

    public Person recipients(Set<Document> documents) {
        this.setRecipients(documents);
        return this;
    }

    public Person addRecipient(Document document) {
        this.recipients.add(document);
        document.getReceiveds().add(this);
        return this;
    }

    public Person removeRecipient(Document document) {
        this.recipients.remove(document);
        document.getReceiveds().remove(this);
        return this;
    }

    public Set<Document> getQuoteds() {
        return this.quoteds;
    }

    public void setQuoteds(Set<Document> documents) {
        if (this.quoteds != null) {
            this.quoteds.forEach(i -> i.removeQuotedIn(this));
        }
        if (documents != null) {
            documents.forEach(i -> i.addQuotedIn(this));
        }
        this.quoteds = documents;
    }

    public Person quoteds(Set<Document> documents) {
        this.setQuoteds(documents);
        return this;
    }

    public Person addQuoted(Document document) {
        this.quoteds.add(document);
        document.getQuotedIns().add(this);
        return this;
    }

    public Person removeQuoted(Document document) {
        this.quoteds.remove(document);
        document.getQuotedIns().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", latinName='" + getLatinName() + "'" +
            ", aka='" + getAka() + "'" +
            ", birth=" + getBirth() +
            ", death=" + getDeath() +
            ", affiliation='" + getAffiliation() + "'" +
            "}";
    }
}
