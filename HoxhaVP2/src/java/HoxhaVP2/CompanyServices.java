package HoxhaVP2;

import javax.ws.rs.core.*;
import javax.ws.rs.*;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * REST Web Service
 *
 * @author Vjori Hoxha
 * @version 2.11.2019
 */
@Path("CompanyServices")
public class CompanyServices {

    @Context
    private UriInfo context;
    private CompanyBusiness cb;

    /**
     * Creates a new instance of CompanyServices
     */
    public CompanyServices() {
        try {
            cb = new CompanyBusiness();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 1 - Company DELETE
     */
    @Path("company")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompany(@QueryParam("company") String company) {
        return Response.ok(cb.deleteCompany(company)).build();
    }

    /*
     * 2 - Department GET
     */
    @Path("department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int dept_id) {
        return Response.ok(cb.getDepartment(company, dept_id)).build();
    }

    /**
     * 3 - Departments GET
     *
     * @param company
     * @return
     */
    @GET
    @Path("/departments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDepartments(@DefaultValue("vxh5681") @QueryParam("company") String company) {
        return cb.getAllDepartments(company);
    }

    /**
     * 4 - Department Update
     *
     * @param inJson
     * @return
     */
    @Path("department")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDepartment(@QueryParam("inJson") String inJson) {
        return cb.updateDepartment(inJson);
    }

    /*
     * 5- Department Create
     */
    @POST
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertDepartment(@FormParam("company") String company,
            @FormParam("dept_name") String dept_name,
            @FormParam("dept_no") String dept_no,
            @FormParam("location") String location) {
        return cb.insertDepartment(company, dept_name, dept_no, location);
    }

    /*
     * 6 - Department DELETE
     */
    @Path("department")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int dept_id) {
        return cb.deleteDepartment(company, dept_id);
    }

    /*
     * 7 - Department GET
     */
    @Path("employee")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployee(@QueryParam("emp_id") int emp_id) {
        return cb.getEmployee(emp_id);
    }

    /**
     * 8 - Employees GET
     *
     * @param company
     * @return
     */
    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@DefaultValue("vxh5681") @QueryParam("company") String company) {
        return cb.getAllEmployees(company);
    }

    /*
     * 9- Employee POST
     */
    @POST
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(@FormParam("emp_name") String emp_name,
            @FormParam("emp_no") String emp_no,
            @FormParam("hire_date") Date hire_date,
            @FormParam("job") String job,
            @FormParam("salary") double salary,
            @FormParam("dept_id") int dept_id,
            @FormParam("mng_id") int mng_id) {

        return cb.insertEmployee(emp_name, emp_no, hire_date, job, salary, dept_id, mng_id);
    }

    /*
     * 10- Employee PUT
     */
    @PUT
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(@QueryParam("inJSON") String inJSON) {
        return cb.updateEmployee(inJSON);
    }

    /*
     * 11 - Department DELETE
     */
    @Path("employee")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(@QueryParam("emp_id") int emp_id) {
        return cb.deleteEmployee(emp_id);
    }

    /*
     * 12 - Timecard GET
     */
    @Path("timecard")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecard(@QueryParam("timecard_id") int timecard_id) {
        return cb.getTimecard(timecard_id);
    }

    /*
     * 13 - Timecards GET
     */
    @GET
    @Path("/timecards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTimecards(@QueryParam("emp_id") int emp_id) {
        return cb.getAllTimecards(emp_id);
    }

    /*
     * 14- Timecard POST
     */
    @POST
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTimecard(@FormParam("emp_id") int emp_id,
            @FormParam("start_time") Timestamp start_time,
            @FormParam("end_time") Timestamp end_time) {
        return cb.insertTimecard(emp_id, start_time, end_time);
    }

    /*
     * 15- Timecard PUT
     */
    @PUT
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTimecard(@QueryParam("inJSON") String inJSON) {
        return cb.updateTimecard(inJSON);
    }

    /*
     * 16 - Timecard DELETE
     */
    @Path("timecard")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTimecard(@QueryParam("timecard_id") int timecard_id) {
        return cb.deleteTimecard(timecard_id);
    }
}
