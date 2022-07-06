import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './document.reducer';

export const DocumentDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const documentEntity = useAppSelector(state => state.document.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="documentDetailsHeading">
          <Translate contentKey="taitagDemoApp.document.detail.title">Document</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="taitagDemoApp.document.id">Id</Translate>
            </span>
          </dt>
          <dd>{documentEntity.id}</dd>
          <dt>
            <span id="georgianDate">
              <Translate contentKey="taitagDemoApp.document.georgianDate">Georgian Date</Translate>
            </span>
          </dt>
          <dd>
            {documentEntity.georgianDate ? (
              <TextFormat value={documentEntity.georgianDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="type">
              <Translate contentKey="taitagDemoApp.document.type">Type</Translate>
            </span>
          </dt>
          <dd>{documentEntity.type}</dd>
          <dt>
            <span id="incipit">
              <Translate contentKey="taitagDemoApp.document.incipit">Incipit</Translate>
            </span>
          </dt>
          <dd>{documentEntity.incipit}</dd>
          <dt>
            <span id="transcription">
              <Translate contentKey="taitagDemoApp.document.transcription">Transcription</Translate>
            </span>
          </dt>
          <dd>{documentEntity.transcription}</dd>
          <dt>
            <span id="collection">
              <Translate contentKey="taitagDemoApp.document.collection">Collection</Translate>
            </span>
          </dt>
          <dd>{documentEntity.collection}</dd>
          <dt>
            <span id="archive">
              <Translate contentKey="taitagDemoApp.document.archive">Archive</Translate>
            </span>
          </dt>
          <dd>{documentEntity.archive}</dd>
          <dt>
            <span id="folder">
              <Translate contentKey="taitagDemoApp.document.folder">Folder</Translate>
            </span>
          </dt>
          <dd>{documentEntity.folder}</dd>
          <dt>
            <span id="folderN">
              <Translate contentKey="taitagDemoApp.document.folderN">Folder N</Translate>
            </span>
          </dt>
          <dd>{documentEntity.folderN}</dd>
          <dt>
            <span id="shelfMark">
              <Translate contentKey="taitagDemoApp.document.shelfMark">Shelf Mark</Translate>
            </span>
          </dt>
          <dd>{documentEntity.shelfMark}</dd>
          <dt>
            <span id="publication">
              <Translate contentKey="taitagDemoApp.document.publication">Publication</Translate>
            </span>
          </dt>
          <dd>{documentEntity.publication}</dd>
          <dt>
            <span id="field">
              <Translate contentKey="taitagDemoApp.document.field">Field</Translate>
            </span>
          </dt>
          <dd>{documentEntity.field}</dd>
          <dt>
            <Translate contentKey="taitagDemoApp.document.written">Written</Translate>
          </dt>
          <dd>{documentEntity.written ? documentEntity.written.id : ''}</dd>
          <dt>
            <Translate contentKey="taitagDemoApp.document.arrived">Arrived</Translate>
          </dt>
          <dd>{documentEntity.arrived ? documentEntity.arrived.id : ''}</dd>
          <dt>
            <Translate contentKey="taitagDemoApp.document.originated">Originated</Translate>
          </dt>
          <dd>{documentEntity.originated ? documentEntity.originated.id : ''}</dd>
          <dt>
            <Translate contentKey="taitagDemoApp.document.received">Received</Translate>
          </dt>
          <dd>
            {documentEntity.receiveds
              ? documentEntity.receiveds.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {documentEntity.receiveds && i === documentEntity.receiveds.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="taitagDemoApp.document.quotedIn">Quoted In</Translate>
          </dt>
          <dd>
            {documentEntity.quotedIns
              ? documentEntity.quotedIns.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {documentEntity.quotedIns && i === documentEntity.quotedIns.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/document/${documentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DocumentDetail;
