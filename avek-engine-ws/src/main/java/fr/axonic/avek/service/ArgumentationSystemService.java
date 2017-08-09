package fr.axonic.avek.service;

import fr.axonic.avek.engine.ArgumentationSystemAPI;
import fr.axonic.avek.engine.support.SupportRole;
import fr.axonic.avek.engine.support.conclusion.Conclusion;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by cduffau on 16/01/17.
 */
public interface ArgumentationSystemService {

    @POST
    @Path("/system/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response registerArgumentationSystem(@QueryParam("argumentation_system") ArgumentationSystemAPI argumentationSystem);

    @DELETE
    @Path("/{argumentation_system_id}")
    Response removeArgumentationSystem(@PathParam("argumentation_system_id") String argumentationSystemId);

    @GET
    @Path("/systems")
    @Produces(MediaType.APPLICATION_JSON)
    Response getArgumentationSystems();

    @GET
    @Path("/{argumentation_system_id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getArgumentationSystem(@PathParam("argumentation_system_id") String argumentationSystemId);

    @GET
    @Path("/{argumentation_system_id}/patterns")
    @Produces(MediaType.APPLICATION_JSON)
    Response getArgumentationSystemPatterns(@PathParam("argumentation_system_id") String argumentationSystemId);

    @GET
    @Path("/{argumentation_system_id}/patterns/{pattern_id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getArgumentationSystemPattern(@PathParam("argumentation_system_id") String argumentationSystemId, @PathParam("pattern_id") String pattern);


    @POST
    @Path("/step/{argumentation_system_id}/{pattern_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response constructStep(@PathParam("argumentation_system_id") String argumentationSystem, @PathParam("pattern_id") String pattern, @QueryParam("evidences") List<SupportRole> evidences, @QueryParam("conclusion") Conclusion conclusion);

}
