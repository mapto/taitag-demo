import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPerson } from 'app/shared/model/person.model';
import { getEntities as getPeople } from 'app/entities/person/person.reducer';
import { IPlace } from 'app/shared/model/place.model';
import { getEntities as getPlaces } from 'app/entities/place/place.reducer';
import { IDocument } from 'app/shared/model/document.model';
import { DocumentType } from 'app/shared/model/enumerations/document-type.model';
import { Archive } from 'app/shared/model/enumerations/archive.model';
import { Field } from 'app/shared/model/enumerations/field.model';
import { getEntity, updateEntity, createEntity, reset } from './document.reducer';

export const DocumentUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const people = useAppSelector(state => state.person.entities);
  const places = useAppSelector(state => state.place.entities);
  const documentEntity = useAppSelector(state => state.document.entity);
  const loading = useAppSelector(state => state.document.loading);
  const updating = useAppSelector(state => state.document.updating);
  const updateSuccess = useAppSelector(state => state.document.updateSuccess);
  const documentTypeValues = Object.keys(DocumentType);
  const archiveValues = Object.keys(Archive);
  const fieldValues = Object.keys(Field);
  const handleClose = () => {
    props.history.push('/document');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getPeople({}));
    dispatch(getPlaces({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...documentEntity,
      ...values,
      receiveds: mapIdList(values.receiveds),
      quotedIns: mapIdList(values.quotedIns),
      written: people.find(it => it.id.toString() === values.written.toString()),
      arrived: places.find(it => it.id.toString() === values.arrived.toString()),
      originated: places.find(it => it.id.toString() === values.originated.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          type: 'ResearchNotes',
          archive: 'BNF',
          field: 'Pneumatics',
          ...documentEntity,
          written: documentEntity?.written?.id,
          arrived: documentEntity?.arrived?.id,
          originated: documentEntity?.originated?.id,
          receiveds: documentEntity?.receiveds?.map(e => e.id.toString()),
          quotedIns: documentEntity?.quotedIns?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="taitagDemoApp.document.home.createOrEditLabel" data-cy="DocumentCreateUpdateHeading">
            <Translate contentKey="taitagDemoApp.document.home.createOrEditLabel">Create or edit a Document</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="document-id"
                  label={translate('taitagDemoApp.document.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('taitagDemoApp.document.georgianDate')}
                id="document-georgianDate"
                name="georgianDate"
                data-cy="georgianDate"
                type="date"
              />
              <ValidatedField label={translate('taitagDemoApp.document.type')} id="document-type" name="type" data-cy="type" type="select">
                {documentTypeValues.map(documentType => (
                  <option value={documentType} key={documentType}>
                    {translate('taitagDemoApp.DocumentType.' + documentType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('taitagDemoApp.document.incipit')}
                id="document-incipit"
                name="incipit"
                data-cy="incipit"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.transcription')}
                id="document-transcription"
                name="transcription"
                data-cy="transcription"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.collection')}
                id="document-collection"
                name="collection"
                data-cy="collection"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.archive')}
                id="document-archive"
                name="archive"
                data-cy="archive"
                type="select"
              >
                {archiveValues.map(archive => (
                  <option value={archive} key={archive}>
                    {translate('taitagDemoApp.Archive.' + archive)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('taitagDemoApp.document.folder')}
                id="document-folder"
                name="folder"
                data-cy="folder"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.folderN')}
                id="document-folderN"
                name="folderN"
                data-cy="folderN"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.shelfMark')}
                id="document-shelfMark"
                name="shelfMark"
                data-cy="shelfMark"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.publication')}
                id="document-publication"
                name="publication"
                data-cy="publication"
                type="text"
              />
              <ValidatedField
                label={translate('taitagDemoApp.document.field')}
                id="document-field"
                name="field"
                data-cy="field"
                type="select"
              >
                {fieldValues.map(field => (
                  <option value={field} key={field}>
                    {translate('taitagDemoApp.Field.' + field)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="document-written"
                name="written"
                data-cy="written"
                label={translate('taitagDemoApp.document.written')}
                type="select"
              >
                <option value="" key="0" />
                {people
                  ? people.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="document-arrived"
                name="arrived"
                data-cy="arrived"
                label={translate('taitagDemoApp.document.arrived')}
                type="select"
              >
                <option value="" key="0" />
                {places
                  ? places.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="document-originated"
                name="originated"
                data-cy="originated"
                label={translate('taitagDemoApp.document.originated')}
                type="select"
              >
                <option value="" key="0" />
                {places
                  ? places.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('taitagDemoApp.document.received')}
                id="document-received"
                data-cy="received"
                type="select"
                multiple
                name="receiveds"
              >
                <option value="" key="0" />
                {people
                  ? people.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('taitagDemoApp.document.quotedIn')}
                id="document-quotedIn"
                data-cy="quotedIn"
                type="select"
                multiple
                name="quotedIns"
              >
                <option value="" key="0" />
                {people
                  ? people.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/document" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default DocumentUpdate;
