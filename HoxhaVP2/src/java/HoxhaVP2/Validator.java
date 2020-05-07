package HoxhaVP2;

import companydata.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Vjori Hoha
 */
public class Validator {

    private DataLayer dl;
    private static final String COMPANY = "vxh5681";

    public Validator() {
        try {
            dl = new DataLayer("production");  //use “production” if on Glassfish
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDeptNoUnique(String dept_no, String company) {
        List<Department> allDepartmentsForValidation = dl.getAllDepartment(company);
        //if false is dept_no is not unique because it already exists
        return allDepartmentsForValidation.stream().noneMatch((dep) -> (dep.getDeptNo().equals(dept_no)));
    }

    public boolean isDeptIdExistingRecord(int dept_id, String company) {
        List<Department> allDepartmentsForValidation = dl.getAllDepartment(company);
        //if true Dept_id already exists
        return allDepartmentsForValidation.stream().anyMatch((dep) -> (dep.getId() == dept_id));
    }

    public boolean isMngIdExistingRecord(int mng_id, String company) {
        List<Employee> allEmployeeForValidation = dl.getAllEmployee(company);
        return allEmployeeForValidation.stream().anyMatch((emp) -> (emp.getMngId() == mng_id));
    }

    public boolean isDateValid(String date, String type) {
        SimpleDateFormat format = null;
        if (type.equals("HIRE_DATE")) {
            format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        } else if (type.equals("TIMECARD_DATE")) {
            format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        }
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isHireDateEqualToCurrentDateOrEarlier(Date hire_date) {
        LocalDate localDate = hire_date.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (!localDate.isBefore(currentDate)) {
            if (!localDate.isEqual(currentDate)) {
                return false;
            }
        }
        return true;
    }

    public boolean isHireDateWeekday(Date hire_date) {
        LocalDate localDate = hire_date.toLocalDate();
        //if saturday or sunday Return false
        return !(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    public boolean isEmpNoUnique(String emp_no, String company) {
        List<Employee> allEmployeeForValidation = dl.getAllEmployee(company);
        //if false is emp_no is not unique because it already exists
        return allEmployeeForValidation.stream().noneMatch((emp) -> (emp.getEmpNo().equals(emp_no)));
    }

    public boolean isEmpIdExistingRecord(int emp_id, String company) {
        List<Employee> allEmployeeForValidation = dl.getAllEmployee(company);
        //if true Emp_id already exists
        return allEmployeeForValidation.stream().anyMatch((emp) -> (emp.getId() == emp_id));
    }

    public boolean isStartTimeEqualToCurrDateOrOneWeekAgo(Timestamp start_time) {
        LocalDateTime localDateForStartTime = start_time.toLocalDateTime();
        LocalDateTime currentDate = LocalDateTime.now();
        //false if it is not equal to current date or up to one week ago
        if (!(currentDate.minusWeeks(1).compareTo(localDateForStartTime) * localDateForStartTime.compareTo(currentDate) >= 0)) {
            if (!localDateForStartTime.isEqual(currentDate)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEndDateOneHourGreaterAndBeOnSameDayAsStartTime(Timestamp start_time, Timestamp end_time) {
        LocalDateTime localDateForStartTime = start_time.toLocalDateTime();
        LocalDateTime localDateForEndTime = end_time.toLocalDateTime();
        boolean flag = true;
        if ((localDateForEndTime.getHour() - localDateForStartTime.getHour() == 1)) {
            if (localDateForEndTime.getMinute() - localDateForStartTime.getMinute() < 0) {
                flag = false;
            }
        }
        if (!flag) {
            return false;
        } else {
            if (!(localDateForEndTime.getHour() - localDateForStartTime.getHour() >= 1) || !(localDateForStartTime.getDayOfMonth() - localDateForEndTime.getDayOfMonth() == 0)) {
                return false;
            }
        }
        return true;
    }

    public boolean isStartOrEndTimeWeekday(Timestamp start_time, Timestamp end_time) {
        LocalDateTime localDateForStartTime = start_time.toLocalDateTime();
        LocalDateTime localDateForEndTime = end_time.toLocalDateTime();
        //false if it is weekend
        return !(localDateForEndTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateForEndTime.getDayOfWeek() == DayOfWeek.SUNDAY || localDateForStartTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateForStartTime.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    //06:00:00-18:00:00
    public boolean isBetweenHours(Timestamp start_time, Timestamp end_time) {
        LocalDateTime localDateForStartTime = start_time.toLocalDateTime();
        LocalDateTime localDateForEndTime = end_time.toLocalDateTime();
        if (!(localDateForStartTime.getHour() >= 6 && localDateForEndTime.getHour() <= 18)) {
            return false;
        } else if (localDateForEndTime.getHour() == 18) {
            if (localDateForEndTime.getMinute() > 0 || localDateForEndTime.getSecond() > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isStartTimeOnTheSameDay(Timestamp start_time, Timestamp end_time, int emp_id) {
        LocalDateTime localDateForStartTime = start_time.toLocalDateTime();
        LocalDateTime localDateForEndTime = end_time.toLocalDateTime();
        List<Timecard> timecards = dl.getAllTimecard(emp_id);
        boolean flag = true;
        for (Timecard tCards : timecards) {
            Timestamp time = tCards.getStartTime();
            LocalDateTime localTime = time.toLocalDateTime();
            if (localTime.getDayOfMonth() == localDateForStartTime.getDayOfMonth() && localTime.getMonthValue() == localDateForStartTime.getMonthValue() && localTime.getYear() == localDateForStartTime.getYear()) {
                flag = false;
            }
        }
        if (!flag) {
            return false;
        }
        return true;
    }

    public boolean isTimecardIdExistingRecord(int emp_id, int timecard_id) {
        List<Timecard> timeCardForUpdate = dl.getAllTimecard(emp_id);
        //if true Emp_id already exists
        return timeCardForUpdate.stream().anyMatch((tCard) -> (tCard.getId() == timecard_id));
    }
}
