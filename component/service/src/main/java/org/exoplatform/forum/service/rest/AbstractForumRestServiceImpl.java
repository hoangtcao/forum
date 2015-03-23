package org.exoplatform.forum.service.rest;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.lang.CharEncoding;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.forum.common.CommonUtils;
import org.exoplatform.forum.common.UserHelper;
import org.exoplatform.forum.service.Category;
import org.exoplatform.forum.service.Forum;
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
  public static final String          FORUMS         = "forums";

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
    return getQueryParam(uriInfo, "opensocial_viewer_id");
  }
  
  protected String getQueryParam(UriInfo uriInfo, String key) {
    URI uri = uriInfo.getRequestUri();
    if (uri.getQuery() == null) {
      return null;
    }
    List<NameValuePair> params = URLEncodedUtils.parse(uri, CharEncoding.UTF_8);
    for (NameValuePair param : params) {
      if (param.getName().equalsIgnoreCase(key)) {
        return param.getValue();
      }
    }

    return null;
  }
  
  protected String getQueryValueId(UriInfo uriInfo) {
    return getQueryParam(uriInfo, "id");
  }

  protected String getQueryValueFields(UriInfo uriInfo) {
    return getQueryParam(uriInfo, "fields");
  }

  protected String getQueryValueExpand(UriInfo uriInfo) {
    return getQueryParam(uriInfo, "expand");
  }

  protected Integer getQueryValueLimit(UriInfo uriInfo) {
    String limit = getQueryParam(uriInfo, "limit");
    return (limit != null) ? Integer.valueOf(limit) : -1;
  }

  protected Integer getQueryValueOffset(UriInfo uriInfo) {
    String offset = getQueryParam(uriInfo, "offset");
    return (offset != null) ? Integer.valueOf(offset) : -1;
  }

  protected boolean getQueryValueReturnSize(UriInfo uriInfo) {
    return Boolean.valueOf(getQueryParam(uriInfo, "returnSize"));
  }

  public boolean hasCanViewForum(Forum forum, String userId) {
    // check administrator
    if (isManagerCategory(userId)) {
      return true;
    }
    // check moderators
    List<String> roleOfUsers = UserHelper.getAllGroupAndMembershipOfUser(userId);
    if (Utils.hasPermission(Arrays.asList(forum.getModerators()), roleOfUsers)) {
      return true;
    }
    // check is close
    if (!forum.getIsClosed()) {
      // check can view category
      Category cat = getForumService().getCategory(forum.getCategoryId());
      String[] usersPrivates = cat.getUserPrivate();
      return (CommonUtils.isEmpty(usersPrivates)) || Utils.hasPermission(Arrays.asList(usersPrivates), roleOfUsers);
    }
    return false;
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
    return (CommonUtils.isEmpty(usersPrivates)) || isManagerCategory(userId) ||
        Utils.hasPermission(Arrays.asList(usersPrivates), UserHelper.getAllGroupAndMembershipOfUser(userId));
  }

  public boolean isManagerCategory(String userId) {
    try {
      return getForumService().isAdminRole(userId);
    } catch (Exception e) {
      return false;
    }
  }

  protected ForumService getForumService() {
    return CommonsUtils.getService(ForumService.class);
  }
}
