package it.unimi.di.islab.taitag.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.di.islab.taitag.domain.Person;
import it.unimi.di.islab.taitag.repository.PersonRepository;
import it.unimi.di.islab.taitag.repository.search.PersonSearchRepository;
import it.unimi.di.islab.taitag.service.PersonService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Person}.
 */
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;

    private final PersonSearchRepository personSearchRepository;

    public PersonServiceImpl(PersonRepository personRepository, PersonSearchRepository personSearchRepository) {
        this.personRepository = personRepository;
        this.personSearchRepository = personSearchRepository;
    }

    @Override
    public Person save(Person person) {
        log.debug("Request to save Person : {}", person);
        Person result = personRepository.save(person);
        personSearchRepository.save(result);
        return result;
    }

    @Override
    public Person update(Person person) {
        log.debug("Request to save Person : {}", person);
        Person result = personRepository.save(person);
        personSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Person> partialUpdate(Person person) {
        log.debug("Request to partially update Person : {}", person);

        return personRepository
            .findById(person.getId())
            .map(existingPerson -> {
                if (person.getName() != null) {
                    existingPerson.setName(person.getName());
                }
                if (person.getLatinName() != null) {
                    existingPerson.setLatinName(person.getLatinName());
                }
                if (person.getAka() != null) {
                    existingPerson.setAka(person.getAka());
                }
                if (person.getBirth() != null) {
                    existingPerson.setBirth(person.getBirth());
                }
                if (person.getDeath() != null) {
                    existingPerson.setDeath(person.getDeath());
                }
                if (person.getAffiliation() != null) {
                    existingPerson.setAffiliation(person.getAffiliation());
                }

                return existingPerson;
            })
            .map(personRepository::save)
            .map(savedPerson -> {
                personSearchRepository.save(savedPerson);

                return savedPerson;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        log.debug("Request to get all People");
        return personRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        return personRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
        personSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> search(String query) {
        log.debug("Request to search People for query {}", query);
        return StreamSupport.stream(personSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
