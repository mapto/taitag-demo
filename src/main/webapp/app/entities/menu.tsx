import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/document">
        <Translate contentKey="global.menu.entities.document" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/person">
        <Translate contentKey="global.menu.entities.person" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/place">
        <Translate contentKey="global.menu.entities.place" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu as React.ComponentType<any>;
