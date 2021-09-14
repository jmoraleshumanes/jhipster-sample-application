import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Player from './player';
import PlayerDetail from './player-detail';
import PlayerUpdate from './player-update';
import PlayerDeleteDialog from './player-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PlayerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PlayerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PlayerDetail} />
      <ErrorBoundaryRoute path={match.url} component={Player} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PlayerDeleteDialog} />
  </>
);

export default Routes;
