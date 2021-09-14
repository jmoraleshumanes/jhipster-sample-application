import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import League from './league';
import LeagueDetail from './league-detail';
import LeagueUpdate from './league-update';
import LeagueDeleteDialog from './league-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={LeagueUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={LeagueUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={LeagueDetail} />
      <ErrorBoundaryRoute path={match.url} component={League} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={LeagueDeleteDialog} />
  </>
);

export default Routes;
