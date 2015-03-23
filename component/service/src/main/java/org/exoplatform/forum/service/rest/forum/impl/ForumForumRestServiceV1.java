package org.exoplatform.forum.service.rest.forum.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.forum.service.Forum;
import org.exoplatform.forum.service.Utils;
import org.exoplatform.forum.service.rest.AbstractForumRestServiceImpl;
import org.exoplatform.forum.service.rest.RestUtils;
import org.exoplatform.forum.service.rest.api.ForumForumRestService;
import org.exoplatform.forum.service.rest.model.BaseEntity;
import org.exoplatform.forum.service.rest.model.CategoryEntity;
import org.exoplatform.forum.service.rest.model.ForumEntity;

@Path("v1/forum/forums")
public class ForumForumRestServiceV1 extends AbstractForumRestServiceImpl implements ForumForumRestService {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getForum(@Context SecurityContext sc, @Context UriInfo uriInfo,
                            @PathParam("id") String id) throws Exception {
    try {
      String userId = getUserId(sc, uriInfo);
      String fields = getQueryValueFields(uriInfo);
      String expand = getQueryValueExpand(uriInfo);
      
      Forum forum = (Forum) getForumService().getObjectNameById(id, Utils.FORUM);

      if (forum == null || !hasCanViewForum(forum, userId)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      ForumEntity entity = new ForumEntity(forum);
      entity.setHref(RestUtils.getRestUrl(FORUMS, forum.getId(), uriInfo.getPath()));
      //
      String categoryHref = RestUtils.getRestUrl(CATEGORIES, forum.getCategoryId(), uriInfo.getPath());
      if ("category".equals(expand)) {
        BaseEntity href = new CategoryEntity(getForumService().getCategory(forum.getCategoryId()));
        entity.setCategory(href.setHref(categoryHref));
      } else {
        entity.setCategory(new BaseEntity(categoryHref));
      }
      
      return Response.ok(entity.getData(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateForum(@Context SecurityContext sc, @Context UriInfo uriInfo,
                           @PathParam("id") String id, ForumEntity entityInput) throws Exception {
    try {
      String userId = getUserId(sc, uriInfo);
      String fields = getQueryValueFields(uriInfo);
      String expand = getQueryValueExpand(uriInfo);
      //
      System.out.println("\n jsonInput: " + entityInput.toString());
      
      Forum forum = (Forum) getForumService().getObjectNameById(id, Utils.FORUM);
      
      if (forum == null || !hasCanViewForum(forum, userId)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      
      ForumEntity entity = new ForumEntity(forum);
      return Response.ok(entity.getData(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeForum(@Context SecurityContext sc, @Context UriInfo uriInfo,
                              @PathParam("id") String id) throws Exception {
    try {
      String userId = getUserId(sc, uriInfo);
      String fields = getQueryValueFields(uriInfo);
      String expand = getQueryValueExpand(uriInfo);
      //
      Forum forum = (Forum) getForumService().getObjectNameById(id, Utils.FORUM);
      
      if (forum == null || !isManagerCategory(userId)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      
      ForumEntity entity = new ForumEntity(forum);
      return Response.ok(entity.getData(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("/{id}/topics")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTopics(@Context SecurityContext sc, @Context UriInfo uriInfo,
                              @PathParam("id") String id) throws Exception {
    try {
      String userId = getUserId(sc, uriInfo);
      String fields = getQueryValueFields(uriInfo);
      String expand = getQueryValueExpand(uriInfo);
      //
      Forum forum = (Forum) getForumService().getObjectNameById(id, Utils.FORUM);
      
      if (forum == null || !hasCanViewForum(forum, userId)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      
      ForumEntity entity = new ForumEntity(forum);
      return Response.ok(entity.getData(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response createTopic(@Context SecurityContext sc, @Context UriInfo uriInfo,
                            @PathParam("id") String id) throws Exception {
    try {
      String userId = getUserId(sc, uriInfo);
      String fields = getQueryValueFields(uriInfo);
      String expand = getQueryValueExpand(uriInfo);
      //
      Forum forum = (Forum) getForumService().getObjectNameById(id, Utils.FORUM);
      
      if (forum == null || !hasCanViewForum(forum, userId)) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      
      ForumEntity entity = new ForumEntity(forum);
      return Response.ok(entity.getData(), MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  

}
