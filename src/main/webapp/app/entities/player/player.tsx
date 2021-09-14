import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Col, Row, Table } from 'reactstrap';
import { Translate, translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { searchEntities, getEntities } from './player.reducer';
import { IPlayer } from 'app/shared/model/player.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Player = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [search, setSearch] = useState('');

  const playerList = useAppSelector(state => state.player.entities);
  const loading = useAppSelector(state => state.player.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(searchEntities({ query: search }));
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="player-heading" data-cy="PlayerHeading">
        <Translate contentKey="jhipsterSampleApplicationApp.player.home.title">Players</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="jhipsterSampleApplicationApp.player.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipsterSampleApplicationApp.player.home.createLabel">Create new Player</Translate>
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
                  placeholder={translate('jhipsterSampleApplicationApp.player.home.search')}
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
        {playerList && playerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.country">Country</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.age">Age</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.position">Position</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.foot">Foot</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.signed">Signed</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.contractUntil">Contract Until</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.value">Value</Translate>
                </th>
                <th>
                  <Translate contentKey="jhipsterSampleApplicationApp.player.team">Team</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {playerList.map((player, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${player.id}`} color="link" size="sm">
                      {player.id}
                    </Button>
                  </td>
                  <td>{player.name}</td>
                  <td>{player.country}</td>
                  <td>{player.age}</td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.Position.${player.position}`} />
                  </td>
                  <td>
                    <Translate contentKey={`jhipsterSampleApplicationApp.Foot.${player.foot}`} />
                  </td>
                  <td>{player.signed ? <TextFormat type="date" value={player.signed} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{player.contractUntil ? <TextFormat type="date" value={player.contractUntil} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{player.value}</td>
                  <td>{player.team ? <Link to={`team/${player.team.id}`}>{player.team.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${player.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${player.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${player.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="jhipsterSampleApplicationApp.player.home.notFound">No Players found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Player;
