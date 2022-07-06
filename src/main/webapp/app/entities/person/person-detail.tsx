import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './person.reducer';

export const PersonDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const personEntity = useAppSelector(state => state.person.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="personDetailsHeading">
          <Translate contentKey="taitagDemoApp.person.detail.title">Person</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{personEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="taitagDemoApp.person.name">Name</Translate>
            </span>
          </dt>
          <dd>{personEntity.name}</dd>
          <dt>
            <span id="latinName">
              <Translate contentKey="taitagDemoApp.person.latinName">Latin Name</Translate>
            </span>
          </dt>
          <dd>{personEntity.latinName}</dd>
          <dt>
            <span id="aka">
              <Translate contentKey="taitagDemoApp.person.aka">Aka</Translate>
            </span>
          </dt>
          <dd>{personEntity.aka}</dd>
          <dt>
            <span id="birth">
              <Translate contentKey="taitagDemoApp.person.birth">Birth</Translate>
            </span>
          </dt>
          <dd>{personEntity.birth}</dd>
          <dt>
            <span id="death">
              <Translate contentKey="taitagDemoApp.person.death">Death</Translate>
            </span>
          </dt>
          <dd>{personEntity.death}</dd>
          <dt>
            <span id="affiliation">
              <Translate contentKey="taitagDemoApp.person.affiliation">Affiliation</Translate>
            </span>
          </dt>
          <dd>{personEntity.affiliation}</dd>
        </dl>
        <Button tag={Link} to="/person" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/person/${personEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PersonDetail;
