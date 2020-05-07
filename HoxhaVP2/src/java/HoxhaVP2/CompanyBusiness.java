package HoxhaVP2;

import companydata.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * @author Vjori Hoxha
 */
public class CompanyBusiness {

    private DataLayer dl;
    private Validator validator;
    private static final String COMPANY = "vxh5681";

    public CompanyBusiness() {
        try {
            dl = new DataLayer("production");  //use “production” if on Glassfish
            validator = new Validator();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String deleteCompany(String company) {
        String result = "";
        int flag = dl.deleteCompany(company);
        if (flag > 0) {
            result = "{\"success\": \"Company " + flag + "" + " with id " + company + " was deleted.\"}";
            List<Department> deptList = dl.getAllDepartment(company);
            if (deptList.size() > 0) {
                deptList.forEach((dept) -> {
                    dl.deleteDepartment(company, dept.getId());
                });
            }
            List<Employee> emplList = dl.getAllEmployee(company);
            if (emplList.size() > 0) {
                emplList.forEach((empl) -> {
                    List<Timecard> tCardList = dl.getAllTimecard(empl.getId());
                    if (tCardList.size() > 0) {
                        tCardList.forEach((tCard) -> {
                            dl.deleteTimecard(tCard.getId());
                        });
                    }
                    dl.deleteEmployee(empl.getId());
                });
            }

        } else {
            result = ErrorMessage.getError("Company " + company + "" + " was not deleted.");
        }
        return result;
    }

    public String getDepartment(String company, int dept_id) {
        Department pulledDepartment = dl.getDepartment(company, dept_id);
        String responseJSON = "";
        if (pulledDepartment == null) {
            responseJSON = ErrorMessage.getError("Department does not exists.");
        } else {
            responseJSON
                    = "{"
                    + "\"dept_id\": " + pulledDepartment.getId() + "" + ","
                    + "\"company\":\"" + pulledDepartment.getCompany() + "\","
                    + "\"dept_name\":\"" + pulledDepartment.getDeptName() + "\","
                    + "\"dept_no\":\"" + pulledDepartment.getDeptNo() + "\","
                    + "\"location\":\"" + pulledDepartment.getLocation() + "\""
                    + "}";
        }
        return responseJSON;
    }

    public Response getAllDepartments(String company) {
        List<Department> list = dl.getAllDepartment(company);
        if (list.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.getError("There are no companies to be found")).build();
        }
        String responseJSON = "[";

        for (int i = 0; i < list.size(); i++) {
            responseJSON += "{"
                    + "\"dept_id\": " + list.get(i).getId() + "" + ","
                    + "\"company\":\"" + list.get(i).getCompany() + "\","
                    + "\"dept_name\":\"" + list.get(i).getDeptName() + "\","
                    + "\"dept_no\":\"" + list.get(i).getDeptNo() + "\","
                    + "\"location\":\"" + list.get(i).getLocation() + "\""
                    + "}";
            if (i != list.size() - 1) {
                responseJSON += ",";
            }
        }

        responseJSON += "]";
        return Response.ok(responseJSON).build();
    }

    public Response updateDepartment(String inJson) {
        JSONObject obj = new JSONObject(inJson);
        //Variables----------------------------------
        String company = obj.getString("company");
        String dept_name = obj.getString("dept_name");
        String dept_no = obj.getString("dept_no");
        int dept_id = obj.getInt("dept_id");
        String location = obj.getString("location");
        //-------------------------------------------

        if (!validator.isDeptNoUnique(dept_no, company)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Department number is not unique for a department")).build();
        }

        if (!validator.isDeptIdExistingRecord(dept_id, company)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Department id does not exists for a department")).build();
        }

        Department departmentToBeUpdated = new Department(dept_id, company, dept_name, dept_no, location);
        Department updatedDepartment = dl.updateDepartment(departmentToBeUpdated);
        String responseJSON = "";
        if (updatedDepartment == null) {
            responseJSON = ErrorMessage.getError("Department was not updated, something went wrong.");
        } else {
            responseJSON = "{\"success\": {"
                    + "\"dept_id\": " + updatedDepartment.getId() + "" + ","
                    + "\"company\":\"" + updatedDepartment.getCompany() + "\","
                    + "\"dept_name\":\"" + updatedDepartment.getDeptName() + "\","
                    + "\"dept_no\":\"" + updatedDepartment.getDeptNo() + "\","
                    + "\"location\":\"" + updatedDepartment.getLocation() + "\""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response insertDepartment(String company, String dept_name, String dept_no, String location) {
        if (!validator.isDeptNoUnique(dept_no, company)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Department number is not unique for a department")).build();
        }

        Department departmentToBeInserted = new Department(company, dept_name, dept_no, location);
        Department insertedDepartment = dl.insertDepartment(departmentToBeInserted);
        String responseJSON = "";
        if (insertedDepartment == null) {
            responseJSON = ErrorMessage.getError("Department was not created.");
        } else {
            responseJSON += "{\"success\": {"
                    + "\"dept_id\": " + insertedDepartment.getId() + "" + ","
                    + "\"company\":\"" + insertedDepartment.getCompany() + "\","
                    + "\"dept_name\":\"" + insertedDepartment.getDeptName() + "\","
                    + "\"dept_no\":\"" + insertedDepartment.getDeptNo() + "\","
                    + "\"location\":\"" + insertedDepartment.getLocation() + "\""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response deleteDepartment(String company, int dept_id) {
        String result = "";
        int flag = dl.deleteDepartment(company, dept_id);
        if (flag > 0) {
            result = "{\"success\": \"Department " + dept_id + "" + "from " + company + " deleted.\"}";
        } else {
            result = ErrorMessage.getError("Department " + dept_id + "" + "from " + company + " was not deleted.");
        }
        return Response.ok(result).build();
    }

    public Response getEmployee(int emp_id) {
        Employee pulledEmployee = dl.getEmployee(emp_id);
        String responseJSON = "";
        if (pulledEmployee == null) {
            responseJSON = ErrorMessage.getError("No employee was found");
        } else {
            responseJSON
                    = "{"
                    + "\"emp_id\": " + pulledEmployee.getId() + "" + ","
                    + "\"emp_name\":\"" + pulledEmployee.getEmpName() + "\","
                    + "\"emp_no\":\"" + pulledEmployee.getEmpNo() + "\","
                    + "\"hire_date\":" + pulledEmployee.getHireDate() + "" + ","
                    + "\"job\":\"" + pulledEmployee.getJob() + "\","
                    + "\"salary\":" + pulledEmployee.getSalary() + "" + ","
                    + "\"dept_id\":" + pulledEmployee.getDeptId() + "" + ","
                    + "\"mng_id\":" + pulledEmployee.getMngId() + ""
                    + "}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response getAllEmployees(String company) {
        List<Employee> list = dl.getAllEmployee(company);
        if (list.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.getError("There are no employees to be found")).build();
        }
        String responseJSON = "[";

        for (int i = 0; i < list.size(); i++) {
            responseJSON += "{"
                    + "\"emp_id\": " + list.get(i).getId() + "" + ","
                    + "\"emp_name\":\"" + list.get(i).getEmpName() + "\","
                    + "\"emp_no\":\"" + list.get(i).getEmpNo() + "\","
                    + "\"hire_date\":" + list.get(i).getHireDate() + "" + ","
                    + "\"job\":\"" + list.get(i).getJob() + "\","
                    + "\"salary\":" + list.get(i).getSalary() + "" + ","
                    + "\"dept_id\":" + list.get(i).getDeptId() + "" + ","
                    + "\"mng_id\":" + list.get(i).getMngId() + ""
                    + "}";
            if (i != list.size() - 1) {
                responseJSON += ",";
            }
        }

        responseJSON += "]";
        return Response.ok(responseJSON).build();
    }

    public Response insertEmployee(String emp_name, String emp_no, Date hire_date, String job, double salary, int dept_id, int mng_id) {
        /*First validation*/
        if (!validator.isDeptIdExistingRecord(dept_id, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Department id does not exists for employee")).build();
        }

        /*Second validation*/
        if (!validator.isMngIdExistingRecord(mng_id, COMPANY)) {
            mng_id = 0;
        }

        /*Third validation and fourth validation about date*/
        if (!validator.isDateValid(hire_date + "", "HIRE_DATE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage.getError("Hire date is not a valid date.")).build();
        }

        if (!validator.isHireDateWeekday(hire_date)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("You cannot hire on Saturday and Sunday.")).build();
        }

        if (!validator.isHireDateEqualToCurrentDateOrEarlier(hire_date)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("This is a future date.")).build();

        }

        /*Fifth validation
            There was a type in the Project2-Assigment document, it said emp_id to validate
            but in fact it should be written emp_no
         */
        if (!validator.isEmpNoUnique(emp_no, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Employee Number must be unique")).build();
        }

        Employee employeeToBeInserted = new Employee(emp_name, emp_no, hire_date, job, salary, dept_id, mng_id);
        Employee insertedEmployee = dl.insertEmployee(employeeToBeInserted);
        String responseJSON = "";
        if (insertedEmployee == null) {
            responseJSON = ErrorMessage.getError("No employee was created, something went wrong.");
        } else {
            responseJSON += "{\"success\": {"
                    + "\"emp_id\": " + insertedEmployee.getId() + "" + ","
                    + "\"emp_name\":\"" + insertedEmployee.getEmpName() + "\","
                    + "\"emp_no\":\"" + insertedEmployee.getEmpNo() + "\","
                    + "\"hire_date\":" + insertedEmployee.getHireDate() + "" + ","
                    + "\"job\":\"" + insertedEmployee.getJob() + "\","
                    + "\"salary\":" + insertedEmployee.getSalary() + "" + ","
                    + "\"dept_id\":" + insertedEmployee.getDeptId() + "" + ","
                    + "\"mng_id\":" + insertedEmployee.getMngId() + ""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response updateEmployee(String inJSON) {
        JSONObject obj = new JSONObject(inJSON);
        //Variables----------------------------------
        int emp_id = obj.getInt("emp_id");
        String emp_name = obj.getString("emp_name");
        String emp_no = obj.getString("emp_no");
        Date hire_date = Date.valueOf(obj.getString("hire_date"));
        String job = obj.getString("job");
        double salary = obj.getDouble("salary");
        int dept_id = obj.getInt("dept_id");
        int mng_id = obj.getInt("mng_id");
        //-------------------------------------------

        /*First validation*/
        if (!validator.isDeptIdExistingRecord(dept_id, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Department id does not exists for employee")).build();
        }

        /*Second validation*/
        if (!validator.isMngIdExistingRecord(mng_id, COMPANY)) {
            mng_id = 0;
        }

        /*Third validation and fourth validation about date*/
        if (!validator.isDateValid(hire_date + "", "HIRE_DATE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage.getError("Hire date is not a valid date.")).build();
        }

        if (!validator.isHireDateWeekday(hire_date)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("You cannot hire on Saturday and Sunday.")).build();
        }

        if (!validator.isHireDateEqualToCurrentDateOrEarlier(hire_date)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("This is a future date.")).build();

        }

        /*Fifth validation
            There was a type in the Project2-Assigment document, it said emp_id to validate
            but in fact it should be written emp_no
         */
        if (!validator.isEmpNoUnique(emp_no, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Employee Number must be unique")).build();
        }

        /*Sixth validation*/
        if (!validator.isEmpIdExistingRecord(emp_id, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Employee id does not exist")).build();
        }
        Employee employeeToBeUpdated = new Employee(emp_id, emp_name, emp_no, hire_date, job, salary, dept_id, mng_id);
        Employee updatedEmployee = dl.updateEmployee(employeeToBeUpdated);
        String responseJSON = "";
        if (updatedEmployee == null) {
            responseJSON = ErrorMessage.getError("No employee was updated, something went wrong.");
        } else {
            responseJSON += "{\"success\": {"
                    + "\"emp_id\": " + updatedEmployee.getId() + "" + ","
                    + "\"emp_name\":\"" + updatedEmployee.getEmpName() + "\","
                    + "\"emp_no\":\"" + updatedEmployee.getEmpNo() + "\","
                    + "\"hire_date\":" + updatedEmployee.getHireDate() + "" + ","
                    + "\"job\":\"" + updatedEmployee.getJob() + "\","
                    + "\"salary\":" + updatedEmployee.getSalary() + "" + ","
                    + "\"dept_id\":" + updatedEmployee.getDeptId() + "" + ","
                    + "\"mng_id\":" + updatedEmployee.getMngId() + ""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response deleteEmployee(int emp_id) {
        String result = "";
        int flag = dl.deleteEmployee(emp_id);
        if (flag > 0) {
            result = "{\"success\": \"Employee " + emp_id + "" + " was deleted.\"}";
        } else {
            result = ErrorMessage.getError("Employee " + emp_id + "" + " was not deleted.");
        }
        return Response.ok(result).build();
    }

    public Response getTimecard(int timecard_id) {
        Timecard pulledTimecard = dl.getTimecard(timecard_id);
        String responseJSON = "";
        if (pulledTimecard == null) {
            responseJSON = "{\"error\": \"Timecard with given " + timecard_id + "" + " number does not exists.\"}";
        } else {
            responseJSON = "{\"success\": {"
                    + "\"timecard_id\": " + pulledTimecard.getId() + "" + ","
                    + "\"start_time\":\"" + pulledTimecard.getStartTime() + "\","
                    + "\"end_time\":\"" + pulledTimecard.getEndTime() + "\","
                    + "\"emp_id\":" + pulledTimecard.getEmpId() + ""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response getAllTimecards(int emp_id) {
        List<Timecard> list = dl.getAllTimecard(emp_id);
        if (list.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.getError("There are no timecards to be found")).build();
        }
        String responseJSON = "[";
        for (int i = 0; i < list.size(); i++) {
            responseJSON += "{"
                    + "\"timecard_id\": " + list.get(i).getId() + "" + ","
                    + "\"start_time\":\"" + list.get(i).getStartTime() + "\","
                    + "\"end_time\":\"" + list.get(i).getEndTime() + "\","
                    + "\"emp_id\":" + list.get(i).getEmpId() + ""
                    + "}";
            if (i != list.size() - 1) {
                responseJSON += ",";
            }
        }
        responseJSON += "]";
        return Response.ok(responseJSON).build();
    }

    public Response insertTimecard(int emp_id, Timestamp start_time, Timestamp end_time) {
        /*First validation*/
        if (!validator.isEmpIdExistingRecord(emp_id, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Employee id does not exist")).build();
        }

        /*Valid time*/
        if (!validator.isDateValid(start_time + "", "TIMECARD_DATE") || !validator.isDateValid(end_time + "", "TIMECARD_DATE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage.getError("Hire date is not a valid date.")).build();
        }

        /*Second validation*/
        if (!validator.isStartTimeEqualToCurrDateOrOneWeekAgo(start_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("The start time must be today or up to 1 week ago from the current date.")).build();
        }

        /*Third validation*/
        if (!validator.isEndDateOneHourGreaterAndBeOnSameDayAsStartTime(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("The end time at least 1 hour greater than the start time and be on the same day as the start time.")).build();
        }


        /*Fourth validation*/
        if (!validator.isStartOrEndTimeWeekday(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Date must not be Saturday or Sunday.")).build();
        }

        /*Fifth validation*/
        if (!validator.isBetweenHours(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Time must be between 06:00:00 to 18:00:00 and End time minutes or time seconds must be 00.")).build();
        }

        /*Sixth validation*/
        if (!validator.isStartTimeOnTheSameDay(start_time, end_time, emp_id)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Start time must not be on the same day as any other start time for that employee.")).build();
        }

        Timecard timecardToBeInserted = new Timecard(start_time, end_time, emp_id);
        Timecard insertedEmployee = dl.insertTimecard(timecardToBeInserted);
        String responseJSON = "";
        if (insertedEmployee == null) {
            responseJSON = ErrorMessage.getError("No timecard was created, something went wrong.");
        } else {
            responseJSON += "{\"success\": {"
                    + "\"timecard_id\": " + insertedEmployee.getId() + "" + ","
                    + "\"start_time\":\"" + insertedEmployee.getStartTime() + "\","
                    + "\"end_time\":\"" + insertedEmployee.getEndTime() + "\","
                    + "\"emp_id\":" + insertedEmployee.getEmpId() + ""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response updateTimecard(String inJSON) {
        JSONObject obj = new JSONObject(inJSON);
        //Variables----------------------------------
        int timecard_id = obj.getInt("timecard_id");
        Timestamp start_time = Timestamp.valueOf(obj.getString("start_time"));
        Timestamp end_time = Timestamp.valueOf(obj.getString("end_time"));
        int emp_id = obj.getInt("emp_id");
        /*Note: Word document it does not have written emp_id as input, but you need it for validations and to create Timecard object to update*/
        //-------------------------------------------

        /*First validation*/
        if (!validator.isEmpIdExistingRecord(emp_id, COMPANY)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Employee id does not exist")).build();
        }

        /*Valid time*/
        if (!validator.isDateValid(start_time + "", "TIMECARD_DATE") || !validator.isDateValid(end_time + "", "TIMECARD_DATE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorMessage.getError("Hire date is not a valid date.")).build();
        }

        /*Second validation*/
        if (!validator.isStartTimeEqualToCurrDateOrOneWeekAgo(start_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("The start time must be today or up to 1 week ago from the current date.")).build();
        }

        /*Third validation*/
        if (!validator.isEndDateOneHourGreaterAndBeOnSameDayAsStartTime(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("The end time at least 1 hour greater than the start time and be on the same day as the start time.")).build();
        }

        /*Fourth validation*/
        if (!validator.isStartOrEndTimeWeekday(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Date must not be Saturday or Sunday.")).build();
        }

        /*Fifth validation*/
        if (!validator.isBetweenHours(start_time, end_time)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Time must be between 06:00:00 to 18:00:00 and End time minutes or time seconds must be 00.")).build();
        }

        /*Sixth validation*/
        if (!validator.isStartTimeOnTheSameDay(start_time, end_time, emp_id)) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorMessage.getError("Start time must not be on the same day as any other start time for that employee.")).build();
        }

        /*Seventh validation*/
        if (!validator.isTimecardIdExistingRecord(emp_id, timecard_id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorMessage.getError("Timecard id not found to be updated..")).build();
        }

        Timecard timecardToBeUpdated = new Timecard(timecard_id, start_time, end_time, emp_id);
        Timecard updatedEmployee = dl.updateTimecard(timecardToBeUpdated);
        String responseJSON = "";
        if (updatedEmployee == null) {
            responseJSON = ErrorMessage.getError("No timecard was not updated, something went wrong.");
        } else {
            responseJSON += "{\"success\": {"
                    + "\"timecard_id\": " + updatedEmployee.getId() + "" + ","
                    + "\"start_time\":\"" + updatedEmployee.getStartTime() + "\","
                    + "\"end_time\":\"" + updatedEmployee.getEndTime() + "\","
                    + "\"emp_id\":" + updatedEmployee.getEmpId() + ""
                    + "}}";
        }
        return Response.ok(responseJSON).build();
    }

    public Response deleteTimecard(int timecard_id) {
        String result = "";
        int flag = dl.deleteTimecard(timecard_id);
        if (flag > 0) {
            result = "{\"success\": \"Timecard " + flag + "" + " with id " + timecard_id + "" + " was deleted.\"}";
        } else {
            result = ErrorMessage.getError("Timecard " + flag + "" + " was not deleted.");
        }
        return Response.ok(result).build();
    }
}