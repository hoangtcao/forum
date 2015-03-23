package org.exoplatform.forum.service.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.forum.service.rest.AbstractForumRest;


public interface PostForumRestService extends AbstractForumRest {

  /**
   * Process to return all posts in json format
   * 
   * @param uriInfo
   * @param returnSize true if the response must contain the total size of all posts found
   * @param offset index of the first post to return 
   * @param limit the maximum number of posts to return
   * @return
   * @throws Exception
   */
  @GET
  public Response getPosts(@Context SecurityContext sc, @Context UriInfo uriInfo,
                            @QueryParam("returnSize") boolean returnSize,
                            @QueryParam("offset") int offset,
                            @QueryParam("limit") int limit) throws Exception;
}
