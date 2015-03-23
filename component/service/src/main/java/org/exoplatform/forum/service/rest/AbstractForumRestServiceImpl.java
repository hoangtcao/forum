package org.exoplatform.forum.service.rest;

import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.forum.common.CommonUtils;
import org.exoplatform.forum.common.UserHelper;
import org.exoplatform.forum.service.Category;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.Utils;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;

public abstract class AbstractForumRestServiceImpl {
  
  public static final int             DEFAULT_LIMIT  = 20;
  public static final int             HARD_LIMIT     = 50;
  public static final int             DEFAULT_OFFSET = 0;
  public static final String          CATEGORIES     = "categories";

  protected static final CacheControl cc;
  static {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    cc = new CacheControl();
    cc.setNoCache(true);
    cc.setNoStore(true);
  }

  protected String getUserId(SecurityContext sc, UriInfo uriInfo) {
    try {
      String userId = getUserId();
      return (CommonUtils.isEmpty(userId)) ? sc.getUserPrincipal().getName() : userId;
    } catch (NullPointerException e) {
      return getViewerId(uriInfo);
    } catch (Exception e) {
      return null;
    }
  }
  
  private String getUserId() {
    try {
      ConversationState conversionState = ConversationState.getCurrent();
      Identity identity = conversionState.getIdentity();
      String userId = identity.getUserId();
      return (CommonUtils.isEmpty(userId) || IdentityConstants.ANONIM.equals(userId)) ? null : userId;
    } catch (Exception e) {
      return null;
    }
  }

  private String getViewerId(UriInfo uriInfo) {
    URI uri = uriInfo.getRequestUri();
    String requestString = uri.getQuery();
    if (requestString == null) {
      return null;
    }
    String[] queryParts = requestString.split("&");
    for (String queryPart : queryParts) {
      if (queryPart.startsWith("opensocial_viewer_id")) {
        return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
      }
    }
    return null;
  }
  
  /**
   * Check if the current user has permission to view the category
   * 
   * @param category
   * @param userId
   * @return
   */
  public boolean hasCanViewCategory(Category category, String userId) {
    String[] usersPrivates = category.getUserPrivate();
    return (CommonUtils.isEmpty(usersPrivates)) || 
        Utils.hasPermission(Arrays.asList(usersPrivates), UserHelper.getAllGroupAndMembershipOfUser(userId));
  }

  public boolean isManagerCategory(String userId) throws Exception {
    return getForumService().isAdminRole(userId);
  }

  protected ForumService getForumService() {
    return CommonsUtils.getService(ForumService.class);
  }
}
