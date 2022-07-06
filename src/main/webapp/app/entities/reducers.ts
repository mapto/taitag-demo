import document from 'app/entities/document/document.reducer';
import person from 'app/entities/person/person.reducer';
import place from 'app/entities/place/place.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  document,
  person,
  place,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
