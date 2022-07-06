package it.unimi.di.islab.taitag.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimi.di.islab.taitag.domain.enumeration.Archive;
import it.unimi.di.islab.taitag.domain.enumeration.DocumentType;
import it.unimi.di.islab.taitag.domain.enumeration.Field;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "document")
public class Document implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "georgian_date")
    private LocalDate georgianDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DocumentType type;

    @Column(name = "incipit")
    private String incipit;

    @Column(name = "transcription")
    private String transcription;

    @Column(name = "collection")
    private String collection;

    @Enumerated(EnumType.STRING)
    @Column(name = "archive")
    private Archive archive;

    @Column(name = "folder")
    private String folder;

    @Column(name = "folder_n")
    private String folderN;

    @Column(name = "shelf_mark")
    private String shelfMark;

    @Column(name = "publication")
    private String publication;

    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    private Field field;

    @Transient
    private boolean isPersisted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "authors", "recipients", "quoteds" }, allowSetters = true)
    private Person written;

    @ManyToOne
    @JsonIgnoreProperties(value = { "placeIns", "placeOuts" }, allowSetters = true)
    private Place arrived;

    @ManyToOne
    @JsonIgnoreProperties(value = { "placeIns", "placeOuts" }, allowSetters = true)
    private Place originated;

    @ManyToMany
    @JoinTable(
        name = "rel_document__received",
        joinColumns = @JoinColumn(name = "document_id"),
        inverseJoinColumns = @JoinColumn(name = "received_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "authors", "recipients", "quoteds" }, allowSetters = true)
    private Set<Person> receiveds = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_document__quoted_in",
        joinColumns = @JoinColumn(name = "document_id"),
        inverseJoinColumns = @JoinColumn(name = "quoted_in_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "authors", "recipients", "quoteds" }, allowSetters = true)
    private Set<Person> quotedIns = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Document id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getGeorgianDate() {
        return this.georgianDate;
    }

    public Document georgianDate(LocalDate georgianDate) {
        this.setGeorgianDate(georgianDate);
        return this;
    }

    public void setGeorgianDate(LocalDate georgianDate) {
        this.georgianDate = georgianDate;
    }

    public DocumentType getType() {
        return this.type;
    }

    public Document type(DocumentType type) {
        this.setType(type);
        return this;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getIncipit() {
        return this.incipit;
    }

    public Document incipit(String incipit) {
        this.setIncipit(incipit);
        return this;
    }

    public void setIncipit(String incipit) {
        this.incipit = incipit;
    }

    public String getTranscription() {
        return this.transcription;
    }

    public Document transcription(String transcription) {
        this.setTranscription(transcription);
        return this;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getCollection() {
        return this.collection;
    }

    public Document collection(String collection) {
        this.setCollection(collection);
        return this;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Archive getArchive() {
        return this.archive;
    }

    public Document archive(Archive archive) {
        this.setArchive(archive);
        return this;
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public String getFolder() {
        return this.folder;
    }

    public Document folder(String folder) {
        this.setFolder(folder);
        return this;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFolderN() {
        return this.folderN;
    }

    public Document folderN(String folderN) {
        this.setFolderN(folderN);
        return this;
    }

    public void setFolderN(String folderN) {
        this.folderN = folderN;
    }

    public String getShelfMark() {
        return this.shelfMark;
    }

    public Document shelfMark(String shelfMark) {
        this.setShelfMark(shelfMark);
        return this;
    }

    public void setShelfMark(String shelfMark) {
        this.shelfMark = shelfMark;
    }

    public String getPublication() {
        return this.publication;
    }

    public Document publication(String publication) {
        this.setPublication(publication);
        return this;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public Field getField() {
        return this.field;
    }

    public Document field(Field field) {
        this.setField(field);
        return this;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Document setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Person getWritten() {
        return this.written;
    }

    public void setWritten(Person person) {
        this.written = person;
    }

    public Document written(Person person) {
        this.setWritten(person);
        return this;
    }

    public Place getArrived() {
        return this.arrived;
    }

    public void setArrived(Place place) {
        this.arrived = place;
    }

    public Document arrived(Place place) {
        this.setArrived(place);
        return this;
    }

    public Place getOriginated() {
        return this.originated;
    }

    public void setOriginated(Place place) {
        this.originated = place;
    }

    public Document originated(Place place) {
        this.setOriginated(place);
        return this;
    }

    public Set<Person> getReceiveds() {
        return this.receiveds;
    }

    public void setReceiveds(Set<Person> people) {
        this.receiveds = people;
    }

    public Document receiveds(Set<Person> people) {
        this.setReceiveds(people);
        return this;
    }

    public Document addReceived(Person person) {
        this.receiveds.add(person);
        person.getRecipients().add(this);
        return this;
    }

    public Document removeReceived(Person person) {
        this.receiveds.remove(person);
        person.getRecipients().remove(this);
        return this;
    }

    public Set<Person> getQuotedIns() {
        return this.quotedIns;
    }

    public void setQuotedIns(Set<Person> people) {
        this.quotedIns = people;
    }

    public Document quotedIns(Set<Person> people) {
        this.setQuotedIns(people);
        return this;
    }

    public Document addQuotedIn(Person person) {
        this.quotedIns.add(person);
        person.getQuoteds().add(this);
        return this;
    }

    public Document removeQuotedIn(Person person) {
        this.quotedIns.remove(person);
        person.getQuoteds().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Document)) {
            return false;
        }
        return id != null && id.equals(((Document) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Document{" +
            "id=" + getId() +
            ", georgianDate='" + getGeorgianDate() + "'" +
            ", type='" + getType() + "'" +
            ", incipit='" + getIncipit() + "'" +
            ", transcription='" + getTranscription() + "'" +
            ", collection='" + getCollection() + "'" +
            ", archive='" + getArchive() + "'" +
            ", folder='" + getFolder() + "'" +
            ", folderN='" + getFolderN() + "'" +
            ", shelfMark='" + getShelfMark() + "'" +
            ", publication='" + getPublication() + "'" +
            ", field='" + getField() + "'" +
            "}";
    }
}
