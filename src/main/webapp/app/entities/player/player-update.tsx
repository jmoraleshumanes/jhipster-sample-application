import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ITeam } from 'app/shared/model/team.model';
import { getEntities as getTeams } from 'app/entities/team/team.reducer';
import { getEntity, updateEntity, createEntity, reset } from './player.reducer';
import { IPlayer } from 'app/shared/model/player.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PlayerUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const teams = useAppSelector(state => state.team.entities);
  const playerEntity = useAppSelector(state => state.player.entity);
  const loading = useAppSelector(state => state.player.loading);
  const updating = useAppSelector(state => state.player.updating);
  const updateSuccess = useAppSelector(state => state.player.updateSuccess);

  const handleClose = () => {
    props.history.push('/player');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getTeams({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.signed = convertDateTimeToServer(values.signed);
    values.contractUntil = convertDateTimeToServer(values.contractUntil);

    const entity = {
      ...playerEntity,
      ...values,
      team: teams.find(it => it.id.toString() === values.teamId.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          signed: displayDefaultDateTime(),
          contractUntil: displayDefaultDateTime(),
        }
      : {
          ...playerEntity,
          position: 'POR',
          foot: 'LEFT',
          signed: convertDateTimeFromServer(playerEntity.signed),
          contractUntil: convertDateTimeFromServer(playerEntity.contractUntil),
          teamId: playerEntity?.team?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jhipsterSampleApplicationApp.player.home.createOrEditLabel" data-cy="PlayerCreateUpdateHeading">
            <Translate contentKey="jhipsterSampleApplicationApp.player.home.createOrEditLabel">Create or edit a Player</Translate>
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
                  id="player-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.name')}
                id="player-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.country')}
                id="player-country"
                name="country"
                data-cy="country"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.age')}
                id="player-age"
                name="age"
                data-cy="age"
                type="text"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.position')}
                id="player-position"
                name="position"
                data-cy="position"
                type="select"
              >
                <option value="POR">{translate('jhipsterSampleApplicationApp.Position.POR')}</option>
                <option value="LI">{translate('jhipsterSampleApplicationApp.Position.LI')}</option>
                <option value="LD">{translate('jhipsterSampleApplicationApp.Position.LD')}</option>
                <option value="DFC">{translate('jhipsterSampleApplicationApp.Position.DFC')}</option>
                <option value="MD">{translate('jhipsterSampleApplicationApp.Position.MD')}</option>
                <option value="MI">{translate('jhipsterSampleApplicationApp.Position.MI')}</option>
                <option value="MC">{translate('jhipsterSampleApplicationApp.Position.MC')}</option>
                <option value="DEL">{translate('jhipsterSampleApplicationApp.Position.DEL')}</option>
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.foot')}
                id="player-foot"
                name="foot"
                data-cy="foot"
                type="select"
              >
                <option value="LEFT">{translate('jhipsterSampleApplicationApp.Foot.LEFT')}</option>
                <option value="RIGHT">{translate('jhipsterSampleApplicationApp.Foot.RIGHT')}</option>
                <option value="BOTH">{translate('jhipsterSampleApplicationApp.Foot.BOTH')}</option>
              </ValidatedField>
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.signed')}
                id="player-signed"
                name="signed"
                data-cy="signed"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.contractUntil')}
                id="player-contractUntil"
                name="contractUntil"
                data-cy="contractUntil"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('jhipsterSampleApplicationApp.player.value')}
                id="player-value"
                name="value"
                data-cy="value"
                type="text"
              />
              <ValidatedField
                id="player-team"
                name="teamId"
                data-cy="team"
                label={translate('jhipsterSampleApplicationApp.player.team')}
                type="select"
              >
                <option value="" key="0" />
                {teams
                  ? teams.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/player" replace color="info">
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

export default PlayerUpdate;
