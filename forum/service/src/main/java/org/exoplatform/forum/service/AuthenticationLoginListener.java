/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.forum.service;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;

public class AuthenticationLoginListener extends Listener<ConversationRegistry, ConversationState> {
  private ExoContainerContext context;

  public AuthenticationLoginListener(ExoContainerContext context) throws Exception {
    this.context = context;
  }

  @Override
  public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {
    ConversationState state = event.getData();
    String userId = state.getIdentity().getUserId();
    ExoContainer container = context.getContainer();
    ForumService fservice = (ForumService) container.getComponentInstanceOfType(ForumService.class);
    String masterHost = System.getProperty("tenant.masterhost");
    if(masterHost == null) {
      ConversationState.setCurrent(state);
      fservice.userLogin(userId);
    } else {
      String currentLoginRepo = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class))
          .getCurrentRepository().getConfiguration().getName();
      fservice.userLogin(currentLoginRepo, userId);
    }
  }
}
