import React, { useState, useEffect } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate, TextFormat, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IDocument } from 'app/shared/model/document.model';
import { searchEntities, getEntities, reset } from './document.reducer';

export const Document = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );
  const [sorting, setSorting] = useState(false);

  const documentList = useAppSelector(state => state.document.entities);
  const loading = useAppSelector(state => state.document.loading);
  const totalItems = useAppSelector(state => state.document.totalItems);
  const links = useAppSelector(state => state.document.links);
  const entity = useAppSelector(state => state.document.entity);
  const updateSuccess = useAppSelector(state => state.document.updateSuccess);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    }
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(reset());
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    dispatch(reset());
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting, search]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const { match } = props;

  return (
    <div>
      <h2 id="document-heading" data-cy="DocumentHeading">
        <Translate contentKey="taitagDemoApp.document.home.title">Documents</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="taitagDemoApp.document.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/document/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="taitagDemoApp.document.home.createLabel">Create new Document</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('taitagDemoApp.document.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={documentList ? documentList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {documentList && documentList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="taitagDemoApp.document.id">Id</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('georgianDate')}>
                    <Translate contentKey="taitagDemoApp.document.georgianDate">Georgian Date</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('type')}>
                    <Translate contentKey="taitagDemoApp.document.type">Type</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('incipit')}>
                    <Translate contentKey="taitagDemoApp.document.incipit">Incipit</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('transcription')}>
                    <Translate contentKey="taitagDemoApp.document.transcription">Transcription</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('collection')}>
                    <Translate contentKey="taitagDemoApp.document.collection">Collection</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('archive')}>
                    <Translate contentKey="taitagDemoApp.document.archive">Archive</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('folder')}>
                    <Translate contentKey="taitagDemoApp.document.folder">Folder</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('folderN')}>
                    <Translate contentKey="taitagDemoApp.document.folderN">Folder N</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('shelfMark')}>
                    <Translate contentKey="taitagDemoApp.document.shelfMark">Shelf Mark</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('publication')}>
                    <Translate contentKey="taitagDemoApp.document.publication">Publication</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('field')}>
                    <Translate contentKey="taitagDemoApp.document.field">Field</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="taitagDemoApp.document.written">Written</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="taitagDemoApp.document.arrived">Arrived</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="taitagDemoApp.document.originated">Originated</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {documentList.map((document, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/document/${document.id}`} color="link" size="sm">
                        {document.id}
                      </Button>
                    </td>
                    <td>
                      {document.georgianDate ? (
                        <TextFormat type="date" value={document.georgianDate} format={APP_LOCAL_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>
                      <Translate contentKey={`taitagDemoApp.DocumentType.${document.type}`} />
                    </td>
                    <td>{document.incipit}</td>
                    <td>{document.transcription}</td>
                    <td>{document.collection}</td>
                    <td>
                      <Translate contentKey={`taitagDemoApp.Archive.${document.archive}`} />
                    </td>
                    <td>{document.folder}</td>
                    <td>{document.folderN}</td>
                    <td>{document.shelfMark}</td>
                    <td>{document.publication}</td>
                    <td>
                      <Translate contentKey={`taitagDemoApp.Field.${document.field}`} />
                    </td>
                    <td>{document.written ? <Link to={`/person/${document.written.id}`}>{document.written.id}</Link> : ''}</td>
                    <td>{document.arrived ? <Link to={`/place/${document.arrived.id}`}>{document.arrived.id}</Link> : ''}</td>
                    <td>{document.originated ? <Link to={`/place/${document.originated.id}`}>{document.originated.id}</Link> : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/document/${document.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`/document/${document.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`/document/${document.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="taitagDemoApp.document.home.notFound">No Documents found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default Document;
