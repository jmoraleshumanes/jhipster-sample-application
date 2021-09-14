import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './player.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PlayerDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const playerEntity = useAppSelector(state => state.player.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="playerDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.player.detail.title">Player</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{playerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="jhipsterSampleApplicationApp.player.name">Name</Translate>
            </span>
          </dt>
          <dd>{playerEntity.name}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="jhipsterSampleApplicationApp.player.country">Country</Translate>
            </span>
          </dt>
          <dd>{playerEntity.country}</dd>
          <dt>
            <span id="age">
              <Translate contentKey="jhipsterSampleApplicationApp.player.age">Age</Translate>
            </span>
          </dt>
          <dd>{playerEntity.age}</dd>
          <dt>
            <span id="position">
              <Translate contentKey="jhipsterSampleApplicationApp.player.position">Position</Translate>
            </span>
          </dt>
          <dd>{playerEntity.position}</dd>
          <dt>
            <span id="foot">
              <Translate contentKey="jhipsterSampleApplicationApp.player.foot">Foot</Translate>
            </span>
          </dt>
          <dd>{playerEntity.foot}</dd>
          <dt>
            <span id="signed">
              <Translate contentKey="jhipsterSampleApplicationApp.player.signed">Signed</Translate>
            </span>
          </dt>
          <dd>{playerEntity.signed ? <TextFormat value={playerEntity.signed} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="contractUntil">
              <Translate contentKey="jhipsterSampleApplicationApp.player.contractUntil">Contract Until</Translate>
            </span>
          </dt>
          <dd>
            {playerEntity.contractUntil ? <TextFormat value={playerEntity.contractUntil} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="value">
              <Translate contentKey="jhipsterSampleApplicationApp.player.value">Value</Translate>
            </span>
          </dt>
          <dd>{playerEntity.value}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.player.team">Team</Translate>
          </dt>
          <dd>{playerEntity.team ? playerEntity.team.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/player" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/player/${playerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PlayerDetail;
