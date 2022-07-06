import React from 'react';
import { Switch } from 'react-router-dom';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Document from './document';
import Person from './person';
import Place from './place';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default ({ match }) => {
  return (
    <div>
      <Switch>
        {/* prettier-ignore */}
        <ErrorBoundaryRoute path={`${match.url}document`} component={Document} />
        <ErrorBoundaryRoute path={`${match.url}person`} component={Person} />
        <ErrorBoundaryRoute path={`${match.url}place`} component={Place} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </Switch>
    </div>
  );
};
